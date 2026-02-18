# Jewelry Promo Scratch Card System

This repository contains:
1. **Node.js + Express backend** connected to **MS Access** using `node-adodb`.
2. **Android (Kotlin) tablet app** with registration, scratch game, and success screens.

## 1) Database Schema (MS Access)
Run `backend/CustomersSchema.sql` in your Access DB to create table `Customers`.

## 2) Backend Setup
```bash
cd backend
cp .env.example .env
# update ACCESS_DB_PATH for your Windows machine
npm install
npm start
```

### API Endpoints
- `POST /api/register`
- `POST /api/update-discount`

## 3) Android Setup
1. Open `android-app` in Android Studio.
2. Ensure JDK 17 is selected.
3. Update backend IP in `NetworkModule.kt`.
4. Build and run on tablet.

## Flow Summary
- Customer registers with name/mobile/DOB.
- Backend calculates age and generates 3 discount chances.
- Customer can lock current chance or risk next chance (max 3 attempts).
- Final selected discount is written back to backend.
