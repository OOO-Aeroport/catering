package matvey.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "board-client", url = "${board-url}")
public interface BoardClient {
    @GetMapping("/catering/{planeId}")
    public void ReportFoodDeliveryCompletionToBoard(@PathVariable Integer planeId);

    @GetMapping("/catering_out/{planeId}")
    public void ReportFoodUnloadCompletionToBoard(@PathVariable Integer planeId);
}
