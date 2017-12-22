import { Component, OnInit, Input } from '@angular/core';

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

  constructor() { }

  ngOnInit() {
  }

}
