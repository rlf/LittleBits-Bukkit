package com.github.rlf.bitcloud.model;

/**
 * Created by R4zorax on 12/09/2016.
 */
public interface AbstractDB {
    /**
     * Loads the database from the backing storage.
     */
    void load();

    /**
     * Do an explicit save.
     */
    void save();
}
