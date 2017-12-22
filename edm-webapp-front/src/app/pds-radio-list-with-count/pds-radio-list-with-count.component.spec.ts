import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RadioListWithCountComponent } from './pds-radio-list-with-count.component';

describe('RadioListWithCountComponent', () => {
  let component: RadioListWithCountComponent;
  let fixture: ComponentFixture<RadioListWithCountComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RadioListWithCountComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RadioListWithCountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
