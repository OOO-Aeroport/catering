package matvey.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "uno-client", url = "${uno-url}")
public interface UNOClient {
    @PostMapping("/uno/api/v1/order/successReport/{orderId}/catering-unload")
    void reportUnloadOrderCompletionToUNO(@PathVariable Integer orderId);

    @PostMapping("/uno/api/v1/order/successReport/{orderId}/catering-load")
    void reportLoadOrderCompletionToUNO(@PathVariable Integer orderId);
}
