package com.example.geolog;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TrackingActivity extends AppCompatActivity {

    // ── Fields ───────────────────────────────────────────────────────────────

    private double snapshotLat;
    private double snapshotLng;
    private double snapshotAlt;
    private float  snapshotAccuracy;

    private TextView     tvSnapshot;
    private TextView     tvServerStatus;
    private RequestQueue httpQueue;

    private static final String SERVER_URL =
            "http://10.0.2.2/gps_tracker/logPoint.php";

    private static final int   PERM_REQUEST_CODE = 42;
    private static final long  MIN_TIME_MS       = 60_000L;
    private static final float MIN_DISTANCE_M    = 150f;

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        tvSnapshot     = findViewById(R.id.tvSnapshot);
        tvServerStatus = findViewById(R.id.tvServerStatus);
        httpQueue      = Volley.newRequestQueue(getApplicationContext());

        if (!hasLocationPermissions()) {
            requestLocationPermissions();
            return;
        }

        startListening();
    }

    // ── Permissions ───────────────────────────────────────────────────────────

    private boolean hasLocationPermissions() {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)   == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                PERM_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[]    grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERM_REQUEST_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        } else {
            Toast.makeText(this,
                    "Location permission is required.",
                    Toast.LENGTH_LONG).show();
        }
    }

    // ── GPS Listener ──────────────────────────────────────────────────────────

    @SuppressWarnings("MissingPermission")
    private void startListening() {
        LocationManager gpsManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        gpsManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_MS,
                MIN_DISTANCE_M,
                new LocationListener() {

                    @Override
                    public void onLocationChanged(Location fix) {
                        snapshotLat      = fix.getLatitude();
                        snapshotLng      = fix.getLongitude();
                        snapshotAlt      = fix.getAltitude();
                        snapshotAccuracy = fix.getAccuracy();

                        String display =
                                "📍 Snapshot recorded\n" +
                                        "Lat: "      + snapshotLat      + "\n" +
                                        "Lng: "      + snapshotLng      + "\n" +
                                        "Alt: "      + snapshotAlt      + " m\n" +
                                        "Accuracy: ±" + snapshotAccuracy + " m";

                        tvSnapshot.setText(display);
                        dispatchSnapshot(snapshotLat, snapshotLng);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        String label;
                        switch (status) {
                            case LocationProvider.OUT_OF_SERVICE:
                                label = "OUT_OF_SERVICE";          break;
                            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                                label = "TEMPORARILY_UNAVAILABLE"; break;
                            default:
                                label = "AVAILABLE";               break;
                        }
                        Toast.makeText(getApplicationContext(),
                                "Source \"" + provider + "\" → " + label,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Toast.makeText(getApplicationContext(),
                                "Signal source \"" + provider + "\" is now active",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Toast.makeText(getApplicationContext(),
                                "Signal source \"" + provider + "\" has gone offline",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    // ── HTTP POST ─────────────────────────────────────────────────────────────

    private void dispatchSnapshot(final double lat, final double lng) {

        StringRequest post = new StringRequest(
                Request.Method.POST,
                SERVER_URL,

                response -> {
                    tvServerStatus.setText("✓ " + response);
                },

                error -> {
                    tvServerStatus.setText("✗ Transmission failed — check IP/server.");
                    Toast.makeText(getApplicationContext(),
                            "Network error: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SimpleDateFormat sdf =
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                Map<String, String> fields = new HashMap<>();
                fields.put("lat",           String.valueOf(lat));
                fields.put("lng",           String.valueOf(lng));
                fields.put("captured_at",   sdf.format(new Date()));
                fields.put("device_serial", resolveDeviceSerial());
                return fields;
            }
        };

        httpQueue.add(post);
    }

    // ── Device ID ─────────────────────────────────────────────────────────────

    /**
     * Returns a stable unique device identifier that works on all
     * Android versions without any special permission.
     */
    private String resolveDeviceSerial() {
        String serial = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        return (serial != null && !serial.isEmpty()) ? serial : "DEVICE_FALLBACK";
    }
}