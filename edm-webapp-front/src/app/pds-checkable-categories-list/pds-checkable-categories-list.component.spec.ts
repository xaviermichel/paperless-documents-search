import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CheckableCategoriesListComponent } from './checkable-categories-list.component';

describe('CheckableCategoriesListComponent', () => {
  let component: CheckableCategoriesListComponent;
  let fixture: ComponentFixture<CheckableCategoriesListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CheckableCategoriesListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckableCategoriesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
