<?php

/**
 * GpsPoint — represents a single GPS snapshot from a tracked device.
 * Maps directly to one row in the `gps_record` table.
 */
class GpsPoint {

    private $pointId;
    private $lat;
    private $lng;
    private $capturedAt;
    private $deviceSerial;

    public function __construct($pointId, $lat, $lng, $capturedAt, $deviceSerial) {
        $this->pointId      = $pointId;
        $this->lat          = $lat;
        $this->lng          = $lng;
        $this->capturedAt   = $capturedAt;
        $this->deviceSerial = $deviceSerial;
    }

    // ---------- Getters ----------

    public function getPointId() {
        return $this->pointId;
    }

    public function getLat() {
        return $this->lat;
    }

    public function getLng() {
        return $this->lng;
    }

    public function getCapturedAt() {
        return $this->capturedAt;
    }

    public function getDeviceSerial() {
        return $this->deviceSerial;
    }

    // ---------- Setters ----------

    public function setPointId($pointId) {
        $this->pointId = $pointId;
    }

    public function setLat($lat) {
        $this->lat = $lat;
    }

    public function setLng($lng) {
        $this->lng = $lng;
    }

    public function setCapturedAt($capturedAt) {
        $this->capturedAt = $capturedAt;
    }

    public function setDeviceSerial($deviceSerial) {
        $this->deviceSerial = $deviceSerial;
    }
}
?>
