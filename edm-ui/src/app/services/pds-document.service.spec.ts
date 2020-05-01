import { TestBed, inject } from '@angular/core/testing';

import { PdsDocumentService } from './pds-document.service';

describe('PdsDocumentService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PdsDocumentService]
    });
  });

  it('should be created', inject([PdsDocumentService], (service: PdsDocumentService) => {
    expect(service).toBeTruthy();
  }));
});
