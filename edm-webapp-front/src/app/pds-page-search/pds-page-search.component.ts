import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';

import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';

import { PdsSearchService } from '../services/pds-search.service';
import { PdsDocumentService } from '../services/pds-document.service';
import { PdsCategoryService } from '../services/pds-category.service';
import { PdsSearchResultModel } from '../models/pds-search-result.model';
import { PdsDocumentModel } from '../models/pds-document.model';
import { PdsCategoryModel } from '../models/pds-category.model';
import { PdsSearchSuggestionsModel } from '../models/pds-search-suggestions.model';

@Component({
  selector: 'pds-page-search',
  templateUrl: './pds-page-search.component.html',
  styleUrls: ['./pds-page-search.component.css']
})
export class PdsPageSearchComponent implements OnInit {

  constructor(private sdsSearchService: PdsSearchService, private sdsDocumentService: PdsDocumentService, private sdsCategoryService: PdsCategoryService) { }

  @ViewChild('searchForm') searchForm;

  pattern: string = "";
  suggestions: PdsSearchSuggestionsModel = new PdsSearchSuggestionsModel();
  searchResult: PdsSearchResultModel = null;

  ngOnInit() {
  }

  ngAfterViewInit() {
    this.searchForm.control.valueChanges.debounceTime(800).distinctUntilChanged()
    .subscribe(
        data => this.sdsSearchService.getSuggestionsForPattern(data.pattern)
          .subscribe((sdsSearchSuggestionsModel: PdsSearchSuggestionsModel) => this.suggestions = sdsSearchSuggestionsModel)
    );
    this.searchForm.control.valueChanges.debounceTime(400).distinctUntilChanged()
      .subscribe(data => this.onSubmitSearchForm());
  }

  onSubmitSearchForm() {
    if (this.pattern === "") {
      this.suggestions = new PdsSearchSuggestionsModel();
      this.searchResult = null;
      return;
    }
    this.sdsSearchService.searchForPattern(this.pattern)
    .subscribe((searchResult: PdsSearchResultModel) => this.searchResult = searchResult);
  }

  linkToDocument(edmDocument: PdsDocumentModel) {
    return this.sdsDocumentService.getDocumentLink(edmDocument);
  }

  getDocumentIcon(edmDocument: PdsDocumentModel) {
    return this.sdsDocumentService.getDocumentIcon(edmDocument);
  }

  getCategoryById(categoryId: string): PdsCategoryModel {
    return this.sdsCategoryService.findById(categoryId);
  }
}
