import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  signal,
  WritableSignal,
} from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { DateTime } from 'luxon';
import { ToastrService } from 'ngx-toastr';
import { AppointmentsService } from '../../services/appointments.service';
import { Hairoffers } from '../../models/hairoffers';
import { HairoffersService } from '../../services/hairoffers.service';
import { MatSelectModule } from '@angular/material/select';
import Big from 'big.js';
import {
  AvailableSlotDto,
  CreateAppointmentForm,
} from '../../models/appointment';
import { HairdresserDto } from '../../models/hairdresser-dto';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../services/auth.service';

interface AvailableSlot {
  time: string; // np. "09:00"
  // Możesz dodać też pełną datę i czas, jeśli potrzebne
}

interface HairdresserAvailability {
  hairdresserId: string;
  hairdresserName: string;
  availableSlots: string[]; // Lista stringów "HH:MM"
}

@Component({
  selector: 'app-assign-appointment',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
  ],
  templateUrl: './assign-appointment.component.html',
  styleUrl: './assign-appointment.component.css',
})
export class AssignAppointmentComponent implements OnInit {
  @Input() selectedDate!: DateTime; // Data wybrana z kalendarza Luxon.DateTime

  @Output() bookingCompleted = new EventEmitter<void>();
  @Output() cancelBooking = new EventEmitter<void>();

  bookingForm: FormGroup;

  selectedHairOffers: WritableSignal<Hairoffers[]> = signal([]); // Wybrane usługi

  availableHairdressers: WritableSignal<HairdresserDto[]> = signal([]);
  availableSlotsForSelectedHairdresser: WritableSignal<string[]> = signal([]);
  availableSlots: WritableSignal<AvailableSlotDto[]> = signal([]);
  selectedServicesTotalDuration: WritableSignal<number> = signal(0);
  hairoffers: WritableSignal<Hairoffers[]> = signal([]);

  constructor(
    private appointmentsService: AppointmentsService,
    private hairOfferService: HairoffersService, // Wstrzyknij serwis
    private toastr: ToastrService,
    private fb: FormBuilder,
    private authService: AuthService
  ) {
    this.bookingForm = this.fb.group({
      selectedServices: [null, Validators.required],
      selectedSlot: [null, Validators.required], // Nowe pole dla wybranego slotu
    });

    // Subskrybuj zmiany w wybranych usługach, aby zaktualizować totalDuration
    // Reaguj na zmiany wybranych usług, aby pobrać dostępnych fryzjerów/sloty
    this.bookingForm
      .get('selectedServices')
      ?.valueChanges.subscribe((selectedServiceIds: string[]) => {
        if (selectedServiceIds && selectedServiceIds.length > 0) {
          const totalDuration = selectedServiceIds.reduce((sum, serviceId) => {
            // Używamy this.hairOffers() (sygnału) zamiast this.hairOffers (Inputu)
            const service = this.hairoffers().find(
              (ho) => String(ho.id) === String(serviceId)
            );
            return sum + (service ? service.duration : 0);
          }, 0);
          this.selectedServicesTotalDuration.set(totalDuration);
        } else {
          this.selectedServicesTotalDuration.set(0);
        }
        this.availableSlots.set([]);
        this.bookingForm.get('selectedSlot')?.reset();
      });
  }

  async ngOnInit(): Promise<void> {
    await this.loadHairOffers();
  }

  async loadHairOffers(): Promise<void> {
    try {
      // Zakładamy, że masz metodę w serwisie do pobierania usług
      const offers = await this.hairOfferService.getAllHairOffers(
        {
          name: '',
          priceLow: Big(0),
          priceHigh: Big(999),
          durationLow: 0,
          durationHigh: 999,
        },
        { page: 1, size: 20, sortDirection: 'ASC', sortField: 'name' }
      );
      this.hairoffers.set(offers.content);
    } catch (error) {
      console.error('Błąd podczas pobierania usług:', error);
      this.toastr.error('Nie udało się załadować usług.');
    }
  }

