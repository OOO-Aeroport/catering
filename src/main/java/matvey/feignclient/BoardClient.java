package matvey.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "board-client", url = "${board-url}")
public interface BoardClient {
    @GetMapping("/catering/{planeId}")
    void ReportFoodDeliveryCompletionToBoard(@PathVariable Integer planeId);

    @GetMapping("/catering_out/{planeId}")
    void ReportFoodUnloadCompletionToBoard(@PathVariable Integer planeId);
}
