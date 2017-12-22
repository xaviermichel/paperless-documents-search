import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

import { PdsSearchService } from '../services/pds-search.service';
import { PdsAggregationsModel } from '../models/pds-aggregations.model';

@Component({
  selector: 'pds-cloud-tags',
  templateUrl: './pds-cloud-tags.component.html',
  styleUrls: ['../common.css', './pds-cloud-tags.component.css']
})
export class PdsCloudTagsComponent implements OnInit {

  @Input()
  set pattern(pattern: string) {
  	this.pdsSearchService.getTopTermsRelatedToPattern(pattern)
    .subscribe((pdsAggregationsModel: PdsAggregationsModel) => this.aggregationTopTerms = pdsAggregationsModel);
  }

  @Output() tagClicked = new EventEmitter<string>();

  aggregationTopTerms: PdsAggregationsModel = new PdsAggregationsModel();

  constructor(private pdsSearchService: PdsSearchService) { }

  ngOnInit() {
  }

  onTagClick(tagKey: string) {
    this.tagClicked.emit(tagKey);
  }

}
