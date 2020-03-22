import { Component, OnChanges, Input, SimpleChanges, OnInit, ViewChild, AfterViewChecked } from '@angular/core';
import { TimePortService } from '../time-port.service'


@Component({
  selector: 'time-scrollbar',
  templateUrl: './time-scrollbar.component.html',
  styleUrls: ['./time-scrollbar.component.styl']
})
export class TimeScrollbarComponent implements OnChanges {
  @Input() currentScrollProperties:object;
  @ViewChild("body") body;
  scrollPosition : number;
  bodyHeight: number = 0;

  constructor(public tp:TimePortService) {}

  calculateRelativePosition(absoluteTopOffset:number):number {
    return absoluteTopOffset/this.bodyHeight * 100;
  }

  ngOnChanges(changes: SimpleChanges) {
    let st = changes.currentScrollProperties.currentValue.scrollTop;
    this.bodyHeight = changes.currentScrollProperties.currentValue.scrollHeight;

    this.scrollPosition = st / this.bodyHeight * 100;
  }
}
