import { Component, Input } from '@angular/core';
import { TimePortService } from '../time-port.service'


@Component({
  selector: 'time-scrollbar',
  templateUrl: './time-scrollbar.component.html',
  styleUrls: ['./time-scrollbar.component.styl']
})
export class TimeScrollbarComponent {
  constructor(public tp:TimePortService) {}
}