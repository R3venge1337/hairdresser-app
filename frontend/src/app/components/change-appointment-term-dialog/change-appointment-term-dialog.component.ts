import { CommonModule } from '@angular/common';
import { Component, Inject, signal, WritableSignal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatNativeDateModule } from '@angular/material/core';
import {
  MatDatepickerInputEvent,
  MatDatepickerModule,
} from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { Appointment, AvailableSlotDto } from '../../models/appointment';
import { AppointmentsService } from '../../services/appointments.service';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';

@Component({
  selector: 'app-change-appointment-term-dialog',
  imports: [
    CommonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    FormsModule,
    TranslateModule,
    MatProgressSpinnerModule,
    MatRadioModule,
  ],
  templateUrl: './change-appointment-term-dialog.component.html',
  styleUrl: './change-appointment-term-dialog.component.css',
})
export class ChangeAppointmentTermDialogComponent {
  appointment: Appointment;
  selectedDate: Date | null = null;
  availableSlots: WritableSignal<AvailableSlotDto[]> = signal([]);
  isLoadingSlots: WritableSignal<boolean> = signal(false);
  selectedSlot: AvailableSlotDto | null = null;
  minDate: Date; // Minimalna data do wyboru (dzisiaj)

  constructor(
    public dialogRef: MatDialogRef<ChangeAppointmentTermDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Appointment,
    private appointmentService: AppointmentsService,
    private toastr: ToastrService,
    private translate: TranslateService
  ) {
    this.appointment = data;
    this.minDate = new Date(); // Dziś jako minimalna data
  }

  ngOnInit(): void {
    // Możesz zainicjować wybraną datę na aktualną datę spotkania
    // this.selectedDate = new Date(this.appointment.bookedAppointment);
    // this.loadAvailableSlots(); // Jeśli chcesz od razu po otwarciu
  }

  /**
   * Ładuje dostępne sloty dla wybranej daty i fryzjera.
   */
  async loadAvailableSlots(
    event?: MatDatepickerInputEvent<Date>
  ): Promise<void> {
    this.selectedDate = event ? event.value : this.selectedDate;
    if (!this.selectedDate) {
      this.toastr.warning(
        this.translate.instant('changeAppointmentDialog.selectDateWarning')
      );
      return;
    }

    this.isLoadingSlots.set(true);
    this.availableSlots.set([]); // Wyczyść poprzednie sloty
    this.selectedSlot = null; // Wyczyść wybrany slot

    const totalDuration = this.appointment.procedures.reduce(
      (sum, offer) => sum + offer.duration,
      0
    );

    // Konwertuj Date na LocalDate
    const localDate = new Date(this.selectedDate.getTime())
      .toISOString()
      .split('T')[0];

    // Backend potrzebuje LocalDateTime, ale tutaj generujemy tylko datę
    // Dostępne sloty będą generowane dla konkretnych godzin dla DANEGO FRYZJERA
    // Możesz pobrać dostępne sloty dla TEGO SAMEGO fryzjera i tych samych usług.
    // Backend potrzebuje selectedDate (LocalDate) i totalDuration.
    // Backend generuje listę slotów dla WSZYSTKICH fryzjerów, my musimy filtrować tutaj po fryzjerze.

    try {
      const slots = await this.appointmentService.generateAvailableSlots(
        localDate,
        totalDuration
      );
      const filteredSlots = slots.filter(
        (slot: AvailableSlotDto) =>
          slot.hairdresserId === this.appointment.hairDresserUuid
      );
      this.availableSlots.set(filteredSlots);
      this.isLoadingSlots.set(false);
      if (filteredSlots.length === 0) {
        this.toastr.info(
          this.translate.instant('changeAppointmentDialog.noSlotsInfo')
        );
      }
    } catch (error) {
      console.error('Error loading available slots:', error);
      this.toastr.error(
        this.translate.instant('changeAppointmentDialog.errorLoadingSlots')
      );
      this.isLoadingSlots.set(false);
    }
  }

  /**
   * Wybieranie slotu przez użytkownika.
   * @param slot Wybrany slot
   */
  selectSlot(slot: AvailableSlotDto): void {
    this.selectedSlot = slot;
  }

  /**
   * Potwierdza zmianę terminu spotkania.
   */
  async confirmReschedule(): Promise<void> {
    if (!this.selectedSlot) {
      this.toastr.warning(
        this.translate.instant('changeAppointmentDialog.selectSlotWarning')
      );
      return;
    }

    // Skonstruuj nową datę i godzinę ze selectedDate i selectedSlot.startTime
    const newBookedDateTime = `${this.formatLocalDateForBackend(
      this.selectedDate!
    )}T${this.selectedSlot.startTime}`;

    try {
      // Wywołaj serwis do zmiany terminu spotkania używając async/await
      await this.appointmentService.changeAppointmentTerm(
        this.appointment.appointmentUuid,
        newBookedDateTime
      );
      // Sukces jest obsługiwany przez toastr w serwisie, więc tutaj tylko zamykamy dialog.
      this.dialogRef.close(true); // Zamknij dialog i zwróć 'true' (sukces)
    } catch (error) {
      // Błąd jest obsługiwany przez toastr w serwisie, więc tutaj tylko logujemy i zamykamy dialog.
      console.error('Error rescheduling appointment:', error);
      this.dialogRef.close(false); // Zamknij dialog i zwróć 'false' (błąd)
    }
  }

  /**
   * Anuluje operację i zamyka dialog.
   */
  onNoClick(): void {
    this.dialogRef.close(false); // Zamknij dialog i zwróć 'false'
  }

  getServicesString(appointment: Appointment): string {
    if (!appointment.procedures || appointment.procedures.length === 0) {
      return '';
    }
    return appointment.procedures.map((offer) => offer.name).join(', ');
  }

  private formatLocalDateForBackend(date: Date): string {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0'); // Miesiące są od 0 do 11
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
