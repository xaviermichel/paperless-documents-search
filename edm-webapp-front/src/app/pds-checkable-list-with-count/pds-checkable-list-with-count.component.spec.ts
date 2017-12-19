import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PdsCheckableListWithCountComponent } from './pds-checkable-list-with-count.component';

describe('PdsCheckableListWithCountComponent', () => {
  let component: PdsCheckableListWithCountComponent;
  let fixture: ComponentFixture<PdsCheckableListWithCountComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PdsCheckableListWithCountComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PdsCheckableListWithCountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
