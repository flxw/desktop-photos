import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule }    from '@angular/common/http';
import {ScrollingModule} from '@angular/cdk/scrolling';
import { FlexLayoutModule } from '@angular/flex-layout';
import { LazyLoadImageModule } from 'ng-lazyload-image';
import { LazyLoadingImageTileComponent } from './lazy-loading-image-tile/lazy-loading-image-tile.component';
import { TimeScrollbarComponent } from './time-scrollbar/time-scrollbar.component';
import { TimeScrollbarAnchorComponent } from './time-scrollbar-anchor/time-scrollbar-anchor.component';
import { DateTileComponent } from './date-tile/date-tile.component';

@NgModule({
  declarations: [
    AppComponent,
    LazyLoadingImageTileComponent,
    TimeScrollbarComponent,
    TimeScrollbarAnchorComponent,
    DateTileComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ScrollingModule,
    FlexLayoutModule,
    LazyLoadImageModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
