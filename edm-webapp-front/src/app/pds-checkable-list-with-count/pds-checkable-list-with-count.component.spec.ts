import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { PdsAggregationsModel } from '../models/pds-aggregations.model';
import { PdsCheckableListWithCountComponent } from './pds-checkable-list-with-count.component';

describe('PdsCheckableListWithCountComponent', () => {
  let component: PdsCheckableListWithCountComponent;
  let fixture: ComponentFixture<PdsCheckableListWithCountComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PdsCheckableListWithCountComponent ],
      schemas: [ NO_ERRORS_SCHEMA ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PdsCheckableListWithCountComponent);
    component = fixture.componentInstance;
    component.filterAggregate = new PdsAggregationsModel();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fill template dynamic title', () => {
    // given
    component.blockTitle = 'Testing title';

    // when
    fixture.detectChanges();

    // then
    const element = fixture.nativeElement;
    expect(element.querySelector('.filter-title-text').textContent).toBe('Testing title');
  });

});
