export abstract class Tile {
  protected abstract type:string;
  public static margin:number = 5;
  public static initialWidth:number = 100;
  public static initialHeight:number = 200;
  
  public width:number = Tile.initialWidth;
  public height:number = Tile.initialHeight;

  public getType():string {
    return this.type;
  }

  public static getScaledWidthForHeight(height:number, d:Tile):number {
    return Math.round(d.width * (height/d.height)) + 2*this.margin;
  }
}