package com.micka.lama.istt.mappers;

import com.micka.lama.istt.dtos.ParkingDTO;
import com.micka.lama.istt.entities.Parking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * {@link Parking} mapper.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IParkingMapper {

    /**
     * Convert the entity {@link Parking} to the DTO {@link ParkingDTO}.
     *
     * @param parking  The entity.
     * @param distance The distance computed, between the user and the parking.
     * @return The DTO.
     */
    @Mapping(target = "id", expression = "java(parking.getId() == null ? null : parking.getId().toString())")
    @Mapping(target = "latitude", expression = "java(parking.getLocation().getY())")
    @Mapping(target = "longitude", expression = "java(parking.getLocation().getX())")
    ParkingDTO toDto(Parking parking, double distance);

}
