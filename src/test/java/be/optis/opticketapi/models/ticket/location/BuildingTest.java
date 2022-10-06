package be.optis.opticketapi.models.ticket.location;

import be.optis.opticketapi.models.ticket.location.Building;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BuildingTest {

    @Test
    void fromFullAddress_ValidAddress_ReturnsBuilding() {
        var fullAddress = "Gaston Geenslaan 11/B4, Leuven";

        var building = Building.fromFullAddress(fullAddress);

        assertEquals(Building.GASTON_GEENSLAAN_11B4, building);
    }

    @Test
    void fromFullAddress_InvalidAddress_ThrowsIllegalArgumentException() {
        var fullAddress = "Gaston Geenslaan, Leuven";

        assertThrows(IllegalArgumentException.class, () -> Building.fromFullAddress(fullAddress));
    }
}
