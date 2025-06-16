import { Hairoffers } from './hairoffers';

export interface Appointment {
  appointmentUuid: string;
  customerUuid: string; // UUID jako string, jeśli nie używasz modułu crypto
  customerFirstname: string;
  customerSurname: string;
  hairDresserUuid: string; // UUID jako string
  hairDresserFirstname: string;
  hairDresserSurname: string;
  totalCost: number; // BigDecimal w TS to zazwyczaj number lub string dla precyzji
  procedures: Hairoffers[]; // Zestaw usług, które wchodzą w skład spotkania
  appointmentStatus: string; // np. 'BOOKED', 'COMPLETED', 'CANCELLED'
  bookedAppointment: string; // LocalDateTime w TS to string (ISO 8601)
  finishedAppointment: string;
}

export interface CreateAppointmentForm {
  customerUuid: string; // UUID w Javie -> string w TS
  hairdresserUuid: string; // UUID w Javie -> string w TS
  hairoffers: number[]; // Lista Longów w Javie -> number[] w TS (zakładając, że Longi to IDki)
  bookedDate: string;
  finishedDate: string; // LocalDateTime w Javie -> string w TS (format ISO 8601, np. "2025-05-22T10:30:00")
}

export interface AppointmentFilterForm {
  status?: string | null;
  bookedDateStart?: string | null; // LocalDateTime jako string ISO 8601
  bookedDateEnd?: string | null; // LocalDateTime jako string ISO 8601
  hairdresserName?: string | null;
  hairdresserSurname?: string | null;
}

export interface AvailableSlotDto {
  startTime?: string | null; // LocalDateTime jako string ISO 8601
  endTime?: string | null; // LocalDateTime jako string ISO 8601
  hairdresserId: string;
  hairdresserName: string;
  hairdresserSurname: string;
}

export interface StatusDto {
  statusValue: string;
}

export interface RescheduleAppointmentForm {
  newBookedDate: string;
}
