import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { PdsCategoryService } from '../services/pds-category.service';
import { PdsSearchService } from '../services/pds-search.service';
import { PdsCloudTagsComponent } from './pds-cloud-tags.component';

describe('PdsCloudTagsComponent', () => {
  let component: PdsCloudTagsComponent;
  let fixture: ComponentFixture<PdsCloudTagsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PdsCloudTagsComponent ],
      schemas: [ NO_ERRORS_SCHEMA ],
      imports: [ HttpClientTestingModule ],
      providers: [
        PdsCategoryService,
        PdsSearchService
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PdsCloudTagsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
