const home    = require('os').homedir();
const dbfile  = '4413/pkg/sqlite/Models_R_US.db';
const dbpath  = require('path').join(home, ...dbfile.split('/'));
const sqlite3 = require('sqlite3').verbose();
const db      = new sqlite3.Database(dbpath);


const GET_ALL = `SELECT id, name FROM Category`;


module.exports = {
  getCatalog: function (id, success, failure) {
    let statement = GET_ALL;
    let params = [];
    if( id != null ){
      params.push(id);
      statement += ' WHERE id= ?';
    }

    db.all(statement, params, (err, rows) => {
        if(err == null){
          success(rows);
        }else{
          failure(err);
        }
    });
    }
    
  
};