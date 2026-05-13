<?php

include_once 'contract/IRepository.php';
include_once 'entity/GpsPoint.php';
include_once 'db/DbLink.php';

/**
 * GpsPointRepository — handles all SQL operations for the gps_record table.
 * Implements IRepository so the calling code is never tied to a specific DB driver.
 */
class GpsPointRepository implements IRepository {

    private $db;

    public function __construct() {
        $link     = new DbLink();
        $this->db = $link->getPdo();
    }

    // ---------------------------------------------------------------
    // INSERT
    // ---------------------------------------------------------------

    /**
     * Persists a new GpsPoint to the database.
     * @param  GpsPoint $entity
     * @return int       The auto-generated record_id
     */
    public function save($entity) {
        $sql = "INSERT INTO gps_record (lat, lng, captured_at, device_serial)
                VALUES (:lat, :lng, :captured_at, :device_serial)";

        $stmt = $this->db->prepare($sql);
        $stmt->execute([
            ':lat'           => $entity->getLat(),
            ':lng'           => $entity->getLng(),
            ':captured_at'   => $entity->getCapturedAt(),
            ':device_serial' => $entity->getDeviceSerial(),
        ]);

        return (int) $this->db->lastInsertId();
    }

    // ---------------------------------------------------------------
    // UPDATE  (stub — extend later)
    // ---------------------------------------------------------------

    public function modify($entity) {
        // TODO: implement when needed
    }

    // ---------------------------------------------------------------
    // DELETE  (stub — extend later)
    // ---------------------------------------------------------------

    public function remove($entity) {
        // TODO: implement when needed
    }

    // ---------------------------------------------------------------
    // SELECT BY ID  (stub — extend later)
    // ---------------------------------------------------------------

    public function findById($id) {
        // TODO: implement when needed
    }

    // ---------------------------------------------------------------
    // SELECT ALL  (stub — extend later)
    // ---------------------------------------------------------------

    public function findAll() {
        // TODO: implement when needed
    }
}
?>
