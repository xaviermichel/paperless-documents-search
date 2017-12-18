import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';

import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';

import { SdsSearchService } from '../services/sds-search.service';
import { SdsDocumentService } from '../services/sds-document.service';
import { SdsCategoryService } from '../services/sds-category.service';
import { SdsSearchResultModel } from '../models/sds-search-result.model';
import { SdsDocumentModel } from '../models/sds-document.model';
import { SdsCategoryModel } from '../models/sds-category.model';
import { SdsSearchSuggestionsModel } from '../models/sds-search-suggestions.model';

@Component({
  selector: 'sds-page-search',
  templateUrl: './sds-page-search.component.html',
  styleUrls: ['./sds-page-search.component.css']
})
export class SdsPageSearchComponent implements OnInit {

  constructor(private sdsSearchService: SdsSearchService, private sdsDocumentService: SdsDocumentService, private sdsCategoryService: SdsCategoryService) { }

  @ViewChild('searchForm') searchForm;

  pattern: string = "";
  suggestions: SdsSearchSuggestionsModel = new SdsSearchSuggestionsModel();
  searchResult: SdsSearchResultModel = null;

  ngOnInit() {
  }

  ngAfterViewInit() {
    this.searchForm.control.valueChanges.debounceTime(800).distinctUntilChanged()
    .subscribe(
        data => this.sdsSearchService.getSuggestionsForPattern(data.pattern)
          .subscribe((sdsSearchSuggestionsModel: SdsSearchSuggestionsModel) => this.suggestions = sdsSearchSuggestionsModel)
    );
    this.searchForm.control.valueChanges.debounceTime(400).distinctUntilChanged()
      .subscribe(data => this.onSubmitSearchForm());
  }

  onSubmitSearchForm() {
    if (this.pattern === "") {
      this.suggestions = new SdsSearchSuggestionsModel();
      this.searchResult = null;
      return;
    }
    this.sdsSearchService.searchForPattern(this.pattern)
    .subscribe((searchResult: SdsSearchResultModel) => this.searchResult = searchResult);
  }

  linkToDocument(edmDocument: SdsDocumentModel) {
    return this.sdsDocumentService.getDocumentLink(edmDocument);
  }

  getDocumentIcon(edmDocument: SdsDocumentModel) {
    return this.sdsDocumentService.getDocumentIcon(edmDocument);
  }

  getCategoryById(categoryId: string): SdsCategoryModel {
    return this.sdsCategoryService.findById(categoryId);
  }
}
