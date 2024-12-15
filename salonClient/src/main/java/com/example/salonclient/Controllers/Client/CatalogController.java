package com.example.salonclient.Controllers.Client;

import com.example.salonclient.Controllers.Client.Cards.ServiceCardController;
import com.example.salonclient.Model.BasicClasses.Service;
import com.example.salonclient.Model.JsonConverting;
import com.example.salonclient.Model.ServerAccess;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class CatalogController implements LinkedToMainWindow{
    @FXML private VBox vBox;

    @FXML private Slider minPrice;
    @FXML private Slider maxPrice;
    @FXML private Label minPriceLabel;
    @FXML private Label maxPriceLabel;

    @FXML private ComboBox<String> minHour;
    @FXML private ComboBox<String> maxHour;

    @FXML private TextField search;
    private BigDecimal minServicePrice;
    private BigDecimal maxServicePrice;
    private static MainClientViewController mainClientViewController;

    private Map<String, Object> criteria = new HashMap<>();
    private PauseTransition pause;
    private boolean isResetting = false;
    private static List<Service> currentServices = new ArrayList<>();
    @FXML private MenuItem priceSort;
    @FXML private MenuItem durationSort;
    private boolean sortByPrice = false;
    private boolean sortByDuration = false;

    public void setMainClientViewController(MainClientViewController mainClientViewController) {
        CatalogController.mainClientViewController = mainClientViewController;}

    public void setData(List<Service> services){
        vBox.getChildren().clear();
        try {
            for(Service service:services) {
                FXMLLoader load = new FXMLLoader();
                load.setLocation(getClass().getResource("/com/example/salonclient/service_card.fxml"));
                Pane pane = load.load();
                ServiceCardController cardC = load.getController();
                cardC.setMainClientViewController(mainClientViewController);
                cardC.setData(service);
                VBox.setVgrow(pane, Priority.ALWAYS);
                vBox.getChildren().add(pane);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void refreshData() {
            ServerAccess.sendListRequest("/client/service", "GET", "", Service[].class,
                    response ->
                            Platform.runLater(() -> {
                                currentServices = response;
                                setData(response);
                                calculatePriceRange(response);
                                initializeSliderBounds();}),
                    error -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка получения данных услуг: "
                                + error.getMessage(), ButtonType.OK);
                        alert.show();
                        return;
                    });
    }
    @FXML private void initialize(){
        minPrice.setBlockIncrement(1.00);
        maxPrice.setBlockIncrement(1.00);
        for (int hour = 1; hour <= 6; hour++) {
            minHour.getItems().add(String.valueOf(hour));
            maxHour.getItems().add(String.valueOf(hour));
        }
        pause = new PauseTransition(Duration.millis(500));
        pause.setOnFinished(event -> updateCriteriaAndRefreshTable());
        search.textProperty().addListener((observable, oldValue, newValue) -> updateCriteriaAndRefreshTable());
        minPrice.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!isResetting) {
                maxPrice.setMin(newValue.doubleValue());
                minPriceLabel.setText(String.format("%.2f", newValue.doubleValue()));
                restartPause(); } });
        maxPrice.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!isResetting) {
                minPrice.setMax(newValue.doubleValue());
                maxPriceLabel.setText(String.format("%.2f", newValue.doubleValue()));
                restartPause(); } });
        minHour.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!isResetting) {
                updateCriteriaAndRefreshTable(); } });
        maxHour.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!isResetting) {
                updateCriteriaAndRefreshTable(); } });
        priceSort.setOnAction(event -> {
            sortByPrice = true;
            sortByDuration = false;
            applySorting(); });
        durationSort.setOnAction(event -> {
            sortByPrice = false;
            sortByDuration = true;
            applySorting(); });
        refreshData();
    }
    private void calculatePriceRange(List<Service> services) {
        minServicePrice = services.stream()
                .map(Service::getPrice)
                .min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        maxServicePrice = services.stream()
                .map(Service::getPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }
    private void initializeSliderBounds() {
        minPrice.setMin(minServicePrice.doubleValue());
        minPrice.setMax(maxServicePrice.doubleValue());
        minPrice.setValue(minServicePrice.doubleValue());
        minPrice.setBlockIncrement(1.00);
        minPriceLabel.setText(String.format("%.2f", minServicePrice.doubleValue()));

        maxPrice.setMin(minServicePrice.doubleValue());
        maxPrice.setMax(maxServicePrice.doubleValue());
        maxPrice.setValue(maxServicePrice.doubleValue());
        maxPrice.setBlockIncrement(1.00);
        maxPriceLabel.setText(String.format("%.2f", maxServicePrice.doubleValue()));}
    private void updateCriteriaAndRefreshTable() {
        criteria.clear();
        BigDecimal minPriceValue = BigDecimal.valueOf(minPrice.getValue()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal maxPriceValue = BigDecimal.valueOf(maxPrice.getValue()).setScale(2, RoundingMode.HALF_UP);
        criteria.put("priceMin", minPriceValue.toString());
        criteria.put("priceMax", maxPriceValue.toString());
        if (minHour.getValue() != null) {
            criteria.put("durationMin", LocalTime.of(Integer.parseInt(minHour.getValue()), 0)); }
        if (maxHour.getValue() != null) {
            criteria.put("durationMax", LocalTime.of(Integer.parseInt(maxHour.getValue()), 0)); }
        if (search.getText() != null && !search.getText().isEmpty()) {
            criteria.put("searchQuery", search.getText());
        }
        if (!criteria.isEmpty()) {
            String jsonInputString = JsonConverting.createJson(criteria);
            ServerAccess.sendListRequest("/client/service/search_filter", "POST", jsonInputString, Service[].class,
                    response ->
                            Platform.runLater(() -> {
                                currentServices = response;
                                applySorting();
                                setData(currentServices);}),
                    error -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка получения данных услуг: " +
                                error.getMessage(), ButtonType.OK);
                        alert.show();
                    });
        }
    }
    private void restartPause() {
        if (pause.getStatus() == Animation.Status.RUNNING) {
            pause.stop(); }
        pause.playFromStart(); // Перезапускать таймер при каждом изменении значения слайдера
    }
    @FXML private void resetFilters() {
        isResetting = true;
        minPrice.setValue(minServicePrice.doubleValue());
        maxPrice.setValue(maxServicePrice.doubleValue());
        minPriceLabel.setText(String.format("%.2f", minServicePrice.doubleValue()));
        maxPriceLabel.setText(String.format("%.2f", maxServicePrice.doubleValue()));
        minHour.getSelectionModel().clearSelection();
        maxHour.getSelectionModel().clearSelection();
        search.clear();
        refreshData();
        sortByPrice = false;
        sortByDuration = false;
        isResetting = false;
    }
    private void applySorting() {
        if (sortByPrice) {
            currentServices = currentServices.stream()
                    .sorted(Comparator.comparing(Service::getPrice))
                    .collect(Collectors.toList());
        } else if (sortByDuration) {
            currentServices = currentServices.stream()
                    .sorted(Comparator.comparing(Service::getDuration))
                    .collect(Collectors.toList());
        } setData(currentServices);
    }
}

