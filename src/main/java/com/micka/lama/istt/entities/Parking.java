package com.micka.lama.istt.entities;

import com.micka.lama.istt.scheduler.tasks.partners.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Parking entity.
 */
@Document("parking")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(def = "{'externalId': 1, 'source': 1}", unique = true)
public class Parking {

    /**
     * Identifier.
     */
    @Id
    private ObjectId id;

    /**
     * External identifier.
     * <p>
     * This identifier is from the partner.
     */
    private String externalId;

    /**
     * Source of the data (from which partner the entity has been created).
     */
    private DataSourceType source;

    /**
     * Coordinates of the parking.
     */
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    /**
     * Available spots.
     */
    private int availableSpots;

}
