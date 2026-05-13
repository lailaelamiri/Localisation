<?php

/**
 * IRepository — contract every data-access class must fulfill.
 * Defines the five standard CRUD operations.
 */
interface IRepository {

    /** Insert a new record and return the generated ID. */
    public function save($entity);

    /** Update an existing record. */
    public function modify($entity);

    /** Remove a record by its entity object. */
    public function remove($entity);

    /** Fetch a single record by its primary key. */
    public function findById($id);

    /** Fetch every record in the table. */
    public function findAll();
}
?>
