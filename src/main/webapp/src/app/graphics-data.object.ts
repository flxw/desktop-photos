export class GraphicsData {
  public id: number;
  public height: number;
  public width: number;
  public timeStamp:Date;

  public static getScaledWidthForHeight(height:number, d:GraphicsData):number {
    return Math.round(d.width * (height/d.height));
  }
}