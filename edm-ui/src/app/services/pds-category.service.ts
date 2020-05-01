import { Injectable, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { PdsCategoryModel } from '../models/pds-category.model';

import { environment } from '../../environments/environment';

@Injectable()
export class PdsCategoryService {

  // categories cache
  categoriesCache: Array<PdsCategoryModel> = null;

  constructor(private http: HttpClient) {
    http.get<PdsCategoryModel>(`${environment.sdsApiBaseUrl}/categories`)
      .subscribe((categories: any) => this.categoriesCache = categories);
  }

  findById(id: string): PdsCategoryModel {
    return this.categoriesCache.filter(c => c.id === id)[0];
  }

  findAll(): Observable<Array<PdsCategoryModel>> {
    return this.http.get<Array<PdsCategoryModel>>(`${environment.sdsApiBaseUrl}/categories`);
  }
}
