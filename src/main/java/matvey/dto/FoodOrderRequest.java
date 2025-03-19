package matvey.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
@Builder
public class FoodOrderRequest {
    @JsonProperty("list_key")
    private List<RTORequest> foodList;

}

