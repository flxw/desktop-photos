import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TimeScrollbarAnchorComponent } from './time-scrollbar-anchor.component';

describe('TimeScrollbarAnchorComponent', () => {
  let component: TimeScrollbarAnchorComponent;
  let fixture: ComponentFixture<TimeScrollbarAnchorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TimeScrollbarAnchorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TimeScrollbarAnchorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
