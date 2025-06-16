import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HairdresserComponent } from './hairdresser.component';

describe('HairdresserComponent', () => {
  let component: HairdresserComponent;
  let fixture: ComponentFixture<HairdresserComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HairdresserComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HairdresserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
