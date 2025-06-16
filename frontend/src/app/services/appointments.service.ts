import { Injectable } from '@angular/core';
import axios from 'axios';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from './auth.service';
import {
  Appointment,
  AppointmentFilterForm,
  CreateAppointmentForm,
  RescheduleAppointmentForm,
  StatusDto,
} from '../models/appointment';
import { PageableRequest, PageDto } from '../models/pageable-request';

@Injectable({
  providedIn: 'root',
})
export class AppointmentsService {
  token = localStorage.getItem('accessToken');
  constructor(
    private toastr: ToastrService,
    private authService: AuthService
  ) {}

  async findAllAppointments(
    filterForm: AppointmentFilterForm,
    pageableRequest: PageableRequest
  ): Promise<PageDto<Appointment>> {
    const params = new URLSearchParams();

    // Dodawanie parametrów paginacji
    params.append('page', pageableRequest.page.toString());
    params.append('size', pageableRequest.size.toString());

    // POPRAWKA TUTAJ: Jeśli sortField i sortDirection są dostępne, dodaj je jako jeden parametr 'sort'
    if (pageableRequest.sortField && pageableRequest.sortDirection) {
      params.append('sortField', `${pageableRequest.sortField}`);
      params.append('sortDirection', `${pageableRequest.sortDirection}`);
    }

    // Przygotowanie danych do wysłania w ciele żądania (filterForm)
    const requestBody = {
      status: filterForm.status,
      bookedDateStart: filterForm.bookedDateStart,
      bookedDateEnd: filterForm.bookedDateEnd,
      hairdresserName: filterForm.hairdresserName,
      hairdresserSurname: filterForm.hairdresserSurname,
    };

    try {
      const response = await axios.post(`/appointments/all`, requestBody, {
        params: params,
        headers: {
          Authorization: `Bearer ${this.token}`,
          'Content-Type': 'application/json',
        },
      });
      this.toastr.success('Udało się pobrać wszystkie spotkania');
      return response.data;
    } catch (error) {
      this.toastr.error('Błąd pobierania wszystkich spotkań');
      console.error('Błąd pobierania all appointments', error);
      throw error;
    }
  }

  async getAllAppointmentsByDate(date: Date): Promise<any> {
    try {
      const dateFormatted = date.toISOString().split('T')[0];
      const response = await axios.get(
        `/appointments-singleday?date=${dateFormatted}`,
        {
          headers: {
            Authorization: `Bearer ${this.token}`,
          },
        }
      );
      this.toastr.success('Udało się pobrać appointments');
      console.log(response);
      return response.data;
    } catch (error) {
      this.toastr.error('Błąd pobierania appointments');
      console.error('Błąd pobierania appointments', error);
      throw error;
    }
  }

  async getAllAppointmentsSpecificUser(
    filterForm: AppointmentFilterForm,
    pageableRequest: PageableRequest
  ): Promise<PageDto<Appointment>> {
    const params = new URLSearchParams();

    // Dodawanie parametrów paginacji
    params.append('page', pageableRequest.page.toString());
    params.append('size', pageableRequest.size.toString());

    // POPRAWKA TUTAJ: Jeśli sortField i sortDirection są dostępne, dodaj je jako jeden parametr 'sort'
    if (pageableRequest.sortField && pageableRequest.sortDirection) {
      params.append('sortField', `${pageableRequest.sortField}`);
      params.append('sortDirection', `${pageableRequest.sortDirection}`);
    }

    // Przygotowanie danych do wysłania w ciele żądania (filterForm)
    const requestBody = {
      status: filterForm.status,
      bookedDateStart: filterForm.bookedDateStart,
      bookedDateEnd: filterForm.bookedDateEnd,
      hairdresserName: filterForm.hairdresserName,
      hairdresserSurname: filterForm.hairdresserSurname,
    };

    try {
      const response = await axios.post(
        `/users/${this.authService.getUserId()}/appointments`,
        requestBody,
        {
          params: params,
          headers: {
            Authorization: `Bearer ${this.token}`,
            'Content-Type': 'application/json',
          },
        }
      );
      this.toastr.success(
        'Udało się pobrać appointments usera o id' +
          this.authService.getUserId()
      );
      return response.data;
    } catch (error) {
      this.toastr.error(
        'Błąd pobierania appointmentts usera o id' +
          this.authService.getUserId()
      );
      console.error('Błąd pobierania appointments usera', error);
      throw error;
    }
  }

  async addAppointment(
    createAppointmentForm: CreateAppointmentForm
  ): Promise<any> {
    try {
      const response = await axios.post(
        `/appointments`,
        createAppointmentForm,
        {
          headers: {
            Authorization: `Bearer ${this.token}`,
          },
        }
      );
      this.toastr.success('Udało się stworzyć appointments');
      return response.data;
    } catch (error) {
      this.toastr.error('Błąd tworzenia appointments');
      console.error('Błąd tworzenia appointments', error);
      throw error;
    }
  }

