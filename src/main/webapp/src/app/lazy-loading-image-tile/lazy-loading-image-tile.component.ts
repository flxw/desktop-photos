import { Component, Input, OnInit, HostBinding } from '@angular/core';
import { GraphicsData } from '../graphics-data.object';
import { Tile } from '../tile';

@Component({
    selector: 'lazy-loading-image-tile',
    templateUrl: './lazy-loading-image-tile.component.html',
    styleUrls: ['./lazy-loading-image-tile.component.styl']
})
export class LazyLoadingImageTileComponent extends Tile implements OnInit {
    @Input() metadata : GraphicsData;
    defaultImage:string = 'https://www.placecage.com/1000/1000';
    url:string = "";

    ngOnInit() {
        let aspectRatio = this.metadata.width / this.metadata.height;
        this.width = aspectRatio * this.height;

        if (isNaN(this.width)) {
            this.width = 600;
        }

        this.url = "http://localhost:8080/graphics?id=" + this.metadata.id;
    }
}