import Big from 'big.js';

export interface Hairoffers {
  id: number;
  name: string;
  description: string;
  price: Big;
  duration: number;
}

export interface HairOfferFilterForm {
  name: string;
  priceLow: Big | null;
  priceHigh: Big | null;
  durationLow: number | null;
  durationHigh: number | null;
}

export interface UpdateHairOfferForm {
  name: string;
  description: string;
  price: Big;
  duration: number;
}

export interface CreateHairOfferForm {
  name: string;
  description: string;
  price: Big;
  duration: number;
}
