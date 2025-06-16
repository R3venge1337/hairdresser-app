import { Injectable } from '@angular/core';
import axios from 'axios';
import { ToastrService } from 'ngx-toastr';
import { HairdresserDto } from '../models/hairdresser-dto';
import { RegisterForm } from '../models/register-form';

@Injectable({
  providedIn: 'root',
})
export class HairdresserService {
  token = localStorage.getItem('accessToken');
  constructor(private toastr: ToastrService) {}

  async getAllUsersByRole(role: string): Promise<HairdresserDto[]> {
    try {
      const response = await axios.get(`/users?roleName=${role}`, {
        headers: {
          Authorization: `Bearer ${this.token}`,
        },
      });
      this.toastr.success('Udało się pobrać userów o roli hairdressers');
      return response.data;
    } catch (error) {
      this.toastr.error('Błąd pobierania userow o roli hairdressers');
      console.error('Błąd pobierania userow o roli hairdressers', error);
      throw error;
    }
  }

  async addHairdresser(registerForm: RegisterForm): Promise<void> {
    try {
      const response = await axios.post(`/hairdressers`, registerForm, {
        headers: {
          Authorization: `Bearer ${this.token}`,
        },
      });
      this.toastr.success('Udało się dodać nowego fryzjera');
    } catch (error) {
      this.toastr.error('Błąd dodania nowego fryzjera');
      console.error('Błąd dodania nowego fryzjera', error);
      throw error;
    }
  }
}
