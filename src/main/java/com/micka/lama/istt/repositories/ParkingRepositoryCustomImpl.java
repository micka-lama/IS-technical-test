package com.micka.lama.istt.repositories;

import com.micka.lama.istt.entities.Parking;
import com.mongodb.bulk.BulkWriteResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

/**
 * Parking repository custom implementation.
 */
@RequiredArgsConstructor
public class ParkingRepositoryCustomImpl implements ParkingRepositoryCustom {

    /**
     * Mongo template.
     */
    private final MongoTemplate mongoTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<GeoResult<Parking>> findNearest(final Point point, final double minDistanceKm, final double maxDistanceKm, final int limit) {
        final NearQuery query = NearQuery.near(point)
                .minDistance(minDistanceKm, Metrics.KILOMETERS)
                .maxDistance(maxDistanceKm, Metrics.KILOMETERS)
                .limit(limit);

        return mongoTemplate.geoNear(query, Parking.class).getContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BulkWriteResult upsert(final List<Parking> parkings) {
        // Unordered execution order to prevent the termination of the upsert process in case of an error.
        final BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Parking.class);

        for (final Parking parking : parkings) {
            final Query query = new Query(
                    Criteria.where("externalId").is(parking.getExternalId())
                            .and("source").is(parking.getSource()));

            final Update update = new Update()
                    .set("location", parking.getLocation())
                    .set("availableSpots", parking.getAvailableSpots());

            bulkOps.upsert(query, update);
        }

        return bulkOps.execute();
    }

}
