import { PdsSearchResultItemModel } from './pds-search-result-item.model';

export class PdsSearchResultModel {
  searchResults: Array<PdsSearchResultItemModel>;
  totalHitsCount: number;
  tookTime: number;
}
