const home    = require('os').homedir();
const dbfile  = '4413/pkg/sqlite/Models_R_US.db';
const dbpath  = require('path').join(home, ...dbfile.split('/'));
const sqlite3 = require('sqlite3').verbose();
const db      = new sqlite3.Database(dbpath);


const GET_PRODUCTS_WITH_CAT = 'SELECT * FROM Product WHERE catId = ?';
const GET_PRODUCTS_WITH_ID = 'SELECT * FROM Product WHERE id = ?';


module.exports = {
  getCategory: function (id, success, failure = console.log) {
    let statement = GET_PRODUCTS_WITH_CAT;

    db.all(statement, id, (err, rows) => {
        if(err == null){
          success(rows);
        }else{
          failure(err);
        }
    });
    },

    getProducts: function(id, success, failure = console.log){
      let statement = GET_PRODUCTS_WITH_ID;

      db.all(statement, id, (err, rows) => {
          if(err == null){
            success(rows);
          }else{
            failure(err);
          }
      });

    },

    getProductswithPromise: function(id){
      let statement = GET_PRODUCTS_WITH_ID;
      
      return new Promise( (success, failure) => {
        db.all(statement, id, (err, rows) => {
          if(err == null){
            success(rows[0]);
          }else{
            failure(err);
          }
        });
      })

    }
    
  
};