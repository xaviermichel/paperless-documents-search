import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PdsPageSearchComponent } from './pds-page-search.component';

describe('PdsPageSearchComponent', () => {
  let component: PdsPageSearchComponent;
  let fixture: ComponentFixture<PdsPageSearchComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PdsPageSearchComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PdsPageSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
