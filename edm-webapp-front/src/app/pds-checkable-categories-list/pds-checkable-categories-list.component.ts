import {Component, OnInit, Output, EventEmitter, Input} from '@angular/core';

import { PdsCategoryService } from '../services/pds-category.service';

import { PdsCategoryModel, PdsCheckableCategoriesListComponentStatus } from '../models/pds-category.model';
import { PdsCategoryAggregationsModel } from '../models/pds-category-aggregations.model';
import { PdsCategoryAggregationResultModel } from '../models/pds-category-aggregation-item.model';

@Component({
  selector: 'pds-checkable-categories-list',
  templateUrl: './pds-checkable-categories-list.component.html',
  styleUrls: ['../common.css', './pds-checkable-categories-list.component.css']
})
export class PdsCheckableCategoriesListComponent implements OnInit {

  constructor() { }

  @Input()
  filterCategory: PdsCategoryAggregationsModel;

  @Output() selectionChanged = new EventEmitter<Array<PdsCategoryAggregationResultModel>>();

  ngOnInit() {
  }

  categorySelectionUpdated() {
    this.selectionChanged.emit(
      this.filterCategory.aggregates.filter(c => c.pdsAggregationItem.checked === true)
    );
  }
}
