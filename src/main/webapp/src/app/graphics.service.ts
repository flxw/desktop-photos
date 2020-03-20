import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { GraphicsData } from './graphics-data.object';

@Injectable({
  providedIn: 'root'
})
export class GraphicsService {
  timelineUrl = 'http://localhost:8080/api/v1/timeline';

  constructor(private http: HttpClient) {
  }

  getTimeline() {
    return this.http.get<Map<Date, any[]>>(this.timelineUrl);
  }
}
