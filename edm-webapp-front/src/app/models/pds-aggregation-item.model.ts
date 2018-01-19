export class PdsAggregationResultModelAdditionalFields {
  prettyLabel: string;
  filterValue: any;
  checked: boolean = true;

  constructor(prettyLabel: string = "", filterValue: any = 0) {
    this.prettyLabel = prettyLabel;
  }
}

export class PdsAggregationResultModel {
  docCount: number;
  key: string;
  pdsAggregationItem: PdsAggregationResultModelAdditionalFields;

  constructor(key: string = "", docCount: number = 0) {
    this.key = key;
    this.docCount = docCount;
  }
}
