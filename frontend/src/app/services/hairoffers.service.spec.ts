import { TestBed } from '@angular/core/testing';

import { HairoffersService } from './hairoffers.service';

describe('HairoffersService', () => {
  let service: HairoffersService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HairoffersService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
