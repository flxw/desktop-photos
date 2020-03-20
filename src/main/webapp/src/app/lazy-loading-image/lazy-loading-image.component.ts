import { Component, Input, OnInit, HostBinding } from '@angular/core';
import { GraphicsData } from '../graphics-data.object';

@Component({
    selector: 'lazy-loading-image',
    templateUrl: './lazy-loading-image.component.html',
    styleUrls: ['./lazy-loading-image.component.styl']
})
export class LazyLoadingImageComponent implements OnInit {
    @Input() metadata : GraphicsData;

    @HostBinding("style.--width") width:number = 0;
    @HostBinding("style.--height") height:number = 400;

    defaultImage = 'https://www.placecage.com/1000/1000';
    url = "";

    ngOnInit() {
        let aspectRatio = this.metadata.width / this.metadata.height;
        this.width = aspectRatio * this.height;

        if (isNaN(this.width)) {
            this.width = 600;
        }

        this.url = "http://localhost:8080/graphics?id=" + this.metadata.fileName;
    }
}