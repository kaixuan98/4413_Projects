import { Component, Input, OnInit , EventEmitter, Output} from '@angular/core';
import { Router } from '@angular/router';
import { CartItem } from 'src/app/models/cart.model';
import { CartService } from 'src/app/services/cart.service';

@Component({
  selector: 'app-cart-table',
  templateUrl: './cart-table.component.html',
  styles: [
  ]
})
export class CartTableComponent implements OnInit {

  @Input() updatable: boolean = false;
  @Output() onCartUpdate = new EventEmitter<CartItem[]>();

  items : CartItem[] = [];
  total : number = 0;

  constructor(
              private api: CartService,
              private router: Router) { }

  ngOnInit(): void {
    this.api.getCart().subscribe( items => {
      this.onCartUpdate.emit(this.items);
      this.items = items;
      items.map( (item, i) => {
        this.total += ((item.product!.cost) * item.qty); 
      })
    })

  }

  qtyAsNumber(item: CartItem) : void{
    item.qty = item.qty ? +item.qty : 0;
  }

  updateCart(item: CartItem) : void{
    this.api.updateCart(item).subscribe( () => {
      this.onCartUpdate.emit(this.items);
      this.router.navigate(['/']).then( () => {
        this.router.navigate(['/cart']);
      })
    })
  }

}
