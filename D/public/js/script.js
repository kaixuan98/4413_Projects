(function () {
  'use strict';

  /**
   * Supplant does variable substitution on the string. It scans through the
   * string looking for expressions enclosed in {{ }} braces. If an expression
   * is found, use it as a key on the object, and if the key has a string value
   * or number value, it is substituted for the bracket expression and it repeats.
   * This is useful for automatically fixing URLs or for templating HTML.
   * Based on: http://www.crockford.com/javascript/remedial.html
   *
   * @param {string} str
   * @param {object} object
   * @returns {string}
   */
  function supplant(str, object) {
    return str.replace(
      /\{\{[ ]*([^{} ]*)[ ]*\}\}/g,
      function (a, b) {
        let r = object[b];
        return typeof r === 'string' || typeof r === 'number' ? r : a;
      }
    );
  }

  /**
   * Make an AJAX request with XHR. Returns a Promise.
   *
   * @param {string} url
   * @param {'GET'|'POST'|'PUT'|'HEAD'|'DELETE'} method
   * @param {*} data
   * @returns {Promise}
   */
  function doAjax(url, method, data) {
    const request = new XMLHttpRequest();                    // create the XHR request
    return new Promise(function (resolve, reject) {          // return it as a Promise
      request.onreadystatechange = function () {             // setup our listener to process compeleted requests
        if (request.readyState !== 4) return;                // only run if the request is complete
        if (request.status >= 200 && request.status < 300) { // process the response, when successful
          resolve(JSON.parse(request.responseText));
        } else { // when failed
          reject({
            status: request.status,
            statusText: request.statusText
          });
        }
      };
      request.open(method || 'GET', url, true);                       // setup our HTTP request
      if (data) {                                                     // when data is given...
        request.setRequestHeader("Content-type", "application/json"); // set the request content-type as JSON, and
        request.send(JSON.stringify(data));                           // send data as JSON in the request body.
      } else {
        request.send(); // send the request
      }
    });
  }

  /**
   * Invoke the route associated with the given URL hash. If no route matches,
   * redirects to the default route.
   *
   * @param {string} hash
   */
  function invokeRoute(hash) {
    if (hash.startsWith('#/')) {
      const [ route, ...params ] = hash.substr(2).split('/');
      if (routes[route]) {
        routes[route](...params);
      } else {
        routes.index();
      }
    } else {
      routes.index();
    }
  }

  /**
   * Actually change the content of the view. Replaces the innerHTML in the
   * #page element, and then updates the history, either by pushing a new state
   * or replacing the existing state if this is a redirection.
   *
   * @param {string} url
   * @param {string} title
   * @param {string} html
   * @param {boolean} isRedirect
   */
  function changeView(url, title, html, isRedirect = false) {
    const data = { url, title, html };
    document.title = title;
    document.getElementById('page').innerHTML = html;
    if (window.location.hash !== url) {
      if (isRedirect) {
        window.history.replaceState(data, '', url);
      } else {
        window.history.pushState(data, '', url);
      }
    }
  }

  const GetCatalog            = 'http://localhost:44130/Catalog'; // From Project C
  const GetProductsByCategory = '/api/products/category/:id';
  const GetProductById        = '/api/products/:id';
  const GetCart               = '/api/cart';
  const UpdateCart            = '/api/cart/update';

  var prod = {
    id : '',
    qty : '',
  }

  const templates = {};
  const routes = {
    catalog(isRedirect) {
      /**
        TODO:
          1. GET all of the categories via the Catalog API
          2. Populate the templates:
              - category-card for each category
              - catalog-page for the page
          3. Show the view at the hash '#/catalog'
              - set the title to 'Catalog'
              - include the isRedirect as the last argument of changeView().
          4. Add a 'click' event listener to each div.category-card to show the category view.
      */
      doAjax(GetCatalog).then((categories) => {
          const content  = categories.map(cat => supplant(templates['category-card'], cat )).join('');
          const html = supplant(templates['catalog-page'], {content})

          changeView('#/catalog', 'Catalog', html, isRedirect);

          function getCategory(catId){
            routes.category(catId);
          }

          var cards = document.getElementsByClassName('category-card');
          for(let i = 0 ; i < cards.length; i++){
            cards[i].addEventListener('click', () => getCategory(cards[i].getAttribute('data-id')));
          }

      })

    },
    category(id) {
      /**
        TODO:
          1. GET all of the categories via the Catalog API
              - Find the category that matches the given ID.
              - If category does not exist, redirect view to routes.index().
          2. GET all the products that belong that category via ProductsByCategory API.
          3. Populate the templates:
              - category-card for the title
              - product-card for each product
              - category-page for the page
          4. Show the view at '#/category/:id' where :id is the category's ID.
              - set the title to the category's name
          5. Add a 'click' event listener to the div.category-card to return to the Catalog view.
      */


     // Question: Can I use Async and await here? you can, just make it cleaner
     // Question: is the content variable, needs to have the same name? Why? // content: any-name
     doAjax(GetCatalog).then( (categories) => {
      var result = categories.find(element => (element.id == id));
      if(!result){ 
        routes.index();
      }else{
        doAjax(GetProductsByCategory.replace(':id', result.id)).then( (prods) => {
          const title = supplant(templates['category-card'], result);
          const content = prods.map(p => supplant(templates['product-card'], p)).join('');
          const html = supplant(templates['category-page'], {title, content});
   
          changeView(`#/category/${result.id}`, `${result.name}`, html);
          
          function backToCatalog(){
            routes.catalog();
          }
          var card = document.getElementsByClassName('category-card');
          card[0].addEventListener('click', backToCatalog );
        });
      }
     });

     
    },
    products(id) {
      /**
        TODO:
          1. GET the product that matches the given ID via the ProductById API
              - If the product does not exist, redirect view to routes.index().
          2. GET all of the categories via the Catalog API
              - Find the category that matches the product's ID.  ---->>  product's category id??
          3. GET all the products that belong that category via ProductsByCategory API.
          4. Populate the templates:
              - category-card for the title
              - product-card for each product
              - category-page for the page
              - product-page for the product
                - fix the product's cost to be show 2 decimals. (use the toFixed method) --> Question? 
          5. Show the view at '#/products/:id' where :id is the product's ID.
              - set the title to the product's name
              - the HTML is the populated category-page template
          6. Add a 'click' event listener to the div.category-card to return to the Catalog view.
          7. Replace the content of 'product-info' (id) element:
              - with the populated product-page template
          8. Find the product-card <a> tag with the data-id that matches the given ID:
              - append the CSS class 'active' (remember to prefix it with a whitespace)
              - set the initial focus on that element (invoke the element's focus method). (fixes UI bug in list view)
          9. Add a 'click' event listener to the 'add-to-cart' button that:
              - GET all of items in the cart via Cart API
                - Find the item in the cart that matches the product, increment the qty by 1
                - If the item does not exist, let the qty be 1.
              - POST to UpdateCart API with the ID and new qty. (pass as the data argument to doAjax)
              - Show the Cart page.
      */
     doAjax(GetProductById.replace(':id', id)).then( (product) => {
       if(!product){
        //  routes.index();
       }else{
        doAjax(GetCatalog).then( (categories) => {
          var category = categories.find(element => (element.id == product.catId))
          if(!category){ 
            routes.index();
          }else{
            doAjax(GetProductsByCategory.replace(':id', category.id)).then( (prods) => {
              const title = supplant(templates['category-card'], category);     // first column 
              const productCard = prods.map(p => supplant(templates['product-card'], p)).join('');  // second column
              const productDetail = supplant(templates['product-page'], product); // third column
              const html = supplant(templates['category-page'], {title, content: productCard}); // place each product card to the middle column
                        
              changeView(`#/products/${product.id}`, `${product.name}`, html);

              // append the product info to the last column
              document.getElementById('product-info').innerHTML = productDetail; // this need to be here to get the element
              var productList = document.getElementById('product-list');
              for (let i = 0; i < productList.children.length; i++) {
                if(productList.children[i].getAttribute('data-id') == id ){
                  productList.children[i].classList.add('active');
                  productList.children[i].focus()
                  break
                }
              }


              doAjax(GetCart).then( cItems => {
                var addBtn = document.querySelector('.add-to-cart');
                addBtn.addEventListener('click', function addToCart(){
                  var product_id = addBtn.getAttribute('data-id');
                  var index = cItems.findIndex(product => product.id === product_id); 
                  if(index  >= 0 ){ 
                    prod.id = product_id;
                    prod.qty = cItems[index].qty + 1 ;  
                  }else{                                                 
                    prod.id = product_id;
                    prod.qty = 1 ; 
                  }
                  // because I need to pass in a data 
                  doAjax(UpdateCart, "POST", prod).then(
                    success => {
                      // console.log(success)
                      routes.cart();
                    }
                  );
                  })
              });
              
              // click event for the title 
              var card = document.querySelector('.category-card');
              card.addEventListener('click', function backToCatalog(){
                routes.catalog();
              });
            });
          }
        });
        }
      });
    },
    async cart() {
      /**
        TODO:
          1. GET all of items in the cart via Cart API
          2. For each item in the cart, GET the product via the ProductById API
              - Use Promise.all to synchronise all the requests
          3. When all of the request are resolved, populate the templates:
              - cart-row for each item
                - fix the product's cost to be show 2 decimals. (use the toFixed method)
              - cart-page for the page
          4. Show the view at the hash '#/catalog'
              - set the title to 'Cart'
          5. Add a 'click' event listener to each 'update-cart' button that:
              - POST to UpdateCart API with the ID and new qty in the adjacent input. (its ID is `qty-${id}`)
                - pass POST data to doAjax as the data argument
                - to get the value of an input, use the value attribute on the element.
              - Refresh the Cart view.
              - Popup a message 'Cart Updated.'. (use alert)
      */
      const cart = await doAjax(GetCart)
      const products = await Promise.all(cart.map( product => doAjax(GetProductById.replace(':id', product.id))));

      products.forEach( p => { p.cost = p.cost.toFixed(2)});

      const content = cart.map( (c, i) => supplant(supplant(templates['cart-row'], c), products[i])).join('');     
      

      const html = supplant(templates['cart-page'], {content});

      changeView('#/cart', 'Cart', html);

      var updateBtns = document.querySelectorAll('.update-cart');
      updateBtns.forEach( (button) => {
        button.addEventListener('click', function cartUpdate(){ 
          var input = document.querySelectorAll(`#qty-${button.dataset.id}`);
          var newQty = input[0].value;
          prod.id = button.dataset.id; 
          prod.qty = parseInt(newQty);
          doAjax(UpdateCart, "POST", prod).then( success => { 
            routes.cart();
            alert('Cart Updated.');
          })
        })
      })


    },
    index() {
      routes.catalog(true);
    }
  };

  document.querySelectorAll('script[type="text/x-template"]').forEach((el) => { templates[el.id] = el.innerText; });
  window.addEventListener('hashchange', () => invokeRoute(window.location.hash));
  window.addEventListener('popstate', (ev) => {
    if (ev.state) {
      document.title = ev.state.title;
      invokeRoute(ev.state.url);
    }
  });
  window.addEventListener('keyup', (ev) => {
    if (ev.key === 'Enter') {
      document.activeElement.click();
    }
  });
  invokeRoute(window.location.hash);
}());
