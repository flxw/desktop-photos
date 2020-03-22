import { TestBed } from '@angular/core/testing';

import { TimePortService } from './time-port.service';

describe('TimePortService', () => {
  let service: TimePortService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TimePortService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
