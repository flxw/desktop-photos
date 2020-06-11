import { Tile } from './tile';

export class GraphicsTileData extends Tile {
  protected type:string = 'graphics-tile';
  public id: number;
  public index:number;
  public height: number;
  public width: number;
  public timeStamp:Date;
}