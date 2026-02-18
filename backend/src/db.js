const ADODB = require('node-adodb');

const DEFAULT_DB_PATH = 'C:\\JewelryPromoDB\\jewelry_promo.accdb';
const dbPath = process.env.ACCESS_DB_PATH || DEFAULT_DB_PATH;

// ACE provider works for both .accdb and .mdb on Windows when Access Database Engine is installed.
const connection = ADODB.open(`Provider=Microsoft.ACE.OLEDB.12.0;Data Source=${dbPath};Persist Security Info=False;`);

module.exports = {
  connection,
  dbPath,
};
