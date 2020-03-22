import { Component, AfterContentChecked, ElementRef, Input, OnInit, OnDestroy } from '@angular/core';
import { TimePortService } from '../time-port.service'

@Component({
  selector: 'time-scrollbar-anchor',
  templateUrl: './time-scrollbar-anchor.component.html',
  styleUrls: ['./time-scrollbar-anchor.component.styl']
})
export class TimeScrollbarAnchorComponent implements AfterContentChecked, OnInit, OnDestroy {
  @Input() text: string;
  private id:string;

  constructor(private el: ElementRef, private tp: TimePortService) { }

  ngAfterContentChecked() {
    //let offsetTop = this.el.nativeElement.getBoundingClientRect().top;
    //this.tp.upsertAnchor(this.id, this.text, offsetTop);
  }

  ngOnInit() {
    this.id = this.tp.registerId();
    let offsetTop = this.el.nativeElement.getBoundingClientRect().top;
    this.tp.upsertAnchor(this.id, this.text, offsetTop);
  }

  ngOnDestroy() {
    this.tp.unregisterId(this.id);
  }
}
