import { Directive, HostListener } from '@angular/core';
import { TimeshowService } from './timeshow.service';

@Directive({
  selector: '[timeshowtrigger]'
})
export class TimeshowtriggerDirective {
  private isMouseDown:boolean = false;

  constructor(private tss:TimeshowService) { }

  @HostListener("scroll", ['$event'])
  onScroll(e) {
    if (this.isMouseDown) {
      this.tss.setTimeshowTriggered(true);
    }
  }

  @HostListener("mousedown", ['$event'])
  onMouseDown(e) {
    this.isMouseDown = true;
  }

  @HostListener("mouseup", ['$event'])
  onMouseUp(e) {
    this.isMouseDown = false;
    this.tss.setTimeshowTriggered(false);
  }
}
