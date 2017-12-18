import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SdsCheckableListComponent } from './sds-checkable-list.component';

describe('SdsCheckableListComponent', () => {
  let component: SdsCheckableListComponent;
  let fixture: ComponentFixture<SdsCheckableListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SdsCheckableListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SdsCheckableListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
