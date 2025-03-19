package matvey.controller;

import matvey.dto.FoodOrderRequest;
import matvey.dto.LoadOrderRequest;
import matvey.dto.UnloadOrderRequest;
import matvey.service.CateringCar;
import matvey.service.CateringService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CateringController {
    private final CateringService cateringService;

    public CateringController(CateringService cateringService) {
        this.cateringService = cateringService;
    }

    @PostMapping("/catering-service/api/v1/unload-order")
    public void processUnloadOrderFromUNO(@RequestBody UnloadOrderRequest unloadOrderRequest) throws InterruptedException {
        cateringService.processUnloadOrder(unloadOrderRequest.getOrderId(), unloadOrderRequest.getPlaneId());
    }

    @PostMapping("/catering-service/api/v1/load-order")
    public void processOrderFromUNO(@RequestBody LoadOrderRequest loadOrderRequest) throws InterruptedException {
        cateringService.processLoadOrder(loadOrderRequest.getOrderId(), loadOrderRequest.getPlaneId(), loadOrderRequest.getQuantity());
    }

    @Deprecated
    @PostMapping("/catering-service/api/v1/food-order")
    public void processOrderFromRegistration(@RequestBody FoodOrderRequest foodOrderRequest) throws InterruptedException {
        cateringService.legacyProcessFoodOrder(foodOrderRequest);
    }

    @GetMapping("/catering-service/get-all-cars")
    public List<CateringCar> getAllCars() {
//        System.out.println("Попал!");
        return cateringService.getAllCars();
    }

}
