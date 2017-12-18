import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SdsCloudTagsComponent } from './sds-cloud-tags.component';

describe('SdsCloudTagsComponent', () => {
  let component: SdsCloudTagsComponent;
  let fixture: ComponentFixture<SdsCloudTagsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SdsCloudTagsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SdsCloudTagsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
