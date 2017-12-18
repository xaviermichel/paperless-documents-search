import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SdsPageSearchComponent } from './sds-page-search.component';

describe('SdsPageSearchComponent', () => {
  let component: SdsPageSearchComponent;
  let fixture: ComponentFixture<SdsPageSearchComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SdsPageSearchComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SdsPageSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
