import { TestBed, inject } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { PdsCategoryService } from './pds-category.service';

describe('PdsCategoryService', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PdsCategoryService],
      imports: [ HttpClientTestingModule ]
    });
  });

  it('should be created', inject([PdsCategoryService], (service: PdsCategoryService) => {
    expect(service).toBeTruthy();
  }));

});
