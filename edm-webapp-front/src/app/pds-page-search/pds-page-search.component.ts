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
import { PdsGlobalAggregationsWrapperModel } from '../models/pds-global-aggregations.model';
import { PdsAggregationsModel } from '../models/pds-aggregations.model';
import { PdsAggregationResultModel, PdsAggregationItem } from '../models/pds-aggregation-item.model';

@Component({
  selector: 'pds-page-search',
  templateUrl: './pds-page-search.component.html',
  styleUrls: ['../common.css', './pds-page-search.component.css']
})
export class PdsPageSearchComponent implements OnInit {

  constructor(private sdsSearchService: PdsSearchService, private sdsDocumentService: PdsDocumentService, private sdsCategoryService: PdsCategoryService) { }

  @ViewChild('searchForm') searchForm;

  pattern: string = "";
  suggestions: PdsSearchSuggestionsModel = new PdsSearchSuggestionsModel();
  searchResult: PdsSearchResultModel = null;

  filterCategories: Array<PdsCategoryModel> = new Array<PdsCategoryModel>();
  filterFileDate: PdsAggregationsModel = new PdsAggregationsModel();
  filterFileExtension: PdsAggregationsModel = new PdsAggregationsModel();

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

  linkToDocument(edmDocument: PdsDocumentModel) {
    return this.sdsDocumentService.getDocumentLink(edmDocument);
  }

  getDocumentIcon(edmDocument: PdsDocumentModel) {
    return this.sdsDocumentService.getDocumentIcon(edmDocument);
  }

  getCategoryById(categoryId: string): PdsCategoryModel {
    return this.sdsCategoryService.findById(categoryId);
  }

  onSubmitSearchForm() {
    this.submitSearch();
  }

  onCloudTagClicked(tagKey) {
    if (this.pattern === "") {
      this.pattern = tagKey;
      return;
    }
    this.pattern = this.pattern + " AND " + tagKey;
  }

  onCheckableCategoriesListSelectionChanged(categories: Array<PdsCategoryModel>) {
    this.filterCategories = categories;
    this.submitSearch();
  }

  private submitSearch() {
    this.refreshAggregations();

    if (this.pattern === "") {
      this.suggestions = new PdsSearchSuggestionsModel();
      this.searchResult = null;
      return;
    }
    this.sdsSearchService.searchForPattern(this.pattern, this.filterCategories)
      .subscribe((searchResult: PdsSearchResultModel) => this.searchResult = searchResult);
  }

  private refreshAggregations() {
    this.sdsSearchService.getAggregationsForPattern(this.pattern)
      .subscribe((globalAggregations: PdsGlobalAggregationsWrapperModel) => {
        for (let aggregate of globalAggregations.fileDate.aggregates) {
          switch (aggregate.key) {
            case "last_year":
              aggregate.pdsAggregationItem = new PdsAggregationItem("Depuis l'année dernière");
              break;
            case "last_6_months":
              aggregate.pdsAggregationItem = new PdsAggregationItem("Depuis les 6 derniers mois");
              break;
            case "last_2_months":
              aggregate.pdsAggregationItem = new PdsAggregationItem("Depuis les 2 derniers mois");
              break;
            case "last_month":
              aggregate.pdsAggregationItem = new PdsAggregationItem("Durant le mois qui vient de s'écouler");
              break;
          }
        }
        this.filterFileDate = globalAggregations.fileDate;


        this.filterFileExtension = globalAggregations.fileExtension;
      });
  }

}
