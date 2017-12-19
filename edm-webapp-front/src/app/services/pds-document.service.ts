import { Injectable } from '@angular/core';

import { PdsDocumentModel } from '../models/pds-document.model';

import { environment } from '../../environments/environment';

@Injectable()
export class PdsDocumentService {

  constructor() { }

  getDocumentLink(edmDocument: PdsDocumentModel): string {
    // web link
    if (edmDocument.nodePath.indexOf("http") === 0) {
      return edmDocument.nodePath;
    }
    // network link
    if (edmDocument.nodePath.indexOf("//") === 0) {
        return edmDocument.nodePath.replace(/\//g, "\\"); // windows style
      }
    // local file
    return `${environment.sdsApiBaseUrl}/files?docId=${edmDocument.id}`;
  }

  getDocumentIcon(edmDocument: PdsDocumentModel): string {
    switch (edmDocument.fileExtension.toLowerCase()) {
      case "pdf":
        return "pdf";
      case "html":
        return "html";
      case "png":
      case "jpg":
      case "jpeg":
      case "gif":
        return "image";
      case "doc":
      case "docx":
        return "word";
      case "xls":
      case "xlsx":
        return "excel";
      case "ppt":
      case "pptx":
        return "power-point";
      case "txt":
        return "text";
    }
    return "unknown"; // default icon
  }
}
