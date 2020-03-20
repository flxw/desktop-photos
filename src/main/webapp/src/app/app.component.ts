import { Component, OnInit } from '@angular/core';
import { GraphicsService } from './graphics.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.styl']
})
export class AppComponent implements OnInit{
  timeline: Map<Date, any>;

  constructor(public gs: GraphicsService) {}

  ngOnInit() {
    this.populateTimeline();
  }

  populateTimeline() {
    this.gs
        .getTimeline()
        .subscribe(tl => this.timeline = tl);
  }
}
