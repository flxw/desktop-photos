import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-lazy-loading-image',
  templateUrl: './lazy-loading-image.component.html',
  styleUrls: ['./lazy-loading-image.component.styl']
})
export class LazyLoadingImageComponent {
  @Input() src: string;
  defaultImage = 'https://www.placecage.com/1000/1000';
}