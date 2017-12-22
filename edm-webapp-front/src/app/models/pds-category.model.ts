export class PdsCheckableCategoriesListComponentStatus {
  checked: boolean = true;
}

export class PdsCategoryModel {
  backgroundColor: string;
  color: string;
  description: string;
  id: string;
  name: string;

  checkableCategoriesListComponentStatus: PdsCheckableCategoriesListComponentStatus;
}
