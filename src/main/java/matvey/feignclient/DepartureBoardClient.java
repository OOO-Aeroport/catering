package matvey.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "departure-board-client", url = "${departure-board-url}")
public interface DepartureBoardClient {
    @GetMapping("/dep-board/api/v1/time/timeout?timeout={modelTime}")
    void waitFor(@PathVariable Integer modelTime);
}
