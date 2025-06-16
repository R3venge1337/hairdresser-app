import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HairdresserAppointmentDashboardComponent } from './hairdresser-appointment-dashboard.component';

describe('HairdresserAppointmentDashboardComponent', () => {
  let component: HairdresserAppointmentDashboardComponent;
  let fixture: ComponentFixture<HairdresserAppointmentDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HairdresserAppointmentDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HairdresserAppointmentDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
