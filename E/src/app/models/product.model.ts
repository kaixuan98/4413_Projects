export interface Category {
    id: number;
    name: string; 
}

export interface Product {
    id:   string;
    name: string;
    description: string;
    cost:  number;
    msrp:  number;
    qty:   number;
    catId: number;
    venId: number;
  }