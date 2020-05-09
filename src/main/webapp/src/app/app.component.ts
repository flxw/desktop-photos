import { Component, OnInit, AfterViewInit, ViewChild, AfterViewChecked } from '@angular/core';
import { GraphicsService } from './graphics.service';
import { TimePortService } from './time-port.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.styl']
})
export class AppComponent implements OnInit, AfterViewChecked {
  @ViewChild("body") body;
  timeline: Map<Date, any>;
  anchorRenderDates:string[] = [];
  scrollEventProperties: any = {
    scrollTop : 0,
    scrollHeight : 0
  };
  firstViewCheck:boolean = true;

  constructor(public gs: GraphicsService, public tp: TimePortService) {
  }

  ngOnInit() {
    this.populateTimeline();
  }

  ngAfterViewChecked() {
    if (this.firstViewCheck) {
      let ce = this.body.nativeElement.firstChild.firstChild;
      this.tp.setContainerElement(ce);
      this.firstViewCheck = false;
    }
  }

  populateTimeline() {
    this.gs
        .getTimeline()
        .subscribe(tl => this.preProcessTimeline(tl));
  }

  asIsOrder(a, b) {
    return 0;
  }

  setTopScroll(e:any) {
    this.scrollEventProperties = {
      scrollTop: e.srcElement.scrollTop,
      scrollHeight: e.srcElement.scrollHeight
    };
  }

  preProcessTimeline(tl:Map<Date,any>):void {
    // acquire the newest dates in a year to place the anchor there
    this.timeline = tl;
    let previous = null;

    for (let date of Object.keys(tl)) {
      let nDate = new Date(date);
      let nDateString = String(nDate.getUTCFullYear());
      
      if (previous != nDateString) {
        this.anchorRenderDates.push(date);
      }

      previous = nDateString;
    }
  }

  shouldBeRendered(dNow:string):boolean {
    return this.anchorRenderDates.includes(dNow);
  }
}
