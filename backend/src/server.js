require('dotenv').config();

const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');
const { connection: db, dbPath } = require('./db');
const {
  calculateAge,
  generateDiscountChances,
  toAccessDateLiteral,
  escapeSqlText,
} = require('./utils');

const app = express();
const PORT = process.env.PORT || 4000;

app.use(helmet());
app.use(cors());
app.use(express.json());
app.use(morgan('combined'));

async function ensureCustomersTable() {
  try {
    await db.query('SELECT TOP 1 ID FROM Customers');
    console.log('Customers table check: already exists.');
    return;
  } catch (error) {
    console.warn('Customers table check: table not found, creating it now.');
  }

  const createTableSql = `
    CREATE TABLE Customers (
      ID AUTOINCREMENT PRIMARY KEY,
      FullName TEXT(255),
      MobileNumber TEXT(50),
      DOB DATETIME,
      CalculatedAge INTEGER,
      FinalDiscountPercent INTEGER,
      VisitDate DATETIME DEFAULT Now()
    )
  `;

  await db.execute(createTableSql);
  console.log('Customers table created successfully.');
}

async function validateDatabaseOnStartup() {
  console.log(`Using Access DB path: ${dbPath}`);
  await ensureCustomersTable();
}

app.get('/health', (_req, res) => {
  res.status(200).json({ ok: true, service: 'jewel-promo-backend' });
});

app.post('/api/register', async (req, res) => {
  try {
    const { name, mobile, dob } = req.body;

    if (!name || !mobile || !dob) {
      return res.status(400).json({ error: 'name, mobile, and dob are required.' });
    }

    const age = calculateAge(dob);

    if (age < 18) {
      return res.status(400).json({ error: 'Customer must be at least 18 years old.' });
    }

    const chances = generateDiscountChances(age);

    const escapedName = escapeSqlText(name.trim());
    const escapedMobile = escapeSqlText(mobile.trim());
    const dobLiteral = toAccessDateLiteral(dob);

    const insertSql = `
      INSERT INTO Customers (FullName, MobileNumber, DOB, CalculatedAge, FinalDiscountPercent, VisitDate)
      VALUES ('${escapedName}', '${escapedMobile}', ${dobLiteral}, ${age}, 0, Now())
    `;

    await db.execute(insertSql);

    const identityRows = await db.query('SELECT @@IDENTITY AS NewID');
    const userId = identityRows?.[0]?.NewID;

    if (!userId) {
      return res.status(500).json({ error: 'Unable to fetch inserted user ID.' });
    }

    return res.status(201).json({
      userId,
      age,
      chances,
    });
  } catch (error) {
    console.error('Register endpoint failed:', error);
    return res.status(500).json({ error: 'Internal server error while registering customer.' });
  }
});

app.post('/api/update-discount', async (req, res) => {
  try {
    const { userId, finalDiscount } = req.body;

    if (!userId && userId !== 0) {
      return res.status(400).json({ error: 'userId is required.' });
    }

    if (typeof finalDiscount !== 'number') {
      return res.status(400).json({ error: 'finalDiscount must be a number.' });
    }

    const customerRows = await db.query(`SELECT ID, CalculatedAge FROM Customers WHERE ID = ${Number(userId)}`);

    if (!customerRows || customerRows.length === 0) {
      return res.status(404).json({ error: 'Customer not found.' });
    }

    const customer = customerRows[0];
    const maxAllowed = Math.min(customer.CalculatedAge, 50);

    if (finalDiscount < 0 || finalDiscount > maxAllowed) {
      return res.status(400).json({
        error: `finalDiscount must be between 0 and ${maxAllowed}.`,
      });
    }

    const updateSql = `
      UPDATE Customers
      SET FinalDiscountPercent = ${finalDiscount}
      WHERE ID = ${Number(userId)}
    `;

    await db.execute(updateSql);

    return res.status(200).json({ message: 'Final discount updated successfully.' });
  } catch (error) {
    console.error('Update-discount endpoint failed:', error);
    return res.status(500).json({ error: 'Internal server error while updating discount.' });
  }
});

app.use((_req, res) => {
  res.status(404).json({ error: 'Route not found.' });
});

async function startServer() {
  try {
    await validateDatabaseOnStartup();
    app.listen(PORT, () => {
      console.log(`Server running on port ${PORT}`);
    });
  } catch (error) {
    console.error('Server startup failed while validating database:', error);
    process.exit(1);
  }
}

startServer();
