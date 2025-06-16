import { CommonModule } from '@angular/common';
import {
  AfterViewInit,
  Component,
  OnInit,
  signal,
  ViewChild,
  WritableSignal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import {
  MatPaginator,
  MatPaginatorModule,
  PageEvent,
} from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatSort, MatSortModule, Sort } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { Router } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { Appointment, AppointmentFilterForm } from '../../models/appointment';
import { HairdresserDto } from '../../models/hairdresser-dto';
import { PageableRequest, PageDto } from '../../models/pageable-request';
import { AppointmentsService } from '../../services/appointments.service';
import { AuthService } from '../../services/auth.service';
import { HairdresserService } from '../../services/hairdresser.service';
import { ChangeAppointmentTermDialogComponent } from '../change-appointment-term-dialog/change-appointment-term-dialog.component';
import { ToastrModule, ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-hairdresser-appointment-dashboard',
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    MatCardModule,
    MatDatepickerModule,
    MatNativeDateModule,
    TranslateModule,
    ToastrModule,
  ],
  templateUrl: './hairdresser-appointment-dashboard.component.html',
  styleUrl: './hairdresser-appointment-dashboard.component.css',
})
export class HairdresserAppointmentDashboardComponent
  implements OnInit, AfterViewInit
{
  // Dane tabeli
  dataSource = new MatTableDataSource<Appointment>([]);
  displayedColumns: string[] = [
    'id',
    'hairdresser',
    'customer',
    'bookedDate',
    'totalCost',
    'status',
    'services',
    'actions', // Jeśli dodasz przyciski akcji (np. anuluj)
  ];

  // Paginacja
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  totalElements: WritableSignal<number> = signal(0);
  pageSizeOptions: number[] = [5, 10, 25, 50];
  pageSize: WritableSignal<number> = signal(5);
  currentPage: WritableSignal<number> = signal(0);

  // Sortowanie
  @ViewChild(MatSort) sort!: MatSort;
  currentSortActive: WritableSignal<string> = signal('');
  currentSortDirection: WritableSignal<'asc' | 'desc'> = signal('desc');

  // Filtrowanie
  filterForm: WritableSignal<AppointmentFilterForm> = signal({
    status: null,
    bookedDateStart: null,
    bookedDateEnd: null,
    hairdresserName: null,
    hairdresserSurname: null,
  });

  // Dostępne opcje dla statusu (zgodne z Twoim enum AppointmentStatus)
  availableStatuses: string[] = [
    'CREATED',
    'ACCEPTED',
    'CANCELED',
    'RESCHEDULED',
    'COMPLETED',
    'PAID',
  ];
  // Opcjonalnie: Lista fryzjerów do wyświetlenia w filtrze (jeśli chcesz dropdown)
  allHairdressers: WritableSignal<HairdresserDto[]> = signal([]);

  constructor(
    private appointmentsService: AppointmentsService,
    private hairdresserService: HairdresserService,
    private router: Router,
    public authService: AuthService,
    private dialog: MatDialog,
    private toastService: ToastrService
  ) {}

  ngOnInit(): void {
    // Ładowanie fryzjerów może być w OnInit, jeśli nie wpływa na renderowanie paginatora/tabeli
    this.loadAllHairdressers();
    // Usunięto this.loadAppointments(); z ngOnInit, będzie wywołane w ngAfterViewInit
  }

  ngAfterViewInit(): void {
    // WAŻNA ZMIANA: Usuwamy przypisanie paginatora i sortowania do dataSource,
    // ponieważ robimy paginację i sortowanie po stronie serwera.
    // this.dataSource.paginator = this.paginator; // USUŃ
    // this.dataSource.sort = this.sort; // USUŃ

    // Subskrybuj zmiany sortowania
    this.sort.sortChange.subscribe((sortState: Sort) => {
      this.currentPage.set(0); // Resetuj stronę przy zmianie sortowania
      this.currentSortActive.set(sortState.active);
      // Poprawione mapowanie na 'ASC' / 'DESC'
      if (sortState.direction === 'asc') {
        this.currentSortDirection.set('asc');
      } else if (sortState.direction === 'desc') {
        this.currentSortDirection.set('desc');
      } else {
        // Jeśli sortState.direction jest pusty (brak sortowania), ustaw domyślny kierunek
        this.currentSortDirection.set('desc'); // Domyślny kierunek, gdy brak sortowania
      }
      this.loadAppointments();
    });

    // Subskrybuj zmiany paginacji
    // To zdarzenie jest wywoływane, gdy użytkownik zmienia stronę lub rozmiar strony
    this.paginator.page.subscribe((event: PageEvent) => {
      this.currentPage.set(event.pageIndex);
      this.pageSize.set(event.pageSize);
      this.loadAppointments(); // Załaduj dane dla nowej strony
    });

    // Początkowe ładowanie danych po zainicjalizowaniu paginatora i sortowania
    this.loadAppointments();
  }

  async loadAppointments(): Promise<void> {
    try {
      // Przygotowanie PageableRequest
      const pageableRequest: PageableRequest = {
        page: this.currentPage() + 1,
        size: this.pageSize(),
        sortField: this.currentSortActive(),
        sortDirection: this.currentSortDirection().toUpperCase(),
      };

      // Przygotowanie AppointmentFilterForm
      const currentFilterForm = this.filterForm();
      const filterFormToSend: AppointmentFilterForm = {
        status: currentFilterForm.status || null,
        bookedDateStart: currentFilterForm.bookedDateStart || null,
        bookedDateEnd: currentFilterForm.bookedDateEnd || null,
        hairdresserName: currentFilterForm.hairdresserName || null,
        hairdresserSurname: currentFilterForm.hairdresserSurname || null,
      };

      const response: PageDto<Appointment> =
        await this.appointmentsService.getAllHairdresserAppointments(
          filterFormToSend,
          pageableRequest
        );

      this.dataSource.data = response.content;
      // Upewnij się, że totalElements jest poprawnie ustawione
      this.totalElements.set(response.totalElements);

      console.log('API Response (content):', response.content);
      console.log('API Response (totalElements):', response.totalElements);
      console.log('Appointments loaded. Total elements:', this.totalElements());
    } catch (error) {
      console.error('Error loading appointments history:', error);
      this.dataSource.data = [];
      this.totalElements.set(0); // Ustaw 0 w przypadku błędu
    }
  }

  async loadAllHairdressers(): Promise<void> {
    try {
      const hairdressers: HairdresserDto[] =
        await this.hairdresserService.getAllUsersByRole('HAIRDRESSER');
      this.allHairdressers.set(hairdressers);
    } catch (error) {
      console.error('Error loading all hairdressers for filter:', error);
    }
  }

  // Metoda pomocnicza do aktualizacji pól formularza filtra
  updateFilterField<K extends keyof AppointmentFilterForm>(
    field: K,
    value: AppointmentFilterForm[K]
  ): void {
    this.filterForm.update((form) => ({ ...form, [field]: value }));
    this.applyFilters();
  }

  applyFilters(): void {
    this.currentPage.set(0); // Zawsze resetuj stronę przy zastosowaniu filtrów
    this.loadAppointments();
  }

  clearFilters(): void {
    this.filterForm.set({
      status: null,
      bookedDateStart: null,
      bookedDateEnd: null,
      hairdresserName: null,
      hairdresserSurname: null,
    });
    this.currentPage.set(0); // Zawsze resetuj stronę przy czyszczeniu filtrów
    this.loadAppointments();
  }

  getServicesString(appointment: Appointment): string {
    return appointment.procedures.map((offer) => offer.name).join(', ');
  }

  // Ta metoda nie jest używana w HTML, ale zostawiam ją, jeśli była gdzieś indziej
  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleString();
  }

  onPageChange(event: PageEvent): void {
    this.currentPage.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.loadAppointments();
  }

  async makePayment(uuid: string) {
    try {
      await this.appointmentsService.changeAppointmentStatus(uuid, {
        statusValue: 'PAID',
      });
    } catch (error) {
      console.error('Not possible to pay for this appointment', error);
    }
  }
  changeAppointmentDate(appointment: Appointment): void {
    this.dialog
      .open(ChangeAppointmentTermDialogComponent, {
        width: '600px', // Szerokość dialogu
        data: appointment, // Przekaż pełen obiekt spotkania do dialogu
      })
      .afterClosed()
      .subscribe((result) => {
        // Wynik z dialogu (np. true jeśli operacja się powiodła i należy odświeżyć)
        if (result) {
          this.loadAppointments(); // Odśwież listę spotkań po zamknięciu dialogu
        }
      });
  }

  async acceptAppointment(uuid: string) {
    try {
      await this.appointmentsService.changeAppointmentStatus(uuid, {
        statusValue: 'ACCEPTED',
      });
    } catch (error) {
      console.error(
        'Not possible to change appointment status for accepted',
        error
      );
    }
  }
  async cancelAppointment(uuid: string) {
    try {
      await this.appointmentsService.changeAppointmentStatus(uuid, {
        statusValue: 'CANCELED',
      });
    } catch (error) {
      console.error(
        'Not possible to change appointment status for canceled',
        error
      );
    }
  }
}
