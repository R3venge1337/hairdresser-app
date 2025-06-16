import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeAppointmentTermDialogComponent } from './change-appointment-term-dialog.component';

describe('ChangeAppointmentTermDialogComponent', () => {
  let component: ChangeAppointmentTermDialogComponent;
  let fixture: ComponentFixture<ChangeAppointmentTermDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangeAppointmentTermDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChangeAppointmentTermDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
