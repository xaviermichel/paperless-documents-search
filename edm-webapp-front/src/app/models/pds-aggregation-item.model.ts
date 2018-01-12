export class PdsAggregationItem {
  prettyLabel: string;

  constructor(prettyLabel: string) {
    this.prettyLabel = prettyLabel;
  }
}

export class PdsAggregationResultModel {
  docCount: number;
  key: string;

  pdsAggregationItem: PdsAggregationItem;

  constructor(key: string = "", docCount: number = 0) {
    this.key = key;
    this.docCount = docCount;
  }
}
