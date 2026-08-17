package com.micka.lama.istt.repositories;

import com.micka.lama.istt.entities.Parking;
import com.mongodb.bulk.BulkWriteResult;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Point;

import java.util.List;

/**
 * Parking repository custom interface.
 */
public interface ParkingRepositoryCustom {

    /**
     * Find the nearest parking from the given point.
     *
     * @param point         The reference point.
     * @param minDistanceKm The minimum distance before starting the research. The distance is in kilometers.
     * @param maxDistanceKm The maximum distance before stopping the research. The distance is in kilometers.
     * @param limit         The maximum items to return.
     * @return The parking found.
     */
    List<GeoResult<Parking>> findNearest(Point point, double minDistanceKm, double maxDistanceKm, int limit);

    /**
     * Upsert parking entities.
     *
     * @param parking The entities to upsert.
     * @return The bulk write result.
     */
    BulkWriteResult upsert(List<Parking> parking);
}
