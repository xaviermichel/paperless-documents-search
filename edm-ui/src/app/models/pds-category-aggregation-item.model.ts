import {PdsCategoryModel} from './pds-category.model';
import {PdsAggregationResultModelAdditionalFields} from './pds-aggregation-item.model';

export class PdsCategoryAggregationResultModel {
  docCount: number;
  key: string;
  category: PdsCategoryModel;
  pdsAggregationItem: PdsAggregationResultModelAdditionalFields;
}
