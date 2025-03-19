package matvey.service;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
@EqualsAndHashCode(of = "planeId")
@Getter
@Setter
public class CateringCar {

    private final Integer FOOD_CAPACITY = 40;
    private Integer currentFoodQuantity;
    private Integer orderId;
    private Integer planeId;
    private Integer foodNeeded;
    private Integer currentPoint;
    private Queue<Integer> route = new LinkedList<>();
    private Integer proceedingToPointFails;
    public CateringCar() {
        // Для теста. После теста удалить.
        this.foodNeeded = 52;
    }

    public void setRoutePoints(List<Integer> route) {
        this.route.clear();
        this.route.addAll(route);
        System.out.print("Маршрут:");
        for (Integer el : this.route) {
            System.out.print(" " + el);
        }
        System.out.println();
    }

    public void incrementProceedingToPointFails() {
        this.proceedingToPointFails += 1;
    }
    public void loadFoodToCar() {
        this.currentFoodQuantity = Math.min(FOOD_CAPACITY, foodNeeded);
    }
    public void loadFoodToPlane() {
        setFoodNeeded(foodNeeded - currentFoodQuantity);
        setCurrentFoodQuantity(0);
    }
}
