import { Component, OnInit, AfterViewInit, ViewChild, AfterViewChecked, ElementRef } from '@angular/core';
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

  constructor(public graphicsService: GraphicsService) {
  }

  ngAfterViewInit(): void {
    //let that = this;
    this.graphicsService.getTimeline().subscribe((timelineElements:GraphicsData[]) => {
      let rows = [];
      const viewWidth = this.container.elementRef.nativeElement.clientWidth;
  
      for (let i = 0, rowWidth = 0, row = []; i < timelineElements.length; ++i) {
        let itemWidth = GraphicsData.getScaledWidthForHeight(Tile.initialHeight, timelineElements[i]);
        
        if (rowWidth + itemWidth < viewWidth) {
          rowWidth += itemWidth;
          row.push(timelineElements[i]);
        } else {
          rows.push(row);
          rowWidth = itemWidth;
          row = [timelineElements[i]];
        }
      }
  
      this.timelineRows = rows;
    });
  }
}
