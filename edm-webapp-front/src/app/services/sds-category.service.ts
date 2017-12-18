import { Injectable, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { SdsCategoryModel } from '../models/sds-category.model';

import { environment } from '../../environments/environment';

@Injectable()
export class SdsCategoryService {

  // categories cache
  categories: Array<SdsCategoryModel> = null;

  constructor(private http: HttpClient) {
    http.get<SdsCategoryModel>(`${environment.sdsApiBaseUrl}/category`)
      .subscribe((categories: any) => this.categories = categories);
  }

  findById(id: string): SdsCategoryModel {
    return this.categories.filter(c => c.id === id)[0];
  }
}
