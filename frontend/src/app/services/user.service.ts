import { Injectable } from '@angular/core';
import { UserProfileDetailsDto } from '../models/user-profile-details-dto';
import axios from 'axios';
import { ToastrService } from 'ngx-toastr';
import { PageableRequest, PageDto } from '../models/pageable-request';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private token: string;

  constructor(private toastService: ToastrService) {
    this.token = localStorage.getItem('accessToken') || '';
  }

  async getUserProfileDetails(
    uuid: string
  ): Promise<UserProfileDetailsDto | null> {
    try {
      const response = await axios.get<UserProfileDetailsDto>(
        `/my-profile?uuid=${uuid}`,
        {
          headers: {
            Authorization: `Bearer ${this.token}`,
            'Content-Type': 'application/json',
          },
        }
      );
      console.log('Dane profilu użytkownika:', response.data);
      this.toastService.success('Pomyślnie pobrano dane profilu użytkownika.');
      return response.data;
    } catch (error) {
      this.toastService.error(
        'Nie udało się pobrać danych profilu użytkownika.'
      );
      console.error('Błąd podczas pobierania profilu użytkownika:', error);
      // Możesz zwrócić null lub rzucić błąd, w zależności od logiki obsługi błędów
      throw error;
    }
  }

  /**
   * Pobiera listę wszystkich profili użytkowników.
   * @returns Lista obiektów UserProfileDetails.
   */
  async getAllUserProfiles(
    pageableRequest: PageableRequest
  ): Promise<PageDto<UserProfileDetailsDto>> {
    const params = new URLSearchParams();
    // Dodawanie parametrów paginacji
    params.append('page', pageableRequest.page.toString());
    params.append('size', pageableRequest.size.toString());

    // POPRAWKA TUTAJ: Jeśli sortField i sortDirection są dostępne, dodaj je jako jeden parametr 'sort'
    if (pageableRequest.sortField && pageableRequest.sortDirection) {
      params.append('sortField', `${pageableRequest.sortField}`);
      params.append('sortDirection', `${pageableRequest.sortDirection}`);
    }
    try {
      const response = await axios.get(`/all-profiles`, {
        params: params,
        headers: {
          Authorization: `Bearer ${this.token}`,
          'Content-Type': 'application/json',
        },
      });
      console.log('Wszystkie profile użytkowników:', response.data);
      this.toastService.success(
        'Pomyślnie pobrano wszystkie profile użytkowników.'
      );
      return response.data;
    } catch (error) {
      this.toastService.error(
        'Nie udało się pobrać wszystkich profili użytkowników.'
      );
      console.error('Błąd podczas pobierania wszystkich profili:', error);
      throw error;
    }
  }
}
