import { TestBed, inject } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { PdsSearchService } from './pds-search.service';
import { PdsAggregationResultModel, PdsAggregationResultModelAdditionalFields } from '../models/pds-aggregation-item.model';
import { PdsCategoryModel } from '../models/pds-category.model';

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
    service.searchForPattern("query").subscribe(results => {});

    http.expectOne(`${environment.sdsApiBaseUrl}/document?q=query`);
  }));

  it('should construct the right query for search', inject([PdsSearchService], (service: PdsSearchService) => {
    let queryString = service.constructFinalQuery("query");
    expect(queryString).toBe("query");
  }));

  it('should construct the right query for search with category filter', inject([PdsSearchService], (service: PdsSearchService) => {
    let categories: Array<PdsCategoryModel>  = new Array<PdsCategoryModel>();
    categories.push(new PdsCategoryModel("CAT_1", "Category 1"));
    categories.push(new PdsCategoryModel("CAT_2", "Category 2"));

    let queryString = service.constructFinalQuery("query", categories);

    expect(queryString).toBe("query AND (categoryId:CAT_1 OR categoryId:CAT_2)");
  }));

  it('should construct the right query for search with date filter', inject([PdsSearchService], (service: PdsSearchService) => {
    let categories: Array<PdsCategoryModel>  = new Array<PdsCategoryModel>();
    let selectedDateFilter: PdsAggregationResultModel = new PdsAggregationResultModel("FOREVER", 1);
    selectedDateFilter.pdsAggregationItem = new PdsAggregationResultModelAdditionalFields("pretty name", 1);

    let queryString = service.constructFinalQuery("query", categories, selectedDateFilter);

    let fromDate: string = moment().subtract(0, 'months').startOf("month").format('YYYY-MM-DD');
    let toDate: string = moment().endOf("month").format('YYYY-MM-DD'); // can be removed for '*' ?
    let dateFilter:string =  "(fileDate:[" + fromDate + " TO " + toDate + "])";
    expect(queryString).toBe("query AND " + dateFilter);
  }));

  it('should construct the right query for search with category and date filter', inject([PdsSearchService], (service: PdsSearchService) => {
    let categories: Array<PdsCategoryModel>  = new Array<PdsCategoryModel>();
    categories.push(new PdsCategoryModel("CAT_1", "Category 1"));
    categories.push(new PdsCategoryModel("CAT_2", "Category 2"));

    let selectedDateFilter: PdsAggregationResultModel = new PdsAggregationResultModel("FOREVER", 1);
    selectedDateFilter.pdsAggregationItem = new PdsAggregationResultModelAdditionalFields("pretty name", 1);

    let queryString = service.constructFinalQuery("query", categories, selectedDateFilter);

    let fromDate: string = moment().subtract(0, 'months').startOf("month").format('YYYY-MM-DD');
    let toDate: string = moment().endOf("month").format('YYYY-MM-DD'); // can be removed for '*' ?
    let dateFilter:string =  "(fileDate:[" + fromDate + " TO " + toDate + "])";
    expect(queryString).toBe("query AND (categoryId:CAT_1 OR categoryId:CAT_2) AND " + dateFilter);
  }));

});
