import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule }    from '@angular/common/http';
import {ScrollingModule} from '@angular/cdk/scrolling';
import { FlexLayoutModule } from '@angular/flex-layout';
import { LazyLoadImageModule } from 'ng-lazyload-image';
import { TimeScrollbarComponent } from './time-scrollbar/time-scrollbar.component';
import { TimeScrollbarAnchorComponent } from './time-scrollbar-anchor/time-scrollbar-anchor.component';

@NgModule({
  declarations: [
    AppComponent,
    TimeScrollbarComponent,
    TimeScrollbarAnchorComponent,
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
