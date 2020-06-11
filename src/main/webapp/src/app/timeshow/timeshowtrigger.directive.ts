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
      if (this.tss.getTimeshowTriggered()) {
        let currentScrollPosition = e.srcElement.scrollTop;
        let scrollHeight = e.srcElement.scrollTopMax;
        
        this.tss.setRelativeTimeshowPosition(currentScrollPosition / scrollHeight * 100);
      } else {
        this.tss.setTimeshowTriggered(true);
      }
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
