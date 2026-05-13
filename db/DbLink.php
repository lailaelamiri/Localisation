<?php

/**
 * DbLink — opens and exposes a PDO connection to the tracking_sys database.
 * All other classes that need the database go through this single gateway.
 */
class DbLink {

    private $pdo;

    public function __construct() {
        $host     = 'localhost';
        $database = 'tracking_sys';
        $user     = 'root';
        $pass     = '';

        try {
            $dsn = "mysql:host=$host;dbname=$database;charset=utf8mb4";

            $this->pdo = new PDO($dsn, $user, $pass);
            $this->pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            $this->pdo->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);

        } catch (Exception $ex) {
            die(json_encode([
                'status'  => 'error',
                'message' => 'Database connection failed: ' . $ex->getMessage()
            ]));
        }
    }

    /**
     * Returns the active PDO instance.
     * @return PDO
     */
    public function getPdo() {
        return $this->pdo;
    }
}
?>
