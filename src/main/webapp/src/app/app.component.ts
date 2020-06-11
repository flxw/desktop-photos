import { Component, OnInit, AfterViewInit, ViewChild, AfterViewChecked, ElementRef, HostListener } from '@angular/core';
import { GraphicsService } from './graphics.service';
import { Tile } from './tile';
import { GraphicsTileData } from './graphics-tile-data';
import { DateTileData } from './date-tile-data';
import { TimeshowService } from './timeshow/timeshow.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.styl']
})
export class AppComponent implements AfterViewInit {
  @ViewChild("container") container;
  timelineRows = [];
  timelineElements:Tile[] = [];
  timelineContainerWidth:number;
  
  tileHeight:number = Tile.initialHeight;
  rowHeight:number = Tile.initialHeight + 2*Tile.margin;

  lastResizeTime:number = null;
  isResizeTimeoutRunning:boolean = false;
  resizeTimeThreshold:number = 200;

  constructor(public graphicsService: GraphicsService,
              public timeshowService: TimeshowService) { }

  ngAfterViewInit() {
    this.graphicsService.getTimeline().subscribe((timelineMap:object) => {
      let timelineDates = Object.keys(timelineMap);

      timelineDates.forEach(timelineDate => {
        let dateTileData = new DateTileData();
        dateTileData.date = new Date(timelineDate);
        this.timelineElements.push(dateTileData)

        timelineMap[timelineDate].forEach(element => {
          let graphicsTileData = new GraphicsTileData();

          graphicsTileData.height = element.height;
          graphicsTileData.width = element.width;
          graphicsTileData.id = element.id;
          graphicsTileData.timeStamp = element.timeStamp;

          this.timelineElements.push(graphicsTileData)
        });
      });

      this.recalculateTimelineRows();
    });
  }

  recalculateTimelineRows() {
    let rows = [];
    let daterows:Date[] = [];
    this.timelineContainerWidth = this.container.elementRef.nativeElement.clientWidth;

    for (let i = 0, rowWidth = 0, nrow = 0, row = []; i < this.timelineElements.length; ++i) {
      let elem:Tile = this.timelineElements[i]
      let itemWidth = Tile.getScaledWidthForHeight(Tile.initialHeight, elem);

      if (rowWidth + itemWidth < this.timelineContainerWidth) {
        rowWidth += itemWidth;
        row.push(elem);
      } else {
        rows.push(row);
        rowWidth = itemWidth;
        row = [elem];
        nrow = nrow + 1;
      }

      if (elem.getType() == 'date-tile' && daterows.length < nrow) {
        let dateTileElem:DateTileData = elem as DateTileData;
        daterows.push(dateTileElem.date);
      }
    }

    this.timelineRows = rows;
    this.timeshowService.setDateRows(daterows);
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

  @HostListener("window:resize", ["$event"])
  onContainerResize(e:UIEvent) {
    this.lastResizeTime = new Date().getMilliseconds();

    if (this.isResizeTimeoutRunning === false) {
      this.isResizeTimeoutRunning = true;
      this.timeoutResizeEnd();
    }
  }
}
