import { SdsDocumentModel } from './sds-document.model';

export class SdsSearchResultItemModel {
  edmDocument: SdsDocumentModel;
  highlightedDescription: string;
  highlightedFileContentMatching: string;
  highlightedName: string;
  highlightedNodePath: string;
}
