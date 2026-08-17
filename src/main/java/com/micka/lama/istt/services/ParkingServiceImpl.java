package com.micka.lama.istt.services;

import com.micka.lama.istt.dtos.ParkingDTO;
import com.micka.lama.istt.mappers.IParkingMapper;
import com.micka.lama.istt.requests.IParkingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Parking service implementation.
 */
@Service
@RequiredArgsConstructor
public class ParkingServiceImpl implements IParkingService {

    /**
     * Parking request.
     */
    private final IParkingRequest parkingRequest;

    /**
     * Parking mapper.
     */
    private final IParkingMapper parkingMapper;

    /**
     * {@inheritDoc}
     */
    public List<ParkingDTO> findNearestParkings(final double latitude, final double longitude,
                                                final double minDistance, final double maxDistance,
                                                final int limit) {
        return parkingRequest.findParkingsNear(latitude, longitude, minDistance, maxDistance, limit).stream()
                .map(geoResult -> parkingMapper.toDto(geoResult.getContent(), geoResult.getDistance().getValue() * 1000))
                .toList();
    }

}