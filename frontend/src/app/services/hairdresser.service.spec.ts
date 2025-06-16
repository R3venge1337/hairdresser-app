import { TestBed } from '@angular/core/testing';

import { HairdresserService } from './hairdresser.service';

describe('HairdresserService', () => {
  let service: HairdresserService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HairdresserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
