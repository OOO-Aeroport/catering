package matvey.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
@Builder
public class UnloadOrderRequest {

    /**
     * id заказа
     */
    @JsonProperty("order_id")
    private Integer orderId;


    /**
     * id борта
     */
    @JsonProperty("plane_id")
    private Integer planeId;

}
