import { Injectable } from '@angular/core';
import { Shipping } from '../models/shipping.model';

@Injectable({
  providedIn: 'root'
})
export class ShippingService {
  shippingAddress?: Shipping;
}
