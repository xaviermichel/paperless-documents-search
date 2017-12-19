import { PdsDocumentModel } from './pds-document.model';

export class PdsSearchResultItemModel {
  edmDocument: PdsDocumentModel;
  highlightedDescription: string;
  highlightedFileContentMatching: string;
  highlightedName: string;
  highlightedNodePath: string;
}
