import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

import { PdsSearchService } from '../services/pds-search.service';
import { PdsAggregationsModel } from '../models/pds-aggregations.model';

@Component({
  selector: 'pds-cloud-tags',
  templateUrl: './pds-cloud-tags.component.html',
  styleUrls: ['../common.css', './pds-cloud-tags.component.css']
})
export class PdsCloudTagsComponent implements OnInit {

  modelChanged: Subject<string> = new Subject<string>();

  @Input()
  set pattern(pattern: string) {
    this.modelChanged.next(pattern);
  }

  @Output() tagClicked = new EventEmitter<string>();

  aggregationTopTerms: PdsAggregationsModel = new PdsAggregationsModel();

  constructor(private pdsSearchService: PdsSearchService) {
    this.modelChanged.pipe(debounceTime(500), distinctUntilChanged())
      .subscribe(pattern => {
        this.pdsSearchService.getTopTermsRelatedToPattern(pattern)
          .subscribe((pdsAggregationsModel: PdsAggregationsModel) => this.aggregationTopTerms = pdsAggregationsModel);
    });
  }

  ngOnInit() {
  }

  onTagClick(tagKey: string) {
    this.tagClicked.emit(tagKey);
  }

}
