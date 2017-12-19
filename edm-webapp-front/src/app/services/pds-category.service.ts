import { Injectable, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { PdsCategoryModel } from '../models/pds-category.model';

import { environment } from '../../environments/environment';

@Injectable()
export class PdsCategoryService {

  // categories cache
  categories: Array<PdsCategoryModel> = null;

  constructor(private http: HttpClient) {
    http.get<PdsCategoryModel>(`${environment.sdsApiBaseUrl}/category`)
      .subscribe((categories: any) => this.categories = categories);
  }

  findById(id: string): PdsCategoryModel {
    return this.categories.filter(c => c.id === id)[0];
  }
}
