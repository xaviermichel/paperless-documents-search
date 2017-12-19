import { TestBed, inject } from '@angular/core/testing';

import { PdsSearchService } from './pds-search.service';

describe('PdsSearchService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PdsSearchService]
    });
  });

  it('should be created', inject([PdsSearchService], (service: PdsSearchService) => {
    expect(service).toBeTruthy();
  }));
});
