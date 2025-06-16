import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr'; // Ensure this is correctly provided in your app module
import { Router } from '@angular/router'; // Import Router for navigation
import { signal, WritableSignal } from '@angular/core'; // For Angular Signals

// Angular Material Imports
import { MatTableDataSource, MatTableModule } from '@angular/material/table'; // Table
import {
  MatPaginator,
  MatPaginatorModule,
  PageEvent,
} from '@angular/material/paginator'; // Paginator and PageEvent
import { MatSort, MatSortModule, Sort } from '@angular/material/sort'; // Sort and Sort event
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner'; // Loading spinner
import { MatIconModule } from '@angular/material/icon'; // Icons
import { MatButtonModule } from '@angular/material/button'; // Buttons
import { MatCardModule } from '@angular/material/card'; // Card for aesthetics

// Custom service and model imports
import { UserService } from '../../services/user.service';
import { PageableRequest, PageDto } from '../../models/pageable-request';
import { UserProfileDetailsDto } from '../../models/user-profile-details-dto';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [
    CommonModule,
    TranslateModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatProgressSpinnerModule,
    MatIconModule,
    MatButtonModule,
    MatCardModule,
    // ToastrModule is typically imported in your main app module (e.g., AppModule),
    // not directly in standalone components, as it provides a service.
  ],
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css'],
})
export class UserListComponent implements OnInit, AfterViewInit {
  // Data source for MatTable. We initialize it with an empty array.
  dataSource = new MatTableDataSource<UserProfileDetailsDto>([]);
  // Columns to be displayed in the table. These must match matColumnDef in HTML.
  displayedColumns: string[] = [
    'id',
    'firstName',
    'surname',
    'email',
    'name',
    'isActive',
    'actions',
  ];

  isLoading: boolean = true;
  error: string | null = null;

  // Signals for pagination and sorting state
  totalElements: WritableSignal<number> = signal(0);
  pageSizeOptions: number[] = [5, 10, 25, 50];
  pageSize: WritableSignal<number> = signal(5);
  currentPage: WritableSignal<number> = signal(0); // Backend pages are 0-indexed
  currentSortActive: WritableSignal<string> = signal('id'); // Default sort field
  currentSortDirection: WritableSignal<'asc' | 'desc'> = signal('asc'); // Default sort direction

  // ViewChild decorators to get references to MatPaginator and MatSort components from the view
  // 'static: true' can be used if you need access in ngOnInit, but for MatTable, AfterViewInit is typically fine.
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private userService: UserService, // Assuming UserApiService is the correct service
    private toastService: ToastrService,
    private translate: TranslateService,
    private router: Router,
    public authService: AuthService
  ) {}

  // Lifecycle hook: Called once, after the component's properties are initialized.
  ngOnInit(): void {
    // Initial data fetch based on default pagination/sort values
    this.loadAllUserProfiles();
  }

  // Lifecycle hook: Called once after the component's view has been fully initialized.
  ngAfterViewInit(): void {
    // Subscribe to paginator and sort events to trigger data reload
    // This is crucial for MatPaginator and MatSort to control the data.
    this.paginator.page.subscribe((event: PageEvent) =>
      this.onPageChange(event)
    );
    this.sort.sortChange.subscribe((event: Sort) => this.onSortChange(event));

    // Initial setup if data loads before paginator/sort are available (less common with async data)
    // The loadAllUserProfiles handles data assignment.
  }

  /**
   * Handles page change events from MatPaginator.
   * Updates currentPage and pageSize signals, then reloads data.
   * @param event The PageEvent containing new page index and page size.
   */
  onPageChange(event: PageEvent): void {
    this.currentPage.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
    this.loadAllUserProfiles(); // Reload data with new pagination parameters
  }

  /**
   * Handles sort change events from MatSort.
   * Updates currentSortActive and currentSortDirection signals, then reloads data.
   * @param event The Sort event containing the active sort column and direction.
   */
  onSortChange(event: Sort): void {
    this.currentSortActive.set(event.active);
    this.currentSortDirection.set(event.direction as 'asc' | 'desc');
    // Reset to first page when sorting changes
    this.currentPage.set(0);
    if (this.paginator) {
      this.paginator.pageIndex = 0;
    }
    this.loadAllUserProfiles(); // Reload data with new sort parameters
  }

  /**
   * Loads all user profiles from the UserService, applying current pagination and sort parameters.
   * Updates loading state, handles success, and error cases.
   */
  async loadAllUserProfiles(): Promise<void> {
    this.isLoading = true;
    this.error = null;

    // Create the pageable request object based on current component state
    const pageableRequest: PageableRequest = {
      page: this.currentPage() + 1,
      size: this.pageSize(),
      // Ensure sort field matches backend property names
      sortField: this.currentSortActive(),
      sortDirection: this.currentSortDirection().toUpperCase(), // Backend expects uppercase
    };

    try {
      // Assuming getAllUserProfiles now accepts PageableRequest and returns PageResponse<UserProfileDetailsDto>
      const response: PageDto<UserProfileDetailsDto> =
        await this.userService.getAllUserProfiles(pageableRequest);

      this.dataSource.data = response.content; // Assign the content array to the dataSource
      this.totalElements.set(response.totalElements); // Update total elements for paginator

      this.isLoading = false;
      this.toastService.success(
        this.translate.instant('userList.toastMessages.loadSuccess')
      );
    } catch (err) {
      console.error('Failed to fetch all user profiles with pagination:', err);
      this.error = this.translate.instant('userList.errors.fetchFailed');
      this.isLoading = false;
      this.toastService.error(
        this.translate.instant('userList.toastMessages.loadError')
      );
    }
  }

  /**
   * Navigates to the user profile details view for a specific user.
   * @param uuid The UUID of the user to view.
   */
  viewUserProfileDetails(uuid: string): void {
    this.router.navigate(['/profile', uuid]); // Assumes the route is /profile/:uuid
  }
}
