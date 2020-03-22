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

  constructor(public gs: GraphicsService, public tp: TimePortService) {
  }

  ngOnInit() {
    this.populateTimeline();
  }

  ngAfterViewChecked() {
    let bh = this.body.nativeElement.firstChild.firstChild.clientHeight;
    
    if (bh != 0) this.tp.setBodyHeight(bh);
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
    this.timeline = tl;
    let previous = null;

    for (let date of Object.keys(tl)) {
      let nDate = new Date(date);
      let nDateString = nDate.getUTCMonth() + "/" + nDate.getUTCFullYear();
      
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
