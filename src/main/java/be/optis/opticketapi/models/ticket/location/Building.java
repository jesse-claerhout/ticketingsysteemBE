package be.optis.opticketapi.models.ticket.location;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Building {
    VELDKANT_33A("Veldkant 33A, Kontich"),
    VELDKANT_33B("Veldkant 33B, Kontich"),
    VELDKANT_35D("Veldkant 35D, Kontich"),
    VELDKANT_39("Veldkant 39, Kontich"),
    VELDKANT_4("Veldkant 4, Kontich"),
    GASTON_GEENSLAAN_11B4("Gaston Geenslaan 11/B4, Leuven"),
    BELLEVUE_5("Bellevue 5, Gent"),
    NIJVERHEIDSKAAI_3("Nijverheidskaai 3, Kortrijk");

    private final String address;

    Building(String address) {
        this.address = address;
    }

    public static Building fromFullAddress(String address) {
        var building = Arrays.stream(values())
                .filter(b -> b.getAddress().equals(address))
                .findFirst().orElse(null);
        if (building == null) throw new IllegalArgumentException("No building with this address");
        return (building);
    }
}
