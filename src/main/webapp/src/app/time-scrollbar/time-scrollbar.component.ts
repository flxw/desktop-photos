import { Component, OnChanges, Input, SimpleChanges } from '@angular/core';

@Component({
  selector: 'time-scrollbar',
  templateUrl: './time-scrollbar.component.html',
  styleUrls: ['./time-scrollbar.component.styl']
})
export class TimeScrollbarComponent implements OnChanges {
  @Input() currentScrollProperties;
  scrollPosition : number;

  constructor() {}

  ngOnChanges(changes: SimpleChanges) {
    var st = changes.currentScrollProperties.currentValue.scrollTop;
    var sh = changes.currentScrollProperties.currentValue.scrollHeight;
    this.scrollPosition = st / sh * 100;
  }
}
