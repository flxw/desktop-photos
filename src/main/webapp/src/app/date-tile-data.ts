import { Tile } from './tile';

export class DateTileData extends Tile {
  protected type:string = 'date-tile';
  public date:Date;
}