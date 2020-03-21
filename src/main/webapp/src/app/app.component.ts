import { Component, OnInit } from '@angular/core';
import { GraphicsService } from './graphics.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.styl']
})
export class AppComponent implements OnInit{
  timeline: Map<Date, any>;
  scrollEventProperties: any = {
    scrollTop : 0,
    scrollHeight : 0
  };

  constructor(public gs: GraphicsService) {}

  ngOnInit() {
    this.populateTimeline();
  }

  populateTimeline() {
    this.gs
        .getTimeline()
        .subscribe(tl => this.timeline = tl);
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
}
