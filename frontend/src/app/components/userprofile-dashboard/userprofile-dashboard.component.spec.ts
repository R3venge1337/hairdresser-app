import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserprofileDashboardComponent } from './userprofile-dashboard.component';

describe('UserprofileDashboardComponent', () => {
  let component: UserprofileDashboardComponent;
  let fixture: ComponentFixture<UserprofileDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserprofileDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserprofileDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
