import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PdsToolbarComponent } from './pds-toolbar.component';

describe('PdsToolbarComponent', () => {
  let component: PdsToolbarComponent;
  let fixture: ComponentFixture<PdsToolbarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PdsToolbarComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PdsToolbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
