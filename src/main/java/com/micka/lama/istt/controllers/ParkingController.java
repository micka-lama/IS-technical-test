package com.micka.lama.istt.controllers;

import com.micka.lama.istt.definitions.ParkingControllerApiPath;
import com.micka.lama.istt.dtos.ParkingDTO;
import com.micka.lama.istt.services.IParkingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Parking controller.
 */
@RestController
@Validated
@RequestMapping(ParkingControllerApiPath.PARKING_CONTROLLER_ROOT_PATH)
@RequiredArgsConstructor
public class ParkingController {

    /**
     * Parking service.
     */
    private final IParkingService parkingService;

    /**
     * Retrieve the nearest parkings.
     *
     * @param longitude   The longitude of the user.
     * @param latitude    The latitude of the user.
     * @param minDistance The minimum distance (in meter) before starting the research.
     * @param maxDistance The maximum distance (in meter) before stopping the research.
     * @param limit       The maximum items returned.
     * @return Response containing the parkings found based on the criteria.
     */
    @GetMapping(ParkingControllerApiPath.NEAREST_PATH)
    @Operation(description = "Retrieve the nearest parkings from the user position.")
    @Parameter(name = "longitude", description = "Longitude of the user", required = true, example = "0.0")
    @Parameter(name = "latitude", description = "Latitude of the user", required = true, example = "0.0")
    @Parameter(name = "minDistance", description = "The minimum distance (in meter) before starting to research", example = "0")
    @Parameter(name = "maxDistance", description = "The maximum distance (in meter) before stopping the research", example = "5000")
    @Parameter(name = "limit", description = "The maximum items returned", example = "50")
    public ResponseEntity<List<ParkingDTO>> getNearestParkings(
            @RequestParam @Min(-180) @Max(180) final double longitude,
            @RequestParam @Min(-90) @Max(90) final double latitude,
            @RequestParam(defaultValue = "0") @Min(0) final double minDistance,
            @RequestParam(defaultValue = "5000") @Min(0) final double maxDistance,
            @RequestParam(defaultValue = "50") @Min(1) @Max(50) final int limit) {
        return ResponseEntity.ok(parkingService.findNearestParkings(latitude, longitude, minDistance, maxDistance, limit));
    }

}
