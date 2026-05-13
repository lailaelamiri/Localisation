<?php

/**
 * logPoint.php — HTTP entry point.
 *
 * Accepts a POST request carrying GPS data and stores it via GpsPointRepository.
 *
 * Expected POST fields:
 *   lat           — latitude  (double)
 *   lng           — longitude (double)
 *   captured_at   — datetime  (YYYY-MM-DD HH:MM:SS)
 *   device_serial — device identifier string
 */

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(['status' => 'error', 'message' => 'Only POST requests are accepted.']);
    exit;
}

include_once 'repository/GpsPointRepository.php';

storePoint();

function storePoint() {
    $lat          = $_POST['lat']           ?? null;
    $lng          = $_POST['lng']           ?? null;
    $capturedAt   = $_POST['captured_at']   ?? null;
    $deviceSerial = $_POST['device_serial'] ?? null;

    // Basic validation
    if ($lat === null || $lng === null || $capturedAt === null || $deviceSerial === null) {
        http_response_code(400);
        echo json_encode([
            'status'  => 'error',
            'message' => 'Missing required fields: lat, lng, captured_at, device_serial'
        ]);
        return;
    }

    $repo  = new GpsPointRepository();
    $point = new GpsPoint(null, $lat, $lng, $capturedAt, $deviceSerial);

    $newId = $repo->save($point);

    echo json_encode([
        'status'   => 'success',
        'message'  => 'GPS point saved successfully.',
        'record_id' => $newId
    ]);
}
?>
