import { SdsSearchResultItemModel } from './sds-search-result-item.model';

export class SdsSearchResultModel {
  searchResults: Array<SdsSearchResultItemModel>;
  totalHitsCount: number;
  tookTime: number;
}
