import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, zip } from 'rxjs';

import { CartItem,      } from '../models/cart.model';
import { Product        } from '../models/product.model';
import { ProductService } from './product.service';
import { Shipping } from '../models/shipping.model';

@Injectable({
  providedIn: 'root'
})
export class CartService {

  constructor(
    private http: HttpClient,
    private api: ProductService
  ) { }

  getCartItem() {
    return this.http.get<CartItem[]>('/api/cart');
  }

  /**
   * This combines the cart items with the corresponding Product objects. For
   * each cart item, retrieves the associated Product object and assigns it to
   * the CartItem's optional product property.
   *
   * @returns
   *    Items in the Cart via an Observable.
   */
  getCart() {
    return new Observable<CartItem[]>((subscriber) => {
      this.getCartItem().subscribe((items) => {
        zip(items.map(item => this.api.getProductById(item.id))).subscribe((products) => {
          subscriber.next(items.map((item, i) => {
            item.product = products[i];
            return item;
          }));
          subscriber.complete();
        });
      });
    });
  }

  /**
   * This is the overloaded version of updateCart. Takes either a CartItem or a
   * Product and sends a POST request with the optional given qty to the backend
   * service or 1 if a Product is given as the item argument or the CartItem's
   * existing qty if a CartItem is given as the item argument.
   *
   * @param item  A cart item or the product to add to the cart.
   * @param qty   The quantity to update the item in the cart to.
   * @returns     The updated array of items within the Cart via an Observable.
   */
  updateCart(item: CartItem | Product, qty?: number): Observable<CartItem[]> {
    const url = '/api/cart/update';
    if ((item as Product).name) { // item must be a Product
      return this.http.post<CartItem[]>(url, { id: item.id, qty: qty ?? 1 });
    } else { // item is a CartItem
      return this.http.post<CartItem[]>(url, { id: item.id, qty: qty ?? item.qty });
    }
  }

  /**
   * Add the given Product to the cart with the given qty or 1.
   *
   * @param product The product to add to the cart.
   * @param qty     The quantity of the Product to add to the Cart
   * @returns       The updated array of items within the Cart via an Observable.
   */
  addToCart(product: Product, qty: number = 1) {
    return new Observable<CartItem[]>((subscriber) => {
      this.getCartItem().subscribe((items) => {
        const item    = items.find(it => it.id === product.id);
        const updater = this.updateCart(item ?? product, item ? item.qty + qty : qty);
        updater.subscribe((updated) => {
          subscriber.next(updated);
          subscriber.complete();
        });
      });
    });
  }

  /*
  * Post request to empty the cart when user clicks checkout
  */
  checkoutCart(shipping: Shipping){
    return this.http.post<Shipping>('/api/cart/checkout', shipping); 
  }
}