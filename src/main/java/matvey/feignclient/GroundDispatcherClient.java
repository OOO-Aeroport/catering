package matvey.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ground-dispatcher-client", url = "${ground-dispatcher-url}")
public interface GroundDispatcherClient {

    @GetMapping("/dispatcher/garage/{vehicle_type}")
    boolean getPermissionToGetOutOfGarage(@PathVariable String vehicle_type);

    @GetMapping("/dispatcher/{current_point}/luggage") // Маршрут от гаража до склада питания
    List<Integer> getRouteFromGarageToLuggage(@PathVariable Integer current_point);

    @GetMapping("/dispatcher/plane/{current_point}/luggage") // Маршрут от самолета до склада питания
    List<Integer> getRouteFromPlaneToLuggage(@PathVariable Integer current_point);

    @GetMapping("/dispatcher/point/{current_point}/{target}")
    boolean getPermissionToNextPoint(@PathVariable Integer current_point, @PathVariable Integer target);

    @GetMapping("/dispatcher/plane/{current_point}/{plane_id}") // Маршрут от гаража до самолета
    List<Integer> getRouteFromGarageToPlane(@PathVariable Integer current_point, @PathVariable Integer plane_id);

    @GetMapping("dispatcher/plane/luggage/{current_point}/{planeId}") // Маршрут от склада питания до самолета
    List<Integer> getRouteFromLuggageToPlane(@PathVariable Integer current_point, @PathVariable Integer planeId);

    @GetMapping("/dispatcher/{current_point}/garage")
    List<Integer> getRouteToGarage(@PathVariable Integer current_point);

    @DeleteMapping("/dispatcher/garage/free/{endpoint}")
    void deleteCarFromMap(@PathVariable Integer endpoint);
}
