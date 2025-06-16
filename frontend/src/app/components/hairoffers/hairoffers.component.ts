import {
  AfterViewInit,
  Component,
  OnInit,
  signal,
  ViewChild,
} from '@angular/core';
import { Hairoffers, HairOfferFilterForm } from '../../models/hairoffers';
import Big from 'big.js';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { HairoffersService } from '../../services/hairoffers.service';
import { MatIconModule } from '@angular/material/icon';
import { RouterModule } from '@angular/router';
import {
  MatPaginator,
  MatPaginatorModule,
  PageEvent,
} from '@angular/material/paginator';
import { MatSort, MatSortModule, Sort } from '@angular/material/sort';
import { TranslateModule } from '@ngx-translate/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-hairoffers',
  imports: [
    CommonModule,
    FormsModule,
    MatInputModule,
    MatButtonModule,
    MatTableModule,
    MatFormFieldModule,
    MatIconModule,
    RouterModule,
    MatPaginatorModule,
    MatSortModule,
    TranslateModule,
  ],
  templateUrl: './hairoffers.component.html',
  styleUrl: './hairoffers.component.css',
})
export class HairoffersComponent implements OnInit, AfterViewInit {
  hairOffers = signal<Hairoffers[]>([]);
  dataSource = new MatTableDataSource<Hairoffers>([]);
  totalElements = 0;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  filterForm: HairOfferFilterForm = {
    name: '',
    priceLow: null,
    priceHigh: null,
    durationLow: null,
    durationHigh: null,
  };

  pageableRequest = {
    page: 1,
    size: 10,
    sortField: 'name', // Domyślne pole do sortowania
    sortDirection: 'ASC', // Domyślny kierunek sortowania
  };

  showAddOfferForm = false;
  newOffer: Hairoffers = {
    id: 0,
    name: '',
    description: '',
    price: Big(0),
    duration: 0,
  };

  constructor(
    private hairOfferService: HairoffersService,
    public authService: AuthService
  ) {}

  async ngOnInit(): Promise<void> {
    await this.loadHairOffers();
  }

  ngAfterViewInit(): void {
    // Po zainicjowaniu widoku, przypisz paginator i sortowanie do dataSource
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort; // Przypisz MatSort do dataSource

    // Subskrybuj zmiany strony
    this.paginator.page.subscribe((event: PageEvent) => {
      this.pageableRequest.page = event.pageIndex;
      this.pageableRequest.size = event.pageSize;
      this.loadHairOffers();
    });

    // Subskrybuj zmiany sortowania
    this.sort.sortChange.subscribe((sort: Sort) => {
      this.pageableRequest.sortField = sort.active; // Pole, po którym sortujemy
      this.pageableRequest.sortDirection = sort.direction.toUpperCase(); // Kierunek sortowania (ASC/DESC)
      this.pageableRequest.page = 1; // Po zmianie sortowania wracamy na pierwszą stronę
      this.loadHairOffers();
    });
  }

  async loadHairOffers(): Promise<void> {
    try {
      const response = await this.hairOfferService.getAllHairOffers(
        this.filterForm,
        this.pageableRequest
      );
      this.hairOffers.set(response.content);
      this.dataSource.data = response.content;
      this.totalElements = response.totalElements;
      this.paginator.length = response.totalElements;
    } catch (error) {
      console.error('Error loading hair offers:', error);
    }
  }

  async applyFilters() {
    this.pageableRequest.page = 1;
    await this.loadHairOffers();
  }

  // Ta metoda będzie wywoływana przez (matSortChange) w HTML
  onSortChange(sort: Sort): void {
    // Logika sortowania jest już obsługiwana w subskrypcji this.sort.sortChange
    // Możesz tutaj dodać dodatkową logikę, jeśli potrzebujesz
    console.log('Sortowanie zmienione:', sort);
  }

  async addHairoffer() {
    try {
      await this.hairOfferService.addHairoffer(this.newOffer);
      this.showAddOfferForm = false;
      this.newOffer = {
        id: 0,
        name: '',
        description: '',
        price: Big(0),
        duration: 0,
      };
      await this.loadHairOffers();
    } catch (error) {
      console.error('Error adding hair offer:', error);
    }
  }
}