  async findAllClientAppointments(
    filterForm: AppointmentFilterForm,
    pageableRequest: PageableRequest // Używamy poprawionego interfejsu PageableRequest
  ): Promise<PageDto<Appointment>> {
    const params = new URLSearchParams();

    // Dodawanie parametrów paginacji
    params.append('page', pageableRequest.page.toString());
    params.append('size', pageableRequest.size.toString());

    // POPRAWKA TUTAJ: Jeśli sortField i sortDirection są dostępne, dodaj je jako jeden parametr 'sort'
    if (pageableRequest.sortField && pageableRequest.sortDirection) {
      params.append('sortField', `${pageableRequest.sortField}`);
      params.append('sortDirection', `${pageableRequest.sortDirection}`);
    }

    // Przygotowanie danych do wysłania w ciele żądania (filterForm)
    const requestBody = {
      status: filterForm.status,
      bookedDateStart: filterForm.bookedDateStart,
      bookedDateEnd: filterForm.bookedDateEnd,
      hairdresserName: filterForm.hairdresserName,
      hairdresserSurname: filterForm.hairdresserSurname,
    };

    try {
      const response = await axios.post<PageDto<Appointment>>(
        '/my-appointments',
        requestBody,
        {
          params: params,
          headers: {
            Authorization: `Bearer ${this.token}`,
            'Content-Type': 'application/json',
          },
        }
      );

      this.toastr.success('Client appointments loaded successfully.');
      return response.data;
    } catch (error) {
      this.toastr.error('Error loading client appointments.');
      console.error('Error fetching client appointments:', error);
      throw error;
    }
  }

  async generateAvailableSlots(
    dateTime: string,
    duration: number
  ): Promise<any> {
    try {
      const response = await axios.get(
        `/appointments/available-slots?date=${dateTime}&duration=${duration}`,
        {
          headers: {
            Authorization: `Bearer ${this.token}`,
          },
        }
      );
      this.toastr.success('Udało się wygenerowac sloty');
      return response.data;
    } catch (error) {
      this.toastr.error('Błąd generowania slotu');
      console.error('Błąd generowania slotu', error);
      throw error;
    }
  }

  async changeAppointmentStatus(
    uuid: string,
    status: StatusDto
  ): Promise<void> {
    try {
      const response = await axios.patch(
        `/appointments/${uuid}/change-status`,
        status,
        {
          headers: {
            Authorization: `Bearer ${this.token}`,
          },
        }
      );
      this.toastr.success('Udało się zmienić status spotkania');
    } catch (error) {
      this.toastr.error('Błąd zmiany statusu spotkania');
      console.error('Błąd zmiany statusu spotkania', error);
      throw error;
    }
  }

  async changeAppointmentTerm(
    appointmentUuid: string,
    newBookedDate: string
  ): Promise<void> {
    const url = `/appointments/${appointmentUuid}/reschedule`;
    const body: RescheduleAppointmentForm = {
      newBookedDate: newBookedDate,
    };

    try {
      // AxiosService handles Authorization header via its interceptor
      await axios.patch(url, body, {
        headers: {
          Authorization: `Bearer ${this.token}`,
        },
      });
      this.toastr.success('Termin spotkania zmieniony pomyślnie!');
    } catch (error) {
      this.toastr.error('Błąd podczas zmiany terminu spotkania.');
      console.error('Błąd zmiany terminu spotkania:', error);
      throw error; // Przekaż błąd dalej, aby komponent mógł go obsłużyć
    }
  }

  async getAllHairdresserAppointments(
    filterForm: AppointmentFilterForm,
    pageableRequest: PageableRequest
  ): Promise<PageDto<Appointment>> {
    const params = new URLSearchParams();

    // Dodawanie parametrów paginacji
    params.append('page', pageableRequest.page.toString());
    params.append('size', pageableRequest.size.toString());

    // POPRAWKA TUTAJ: Jeśli sortField i sortDirection są dostępne, dodaj je jako jeden parametr 'sort'
    if (pageableRequest.sortField && pageableRequest.sortDirection) {
      params.append('sortField', `${pageableRequest.sortField}`);
      params.append('sortDirection', `${pageableRequest.sortDirection}`);
    }

    // Przygotowanie danych do wysłania w ciele żądania (filterForm)
    const requestBody = {
      status: filterForm.status,
      bookedDateStart: filterForm.bookedDateStart,
      bookedDateEnd: filterForm.bookedDateEnd,
      hairdresserName: filterForm.hairdresserName,
      hairdresserSurname: filterForm.hairdresserSurname,
    };

    try {
      const response = await axios.post(
        `/users/${this.authService.getUserId()}/hairdresser-appointments`,
        requestBody,
        {
          params: params,
          headers: {
            Authorization: `Bearer ${this.token}`,
            'Content-Type': 'application/json',
          },
        }
      );
      this.toastr.success(
        'Udało się pobrać appointments fryzjera o id' +
          this.authService.getUserId()
      );
      return response.data;
    } catch (error) {
      this.toastr.error(
        'Błąd pobierania appointmentts fryzjera o id' +
          this.authService.getUserId()
      );
      console.error('Błąd pobierania appointments usera', error);
      throw error;
    }
  }
}
