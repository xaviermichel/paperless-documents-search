import { TestBed, inject } from '@angular/core/testing';

import { SdsDocumentService } from './sds-document.service';

describe('SdsDocumentService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SdsDocumentService]
    });
  });

  it('should be created', inject([SdsDocumentService], (service: SdsDocumentService) => {
    expect(service).toBeTruthy();
  }));
});
