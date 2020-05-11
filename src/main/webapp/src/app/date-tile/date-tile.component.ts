import { Component, OnInit, Input } from '@angular/core';
import { Tile } from '../tile';

@Component({
  selector: 'date-tile',
  templateUrl: './date-tile.component.html',
  styleUrls: ['./date-tile.component.styl']
})
export class DateTileComponent extends Tile implements OnInit {
  @Input() date:Date;
  
  constructor() {
    super();
  }

  ngOnInit(): void {
  }
}
