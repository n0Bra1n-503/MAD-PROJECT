const mysql = require('mysql2');
require('dotenv').config();

const pool = mysql.createPool({
    host: process.env.DB_HOST || 'localhost',
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || '',
    database: process.env.DB_NAME || 'Qmanage',
    port: process.env.DB_PORT || 3306,
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0,
    typeCast: function (field, next) {
        if (field.type === 'TINY' && field.length === 1) {
            return (field.string() === '1'); // 1 = true, 0 = false
        }
        return next();
    }
});

/*
pool.end((err) => {
  if (err) {
    console.log("Error closing pool");
  } else {
    console.log("All connections closed");
  }
});
*/


// Test the connection
pool.getConnection((err, connection) => {
    if (err) {
        console.error('❌ MySQL Connection Error:', err.message);
        if (err.code === 'ER_ACCESS_DENIED_ERROR') {
            console.error('   Check your DB_USER and DB_PASSWORD in .env');
        } else if (err.code === 'ER_BAD_DB_ERROR') {
            console.error('   Database "Qmanage" does not exist. Please create it first.');
        } else if (err.code === 'ECONNREFUSED') {
            console.error('   MySQL server is not running. Please start it.');
        }
        return;
    }
    console.log('✅ MySQL Connected Successfully');
    connection.release();
});

// Export promise-based pool for async/await usage
module.exports = pool.promise();
