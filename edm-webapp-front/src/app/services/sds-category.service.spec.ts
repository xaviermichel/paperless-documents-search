import { TestBed, inject } from '@angular/core/testing';

import { SdsCategoryService } from './sds-category.service';

describe('SdsCategoryService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SdsCategoryService]
    });
  });

  it('should be created', inject([SdsCategoryService], (service: SdsCategoryService) => {
    expect(service).toBeTruthy();
  }));
});
