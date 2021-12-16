import { Component, Input, OnInit, Output , EventEmitter} from '@angular/core';
import { Router} from '@angular/router';
import { Category } from 'src/app/models/product.model'; // type 

@Component({
  selector: 'app-category-card',
  templateUrl: './category-card.component.html',
  styles: []
})
export class CategoryCardComponent implements OnInit {
  @Input() category!: Category;
  @Input() isBackButton: boolean = false;
  @Output() cardClicked = new EventEmitter();

  urlTarget!: string;

  constructor( private router : Router) { }

  ngOnInit(): void {
    if (this.isBackButton) {
      this.urlTarget = '/';
    } else {
      this.urlTarget = '/category/' + this.category.id;
    }
  }

  // the click event trigger this function
  onClick(){
    this.cardClicked.emit(this.urlTarget);
  }
}


