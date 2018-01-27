import { Component, OnInit, ViewChild, AfterViewInit, EventEmitter } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';

import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';

import { Observable } from 'rxjs/Observable';

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

  @ViewChild('searchForm')
  searchForm;

  pattern: string = '';
  suggestions: PdsSearchSuggestionsModel = new PdsSearchSuggestionsModel();
  searchResult: PdsSearchResultModel = null;

  filterCategories: Array<PdsCategoryModel> = new Array<PdsCategoryModel>();
  selectedCategories: Array<PdsCategoryModel> = null;

  filterFileDate: PdsAggregationsModel = new PdsAggregationsModel();
  selectedDateFilter: PdsAggregationResultModel = null;

  filterFileExtension: PdsAggregationsModel = new PdsAggregationsModel();
  selectedFileExtensions: Array<PdsAggregationResultModel> = null;

  ngOnInit() {
  }

  ngAfterViewInit() {
    this.searchForm.control.valueChanges.debounceTime(500).distinctUntilChanged()
      .subscribe(data => this.onSubmitSearchForm());
    this.refreshAggregations();
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
    if (this.pattern === '') {
      this.pattern = tagKey;
      return;
    }
    this.pattern = this.pattern + ' AND ' + tagKey;
  }

  onCheckableCategoriesListSelectionChanged(categories: Array<PdsCategoryModel>) {
    this.selectedCategories = categories;
    this.submitSearch(false);
  }

  onCheckableDocumentExtensionSelectionChanged(extensions: Array<PdsAggregationResultModel>) {
    this.selectedFileExtensions = extensions;
    this.submitSearch(false);
  }

  onRadioDocumentDateSelectionChanged(selectedDate: PdsAggregationResultModel) {
    this.selectedDateFilter = selectedDate;
    this.submitSearch(false);
  }

  private submitSearch(refreshAggregation: boolean = true) {
    if (this.pattern.trim() === '') {
      this.suggestions = new PdsSearchSuggestionsModel();
      this.searchResult = null;
      return;
    }

    // refresh aggregates only for new search, not when you are filtering with aggregates
    if (refreshAggregation) {
      this.sdsSearchService.searchForPattern(this.pattern)
        .subscribe((searchResult: PdsSearchResultModel) => this.searchResult = searchResult);
      this.refreshAggregations();
    } else {
      this.sdsSearchService.searchForPattern(this.pattern, this.selectedCategories, this.selectedDateFilter, this.selectedFileExtensions)
        .subscribe((searchResult: PdsSearchResultModel) => this.searchResult = searchResult);
    }
  }

  private refreshAggregations() {
    this.sdsSearchService.getAggregationsForPattern(this.pattern)
    .subscribe((globalAggregations: PdsGlobalAggregationsWrapperModel) => {
      for (const aggregate of globalAggregations.fileDate.aggregates) {
        switch (aggregate.key) {
          case 'last_year':
          aggregate.pdsAggregationItem = new PdsAggregationResultModelAdditionalFields('Depuis l\'année dernière');
          aggregate.pdsAggregationItem.filterValue = 12;
          break;
          case 'last_6_months':
          aggregate.pdsAggregationItem = new PdsAggregationResultModelAdditionalFields('Depuis les 6 derniers mois');
          aggregate.pdsAggregationItem.filterValue = 6;
          break;
          case 'last_2_months':
          aggregate.pdsAggregationItem = new PdsAggregationResultModelAdditionalFields('Depuis les 2 derniers mois');
          aggregate.pdsAggregationItem.filterValue = 2;
          break;
          case 'last_month':
          aggregate.pdsAggregationItem = new PdsAggregationResultModelAdditionalFields('Durant le mois qui vient de s\'écouler');
          aggregate.pdsAggregationItem.filterValue = 1;
          break;
          case 'until_now':
          aggregate.pdsAggregationItem = new PdsAggregationResultModelAdditionalFields('Jusqu\'à maintenant');
          aggregate.pdsAggregationItem.filterValue = 0;
          break;
        }
      }
      this.filterFileDate = globalAggregations.fileDate;

      for (const aggregate of globalAggregations.fileExtension.aggregates) {
        aggregate.pdsAggregationItem = new PdsAggregationResultModelAdditionalFields();
      }
      this.filterFileExtension = globalAggregations.fileExtension;
    });
  }

}
