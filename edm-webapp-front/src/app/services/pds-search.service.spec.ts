import { TestBed, inject } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { PdsSearchService } from './pds-search.service';
import { PdsAggregationResultModel, PdsAggregationResultModelAdditionalFields } from '../models/pds-aggregation-item.model';
import { PdsCategoryModel } from '../models/pds-category.model';
import { PdsCategoryAggregationResultModel } from '../models/pds-category-aggregation-item.model';

import { environment } from '../../environments/environment';
import * as moment from 'moment';

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

  it('should call the right http url', inject([PdsSearchService], (service: PdsSearchService) => {
    service.searchForPattern('query').subscribe(results => {});

    http.expectOne(`${environment.sdsApiBaseUrl}/document?q=query`);
  }));

  it('should construct the right query for search', inject([PdsSearchService], (service: PdsSearchService) => {
    const queryString = service.constructFinalQuery('query');
    expect(queryString).toBe('query');
  }));

  it('should construct the right query for search with category filter', inject([PdsSearchService], (service: PdsSearchService) => {
    const categories: Array<PdsCategoryAggregationResultModel>  = new Array<PdsCategoryAggregationResultModel>();
    const cat1: PdsCategoryAggregationResultModel = new PdsCategoryAggregationResultModel();
    cat1.category = new PdsCategoryModel('CAT_1', 'Category 1');
    categories.push(cat1);
    const cat2: PdsCategoryAggregationResultModel = new PdsCategoryAggregationResultModel();
    cat2.category = new PdsCategoryModel('CAT_2', 'Category 2');
    categories.push(cat2);

    const queryString = service.constructFinalQuery('query', categories);

    expect(queryString).toBe('query AND (categoryId:CAT_1 OR categoryId:CAT_2)');
  }));

  it('should construct the right query for search with date filter', inject([PdsSearchService], (service: PdsSearchService) => {
    const categories: Array<PdsCategoryAggregationResultModel>  = new Array<PdsCategoryAggregationResultModel>();
    const selectedDateFilter: PdsAggregationResultModel = new PdsAggregationResultModel('FOREVER', 1);
    selectedDateFilter.pdsAggregationItem = new PdsAggregationResultModelAdditionalFields('pretty name', 1);

    const queryString = service.constructFinalQuery('query', categories, selectedDateFilter);

    const fromDate: string = moment().subtract(0, 'months').startOf('month').format('YYYY-MM-DD');
    const toDate: string = moment().endOf('month').format('YYYY-MM-DD');
    const dateFilter: string = '(fileDate:[' + fromDate + ' TO ' + toDate + '])';
    expect(queryString).toBe('query AND ' + dateFilter);
  }));

  it('should construct the right query for search with file extension filter', inject([PdsSearchService], (service: PdsSearchService) => {
    const extensions: Array<PdsAggregationResultModel>  = new Array<PdsAggregationResultModel>();
    extensions.push(new PdsAggregationResultModel('txt'));
    extensions.push(new PdsAggregationResultModel('pdf'));

    const queryString = service.constructFinalQuery('query', new Array<PdsCategoryAggregationResultModel>(), null, extensions);

    expect(queryString).toBe('query AND (fileExtension:txt OR fileExtension:pdf)');
  }));

  it('should construct the right query for search with category and date filter and extensions', inject([PdsSearchService], (service: PdsSearchService) => {
    const categories: Array<PdsCategoryAggregationResultModel>  = new Array<PdsCategoryAggregationResultModel>();
    const cat1: PdsCategoryAggregationResultModel = new PdsCategoryAggregationResultModel();
    cat1.category = new PdsCategoryModel('CAT_1', 'Category 1');
    categories.push(cat1);
    const cat2: PdsCategoryAggregationResultModel = new PdsCategoryAggregationResultModel();
    cat2.category = new PdsCategoryModel('CAT_2', 'Category 2');
    categories.push(cat2);

    const selectedDateFilter: PdsAggregationResultModel = new PdsAggregationResultModel('FOREVER', 1);
    selectedDateFilter.pdsAggregationItem = new PdsAggregationResultModelAdditionalFields('pretty name', 1);

    const extensions: Array<PdsAggregationResultModel>  = new Array<PdsAggregationResultModel>();
    extensions.push(new PdsAggregationResultModel('txt'));
    extensions.push(new PdsAggregationResultModel('pdf'));

    const queryString = service.constructFinalQuery('query', categories, selectedDateFilter, extensions);

    const fromDate: string = moment().subtract(0, 'months').startOf('month').format('YYYY-MM-DD');
    const toDate: string = moment().endOf('month').format('YYYY-MM-DD');
    const dateFilter: string = '(fileDate:[' + fromDate + ' TO ' + toDate + '])';
    expect(queryString).toBe('query AND (categoryId:CAT_1 OR categoryId:CAT_2) AND ' + dateFilter + ' AND (fileExtension:txt OR fileExtension:pdf)');
  }));

});
