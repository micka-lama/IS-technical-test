package com.micka.lama.istt.definitions;

import com.micka.lama.istt.controllers.ParkingController;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Constants about the paths used by {@link ParkingController}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParkingControllerApiPath {

    /**
     * Root of the controller.
     */
    public static final String PARKING_CONTROLLER_ROOT_PATH = "/parkings";

    /**
     * Endpoint to get the nearest parkings.
     */
    public static final String NEAREST_PATH = "/nearest";

}
