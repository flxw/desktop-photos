import { Directive, HostListener } from '@angular/core';

@Directive({
  selector: '[timeshowtrigger]'
})
export class TimeshowtriggerDirective {

  constructor() { }

  @HostListener("scroll", ['$event'])
  onScroll(e) {
    if (this.isMouseDown) {
      console.log('Scrolling via scrollbar')
    }
  }

  isMouseDown:boolean = false;
  @HostListener("mousedown", ['$event'])
  onMouseDown(e) {
    this.isMouseDown = true;
  }

  @HostListener("mouseup", ['$event'])
  onMouseUp(e) {
    this.isMouseDown = false;
  }
}
