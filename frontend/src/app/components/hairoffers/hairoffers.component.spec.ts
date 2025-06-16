import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HairoffersComponent } from './hairoffers.component';

describe('HairoffersComponent', () => {
  let component: HairoffersComponent;
  let fixture: ComponentFixture<HairoffersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HairoffersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HairoffersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
