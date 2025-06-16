import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HairofferChartComponent } from './hairoffer-chart.component';

describe('HairofferChartComponent', () => {
  let component: HairofferChartComponent;
  let fixture: ComponentFixture<HairofferChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HairofferChartComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HairofferChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
