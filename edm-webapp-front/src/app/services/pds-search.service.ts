import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { PdsSearchResultModel } from '../models/pds-search-result.model';
import { PdsSearchSuggestionsModel } from '../models/pds-search-suggestions.model';
import { PdsAggregationsModel } from '../models/pds-aggregations.model';

import { environment } from '../../environments/environment';

@Injectable()
export class PdsSearchService {

  constructor(private http: HttpClient) { }

  searchForPattern(pattern: string): Observable<PdsSearchResultModel> {
    const params = new HttpParams().set('q', pattern);
    return this.http.get<PdsSearchResultModel>(`${environment.sdsApiBaseUrl}/document`, { params });
  }

  getSuggestionsForPattern(pattern: string): Observable<PdsSearchSuggestionsModel> {
    const params = new HttpParams().set('q', pattern);
    return this.http.get<PdsSearchSuggestionsModel>(`${environment.sdsApiBaseUrl}/document/suggest`, {params});
  }

  getTopTermsRelatedToPattern(pattern: string): Observable<PdsAggregationsModel> {
    const params = new HttpParams().set('q', pattern);
    return this.http.get<PdsAggregationsModel>(`${environment.sdsApiBaseUrl}/document/top_terms`, {params});
  }

}
