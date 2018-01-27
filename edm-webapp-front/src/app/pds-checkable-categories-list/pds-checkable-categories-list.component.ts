import { Component, OnInit, Output, EventEmitter } from '@angular/core';

import { PdsCategoryService } from '../services/pds-category.service';

import { PdsCategoryModel, PdsCheckableCategoriesListComponentStatus } from '../models/pds-category.model';

@Component({
  selector: 'pds-checkable-categories-list',
  templateUrl: './pds-checkable-categories-list.component.html',
  styleUrls: ['../common.css', './pds-checkable-categories-list.component.css']
})
export class PdsCheckableCategoriesListComponent implements OnInit {

  constructor(private pdsCategoryService: PdsCategoryService) { }

  categories: Array<PdsCategoryModel>;

  @Output() selectionChanged = new EventEmitter<Array<PdsCategoryModel>>();

  ngOnInit() {
    this.pdsCategoryService.findAll()
      .subscribe((categories: Array<PdsCategoryModel>) => {
        this.categories = categories;
        // init all as selected
        for (const category of this.categories) {
          category.checkableCategoriesListComponentStatus = new PdsCheckableCategoriesListComponentStatus();
        }
      });
  }

  categorySelectionUpdated() {
    this.selectionChanged.emit(
      this.categories.filter(c => c.checkableCategoriesListComponentStatus.checked === true)
    );
  }
}
