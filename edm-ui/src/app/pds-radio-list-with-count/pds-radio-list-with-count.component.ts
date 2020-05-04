import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

import { PdsAggregationsModel } from '../models/pds-aggregations.model';
import { PdsAggregationResultModel } from '../models/pds-aggregation-item.model';

@Component({
  selector: 'pds-radio-list-with-count',
  templateUrl: './pds-radio-list-with-count.component.html',
  styleUrls: ['../common.css', './pds-radio-list-with-count.component.css']
})
export class RadioListWithCountComponent implements OnInit {

  @Input()
  blockIconName: string;

  @Input()
  blockTitle: string;

  @Input()
  filterAggregate: PdsAggregationsModel;

  @Input()
  aggregateSelected: string = '';

  @Output()
  selectionChanged = new EventEmitter<PdsAggregationResultModel>();

  constructor() { }

  ngOnInit() {
  }

  onSelectionChange() {
    // find aggregate from it's key
    const selectedAggregate: PdsAggregationResultModel = this.filterAggregate.aggregates.filter(aggregate => aggregate.key === this.aggregateSelected)[0];
    this.selectionChanged.emit(selectedAggregate);
  }
}
