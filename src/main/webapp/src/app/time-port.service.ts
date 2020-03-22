import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TimePortService {
  private registeredIds: string[] = [];
  private idToAnchorMapping = {};
  private bodyHeight = 1;

  constructor() { }

  private getUniqueId(parts: number): string {
    const stringArr = [];
    for(let i = 0; i< parts; i++){
      // tslint:disable-next-line:no-bitwise
      const S4 = (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
      stringArr.push(S4);
    }
    return stringArr.join('-');
  }

  public registerId(): string {
    let id = this.getUniqueId(2);
    this.registeredIds.push(id);

    return id;
  }

  public unregisterId(id:string):void {
    this.registeredIds.forEach( (item, index) => {
      if(item === id) this.registeredIds.splice(index,1);
    });
  }

  public upsertAnchor(id:string, anchorText:string, offsetTop:number) {
    this.idToAnchorMapping[id] = {
      text: anchorText,
      top: offsetTop,
    }
  }

  public getAnchorList():object[] {
    return Object.values(this.idToAnchorMapping);
  }

  public setBodyHeight(h:number):void {
    this.bodyHeight = h;
  }

  public getBodyHeight():number {
    return this.bodyHeight;
  }

  public calculateRelativePosition(absoluteTopOffset:number):number {
    return absoluteTopOffset/this.bodyHeight * 100;
  }
}
