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
import { PdsAggregationResultModel, PdsAggregationResultModelAdditionalFields } from '../models/pds-aggregation-item.model';

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
  selectedCategories: Array<PdsCategoryModel> = null;

  filterFileDate: PdsAggregationsModel = new PdsAggregationsModel();
  selectedDateFilter: PdsAggregationResultModel = null;

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
    this.selectedCategories = categories;
    this.submitSearch();
  }

  onRadioDocumentDateSelectionChanged(selectedDate: PdsAggregationResultModel) {
    this.selectedDateFilter = selectedDate;
    this.submitSearch();
  }

  private submitSearch() {
    this.refreshAggregations();

    if (this.pattern === "") {
      this.suggestions = new PdsSearchSuggestionsModel();
      this.searchResult = null;
      return;
    }
    this.sdsSearchService.searchForPattern(this.pattern, this.selectedCategories, this.selectedDateFilter)
    .subscribe((searchResult: PdsSearchResultModel) => this.searchResult = searchResult);
  }

  private refreshAggregations() {
    this.sdsSearchService.getAggregationsForPattern(this.pattern)
    .subscribe((globalAggregations: PdsGlobalAggregationsWrapperModel) => {
      for (let aggregate of globalAggregations.fileDate.aggregates) {
        switch (aggregate.key) {
          case "last_year":
          aggregate.pdsAggregationItem = new PdsAggregationResultModelAdditionalFields("Depuis l'année dernière");
          aggregate.pdsAggregationItem.filterValue = 12;
          break;
          case "last_6_months":
          aggregate.pdsAggregationItem = new PdsAggregationResultModelAdditionalFields("Depuis les 6 derniers mois");
          aggregate.pdsAggregationItem.filterValue = 6;
          break;
          case "last_2_months":
          aggregate.pdsAggregationItem = new PdsAggregationResultModelAdditionalFields("Depuis les 2 derniers mois");
          aggregate.pdsAggregationItem.filterValue = 2;
          break;
          case "last_month":
          aggregate.pdsAggregationItem = new PdsAggregationResultModelAdditionalFields("Durant le mois qui vient de s'écouler");
          aggregate.pdsAggregationItem.filterValue = 1;
          break;
          case "until_now":
          aggregate.pdsAggregationItem = new PdsAggregationResultModelAdditionalFields("Jusqu'à maintenant");
          aggregate.pdsAggregationItem.filterValue = 0;
          break;
        }
      }
      this.filterFileDate = globalAggregations.fileDate;


      this.filterFileExtension = globalAggregations.fileExtension;
    });
  }

}
