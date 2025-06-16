import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { LogoutComponent } from './components/logout/logout.component';
import { HomeComponent } from './components/home/home.component';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { authGuard } from './guards/auth.guard';
import { CalendarComponent } from './components/calendar/calendar.component';
import { HairoffersComponent } from './components/hairoffers/hairoffers.component';
import { EditHairoffersComponent } from './components/edit-hairoffers/edit-hairoffers.component';
import { HairdresserComponent } from './components/hairdresser/hairdresser.component';
import { MyAppointmentComponent } from './components/my-appointment/my-appointment.component';
import { AllAppointmentsComponent } from './components/all-appointments/all-appointments.component';
import { StatisticDashboardComponent } from './components/statistic-dashboard/statistic-dashboard.component';
import { UserprofileDashboardComponent } from './components/userprofile-dashboard/userprofile-dashboard.component';
import { UserListComponent } from './components/user-list/user-list.component';
import { AccessDeniedComponent } from './components/access-denied/access-denied.component';
import { HairdresserAppointmentDashboardComponent } from './components/hairdresser-appointment-dashboard/hairdresser-appointment-dashboard.component';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'access-denied', component: AccessDeniedComponent },
  {
    path: 'all-appointments',
    component: AllAppointmentsComponent,
    canActivate: [authGuard],
    data: { requiredRoles: ['ADMIN'] },
  },
  {
    path: 'appointments',
    component: CalendarComponent,
    canActivate: [authGuard],
    data: { requiredRoles: ['HAIRDRESSER', 'ADMIN', 'CUSTOMER'] },
  },
  {
    path: 'my-client-appointments',
    component: MyAppointmentComponent,
    canActivate: [authGuard],
    data: { requiredRoles: ['ADMIN', 'CUSTOMER'] },
  },
  {
    path: 'my-hairdresser-appointments',
    component: HairdresserAppointmentDashboardComponent,
    canActivate: [authGuard],
    data: { requiredRoles: ['HAIRDRESSER', 'ADMIN'] },
  },
  {
    path: 'hairoffers',
    component: HairoffersComponent,
    canActivate: [authGuard],
    data: { requiredRoles: ['HAIRDRESSER', 'ADMIN', 'CUSTOMER'] },
  },
  {
    path: 'stats',
    component: StatisticDashboardComponent,
    canActivate: [authGuard],
    data: { requiredRoles: ['ADMIN'] },
  },
  {
    path: 'edit-hairoffer/:id',
    component: EditHairoffersComponent,
    canActivate: [authGuard],
    data: { requiredRoles: ['HAIRDRESSER', 'ADMIN'] },
  },
  {
    path: 'hairdressers',
    component: HairdresserComponent,
    canActivate: [authGuard],
    data: { requiredRoles: ['HAIRDRESSER', 'ADMIN', 'CUSTOMER'] },
  },
  {
    path: 'users',
    component: UserListComponent,
    canActivate: [authGuard],
    data: { requiredRoles: ['ADMIN'] },
  },
  {
    path: 'profile',
    component: UserprofileDashboardComponent,
    canActivate: [authGuard],
    data: { requiredRoles: ['HAIRDRESSER', 'ADMIN', 'CUSTOMER'] },
  },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'logout', component: LogoutComponent },
  { path: '**', component: PageNotFoundComponent },
];
