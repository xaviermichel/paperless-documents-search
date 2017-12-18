import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { FlexLayoutModule } from "@angular/flex-layout";

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatToolbarModule, MatIconModule, MatInputModule, MatButtonModule, MatCheckboxModule, MatCardModule } from '@angular/material';

import { SdsSearchService } from './services/sds-search.service';
import { SdsDocumentService } from './services/sds-document.service';
import { SdsCategoryService } from './services/sds-category.service';

import { SdsPageSearchComponent } from './sds-page-search/sds-page-search.component';
import { SdsToolbarComponent } from './sds-toolbar/sds-toolbar.component';
import { SdsCloudTagsComponent } from './sds-cloud-tags/sds-cloud-tags.component';
import { SdsCheckableListComponent } from './sds-checkable-list/sds-checkable-list.component';
import { SdsCheckableListWithCountComponent } from './sds-checkable-list-with-count/sds-checkable-list-with-count.component';


@NgModule({
  declarations: [
    SdsPageSearchComponent,
    SdsToolbarComponent,
    SdsCloudTagsComponent,
    SdsCheckableListComponent,
    SdsCheckableListWithCountComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    FlexLayoutModule,
    BrowserAnimationsModule,
    MatAutocompleteModule,
    MatToolbarModule,
    MatIconModule,
    MatInputModule,
    MatButtonModule,
    MatCheckboxModule,
    MatCardModule
  ],
  providers: [
    SdsSearchService,
    SdsDocumentService,
    SdsCategoryService
  ],
  bootstrap: [SdsToolbarComponent, SdsPageSearchComponent]
})
export class SdsModule { }
