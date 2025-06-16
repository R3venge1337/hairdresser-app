import { Injectable } from '@angular/core';
import {
  CreateHairOfferForm,
  HairOfferFilterForm,
  UpdateHairOfferForm,
} from '../models/hairoffers';
import axios from 'axios';
import { ToastrService } from 'ngx-toastr';
import { PageableRequest } from '../models/pageable-request';

@Injectable({
  providedIn: 'root',
})
export class HairoffersService {
  token = localStorage.getItem('accessToken');
  constructor(private toastr: ToastrService) {}

  async getAllHairOffers(
    filterForm: HairOfferFilterForm,
    pageableRequest: PageableRequest
  ): Promise<any> {
    try {
      const response = await axios.post(
        `/hairoffers/filter?page=${pageableRequest.page}&size=${pageableRequest.size}&sortField=${pageableRequest.sortField}&sortDirection=${pageableRequest.sortDirection}`,
        filterForm,
        {
          headers: {
            Authorization: `Bearer ${this.token}`,
          },
        }
      );
      this.toastr.success('Udało się pobrać hairoffers');
      return response.data;
    } catch (error) {
      this.toastr.error('Błąd pobierania hairoffers');
      console.error('Błąd pobierania hairoffers:', error);
      throw error;
    }
  }

  async getHairOfferById(id: number): Promise<any> {
    try {
      const response = await axios.get(`/hairoffers/${id}`, {
        headers: {
          Authorization: `Bearer ${this.token}`, // Ensure the token is valid
        },
      });
      this.toastr.success('Udało się pobrać pojedyńczego hairOffers');
      return response.data;
    } catch (error) {
      this.toastr.error('Błąd pobierania hairoffers');
      console.error('Błąd pobierania hairoffers:', error);
      throw error;
    }
  }

  async updateHairOffer(
    id: number,
    updateHairOfferForm: UpdateHairOfferForm
  ): Promise<any> {
    try {
      const response = await axios.put(
        `/hairoffers/${id}`,
        updateHairOfferForm,
        {
          headers: {
            Authorization: `Bearer ${this.token}`,
          },
        }
      );
      this.toastr.success('Udało się zaktualizować hairOffer');
      return response.data;
    } catch (error) {
      this.toastr.error('Błąd Aktualizowania hairOffer');
      console.error('Błąd Aktualizowania hairOffer:', error);
      throw error;
    }
  }

  async addHairoffer(createHairOfferForm: CreateHairOfferForm): Promise<any> {
    try {
      const response = await axios.post(`/hairoffers`, createHairOfferForm, {
        headers: {
          Authorization: `Bearer ${this.token}`,
        },
      });
      this.toastr.success('Udało się stworzyć hairOffer');
      return response.data;
    } catch (error) {
      this.toastr.error('Błąd tworzenia hairoffer');
      console.error('Błąd tworzenia hairoffer', error);
      throw error;
    }
  }
}
