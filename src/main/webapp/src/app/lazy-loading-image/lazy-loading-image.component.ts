import { Component, Input, OnInit, HostBinding } from '@angular/core';
import { GraphicsData } from '../graphics-data.object';

@Component({
    selector: 'lazy-loading-image',
    templateUrl: './lazy-loading-image.component.html',
    styleUrls: ['./lazy-loading-image.component.styl']
})
export class LazyLoadingImageComponent implements OnInit {
    @Input() metadata : GraphicsData;
    width:number = 0;
    height:number = 200;
    defaultImage:string = 'https://www.placecage.com/1000/1000';
    url:string = "";

    ngOnInit() {
        let aspectRatio = this.metadata.width / this.metadata.height;
        this.width = aspectRatio * this.height;
        console.log(this.metadata)

        if (isNaN(this.width)) {
            this.width = 600;
        }

        this.url = "http://localhost:8080/graphics?id=" + this.metadata.id;
    }
}