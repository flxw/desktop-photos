import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TimeScrollbarComponent } from './time-scrollbar.component';

describe('TimeScrollbarComponent', () => {
  let component: TimeScrollbarComponent;
  let fixture: ComponentFixture<TimeScrollbarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TimeScrollbarComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TimeScrollbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
