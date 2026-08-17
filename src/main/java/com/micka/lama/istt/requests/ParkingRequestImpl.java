package com.micka.lama.istt.requests;

import com.micka.lama.istt.entities.Parking;
import com.micka.lama.istt.repositories.IParkingRepository;
import com.mongodb.bulk.BulkWriteResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Parking request implementation.
 */
@Component
@RequiredArgsConstructor
public class ParkingRequestImpl implements IParkingRequest {

    /**
     * Parking repository.
     */
    private final IParkingRepository parkingRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GeoResult<Parking>> findParkingsNear(final double latitude, final double longitude, final double minDistance, final double maxDistance, final int limit) {
        return parkingRepository.findNearest(new Point(longitude, latitude), minDistance / 1000d, maxDistance / 1000d, limit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BulkWriteResult upsert(final List<Parking> parking) {
        return parkingRepository.upsert(parking);
    }

}