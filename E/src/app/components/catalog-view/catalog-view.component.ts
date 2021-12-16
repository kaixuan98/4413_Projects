import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Category } from 'src/app/models/product.model';
import { ProductService } from 'src/app/services/product.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-catalog-view',
  templateUrl: './catalog-view.component.html',
  styles: [
  ]
})
export class CatalogViewComponent implements OnInit {
  categories : Category[] = [];


  constructor(private api : ProductService ,
              private title: Title,
              private route: Router) { }

  ngOnInit(): void {
    this.title.setTitle('Catalog');
    this.api.getCatalog().subscribe( categories => {
      this.categories = categories;
    })
  }

  changeView(targetUrl: string){
    this.route.navigate([targetUrl])
  }

}
