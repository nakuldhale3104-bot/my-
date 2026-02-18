const ADODB = require('node-adodb');

const dbPath = process.env.ACCESS_DB_PATH;

if (!dbPath) {
  throw new Error('ACCESS_DB_PATH is missing. Set it in your environment variables.');
}

// ACE provider works for both .accdb and .mdb on Windows when Access Database Engine is installed.
const connection = ADODB.open(`Provider=Microsoft.ACE.OLEDB.12.0;Data Source=${dbPath};Persist Security Info=False;`);

module.exports = connection;
