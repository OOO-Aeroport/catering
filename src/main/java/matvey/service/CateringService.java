package matvey.service;

import matvey.dto.FoodOrderRequest;
import matvey.dto.RTORequest;
import matvey.feignclient.BoardClient;
import matvey.feignclient.DepartureBoardClient;
import matvey.feignclient.GroundDispatcherClient;
import matvey.feignclient.UNOClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CateringService {
    private final ArrayList<CateringCar> cars;
    private final UNOClient unoClient;
    private final BoardClient boardClient;
    private final GroundDispatcherClient groundDispatcherClient;
    private final DepartureBoardClient departureBoardClient;

    @Autowired
    public CateringService(UNOClient unoClient, BoardClient boardClient, GroundDispatcherClient groundDispatcherClient, DepartureBoardClient departureBoardClient) {
        this.unoClient = unoClient;
        this.boardClient = boardClient;
        this.groundDispatcherClient = groundDispatcherClient;
        this.departureBoardClient = departureBoardClient;
        this.cars = new ArrayList<>();
    }
    public void processUnloadOrder(Integer orderId, Integer planeId) throws InterruptedException {
        System.out.println("Получен заказ от УНО на разгрузку пищевых отходов. orderId: " + orderId + ", planeId: " + planeId);

        // Создаем автомобиль, обслуживающий этот самолет
        CateringCar car = new CateringCar();
        car.setOrderId(orderId);
        car.setPlaneId(planeId);
        cars.add(car);

        serveUnloadOrder(car);
    }
    public void serveUnloadOrder(CateringCar car) throws InterruptedException {
        leaveGarage(car);

        takeANap(70);

        System.out.println("planeId: " + car.getPlaneId() + ". Нужно выгрузить мусор из самолета.");
        // Устанавливаем маршрут до самолета, чтобы забрать мусор
        car.setRoutePoints(groundDispatcherClient.getRouteFromGarageToPlane(car.getCurrentPoint(), car.getPlaneId()));
        followTheRoute(car, "garageToPlane");

        takeANap(300);

        // Устанавливаем маршрут до склада с питанием, чтобы выгрузить пищевые отходы
        car.setRoutePoints(groundDispatcherClient.getRouteFromPlaneToLuggage(car.getCurrentPoint()));
        followTheRoute(car, "planeToLuggage");
        System.out.println("planeId: " + car.getPlaneId() + ". Пищевые отходы выгружены.");

//        // Сообщаем самолету, что выгрузка пищевых отходов полностью завершена
//        boardClient.ReportFoodUnloadCompletionToBoard(car.getPlaneId());
//        System.out.println("planeId: " + car.getPlaneId() + ". Отправлено уведомление самолету о завершении выгрузки пищевых отходов.");
//
//        // Сообщаем УНО, что заказ на разгрузку пищевых отходов полностью выполнен
//        unoClient.reportUnloadOrderCompletionToUNO(car.getOrderId());
//        System.out.println("planeId: " + car.getPlaneId() + ". Отправлено уведомление о завершении заказа в УНО.");

        takeANap(300);

        System.out.println("planeId: " + car.getPlaneId() + ". Заказ выполнен. Нужно ехать к гаражу.");
        // Устанавливаем маршрут до гаража, чтобы поставить машину в гараж
        car.setRoutePoints(groundDispatcherClient.getRouteToGarage(car.getCurrentPoint()));
        followTheRoute(car, "garage");

        takeANap(210);

        // Уведомляем Ground Dispatcher о том, что машинка заехала в гараж (и должна быть удалена с карты)
        groundDispatcherClient.deleteCarFromMap(car.getCurrentPoint());

        // Удаление инстанса машинки
        cars.remove(car);
        System.out.println("Машина заехала в гараж");
    }
    public void processLoadOrder(Integer orderId, Integer planeId, Integer quantity) throws InterruptedException {
        System.out.println("Получен заказ от УНО на загрузку питания. orderId: " + orderId + ", planeId: " + planeId + ", quantity: " + quantity);

        // Создаем автомобиль, обслуживающий этот самолет
        CateringCar car = new CateringCar();
        car.setOrderId(orderId);
        car.setPlaneId(planeId);
        car.setFoodNeeded(quantity);
        cars.add(car);

        serveLoadOrder(car);
    }
    public void serveLoadOrder(CateringCar car) throws InterruptedException {
        leaveGarage(car);

        takeANap(70);

        System.out.println("planeId: " + car.getPlaneId() + ". Нужно доставить наборы питания к самолету.");

        // Первая итерация цикла доставки наборов питания на борт
        if(car.getFoodNeeded() > 0) {
            System.out.println("planeId: " + car.getPlaneId() + ". Нужно съездить за питанием (foodNeeded: " + car.getFoodNeeded() + ") на склад.");
            // Устанавливаем маршрут до склада с питанием, чтобы забрать наборы питания
            car.setRoutePoints(groundDispatcherClient.getRouteFromGarageToLuggage(car.getCurrentPoint()));
            followTheRoute(car, "garageToLuggage");
            car.loadFoodToCar();

            takeANap(300);

            System.out.println("planeId: " + car.getPlaneId() + ". Нужно отвезти питание (currentFoodQuantity: " + car.getCurrentFoodQuantity() + ") к самолету.");
            // Устанавливаем маршрут до самолета, чтобы доставить наборы питания на борт
            car.setRoutePoints(groundDispatcherClient.getRouteFromLuggageToPlane(car.getCurrentPoint(), car.getPlaneId()));
            followTheRoute(car, "luggageToPlane");
            car.loadFoodToPlane();

            takeANap(300);
        }

        // Цикл доставки наборов питания на борт
        while (car.getFoodNeeded() > 0) {
            System.out.println("planeId: " + car.getPlaneId() + ". Нужно съездить за питанием (foodNeeded: " + car.getFoodNeeded() + ") на склад.");
            // Устанавливаем маршрут до склада с питанием, чтобы забрать наборы питания
            car.setRoutePoints(groundDispatcherClient.getRouteFromPlaneToLuggage(car.getCurrentPoint()));
            followTheRoute(car, "planeToLuggage");
            car.loadFoodToCar();

            takeANap(300);

            System.out.println("planeId: " + car.getPlaneId() + ". Нужно отвезти питание (currentFoodQuantity: " + car.getCurrentFoodQuantity() + ") к самолету.");
            // Устанавливаем маршрут до самолета, чтобы доставить наборы питания на борт
            car.setRoutePoints(groundDispatcherClient.getRouteFromLuggageToPlane(car.getCurrentPoint(), car.getPlaneId()));
            followTheRoute(car, "luggageToPlane");
            car.loadFoodToPlane();

            takeANap(300);
        }

//        // Сообщаем самолету, что загрузка питания полностью завершена
//        boardClient.ReportFoodDeliveryCompletionToBoard(car.getPlaneId());
//        System.out.println("planeId: " + car.getPlaneId() + ". Отправлено уведомление самолету о завершении доставки питания.");
//
//        // Сообщаем УНО, что заказ на загрузку питания полностью выполнен.
//        unoClient.reportLoadOrderCompletionToUNO(car.getOrderId());
//        System.out.println("planeId: " + car.getPlaneId() + ". Отправлено уведомление о завершении заказа в УНО.");

        System.out.println("planeId: " + car.getPlaneId() + ". Заказ выполнен. Нужно ехать к гаражу.");
        // Устанавливаем маршрут до гаража, чтобы поставить машину в гараж
        car.setRoutePoints(groundDispatcherClient.getRouteToGarage(car.getCurrentPoint()));
        followTheRoute(car, "garage");

        takeANap(210);

        // Уведомление Ground Dispatcher о том, что машинка заехала в гараж (и должна быть удалена с карты)
        groundDispatcherClient.deleteCarFromMap(car.getCurrentPoint());

        // Удаление инстанса машинки
        cars.remove(car);
        System.out.println("Машина заехала в гараж");
    }
    public void leaveGarage(CateringCar car) {
        while (true) {
            if (groundDispatcherClient.getPermissionToGetOutOfGarage("food_truck")) {
                System.out.println("planeId: " + car.getPlaneId() + ". Получено разрешение на выезд из гаража.");
                car.setCurrentPoint(298);
                break;
            }
        }
    }
    public void followTheRoute(CateringCar car, String destination) throws InterruptedException {
        System.out.println("planeId: " + car.getPlaneId() + ". Едем по маршруту. Пункт назначения: " + destination);

        while(!car.getRoute().isEmpty()) {
            takeANap(35);
            proceedToThePoint(car, destination);
        }
    }
    public void takeANap(Integer modelSecsToWait) {
        departureBoardClient.waitFor(modelSecsToWait);
    }
    public void proceedToThePoint(CateringCar car, String destination) {
        Integer targetPoint = car.getRoute().peek();
        if (groundDispatcherClient.getPermissionToNextPoint(car.getCurrentPoint(), targetPoint)) {
            // System.out.println("planeId: " + car.getPlaneId() + ". currentPoint: " + car.getCurrentPoint() + ", targetPoint: " + targetPoint);
            car.setCurrentPoint(targetPoint);
            car.setProceedingToPointFails(0);
            car.getRoute().remove();
        } else {
            car.incrementProceedingToPointFails();
            if (car.getProceedingToPointFails() >= 5) {
                System.out.println("planeId: " + car.getPlaneId() + ". Машина попала в пробку. Перестроим маршрут.");
                if (Objects.equals(destination, "garageToPlane")) {
                    System.out.println("planeId: " + car.getPlaneId() + ". Едем по перестроенному маршруту. Пункт назначения: " + destination);
                    car.setRoutePoints(groundDispatcherClient.getRouteFromGarageToPlane(car.getCurrentPoint(), car.getPlaneId()));
                } else if (Objects.equals(destination, "garageToLuggage")) {
                    System.out.println("planeId: " + car.getPlaneId() + ". Едем по перестроенному маршруту. Пункт назначения: " + destination);
                    car.setRoutePoints(groundDispatcherClient.getRouteFromGarageToLuggage(car.getCurrentPoint()));
                } else if (Objects.equals(destination, "garage")) {
                    System.out.println("planeId: " + car.getPlaneId() + ". Едем по перестроенному маршруту. Пункт назначения: " + destination);
                    car.setRoutePoints(groundDispatcherClient.getRouteToGarage(car.getCurrentPoint()));
                } else if (Objects.equals(destination, "luggageToPlane")) {
                    System.out.println("planeId: " + car.getPlaneId() + ". Едем по перестроенному маршруту. Пункт назначения: " + destination);
                    car.setRoutePoints(groundDispatcherClient.getRouteFromLuggageToPlane(car.getCurrentPoint(), car.getPlaneId()));
                } else { // destination = planeToLuggage
                    System.out.println("planeId: " + car.getPlaneId() + ". Едем по перестроенному маршруту. Пункт назначения: " + destination);
                    car.setRoutePoints(groundDispatcherClient.getRouteFromPlaneToLuggage(car.getCurrentPoint()));
                }
            }
        }
    }
    public List<CateringCar> getAllCars() {
        // System.out.println(cars);
        return cars;
    }

    @Deprecated
    public void legacyProcessLoadOrder(Integer orderId, Integer planeId) throws InterruptedException {
        System.out.println("Получен заказ от УНО. orderId: " + orderId + ", planeId: " + planeId);

        // Поиск автомобиля с заданным planeId
        Optional<CateringCar> existingCar = cars.stream()
                .filter(car -> planeId.equals(car.getPlaneId()))
                .findFirst();

        if (existingCar.isPresent()) {
            // Если автомобиль найден, обновляем его orderId
            CateringCar car = existingCar.get();
            car.setOrderId(orderId);
            System.out.println("Для этого самолета (planeId: " + car.getPlaneId() + ") уже есть машина. Начинаем обслуживание.");
            serveOrder(car);
        } else {
            // Если автомобиль не найден, создаем новый экземпляр
            CateringCar newCar = new CateringCar();

            newCar.setOrderId(orderId);
            newCar.setPlaneId(planeId);
            legacyAddCar(newCar, true);
        }
    }

    @Deprecated
    public void legacyProcessFoodOrder(FoodOrderRequest foodOrderRequest) throws InterruptedException {
        for (RTORequest foodRequest : foodOrderRequest.getFoodList()) {
            System.out.println("Получен заказ от Кассы-Регистрации. planeId: " + foodRequest.getFlightId() + ", foodNeeded: " + foodRequest.getQuantity());

            // Поиск автомобиля с заданным planeId
            Optional<CateringCar> existingCar = cars.stream()
                    .filter(car -> foodRequest.getFlightId().equals(car.getPlaneId()))
                    .findFirst();

            if (existingCar.isPresent()) {
                // Если автомобиль найден, обновляем его orderId
                CateringCar car = existingCar.get();
                car.setFoodNeeded(foodRequest.getQuantity());
                System.out.println("Для этого самолета (planeId: " + car.getPlaneId() + ") уже есть машина. Начинаем обслуживание.");
                serveOrder(car);
            } else {
                // Если автомобиль не найден, создаем новый экземпляр
                CateringCar newCar = new CateringCar();

                newCar.setFoodNeeded(foodRequest.getQuantity());
                newCar.setPlaneId(foodRequest.getFlightId());
                legacyAddCar(newCar, false);
            }
        }
    }

    @Deprecated
    synchronized private void legacyAddCar(CateringCar newCar, boolean flag) throws InterruptedException {
        Optional<CateringCar> existingCar = cars.stream()
                .filter(car -> car.getPlaneId().equals(newCar.getPlaneId()))
                .findFirst();
        if (existingCar.isPresent()) {
            CateringCar car = existingCar.get();
            if (flag) {
                car.setOrderId(newCar.getOrderId());
            } else {
                car.setFoodNeeded(newCar.getFoodNeeded());
            }
            System.out.println("Для этого самолета (planeId: " + car.getPlaneId() + ") уже есть машина. Начинаем обслуживание.");
            serveOrder(car);
        } else {
            cars.add(newCar);

            System.out.println("Для этого самолета (planeId: " + newCar.getPlaneId() + ") нет машины. НО ДЛЯ ТЕСТА начинаем обслуживание.");
            // Для теста. Удалить после теста.
            serveOrder(newCar);
        }
    }

    @Deprecated
    public void serveOrder(CateringCar car) throws InterruptedException {
        // Тест уведомления модуля Board о завершении
        // sendTestReport(car);

        while (true) {
            if (groundDispatcherClient.getPermissionToGetOutOfGarage("food_truck")) {
                System.out.println("planeId: " + car.getPlaneId() + ". Получено разрешение на выезд из гаража.");
                car.setCurrentPoint(298);
                break;
            }
        }

        System.out.println("planeId: " + car.getPlaneId() + ". Нужно выгрузить мусор из самолета.");
        // Устанавливаем маршрут до самолета, чтобы забрать мусор
        car.setRoutePoints(groundDispatcherClient.getRouteFromGarageToPlane(car.getCurrentPoint(), car.getPlaneId()));
        followTheRoute(car, "plane");

        System.out.println("planeId: " + car.getPlaneId() + ". Нужно доставить наборы питания к самолету.");
        // Цикл доставки наборов питания на борт
        while (car.getFoodNeeded() > 0) {
            System.out.println("planeId: " + car.getPlaneId() + ". Нужно съездить за питанием (foodNeeded: " + car.getFoodNeeded() + ") на склад.");
            // Устанавливаем маршрут до склада с питанием, чтобы забрать наборы питания
            car.setRoutePoints(groundDispatcherClient.getRouteFromGarageToLuggage(car.getCurrentPoint()));
            followTheRoute(car, "luggage");
            car.loadFoodToCar();

            System.out.println("planeId: " + car.getPlaneId() + ". Нужно отвезти питание (currentFoodQuantity: " + car.getCurrentFoodQuantity() + ") к самолету.");
            // Устанавливаем маршрут до самолета, чтобы доставить наборы питания на борт
            car.setRoutePoints(groundDispatcherClient.getRouteFromGarageToPlane(car.getCurrentPoint(), car.getPlaneId()));
            followTheRoute(car, "plane");
            car.loadFoodToPlane();
        }

        // Сообщаем самолету, что загрузка питания полностью завершена
        boardClient.ReportFoodDeliveryCompletionToBoard(car.getPlaneId());
        System.out.println("planeId: " + car.getPlaneId() + ". Отправлено уведомление самолету о завершении доставки питания.");

        // Сообщаем УНО, что заказ полностью выполнен
        unoClient.reportLoadOrderCompletionToUNO(car.getOrderId());
        System.out.println("planeId: " + car.getPlaneId() + ". Отправлено уведомление о завершении заказа в УНО.");

        System.out.println("planeId: " + car.getPlaneId() + ". Нужно ехать к гаражу. Заказ выполнен.");
        // Устанавливаем маршрут до гаража, чтобы поставить машину в гараж
        car.setRoutePoints(groundDispatcherClient.getRouteToGarage(car.getCurrentPoint()));
        followTheRoute(car, "garage");

        // Удаление инстанса машинки
        cars.remove(car);
        System.out.println("Машина заехала в гараж");
    }

}
