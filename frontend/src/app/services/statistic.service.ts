import { Injectable } from '@angular/core';
import axios from 'axios';
import { ToastrService } from 'ngx-toastr';
import { HairOfferStatistics, Statistics } from '../models/statistics';

@Injectable({
  providedIn: 'root',
})
export class StatisticService {
  private token: string; // Pamiętaj o obsłudze tokena autoryzacji (np. z localStorage)

  constructor(private toastService: ToastrService) {
    this.token = localStorage.getItem('accessToken') || '';
  }

  async getAppointmentsByStatus(): Promise<Statistics[]> {
    try {
      const response = await axios.get(`/appointments/statistics-by-status`, {
        headers: {
          Authorization: `Bearer ${this.token}`,
          'Content-Type': 'application/json',
        },
      });
      console.log(response);
      this.toastService.success('Udało się pobrać dane do wykresu');
      return response.data;
    } catch (error) {
      this.toastService.error('Nie udało się pobrać danych do wykresu');
      console.error('Error fetching appointments by status:', error);
      throw error;
    }
  }

  async getCompletedHairOfferCounts(): Promise<HairOfferStatistics[]> {
    try {
      const response = await axios.get(
        `/statistics/statistics-by-hairoffer-name`,
        {
          headers: {
            Authorization: `Bearer ${this.token}`,
            'Content-Type': 'application/json',
          },
        }
      );
      console.log(response.data);
      this.toastService.success('Udało się pobrać dane do wykresu usług');
      return response.data;
    } catch (error) {
      this.toastService.error('Nie udało się pobrać danych do wykresu usług');
      console.error('Error fetching completed hair offer counts:', error);
      throw error;
    }
  }
}
