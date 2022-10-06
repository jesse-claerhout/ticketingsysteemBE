package be.optis.opticketapi.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class HistoryEntryDTO {

    private HistoryEntryType type;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dateTime;
    private String handyman;
    private String newState;
    public enum HistoryEntryType {
        CREATED, STATE_CHANGE, HANDYMAN_COMMENT
    }
}
