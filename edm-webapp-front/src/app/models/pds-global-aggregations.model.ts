import { PdsAggregationsModel } from './pds-aggregations.model';
import { PdsCategoryAggregationsModel } from './pds-category-aggregations.model';

export class PdsGlobalAggregationsWrapperModel {
  fileDate: PdsAggregationsModel;
  fileExtension: PdsAggregationsModel;
  fileCategory: PdsCategoryAggregationsModel;
}
