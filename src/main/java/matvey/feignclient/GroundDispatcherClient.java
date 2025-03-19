package matvey.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Queue;

@FeignClient(name = "ground-dispatcher-client", url = "${ground-dispatcher-url}")
public interface GroundDispatcherClient {

    @GetMapping("/dispatcher/garage/{vehicle_type}")
    public boolean getPermissionToGetOutOfGarage(@PathVariable String vehicle_type);

    @GetMapping("/dispatcher/{current_point}/luggage")
    public List<Integer> getRouteToLuggage(@PathVariable Integer current_point);

    @GetMapping("/dispatcher/point/{current_point}/{target}")
    public boolean getPermissionToNextPoint(@PathVariable Integer current_point, @PathVariable Integer target);

    @GetMapping("/dispatcher/plane/{current_point}/{plane_id}")
    public List<Integer> getRouteToPlane(@PathVariable Integer current_point, @PathVariable Integer plane_id);

    @GetMapping("/dispatcher/{current_point}/garage")
    public List<Integer> getRouteToGarage(@PathVariable Integer current_point);

    @DeleteMapping("/dispatcher/garage/free/{endpoint}")
    public void deleteCarFromMap(@PathVariable Integer endpoint);
}
