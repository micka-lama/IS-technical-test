package com.micka.lama.istt.repositories;

import com.micka.lama.istt.entities.Parking;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Parking repository interface.
 */
@Repository
public interface IParkingRepository extends MongoRepository<Parking, ObjectId>, ParkingRepositoryCustom {

}
