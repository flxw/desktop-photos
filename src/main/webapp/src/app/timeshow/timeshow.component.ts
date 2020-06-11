import { Component, OnInit } from '@angular/core';
import { TimeshowService } from './timeshow.service';

@Component({
  selector: 'timeshow',
  templateUrl: './timeshow.component.html',
  styleUrls: ['./timeshow.component.styl']
})
export class TimeshowComponent implements OnInit {
  constructor(public tss:TimeshowService) { }

  ngOnInit(): void {
  }
}
