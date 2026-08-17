package com.micka.lama.istt.requests;

import com.micka.lama.istt.entities.Parking;
import com.mongodb.bulk.BulkWriteResult;
import org.springframework.data.geo.GeoResult;

import java.util.List;

/**
 * Parking request interface.
 */
public interface IParkingRequest {

    /**
     * Retrieve the nearest parkings from a given position.
     *
     * @param latitude    The latitude of the position.
     * @param longitude   The longitude of the position.
     * @param minDistance The minimum distance (in meter) before starting the research.
     * @param maxDistance The maximum distance (in meter) before stopping the research.
     * @param limit       The maximum items returned.
     * @return The parkings found based on the criteria.
     */
    List<GeoResult<Parking>> findParkingsNear(double latitude, double longitude, double minDistance, double maxDistance, int limit);

    /**
     * Upsert parking entities.
     *
     * @param parking The entities to upsert.
     * @return The bulk write result.
     */
    BulkWriteResult upsert(List<Parking> parking);
}
