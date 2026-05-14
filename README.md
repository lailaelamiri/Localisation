# GeoLogger — Real-Time GPS Tracking System

A full-stack GPS tracking application that captures device coordinates on Android and transmits them in real time to a PHP/MySQL backend over HTTP.

---

## Demo

### Live Tracking in Action


https://github.com/user-attachments/assets/abecf350-fac0-4f97-906d-5b919ed18c97


### Database — Recorded GPS Entries

<img width="1147" height="94" alt="Screenshot 2026-05-13 231230" src="https://github.com/user-attachments/assets/ef1c173b-f2e1-4f50-9ad6-aa60b4b5be96" />

---

## Architecture Overview

```
Android Device
     |
     |  HTTP POST (Volley)
     v
PHP Entry Point  -->  Repository Layer  -->  MySQL Database
(logPoint.php)        (GpsPointRepository)   (tracking_sys)
```

The project follows a layered architecture separating concerns across four distinct layers: the HTTP entry point, the repository interface contract, the data access implementation, and the database.

---

## Project Structure

```
Localisation/
│
├── app/                                        # Android application
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/geolog/
│       │   └── TrackingActivity.java           # Main activity
│       └── res/
│           ├── layout/activity_tracking.xml    # UI layout
│           └── values/strings.xml              # String resources
│
├── contract/
│   └── IRepository.php                         # CRUD interface
│
├── db/
│   └── DbLink.php                              # PDO connection handler
│
├── entity/
│   └── GpsPoint.php                            # GPS data model
│
├── repository/
│   └── GpsPointRepository.php                  # Data access layer
│
└── logPoint.php                                # HTTP POST entry point
```

---

## Tech Stack

| Component       | Technology                         |
|-----------------|------------------------------------|
| Mobile client   | Android (Java), min SDK 24         |
| HTTP library    | Volley 1.2.1                       |
| Backend         | PHP 8, PDO                         |
| Database        | MySQL via XAMPP                    |
| Architecture    | Repository pattern, OOP            |
| Version control | Git, GitHub                        |

---

## Database Setup

Open phpMyAdmin or a MySQL terminal and execute:

```sql
CREATE DATABASE IF NOT EXISTS tracking_sys
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE tracking_sys;

CREATE TABLE gps_record (
    record_id     INT AUTO_INCREMENT PRIMARY KEY,
    lat           DOUBLE NOT NULL,
    lng           DOUBLE NOT NULL,
    captured_at   DATETIME NOT NULL,
    device_serial VARCHAR(50) NOT NULL
);
```

---

## Server Setup

**Requirements:** XAMPP (Apache + MySQL)

1. Start Apache and MySQL from the XAMPP control panel.
2. Copy the PHP files into your web root:
   ```
   C:\xampp\htdocs\gps_tracker\
   ```
3. Confirm the server responds correctly:
   ```
   GET http://localhost/gps_tracker/logPoint.php
   ```
   Expected response:
   ```json
   { "status": "error", "message": "Only POST requests are accepted." }
   ```

---

## Android Setup

**Requirements:** Android Studio, API level 24 or higher

1. Clone the repository and open the project in Android Studio.
2. Set the server URL in `TrackingActivity.java`:

   ```java
   // Android Emulator
   private static final String SERVER_URL =
       "http://10.0.2.2/gps_tracker/logPoint.php";

   // Physical device — use your machine's local Wi-Fi IP
   private static final String SERVER_URL =
       "http://192.168.1.X/gps_tracker/logPoint.php";
   ```

3. Sync Gradle dependencies.
4. Run the application and accept location permissions when prompted.
5. Enable GPS on the device or configure a simulated location in the emulator.

---

## API Reference

**Endpoint:** `POST /gps_tracker/logPoint.php`

| Field           | Type     | Description                               |
|-----------------|----------|-------------------------------------------|
| `lat`           | double   | Latitude coordinate                       |
| `lng`           | double   | Longitude coordinate                      |
| `captured_at`   | datetime | Timestamp formatted as YYYY-MM-DD HH:MM:SS |
| `device_serial` | string   | Unique device identifier (ANDROID_ID)     |

**Success response:**
```json
{
  "status": "success",
  "message": "GPS point saved successfully.",
  "record_id": 4
}
```

**Error response:**
```json
{
  "status": "error",
  "message": "Only POST requests are accepted."
}
```

---

## Required Android Permissions

Declared in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
```

Cleartext HTTP traffic is explicitly permitted for local network communication:

```xml
<application android:usesCleartextTraffic="true" ... >
```

---

## GPS Update Parameters

| Parameter        | Value     | Description                                     |
|------------------|-----------|-------------------------------------------------|
| Min interval     | 60 000 ms | Minimum time between two location updates       |
| Min displacement | 150 m     | Minimum distance before triggering a new update |
| Provider         | GPS       | Uses hardware GPS, not network-based positioning|

---

## Testing Checklist

- [ ] Apache and MySQL are running in XAMPP
- [ ] `tracking_sys` database and `gps_record` table exist
- [ ] `logPoint.php` returns correct JSON on a GET request
- [ ] Android app successfully requests location permissions at launch
- [ ] GPS coordinates appear in the Current Snapshot card
- [ ] Server Status card confirms successful transmission
- [ ] New rows are visible in phpMyAdmin under `gps_record`

---

## Known Limitations

- `getDeviceId()` is blocked on Android 10 and above; the app uses `Settings.Secure.ANDROID_ID` as a stable alternative.
- The server URL must be updated manually before deployment on a physical device.
- The application does not persist location updates locally if the server is unreachable.

---

## Author

Laila Elamiri — GPS Tracking Lab, 2026
