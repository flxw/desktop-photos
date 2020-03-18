import { Component, OnInit } from '@angular/core';
import { GraphicsService } from './graphics.service';
import { LazyLoadingImageComponent } from './lazy-loading-image/lazy-loading-image.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.styl']
})
export class AppComponent {
  graphicsDb = {};

  constructor(public gs: GraphicsService) {
    this.graphicsDb = gs.getGraphicsDb().subscribe(res => this.graphicsDb = res);
  }
}
