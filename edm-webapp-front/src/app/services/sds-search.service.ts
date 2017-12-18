import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { SdsSearchResultModel } from '../models/sds-search-result.model';
import { SdsSearchSuggestionsModel } from '../models/sds-search-suggestions.model';
import { SdsAggregationsModel } from '../models/sds-aggregations.model';

import { environment } from '../../environments/environment';

@Injectable()
export class SdsSearchService {

  constructor(private http: HttpClient) { }

  searchForPattern(pattern: string): Observable<SdsSearchResultModel> {
    const params = new HttpParams().set('q', pattern);
    return this.http.get<SdsSearchResultModel>(`${environment.sdsApiBaseUrl}/document`, { params });
  }

  getSuggestionsForPattern(pattern: string): Observable<SdsSearchSuggestionsModel> {
    const params = new HttpParams().set('q', pattern);
    return this.http.get<SdsSearchSuggestionsModel>(`${environment.sdsApiBaseUrl}/document/suggest`, {params});
  }

  getTopTermsRelatedToPattern(pattern: string): Observable<SdsAggregationsModel> {
    const params = new HttpParams().set('q', pattern);
    return this.http.get<SdsAggregationsModel>(`${environment.sdsApiBaseUrl}/document/top_terms`, {params});
  }

}
