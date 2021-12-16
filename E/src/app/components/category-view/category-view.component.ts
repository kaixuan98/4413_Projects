import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute , Router} from '@angular/router';
import { ProductService } from 'src/app/services/product.service';
import { Category, Product } from 'src/app/models/product.model';
import { Title } from '@angular/platform-browser';


@Component({
  selector: 'app-category-view',
  templateUrl: './category-view.component.html' ,
  styles: [
  ]
})
export class CategoryViewComponent implements OnInit {
  products : Product[] = []; 
  @Input() category !: Category; 
  @Input() selected !: Product; 
  @Input() product !: Product;

  constructor( private api: ProductService,
               private router : Router , 
               private route : ActivatedRoute,
               private title : Title) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      if (params.has('catId')) {
        this.showCategory(+params.get('catId')!);
      } else if (params.has('prodId')) {
        this.showProduct(params.get('prodId')!); // assign product to this.selected
      } else {
        this.router.navigate(['/']);
      }
    });
  }


  showCategory(catId: number) {
    this.api.getCategoryById(catId).subscribe({
      next: (category) => {
        this.category = category; 
        this.api.getProductsByCategory(category.id).subscribe( products => {
          this.products = products;
        })
      },
      error: (error) => {
        this.router.navigate(['/']);
      }
    });
  }

  showProduct(prodId: string) {
    this.api.getProductById(prodId).subscribe({
      next: (product) => {
        this.selected = product;
        this.showCategory(product.catId); // need to show list even only getting the product
      },
      error: (error) => {
        this.router.navigate(['/']);
      }
    });
  }

}
