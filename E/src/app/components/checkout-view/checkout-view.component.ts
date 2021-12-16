import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { ShippingService } from 'src/app/services/shipping.service';
import { Location } from '@angular/common';
import { CartService } from 'src/app/services/cart.service';
import { LocationService } from '../../services/location.service';
// import { Coordinate } from 'src/app/models/location.model';

@Component({
  selector: 'app-checkout-view',
  templateUrl: './checkout-view.component.html',
  styles: [
  ]
})
export class CheckoutViewComponent implements OnInit {

  // coordinate!: Coordinate;
  // time : number = 0; 
  // destination: string = "2275+Bayview+Ave"; 

  constructor(
    private location: Location,
    private title: Title,
    private router: Router,
    private shipping: ShippingService,
    private api: CartService,
    private locApi: LocationService,
  ) { }
  
  ngOnInit(): void {
    this.title.setTitle('Checkout');
    if (!this.shipping.shippingAddress) {
      this.router.navigate(['/shipTo'], { replaceUrl: true });
    }
    else{
      // this.locApi.getLocation(this.shipping.shippingAddress.streetAddress).subscribe( (coordinate) =>{
      //   console.log(coordinate);
      //   this.coordinate = coordinate;
      // })
      // this.locApi.getDroneTime(this.shipping.shippingAddress.streetAddress, this.destination).subscribe( (time) => {
      //   this.time=time; 
      // })
    }
  }
  
  get shippingAddress() {
    return this.shipping.shippingAddress;
  }

  goBack() {
    this.location.back();
  }

  checkoutCart(){
    if (!this.shipping.shippingAddress) {
      this.router.navigate(['/shipTo'], { replaceUrl: true });
    }else{
      this.api.checkoutCart(this.shipping.shippingAddress).subscribe( ()=> {
        this.router.navigate(['/finish']);
      })
    }
    
  }

}
