import { TestBed, inject } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { PdsSearchService } from './pds-search.service';

import { environment } from '../../environments/environment';

describe('PdsSearchService', () => {

  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PdsSearchService],
      imports: [ HttpClientTestingModule ]
    });

    http = TestBed.get(HttpTestingController);
  });

  it('should be created', inject([PdsSearchService], (service: PdsSearchService) => {
    expect(service).toBeTruthy();
  }));

  it('should call the right http url for simple query', inject([PdsSearchService], (service: PdsSearchService) => {
    service.searchForPattern("query").subscribe(results => {});

    http.expectOne(`${environment.sdsApiBaseUrl}/document?q=query`);
  }));

});
