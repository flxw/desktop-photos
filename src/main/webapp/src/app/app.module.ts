import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule }    from '@angular/common/http';
import { ScrollingModule } from '@angular/cdk/scrolling';
import { TimeshowtriggerDirective } from './timeshow/timeshowtrigger.directive';
import { TimeshowComponent } from './timeshow/timeshow.component';

@NgModule({
  declarations: [
    AppComponent,
    TimeshowtriggerDirective,
    TimeshowComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ScrollingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
