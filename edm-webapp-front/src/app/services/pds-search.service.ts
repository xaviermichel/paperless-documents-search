import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { PdsSearchResultModel } from '../models/pds-search-result.model';
import { PdsSearchSuggestionsModel } from '../models/pds-search-suggestions.model';
import { PdsAggregationsModel } from '../models/pds-aggregations.model';
import { PdsCategoryModel } from '../models/pds-category.model';
import { PdsGlobalAggregationsWrapperModel } from '../models/pds-global-aggregations.model';

import { environment } from '../../environments/environment';

@Injectable()
export class PdsSearchService {

  constructor(private http: HttpClient) { }

  searchForPattern(pattern: string, categoriesFilter: Array<PdsCategoryModel> = new Array<PdsCategoryModel>()): Observable<PdsSearchResultModel> {
    let query: string = this.constructFinalQuery(pattern, categoriesFilter);
    const params = new HttpParams().set('q', query);
    return this.http.get<PdsSearchResultModel>(`${environment.sdsApiBaseUrl}/document`, { params });
  }

  private constructFinalQuery(pattern: string, categoriesFilter: Array<PdsCategoryModel>): string {
    let query: string = pattern;
    let categoryFilterStringQuery: string = this.categoryFilterToStringQuery(categoriesFilter);
    if (categoryFilterStringQuery != "") {
      query += " AND (" + categoryFilterStringQuery + ")";
    }
    return query;
  }

  private categoryFilterToStringQuery(categoryFilter: Array<PdsCategoryModel>): string {
    return categoryFilter
    .filter(function isSelected(c: PdsCategoryModel) {
      return c.checkableCategoriesListComponentStatus.checked === true
    })
    .map(function formatedQuery(c: PdsCategoryModel) {
      return "categoryId:" + c.id;
    })
    .join(" OR ");
  }

  getSuggestionsForPattern(pattern: string): Observable<PdsSearchSuggestionsModel> {
    const params = new HttpParams().set('q', pattern);
    return this.http.get<PdsSearchSuggestionsModel>(`${environment.sdsApiBaseUrl}/document/suggest`, {params});
  }

  getTopTermsRelatedToPattern(pattern: string): Observable<PdsAggregationsModel> {
    const params = new HttpParams().set('q', pattern);
    return this.http.get<PdsAggregationsModel>(`${environment.sdsApiBaseUrl}/document/top_terms`, {params});
  }

  getAggregationsForPattern(pattern: string): Observable<PdsGlobalAggregationsWrapperModel> {
    const params = new HttpParams().set('q', pattern);
    return this.http.get<PdsGlobalAggregationsWrapperModel>(`${environment.sdsApiBaseUrl}/document/aggregations`, {params});
  }

}
