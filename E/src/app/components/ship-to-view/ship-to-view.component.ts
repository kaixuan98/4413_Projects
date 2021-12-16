import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, ValidatorFn , Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { ShippingService } from '../../services/shipping.service'
import { Location} from '@angular/common';
import { Province, Shipping, ShippingConstants } from 'src/app/models/shipping.model';

@Component({
  selector: 'app-ship-to-view',
  templateUrl : './ship-to-view.components.html' ,
  styles: [
  ]
})
export class ShipToViewComponent implements OnInit {

  PostalCodeRegEx: RegExp =  /^[ABCEGHJKLMNPRSTVXY][0-9][ABCEGHJKLMNPRSTVWXYZ] ?[0-9][ABCEGHJKLMNPRSTVWXYZ][0-9]$/i;
  ZipCodeRegEx : RegExp  = /^[0-9]{5}(?:[-\s][0-9]{4})?$/;

  shippingAddress = this.fb.group({
    recipient:      ['', Validators.required],
    streetAddress:  ['', Validators.required],
    streetAddress2: [''],
    city:           ['',   Validators.required],
    province:       [null, Validators.required],
    postalCode:     ['',  [Validators.required, this.validatePostalOrZipCode.bind(this)]],
    // postalCode:     ['',   Validators.required],
    delivery:       ['Standard']
  });
  CanadianProvincesAndTerritories = ShippingConstants.CanadianProvincesAndTerritories;
  USStatesAndTerritories = ShippingConstants.USStatesAndTerritories;
  DeliveryMethods = ShippingConstants.DeliveryMethods; 
  formSubmitted: boolean = false; 
  
  

  constructor(
    private fb: FormBuilder,
    private shipping: ShippingService,
    private title : Title,
    private location: Location,
  ) { }

  ngOnInit(): void {
    this.title.setTitle('Ship To');
    if (this.shipping.shippingAddress) {
      this.shippingAddress.setValue(this.shipping.shippingAddress);
    }
    this.shippingAddress.get('province')!.valueChanges.subscribe((province?: Province) => {
      const postalCode = this.shippingAddress.get('postalCode')!;
      const validator  = this.getPostalCodeValidator(province);
      postalCode.setValidators([Validators.required, validator]);
      postalCode.updateValueAndValidity();
    });
    
  }

  onSubmit() {
    this.formSubmitted = true;
    if (this.shippingAddress.valid) {
      this.shipping.shippingAddress = this.shippingAddress.value; // More on this later
      this.goBack();
    }
  }

  goBack() {
    this.location.back();
  }

  getPostalCodeValidator(province?: Province) {
    if (!province) {
      return this.validatePostalOrZipCode.bind(this) as ValidatorFn;
    }
    return this.CanadianProvincesAndTerritories.includes(province)
        ? Validators.pattern(this.PostalCodeRegEx)
        : Validators.pattern(this.ZipCodeRegEx);
  }
  

  invalidInput(input: string): boolean {
    if (this.formSubmitted) { // boolean set to true when the submit is clicked
      return this.shippingAddress.get(input)!.invalid; // if there is nothing then invalid
    } else {
      return false;
    }
  }
  
  validInput(input: string): boolean {
    if (this.shippingAddress.touched || this.shippingAddress.dirty) {
      return this.shippingAddress.get(input)!.valid;
    } else {
      return false;
    }
  }

  validatePostalOrZipCode(fc: FormControl) {
    return (this.PostalCodeRegEx.test(fc.value) || this.ZipCodeRegEx.test(fc.value)) ? null : {
      validInput: {
        valid: false
      }
    };
  }

}
