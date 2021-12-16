import { Component, OnInit, ɵRender3ComponentRef } from '@angular/core';
import { CartItem } from 'src/app/models/cart.model';
import { Location } from '@angular/common';
import { CartService } from 'src/app/services/cart.service';
import { Router } from '@angular/router';
import { Product } from 'src/app/models/product.model';
import { ProductService } from 'src/app/services/product.service';

@Component({
  selector: 'app-cart-view',
  templateUrl: './cart-view.component.html' ,
  styles: [
  ]
})
export class CartViewComponent implements OnInit {
  
  cart: CartItem[] = [];
  isEmptyCart: boolean = true;
  recommendations: Product[] = [];

  constructor(private location: Location, 
              private api: CartService,
              private prodApi: ProductService ) { }

  ngOnInit(): void {
    this.api.getCart().subscribe( (items) => {
      if(items.length > 0){
        this.isEmptyCart = false; 
      }
    })
    
  }


  goBack() {
    this.location.back();
  }

  cartUpdate(cart: CartItem[]) {
    this.cart = cart;
    // this.recommendList();
    // check if the recommended item is not in the cart then push to the recommendation
    this.prodApi.getRecommendation().subscribe( (recommendations) => {
      this.recommendations = []; 
      for (let product of recommendations){
        if( !cart.find(item => item.id === product.id)){
          this.recommendations.push(product);
        }
      }
    })
  }

  recommendList(){
    // this.prodApi.getRecommendation().subscribe( (recomendations) => {
    //   this.recommendations = recomendations; 
    // })
    this.prodApi.getRecommendation().subscribe( (recommendations) => {
        this.recommendations = recommendations; 
    })
  }

}
