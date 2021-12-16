const express = require('express');
const session = require('express-session');
const dao     = require('./dao.js');
const path    = require('path');
const cors    = require('cors');
const fs      = require('fs');
const net     = require('net');

const app  = express();
const port = process.argv[2] || 3000;
const tcpPort = '42362'
const tcpHost = 'localhost'

app.enable('trust proxy');
app.use(session({
  name: 'project-D',
  secret: 'secret',
  resave: true,
  saveUninitialized: true,
  proxy: true
}));
app.use(express.json());
app.use(express.static(path.join(__dirname, 'public')));


app.use(cors());

app.param('id', function(req, res, next, id){
    req.id = id; 
    next();
})

// filtering the category id in product tables
app.get('/api/products/category/:id', function (req, res) {
    res.setHeader('Content-Type', 'application/json');
    let id = req.id; 
    dao.getCategory(id, function(rows){
        let response;
        if(rows.length > 0){
          response = rows;
          res.write(JSON.stringify(response));
        }else{
          res.status(404).send("Category Not Found");
        }
        
        res.end();
      });
});


app.get('/api/products/:id', function (req, res) { 
  res.setHeader('Content-Type', 'application/json');
  let id = req.id; 
  dao.getProducts(id, function(rows){
    let response;
    if(rows.length > 0){
      response = rows[0];
      res.write(JSON.stringify(response));
    }else{
      res.status(404).send("Product Not Found");
    }
    res.end();
  });
});

app.get('/api/cart', function (req, res) {
  res.setHeader('Content-Type', 'application/json');
  let response; 
  if(!req.session.cart){
    // no session cart in the session
    req.session.cart = req.session.cart || []; 
  }
  response = req.session.cart;
  res.write(JSON.stringify(response));
  res.end();

});


// just add and remove  and update things in the sessions
app.post('/api/cart/update', function (req, res) {
  res.setHeader('Content-Type', 'application/json');
  console.log("Before: " , req.session.cart , req.session.id);
  var inCart = (req.session.cart).findIndex(item => item.id === req.body.id)
  var id = req.body.id; 
  var qty = req.body.qty; 
  if (inCart >= 0 ){
    if(qty > 0 ){
      // update
      req.session.cart[inCart].qty = qty;
    }else{
      req.session.cart.splice(inCart, 1);
    }
    
  }else{
    let product = {};
    product.id = req.body.id;
    product.qty = req.body.qty;
    req.session.cart.push(product);
  }
  console.log('After: ' ,  req.session.cart,  req.session.id);
  res.write(JSON.stringify(req.session.cart));
  res.end()
});

app.post('/api/cart/checkout', function(req, res){
  var cartItems = JSON.stringify(req.session.cart); 
  var userShipping = JSON.stringify(req.body);
  var content = cartItems + userShipping;
  fs.writeFile('./orders.txt', content ,  err => {
    if (err) {
      console.error(err)
      return
    }
  }); 
  req.session.cart = []; 
  res.end()

})



let response = [];
// Method 1 :get the list of product recommendation
app.get('/api/product/recommendation', function(req, res){
  res.setHeader('Content-Type', 'application/json');
  let tcpRequest = '4'; 
  
  if(!req.session.recommendation){
    const client = net.createConnection(tcpPort, tcpHost, () => {console.log("Connected to Recommender!")}); // the tcp port and host
    client.on('error', (err) => { /* handle error */ });
    client.on('connect', () => {
      client.write(tcpRequest); // Send a request
      client.on('data', (data) => { 
        response.push(data.toString());
      });
      client.on('end', async () => { 
        const ids = response.join('').trim().split('\n');
        const products = await Promise.all( ids.map( pid => dao.getProductswithPromise(pid)));

        req.session.recommendation = products;
        res.json(products);
        res.end()
        client.end(); 
      });  
    });
  }else{ 
    res.json(req.session.recommendation);
    res.end()
  }
})

// Method 2: get the product recommendation id (list of strings) 
app.get('/api/product/recommendationId', function(req, res){
  res.setHeader('Content-Type', 'application/json');
  let tcpRequest = '4' ; 
  
  if(!req.session.recommendation){
    const client = net.createConnection(tcpPort, tcpHost, () => {console.log("Connected to Recommender!")}); // the tcp port and host
    client.on('error', (err) => { /* handle error */ });
    client.on('connect', () => {
      client.write(tcpRequest); // Send a request
      client.on('data', (data) => { 
        response.push(data.toString());
      });
      client.on('end', () => { 
        const ids = response.join('').trim().split('\n');
        req.session.recommendation = ids;
        res.json(ids);
        res.end()
        client.end(); 
      });  
    });
  }else{ 
    res.json(req.session.recommendation);
    res.end()
  }
})


const server = app.listen(port, function () {
  const host = server.address().address;
  const port = server.address().port;
  console.log(`server listening to ${host}:${port}`);
});