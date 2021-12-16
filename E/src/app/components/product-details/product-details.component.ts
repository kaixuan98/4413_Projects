import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CartItem } from 'src/app/models/cart.model';
import { Product } from 'src/app/models/product.model';
import { CartService } from 'src/app/services/cart.service';

@Component({
  selector: 'app-product-details',
  templateUrl: './product-details.component.html',
  styles: [
  ]
})
export class ProductDetailsComponent implements OnInit {

  // this is an input from the parent (category-view)
  @Input() product !: Product;

  constructor(private api: CartService,
              private router: Router) { }

  ngOnInit(): void {

  }
  addToCart(item: Product): void{
    // let index: number = 0 ; 
    // let addProd : CartItem = {id :'', qty: 0 };
    // this.api.getCartItem().subscribe( (items) => {
    //   index = items.findIndex(prod => prod.id === item.id);
    //   if(index >= 0 ){
    //     addProd.id = item.id;
    //     addProd.qty = items[index].qty + 1 ; 
    //   }else{
    //     addProd.id = item.id;
    //     addProd.qty = 1; 
    //   }
    //   this.api.updateCart(addProd).subscribe(() => {
    //     this.router.navigate(['/cart']);
    //   }); 
    // })
    this.api.addToCart(item).subscribe( () => {
      this.router.navigate(['/cart']);
    })
    
  }


}
