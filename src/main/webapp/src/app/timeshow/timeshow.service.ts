import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TimeshowService {
  private isTimeshowTriggered:boolean = false;
  private relativePosition:number = 0;
  private daterows:Date[] = [];

  constructor() { }

  setTimeshowTriggered(tsn:boolean) {
    this.isTimeshowTriggered = tsn;
  }

  getTimeshowTriggered() {
    return this.isTimeshowTriggered;
  }

  setRelativeTimeshowPosition(relativePosition:number) {
    this.relativePosition = relativePosition;
  }

  setDateRows(daterows:Date[]) {
    this.daterows = daterows;
  }

  getCurrentRowDate() {
    let n = Math.floor(this.daterows.length * this.relativePosition);
    return this.daterows[n];
  }
}
