import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditHairoffersComponent } from './edit-hairoffers.component';

describe('EditHairoffersComponent', () => {
  let component: EditHairoffersComponent;
  let fixture: ComponentFixture<EditHairoffersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditHairoffersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditHairoffersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
