function calculateAge(dob) {
  const today = new Date();
  const birthDate = new Date(dob);

  if (Number.isNaN(birthDate.getTime())) {
    throw new Error('Invalid DOB format. Use ISO date format (YYYY-MM-DD).');
  }

  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDiff = today.getMonth() - birthDate.getMonth();

  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age -= 1;
  }

  return age;
}

function randomIntInclusive(min, max) {
  const safeMin = Math.ceil(min);
  const safeMax = Math.floor(max);
  return Math.floor(Math.random() * (safeMax - safeMin + 1)) + safeMin;
}

function generateDiscountChances(age) {
  const absoluteMax = Math.min(age, 50);

  const chance1Max = Math.max(5, Math.floor(Math.min(absoluteMax, age * 0.5)));
  const chance2Max = Math.max(5, Math.floor(Math.min(absoluteMax, age * 0.75)));
  const chance3Max = Math.max(5, Math.floor(absoluteMax));

  return [
    randomIntInclusive(5, chance1Max),
    randomIntInclusive(5, chance2Max),
    randomIntInclusive(5, chance3Max),
  ];
}

function toAccessDateLiteral(inputDate) {
  const d = new Date(inputDate);
  if (Number.isNaN(d.getTime())) {
    throw new Error('Invalid date supplied.');
  }

  const yyyy = d.getFullYear();
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const dd = String(d.getDate()).padStart(2, '0');
  return `#${yyyy}-${mm}-${dd}#`;
}

function escapeSqlText(value) {
  return String(value).replace(/'/g, "''");
}

module.exports = {
  calculateAge,
  generateDiscountChances,
  toAccessDateLiteral,
  escapeSqlText,
};
