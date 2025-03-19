package matvey.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoadOrderRequest {

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


    /**
     * требуемое количество питания
     */
    @JsonProperty("quantity")
    private Integer quantity;

}
