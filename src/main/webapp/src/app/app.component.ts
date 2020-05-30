import { Component, OnInit, AfterViewInit, ViewChild, AfterViewChecked, ElementRef, HostListener } from '@angular/core';
import { GraphicsService } from './graphics.service';
import { Tile } from './tile';
import { GraphicsData } from './graphics-data.object';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.styl']
})
export class AppComponent implements AfterViewInit {
  @ViewChild("container") container;
  sampleTile:Tile = new Tile();
  timelineRows = [];
  timelineElements:GraphicsData[] = [];
  timelineContainerWidth:number;

  lastResizeTime:number = null;
  isResizeTimeoutRunning:boolean = false;
  resizeTimeThreshold:number = 200;

  constructor(public graphicsService: GraphicsService) { }

  ngAfterViewInit() {
    this.graphicsService.getTimeline().subscribe((timelineElements:GraphicsData[]) => {
      this.timelineElements = timelineElements;
      this.recalculateTimelineRows();
    });
  }

  recalculateTimelineRows() {
    let rows = [];
    this.timelineContainerWidth = this.container.elementRef.nativeElement.clientWidth;

    for (let i = 0, rowWidth = 0, row = []; i < this.timelineElements.length; ++i) {
      let itemWidth = GraphicsData.getScaledWidthForHeight(Tile.initialHeight, this.timelineElements[i]);
      
      if (rowWidth + itemWidth < this.timelineContainerWidth) {
        rowWidth += itemWidth;
        row.push(this.timelineElements[i]);
      } else {
        rows.push(row);
        rowWidth = itemWidth;
        row = [this.timelineElements[i]];
      }
    }

    this.timelineRows = rows;
  }

  @HostListener("window:resize", ["$event"])
  onContainerResize(e:UIEvent) {
    this.lastResizeTime = new Date().getMilliseconds();

    if (this.isResizeTimeoutRunning === false) {
      this.isResizeTimeoutRunning = true;
      this.timeoutResizeEnd();
    }
  }
  
  handleResizeEnd() {
    let now = new Date().getMilliseconds();

    if ((now - this.lastResizeTime) < this.resizeTimeThreshold) {
      this.timeoutResizeEnd();
    } else {
      this.isResizeTimeoutRunning = false;
      let newWidth = this.container.elementRef.nativeElement.clientWidth;
      
      if (this.timelineContainerWidth != newWidth) {
        this.recalculateTimelineRows();
      }
    }
  }

  timeoutResizeEnd() {
    let thisCapsule = () => { this.handleResizeEnd() };
    setTimeout(thisCapsule, this.resizeTimeThreshold);
  }
}
