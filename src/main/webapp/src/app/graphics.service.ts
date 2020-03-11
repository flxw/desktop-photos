import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class GraphicsService {
  db = {};
  timelineUrl = 'http://localhost:8080/api/v1/timeline';

  constructor(private http: HttpClient) {
  }

  getGraphicsDb() {
    return this.http.get(this.timelineUrl);
  }
}
