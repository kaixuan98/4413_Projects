import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, zip } from 'rxjs';

import { Shipping } from '../models/shipping.model';
import { Coordinate } from '../models/location.model';

@Injectable({
  providedIn: 'root'
})
export class LocationService {

  constructor(
    private http: HttpClient) { }

  // geoWeb Connection (might think of connecting tcp geo instead of the geoWeb)
  /**
   * @param Lat1
   * @param Lng1
   * @param lat2
   * @param Lng2
   * @return the distance of both coordinates
   * 
   */
    getDistance(){
      // or I can use GeoNode
    }





  // loc connection 
  /**
   * This service connects to the location serivces in Project B
   * that implemented with Tomcat. 
   * Will return the lat and long of a certain location
   * 
   * @param address
   * @return latitude and longtitude
   */
  getLocation(address: string){
    const url1 = `/B/loc?location=${address}`;
    return this.http.get<Coordinate>(url1);
  }


  // drone connection
  /**
   * This will get the estimated time of the drone services
   * @param source
   * @param destination
   * @returns the estimated time of the services
   */
  getDroneTime(source: string, destination: string){
    const url2 = `/B/drone?source=${source}&destination=${destination}`;
    return this.http.get<number>(url2);

  }
}
