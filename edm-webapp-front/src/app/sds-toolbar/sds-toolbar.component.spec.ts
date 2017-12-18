import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SdsToolbarComponent } from './sds-toolbar.component';

describe('SdsToolbarComponent', () => {
  let component: SdsToolbarComponent;
  let fixture: ComponentFixture<SdsToolbarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SdsToolbarComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SdsToolbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
