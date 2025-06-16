import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssignAppointmentComponent } from './assign-appointment.component';

describe('AssignAppointmentComponent', () => {
  let component: AssignAppointmentComponent;
  let fixture: ComponentFixture<AssignAppointmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AssignAppointmentComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AssignAppointmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
