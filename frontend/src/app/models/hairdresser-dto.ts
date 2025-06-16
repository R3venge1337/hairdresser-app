import { DateTime } from 'luxon';

export interface HairdresserDto {
  id: number;
  uuid: string;
  firstname: string;
  availableSlots: string[];
  lastname: string;
}

export interface HairdresserAppointmentChecker {
  startDate: DateTime;
  totalDurations: number;
}
