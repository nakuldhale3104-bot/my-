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

### Backend startup DB behavior
- Default DB path is `C:\JewelryPromoDB\jewelry_promo.accdb` (can be overridden by `ACCESS_DB_PATH`).
- On server startup, backend checks whether `Customers` exists.
- If `Customers` is missing, backend automatically creates it before accepting requests.

## 3) Android Setup (Ready-to-build project)
```bash
cd android-app
# Open this folder in Android Studio Hedgehog+ / Iguana+
```

Project now includes:
- root Gradle project files (`settings.gradle`, root `build.gradle`, `gradle.properties`)
- app module Gradle config and `proguard-rules.pro`
- required Android resources (`themes.xml`, `colors.xml`, `strings.xml`)

Before building on your tablet environment:
1. Use **JDK 17** for Gradle.
2. Start backend and make sure tablet can access it.
3. Update `BASE_URL` in `android-app/app/build.gradle` (`buildConfigField`) to your backend LAN IP.
4. Sync Gradle, then run app module on the tablet.

## Flow Summary
- Customer registers with name/mobile/DOB.
- Backend calculates age and generates 3 discount chances.
- Customer can lock current chance or risk next chance (max 3 attempts).
- Final selected discount is written back to backend.
