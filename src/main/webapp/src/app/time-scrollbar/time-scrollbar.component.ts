import { Component, OnChanges, Input, SimpleChanges } from '@angular/core';
import { TimePortService } from '../time-port.service'


@Component({
  selector: 'time-scrollbar',
  templateUrl: './time-scrollbar.component.html',
  styleUrls: ['./time-scrollbar.component.styl']
})
export class TimeScrollbarComponent implements OnChanges {
  @Input() currentScrollProperties:object;
  scrollPosition : number;

  constructor(public tp:TimePortService) {}

  ngOnChanges(changes: SimpleChanges) {
    let st = changes.currentScrollProperties.currentValue.scrollTop;
    this.scrollPosition = this.tp.calculateScalePositionFromAbsolute(st)
  }
}