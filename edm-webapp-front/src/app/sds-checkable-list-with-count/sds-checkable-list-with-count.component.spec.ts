import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SdsCheckableListWithCountComponent } from './sds-checkable-list-with-count.component';

describe('SdsCheckableListWithCountComponent', () => {
  let component: SdsCheckableListWithCountComponent;
  let fixture: ComponentFixture<SdsCheckableListWithCountComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SdsCheckableListWithCountComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SdsCheckableListWithCountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