  async onSubmit(): Promise<void> {
    if (this.bookingForm.invalid) {
      this.bookingForm.markAllAsTouched();
      this.toastr.error('Proszę wypełnić wszystkie wymagane pola.');
      return;
    }

    const formValue = this.bookingForm.value;
    console.log(formValue);
    const customerId = this.authService.getUserId();
    const hairDresserId = formValue.selectedSlot.hairdresserId;
    const bookingTime = formValue.selectedSlot.startTime; // HH:MM
    const finishedTime = formValue.selectedSlot.endTime;

    if (!this.selectedDate || !bookingTime) {
      this.toastr.error('Błąd daty lub godziny.');
      return;
    }

    // Tworzenie pełnej daty i czasu spotkania
    const startDateTimeISO = this.selectedDate.toISODate() + 'T' + bookingTime; // YYYY-MM-DDTHH:MM:00
    const finishedDateTimeISO =
      this.selectedDate.toISODate() + 'T' + finishedTime; // YYYY-MM-DDTHH:MM:00

    const newAppointment: CreateAppointmentForm = {
      customerUuid: customerId!, // Użyj UUID zalogowanego klienta
      hairdresserUuid: hairDresserId,
      hairoffers: formValue.selectedServices, // Wysyłamy tylko ID wybranych usług
      bookedDate: startDateTimeISO,
      finishedDate: finishedDateTimeISO,
    };

    try {
      // Wywołaj metodę w serwisie do zapisywania rezerwacji
      await this.appointmentsService.addAppointment(newAppointment); // Stwórz tę metodę w serwisie
      this.toastr.success('Spotkanie zarezerwowane pomyślnie!');
      this.bookingForm.reset(); // Resetuj formularz
      this.bookingCompleted.emit(); // Poinformuj komponent rodzica o zakończeniu rezerwacji
    } catch (error) {
      console.error('Błąd podczas rezerwacji spotkania:', error);
      this.toastr.error('Błąd podczas rezerwacji spotkania. Spróbuj ponownie.');
    }
  }

  getTotalDuration(): number {
    // Sprawdzamy, czy selectedHairOffers to tablica i czy ma elementy, aby uniknąć błędów
    const offers = this.selectedHairOffers();
    if (offers && offers.length > 0) {
      return offers.reduce((sum, offer) => sum + offer.duration, 0);
    }
    return 0; // Zwróć 0, jeśli nie ma wybranych ofert lub tablica jest pusta
  }

  async generateAvailableSlots(): Promise<void> {
    if (
      this.bookingForm.get('selectedServices')?.invalid ||
      !this.selectedDate
    ) {
      this.bookingForm.get('selectedServices')?.markAsTouched();
      // this.toastService.error('Proszę wybrać usługi i datę.');
      console.error('Validation failed: Services or date missing.');
      return;
    }

    const duration = this.selectedServicesTotalDuration();
    const date = this.selectedDate.toISODate()!; // Luxon date to YYYY-MM-DD

    console.log(
      `Generating slots for date: ${date}, duration: ${duration} minutes`
    );

    try {
      const slots = await this.appointmentsService.generateAvailableSlots(
        date,
        duration
      );
      console.log(slots);
      this.availableSlots.set(slots);
      this.bookingForm.get('selectedSlot')?.reset(); // Zresetuj wybrany slot, jeśli sloty się zmieniły
      if (slots.length === 0) {
        // this.toastService.info('Brak dostępnych terminów dla wybranych usług w tym dniu.');
        console.log('No available slots for selected criteria.');
      }
    } catch (error) {
      console.error('Error generating available slots:', error);
      // this.toastService.error('Błąd generowania dostępnych terminów.');
    }
  }

  onCancel(): void {
    this.bookingForm.reset();
    this.cancelBooking.emit();
  }
}
