import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { GraphicsData } from './graphics-data.object';
import { Observable } from 'rxjs';
import { map, publishReplay, refCount } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class GraphicsService {
  timelineUrl = 'http://localhost:8080/api/v1/timeline';
  cachedResult:Observable<GraphicsData[]>;

    // TODO: distribute images manually into rows based on their width
    // listen for viewport width changes
    // what's the best practice for using services?

  constructor(private http: HttpClient) {}

  getTimeline() {
    if (this.cachedResult == null) {
      this.cachedResult = this.http.get<GraphicsData[]>(this.timelineUrl);
    }

    return this.cachedResult;
  }
}
