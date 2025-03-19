package matvey.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
@Builder
public class RTORequest {
    @JsonProperty("flight_id")
    private Integer flightId;

    @JsonProperty("quantity")
    private Integer quantity;
}
