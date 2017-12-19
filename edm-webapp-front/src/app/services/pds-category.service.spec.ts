import { TestBed, inject } from '@angular/core/testing';

import { PdsCategoryService } from './pds-category.service';

describe('PdsCategoryService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PdsCategoryService]
    });
  });

  it('should be created', inject([PdsCategoryService], (service: PdsCategoryService) => {
    expect(service).toBeTruthy();
  }));
});
