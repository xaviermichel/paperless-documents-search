import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

import { PdsAggregationsModel } from '../models/pds-aggregations.model';
import { PdsAggregationResultModel } from '../models/pds-aggregation-item.model';

@Component({
  selector: 'pds-checkable-list-with-count',
  templateUrl: './pds-checkable-list-with-count.component.html',
  styleUrls: ['../common.css', './pds-checkable-list-with-count.component.css']
})
export class PdsCheckableListWithCountComponent implements OnInit {

  @Input()
  blockIconName: string;

  @Input()
  blockTitle: string;

  @Input()
  filterAggregate: PdsAggregationsModel;

  @Output()
  selectionChanged = new EventEmitter<Array<PdsAggregationResultModel>>();

  constructor() { }

  ngOnInit() {
  }

  onSelectionChange() {
    this.selectionChanged.emit(
      this.filterAggregate.aggregates.filter(c => c.pdsAggregationItem.checked === true)
    );
  }
}
