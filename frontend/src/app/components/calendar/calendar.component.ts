import { CommonModule, CurrencyPipe } from '@angular/common';
import {
  Component,
  computed,
  effect,
  OnInit,
  Signal,
  signal,
  WritableSignal,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { DateTime, Info, Interval } from 'luxon';
// Import serwisu
import { AppointmentsService } from '../../services/appointments.service';
import { Appointment } from '../../models/appointment';
import { Hairoffers } from '../../models/hairoffers';
import { AssignAppointmentComponent } from '../assign-appointment/assign-appointment.component';
import { AuthService } from '../../services/auth.service';
import { TranslateModule } from '@ngx-translate/core';
// Import nowego komponentu formularza rezerwacji

// Interfejs pomocniczy do reprezentacji dnia kalendarza
interface CalendarDay {
  luxonDate: DateTime;
  day: number;
  month: number;
  year: number;
  monthShort: string;
  toISODate(): string;
}

@Component({
  selector: 'calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css'],
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    CurrencyPipe,
    AssignAppointmentComponent,
    TranslateModule,
  ],
  standalone: true,
})
export class CalendarComponent implements OnInit {
  today: Signal<DateTime> = signal(DateTime.local());
  firstDayOfActiveMonthLuxon: WritableSignal<DateTime> = signal(
    this.today().startOf('month')
  );
  activeDayLuxon: WritableSignal<DateTime | null> = signal(null);

  firstDayOfActiveMonth: Signal<CalendarDay> = computed(() =>
    this.createCalendarDay(this.firstDayOfActiveMonthLuxon())
  );
  activeDay: Signal<CalendarDay | null> = computed(() => {
    const activeLuxon = this.activeDayLuxon();
    return activeLuxon ? this.createCalendarDay(activeLuxon) : null;
  });

  weekDays: Signal<string[]> = signal(Info.weekdays('short'));
  daysOfMonth: Signal<CalendarDay[]> = computed(() => {
    return Interval.fromDateTimes(
      this.firstDayOfActiveMonthLuxon().startOf('week'),
      this.firstDayOfActiveMonthLuxon().endOf('month').endOf('week')
    )
      .splitBy({ day: 1 })
      .map((d) => {
        if (d.start === null) {
          throw new Error('Wrong dates');
        }
        return this.createCalendarDay(d.start);
      });
  });

  dailyAppointments: Appointment[] = [];
  appointmentsDataSource = new MatTableDataSource<Appointment>([]);
  displayedAppointmentsColumns: string[] = [
    'time',
    'customerName',
    'hairDresserName',
    'serviceName',
    'totalCost',
    'status',
  ];

  // NOWY SYGNAŁ DO KONTROLOWANIA WIDOCZNOŚCI FORMULARZA
  showBookingForm: WritableSignal<boolean> = signal(false); // Domyślnie ukryty

  constructor(
    private appointmentsService: AppointmentsService,
    public authService: AuthService
  ) {
    effect(() => {
      const currentActiveDay = this.activeDayLuxon();
      if (currentActiveDay) {
        this.loadAppointmentsForSelectedDay(currentActiveDay);
        // Po zmianie dnia, ukryj formularz rezerwacji
        this.showBookingForm.set(false);
      } else {
        this.dailyAppointments = [];
        this.appointmentsDataSource.data = [];
      }
    });
  }

  ngOnInit(): void {
    this.goToToday();
  }

  private createCalendarDay(luxonDate: DateTime): CalendarDay {
    return {
      luxonDate: luxonDate,
      day: luxonDate.day,
      month: luxonDate.month - 1,
      year: luxonDate.year,
      monthShort: luxonDate.toFormat('MMM'),
      toISODate: () => luxonDate.toISODate() ?? '',
    };
  }

  goToPreviousMonth(): void {
    this.firstDayOfActiveMonthLuxon.set(
      this.firstDayOfActiveMonthLuxon().minus({ month: 1 })
    );
  }

  goToNextMonth(): void {
    this.firstDayOfActiveMonthLuxon.set(
      this.firstDayOfActiveMonthLuxon().plus({ month: 1 })
    );
  }

  goToToday(): void {
    this.firstDayOfActiveMonthLuxon.set(this.today().startOf('month'));
    this.activeDayLuxon.set(this.today());
  }

  private async loadAppointmentsForSelectedDay(
    selectedLuxonDate: DateTime
  ): Promise<void> {
    const dateToSend = selectedLuxonDate.toISODate();

    const dateFormatted = dateToSend!.split('T')[0];
    console.log(`Ładowanie spotkań dla daty: ${dateFormatted}`);

    try {
      const appointments =
        await this.appointmentsService.getAllAppointmentsByDate(
          new Date(dateFormatted)
        );
      this.dailyAppointments = appointments.sort(
        (a: Appointment, b: Appointment) =>
          this.getAppointmentTime(a.bookedAppointment).localeCompare(
            this.getAppointmentTime(b.bookedAppointment)
          )
      );
      this.appointmentsDataSource.data = this.dailyAppointments;
      console.log('Pobrane spotkania:', this.dailyAppointments);
    } catch (error) {
      console.error('Błąd podczas pobierania spotkań:', error);
      this.dailyAppointments = [];
      this.appointmentsDataSource.data = [];
    }
  }

  getAppointmentTime(bookedAppointment: string): string {
    const luxonTime = DateTime.fromISO(bookedAppointment);
    return luxonTime.toFormat('HH:mm');
  }

  getProcedureNames(procedures: Hairoffers[]): string {
    return procedures.map((p) => p.name).join(', ');
  }

  get todayCalendarDay(): CalendarDay {
    return this.createCalendarDay(this.today());
  }

  toggleBookingForm(): void {
    this.showBookingForm.update((value) => !value);
  }

  handleBookingCompleted(): void {
    this.showBookingForm.set(false);
    if (this.activeDayLuxon()) {
      this.loadAppointmentsForSelectedDay(this.activeDayLuxon()!);
    }
  }

  cancelBookingForm(): void {
    this.showBookingForm.set(false);
  }
}
