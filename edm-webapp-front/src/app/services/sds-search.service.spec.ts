import { TestBed, inject } from '@angular/core/testing';

import { SdsSearchService } from './sds-search.service';

describe('SdsSearchService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SdsSearchService]
    });
  });

  it('should be created', inject([SdsSearchService], (service: SdsSearchService) => {
    expect(service).toBeTruthy();
  }));
});
