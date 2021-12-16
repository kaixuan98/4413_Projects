import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, Subscriber, zip } from 'rxjs';

import { Product, Category } from '../models/product.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  constructor(private http: HttpClient) { }

  getCatalog() {
    return this.http.get<Category[]>(`/Catalog`); // From Project C
  }

  getCategoryById(id: number): Observable<Category> {
    return new Observable<Category>((subscriber) => {
      // Use getCatalog instead of direct API call to avoid differences
      // in implementation and requirements between Project C and E.
      this.getCatalog().subscribe((categories) => {
        const category = categories.find(c => c.id === id);
        if (category) {
          subscriber.next(category);
        } else {
          subscriber.error({ status: 404, statusText: 'NOT FOUND' });
        }
        subscriber.complete();
      });
    });
  }

  getProductById(id: string) {
    return this.http.get<Product>(`/api/products/${id}`);
  }

  getProductsByCategory(catId: number) {
    return this.http.get<Product[]>(`/api/products/category/${catId}`);
  }

  /**
   * Method 1: write this if the return list is a products( all the work is done in the server)
   */
  getRecommendation(){
    return this.http.get<Product[]>('/api/product/recommendation');
  }

  /*
  * Method 2: write this if the server return a list of strings
  */
  getProductsRecommendation(): Observable<Product[]> {
    const url = '/api/product/recommendationId';
    return new Observable<Product[]>( (subscriber) => {
      
      
      this.http.get<string[]>(url).subscribe ( (productIds) => {
        productIds.map( pid => {
          zip(this.getProductById(pid)).subscribe( product => {
            subscriber.next(product);
            subscriber.complete();
          })
        })
      })
    })
  }
}





