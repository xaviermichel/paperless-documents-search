import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { PdsAggregationsModel } from '../models/pds-aggregations.model';
import { PdsAggregationResultModel } from '../models/pds-aggregation-item.model';
import { RadioListWithCountComponent } from './pds-radio-list-with-count.component';

describe('RadioListWithCountComponent', () => {
  let component: RadioListWithCountComponent;
  let fixture: ComponentFixture<RadioListWithCountComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RadioListWithCountComponent ],
      schemas: [ NO_ERRORS_SCHEMA ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RadioListWithCountComponent);
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
