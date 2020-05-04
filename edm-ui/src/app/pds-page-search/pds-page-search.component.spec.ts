import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { PdsSearchService } from '../services/pds-search.service';
import { PdsDocumentService } from '../services/pds-document.service';
import { PdsCategoryService } from '../services/pds-category.service';
import { PdsPageSearchComponent } from './pds-page-search.component';

import { environment } from '../../environments/environment';

describe('PdsPageSearchComponent', () => {
  let component: PdsPageSearchComponent;
  let fixture: ComponentFixture<PdsPageSearchComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PdsPageSearchComponent ],
      schemas: [ NO_ERRORS_SCHEMA ],
      imports: [ HttpClientTestingModule, FormsModule, MatAutocompleteModule ],
      providers: [
        PdsSearchService, PdsDocumentService, PdsCategoryService
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PdsPageSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
