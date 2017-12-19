import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PdsCloudTagsComponent } from './pds-cloud-tags.component';

describe('PdsCloudTagsComponent', () => {
  let component: PdsCloudTagsComponent;
  let fixture: ComponentFixture<PdsCloudTagsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PdsCloudTagsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PdsCloudTagsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
