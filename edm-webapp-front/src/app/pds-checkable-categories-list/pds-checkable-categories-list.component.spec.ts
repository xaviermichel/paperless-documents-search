import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { PdsCategoryService } from '../services/pds-category.service';
import { PdsCheckableCategoriesListComponent } from './pds-checkable-categories-list.component';
import { PdsCategoryAggregationsModel } from '../models/pds-category-aggregations.model';

describe('PdsCheckableCategoriesListComponent', () => {
  let component: PdsCheckableCategoriesListComponent;
  let fixture: ComponentFixture<PdsCheckableCategoriesListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PdsCheckableCategoriesListComponent ],
      schemas: [ NO_ERRORS_SCHEMA ],
      imports: [ HttpClientTestingModule ],
      providers: [
        PdsCategoryService
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PdsCheckableCategoriesListComponent);
    component = fixture.componentInstance;
    component.filterCategory = new PdsCategoryAggregationsModel();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
