import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TimeshowService {
  private isTimeshowTriggered:boolean = false;
  private relativePosition:number = 0;

  constructor() { }

  setTimeshowTriggered(tsn:boolean) {
    this.isTimeshowTriggered = tsn;
  }

  getTimeshowTriggered() {
    return this.isTimeshowTriggered;
  }
}
