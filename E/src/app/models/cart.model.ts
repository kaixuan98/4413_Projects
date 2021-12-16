import {Product} from './product.model';

export interface CartItem {
    id:  string;
    qty: number;
    product?: Product;
}