package com.example.salonclient.Controllers.Admin;

import com.example.salonclient.Model.BasicClasses.Client;
import com.example.salonclient.Model.BasicClasses.Master;
import com.example.salonclient.Model.BasicClasses.Schedule;
import com.example.salonclient.Model.ServerAccess;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

public class SummaryMainController {

    @FXML private MenuButton summaryType;

    @FXML private VBox basicView;
    @FXML private VBox salaryView;

    @FXML private HBox yearAndMonth;
    @FXML private MenuButton period;
    @FXML private MenuButton year;
    @FXML private MenuButton month;
    @Getter private static Integer chosenYear;
    @Getter private static String chosenMonth;

    @FXML private TableColumn<Master, String> fio;
    @FXML private TableColumn<Master, Integer> experience;
    @FXML private TableColumn<Master, String> service;
    @FXML private TableColumn<Master, BigDecimal> hourRate;
    @FXML private TableColumn<Master, Integer> hours;
    @FXML private TableColumn<Master, BigDecimal> salary;
    @FXML public TableView<Master> MastersTable;

    @FXML private void toCreate(){
        basicView.setVisible(false);
        salaryView.setVisible(false);
        for(Node node:basicView.getChildren()){
            node.setStyle("-fx-font-size: 14px");}
        switch (summaryType.getText()){
            case "Клиенты" ->{
                basicView.getChildren().clear();
                basicView.setVisible(true);
                    ServerAccess.sendListRequest("/admin/client", "GET", "", Client[].class,
                            response ->
                                    Platform.runLater(() ->{
                                        fillWithClientLabel(response);

                                        HBox hBox = new HBox(createAndAddBarChartAge(response),
                                                createAndAddPieChartGender(response));
                                        basicView.getChildren().add(hBox);
                                    }),
                            error -> {
                                Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка получения данных масетров: "
                                        + error.getMessage(), ButtonType.OK);
                                alert.show();
                                return;
                            });
            }
            case "Записи" ->{
                basicView.getChildren().clear();
                basicView.setVisible(true);
                ServerAccess.sendListRequest("/admin/note", "GET", "", Schedule[].class,
                        response ->
                                Platform.runLater(() ->{
                                    fillWithNoteLabel(response);
                                    fillTable(response, false);
                                    HBox hBox = new HBox(createNotePieChart(response), createNoteAreaChart(response));
                                    basicView.getChildren().add(hBox);
                                }),
                        error -> {
                            Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка получения данных масетров: "
                                    + error.getMessage(), ButtonType.OK);
                            alert.show();
                            return;
                        });
            }
            case "Расчет прибыли" -> {
                basicView.getChildren().clear();
                basicView.setVisible(true);
                ServerAccess.sendListRequest("/admin/note", "GET", "", Schedule[].class,
                        response -> Platform.runLater(() -> {
                            fillWithIncomeLabel(response);
                            fillTable(response, true);
                            createAndAddBarChart(response);
                        }),
                        error -> {
                            Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка получения данных: " + error.getMessage(), ButtonType.OK);
                            alert.show();
                            return;
                        }
                );
            }
            case "Расчет заработной платы" ->{
                salaryView.setVisible(true);
                if(!year.getText().isEmpty()&&!month.getText().isEmpty()) {
                    System.out.println(year.getText()+"   "+chosenYear.toString());
                    if(MastersTable!=null) {
                        MastersTable.setVisible(true);
                        FillMastersTable(MastersTable);
                    }
                }
                else{
                    showAlert("Для отображения расчета заработной платы нужно указать год и месяц!");
                    return;
                }
            }
        }
    }
    public void fillWithIncomeLabel(List<Schedule> schedules){
        Map<String, BigDecimal> serviceProfits = schedules.stream()
                .filter(schedule -> isWithinPeriod(schedule.getDate(), true))
                .collect(Collectors.groupingBy(Schedule::getServiceName,
                        Collectors.reducing(BigDecimal.ZERO, schedule ->
                                schedule.getPrice().multiply(new BigDecimal(schedules.stream()
                                        .filter(s -> s.getServiceName()
                                                .equals(schedule.getServiceName()))
                                        .count())
                                ), BigDecimal::add)));
        BigDecimal minProfit = serviceProfits.values().stream().min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        basicView.getChildren().add(new Label("Минимальная прибыль от услуги: "+
                serviceProfits.entrySet().stream()
                        .filter(entry -> entry.getValue().equals(minProfit))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.joining(", "))));
        BigDecimal maxProfit = serviceProfits.values().stream().max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        basicView.getChildren().add(new Label("Максимальная прибыль от услуги: "+
                serviceProfits.entrySet().stream()
                        .filter(entry -> entry.getValue().equals(maxProfit))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.joining(", "))));
    }
    public void fillWithNoteLabel(List<Schedule> schedules){
        Map<String, Long> serviceCounts = schedules.stream()
                .filter(schedule -> isWithinPeriod(schedule.getDate(), false))
                .collect(Collectors.groupingBy(Schedule::getServiceName, Collectors.counting()));
        basicView.getChildren().add(new Label("Общее записей: "+schedules.size()));
        long minCount = serviceCounts.values().stream().min(Comparator.comparingLong(Long::longValue)).orElse(0L);
        basicView.getChildren().add(new Label("Наименее популярная услуга :"+
                serviceCounts.entrySet().stream()
                        .filter(entry -> entry.getValue() == minCount)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.joining(", "))));
        long maxCount = serviceCounts.values().stream().max(Comparator.comparingLong(Long::longValue)).orElse(0L);
        basicView.getChildren().add(new Label("Самая популярная услуга: "+
                serviceCounts.entrySet().stream()
                        .filter(entry -> entry.getValue() == maxCount)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.joining(", "))));
    }
    public void fillWithClientLabel(List<Client> clients){
        basicView.getChildren().add(new Label(("Общее количество клиентов: "+
                clients.size())));
        basicView.getChildren().add(new Label("Минимальный возраст: " + clients.stream()
                .map(Client::getBirthDate)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .map(date -> Period.between(date, LocalDate.now()).getYears())
                .orElse(null)));
        basicView.getChildren().add(new Label("Максимальный возраст: " + clients.stream()
                .map(Client::getBirthDate)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .map(date -> Period.between(date, LocalDate.now()).getYears())
                .orElse(null)));
    }
    static void FillMastersTable(TableView<Master> mastersTable) {
        if (mastersTable != null) {
            ServerAccess.sendListRequest("/admin/master/full", "GET", "", Master[].class,
                    response ->
                            Platform.runLater(() ->
                                    mastersTable.setItems(FXCollections.observableArrayList(response))),
                    error -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Ошибка получения данных масетров: "
                                + error.getMessage(), ButtonType.OK);
                        alert.show();
                        return;
                    });
        }
    }

    @FXML private void initialize(){
        basicView.setVisible(false);
        salaryView.setVisible(false);
        yearAndMonth.setVisible(false);
        period.setVisible(false);
        chosenYear=LocalDate.now().getYear();
        chosenMonth = LocalDate.now().getMonth().toString();
        for(MenuItem item: period.getItems()){
            item.setOnAction(e -> period.setText(item.getText()));}
        month.getItems().clear();
        year.getItems().clear();
        for(int y = LocalDate.now().getYear()-10;y<=LocalDate.now().getYear();y++){
            MenuItem item = new MenuItem(String.valueOf(y));
            item.setOnAction(e->{
                chosenYear=Integer.parseInt(item.getText());
                year.setText(item.getText());});
            year.getItems().add(item);
        }
        for (Month m : Month.values()) {
            MenuItem item = new MenuItem(String.valueOf(m));
            item.setOnAction(e->{
                chosenMonth=item.getText();
                month.setText(item.getText());});
            month.getItems().add(item);
        }
        year.setText(String.valueOf(LocalDate.now().getYear()));
        month.setText(String.valueOf(LocalDate.now().getMonth()));

        fio.setCellValueFactory(new PropertyValueFactory<>("fio"));
        service.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        experience.setCellValueFactory(new PropertyValueFactory<>("experience"));
        hourRate.setCellValueFactory(new PropertyValueFactory<>("hourlyRate"));
        hours.setCellValueFactory(new PropertyValueFactory<>("workingHours"));
        salary.setCellValueFactory(new PropertyValueFactory<>("salary"));

        for(MenuItem item: summaryType.getItems()){
            item.setOnAction(e -> {
                summaryType.setText(item.getText());
                period.setVisible(false);
                yearAndMonth.setVisible(false);
                switch (item.getText()){
                    case "Расчет прибыли","Записи" -> {
                        period.setVisible(true);
                        yearAndMonth.setVisible(false);
                        break;
                    }
                    case "Расчет заработной платы" -> {
                        period.setVisible(false);
                        yearAndMonth.setVisible(true);
                        break;
                    }
                    default -> {
                        period.setVisible(false);
                        yearAndMonth.setVisible(false);}
                }
            });}

    }

    public void fillTable(List<Schedule> schedules, boolean income) {
        LocalDate today = LocalDate.now();
        schedules = schedules.stream()
                .filter(schedule -> isWithinPeriod(schedule.getDate(), income))
                .collect(Collectors.toList());
        GridPane gridPane = createHeadersForGrid(schedules, income);
        gridPane.setGridLinesVisible(true);
        List<String> columnHeaders = getColumnHeaders(schedules, income);
        List<String> serviceNames = getServiceNames(schedules);

        // Заполнение ячеек
        Map<String, Integer> serviceCounts = new HashMap<>();
        Map<String, BigDecimal> servicePrices = new HashMap<>();
        Map<String, Map<String, Integer>> dateServiceCounts = new HashMap<>();
        for (Schedule schedule : schedules) {
            String serviceName = schedule.getServiceName();
            String dateOrMonth;
            if (period.getText().equals("Год")) dateOrMonth = getDateOrMonth(schedule.getDate());
            else dateOrMonth = schedule.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            int rowIndex = serviceNames.indexOf(serviceName) + 1;
            int colIndex = columnHeaders.indexOf(dateOrMonth) + 1;

            if (rowIndex < 1 || colIndex < 1) { continue;}

            serviceCounts.merge(serviceName, 1, Integer::sum);
            if(income) servicePrices.putIfAbsent(serviceName, schedule.getPrice());
            dateServiceCounts.putIfAbsent(dateOrMonth, new HashMap<>());
            dateServiceCounts.get(dateOrMonth).merge(serviceName, 1, Integer::sum);
        }
        for (int i = 0; i < serviceNames.size(); i++) {
            String serviceName = serviceNames.get(i);
            for (int j = 0; j < columnHeaders.size(); j++) {
                String dateOrMonth = columnHeaders.get(j);
                int rowIndex = i + 1;
                int colIndex = j + 1;
                if (dateServiceCounts.containsKey(dateOrMonth) &&
                        dateServiceCounts.get(dateOrMonth).containsKey(serviceName)) {
                    int count =  dateServiceCounts.get(dateOrMonth).get(serviceName);
                    Label infoLabel;
                    if (income) {
                        BigDecimal price = servicePrices.get(serviceName);
                        infoLabel = new Label(price + " * " + count);
                    } else {
                        infoLabel = new Label(String.valueOf(count));
                    }
                    infoLabel.setStyle("-fx-font-size: 14px; -fx-pref-width: 100px; " +
                            "-fx-pref-height: 30px; -fx-padding: 0 0 0 4");
                    gridPane.add(infoLabel, colIndex, rowIndex);
                }
            }
        }
        for (int i = 0; i < serviceNames.size(); i++) {
            if(columnHeaders.size()>2 &&income || columnHeaders.size()>1&&!income) {
                String serviceName = serviceNames.get(i);
                Label totalCountLabel = new Label(serviceCounts
                        .getOrDefault(serviceName, 0).toString());
                totalCountLabel.setStyle("-fx-font-size: 14px; -fx-pref-width: 115px;" +
                        " -fx-pref-height: 30px; -fx-padding: 0 0 0 4");
                if(income) {
                    Label totalProfitLabel = new Label(servicePrices
                            .getOrDefault(serviceName, BigDecimal.ZERO)
                            .multiply(new BigDecimal(serviceCounts.get(serviceName))).toString());
                    totalProfitLabel.setStyle("-fx-font-size: 14px; -fx-pref-width: 115px;" +
                            " -fx-pref-height: 30px; -fx-padding: 0 0 0 4");
                    gridPane.add(totalCountLabel, columnHeaders.size() - 1, i + 1);
                    gridPane.add(totalProfitLabel, columnHeaders.size(), i + 1);
                }
                else gridPane.add(totalCountLabel, columnHeaders.size(), i + 1);
            }
        }
        basicView.getChildren().add(gridPane);
    }
    public PieChart createNotePieChart(List<Schedule> schedules) {
        PieChart pieChart = new PieChart();
        Map<String, Long> serviceCounts = schedules.stream()
                .filter(schedule -> isWithinPeriod(schedule.getDate(), false))
                .collect(Collectors.groupingBy(Schedule::getServiceName, Collectors.counting()));

        serviceCounts.forEach((serviceName, count) -> {
            PieChart.Data slice = new PieChart.Data(serviceName, count);
            pieChart.getData().add(slice);
        });
        return pieChart;
    }
    public AreaChart<String, Number> createNoteAreaChart(List<Schedule> schedules) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickUnit(1);
        yAxis.setMinorTickCount(0);
        yAxis.setForceZeroInRange(true);
        AreaChart<String, Number> areaChart = new AreaChart<>(xAxis, yAxis);
        yAxis.setLabel("Количество записей");
        xAxis.setSide(Side.BOTTOM);
        xAxis.setTickLabelRotation(90);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        if ("Год".equals(period.getText())) {
            Map<Month, Long> monthlyCounts = schedules.stream()
                    .filter(schedule -> isWithinPeriod(schedule.getDate(), false))
                    .collect(Collectors.groupingBy(schedule -> schedule.getDate().getMonth(), Collectors.counting()));
            monthlyCounts.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> { Month month = entry.getKey();
                        Long count = entry.getValue();
                        series.getData().add(new XYChart.Data<>(month.toString(), count));
                    });
        } else {
            Map<LocalDate, Long> dailyCounts = schedules.stream()
                    .filter(schedule -> isWithinPeriod(schedule.getDate(), false))
                    .collect(Collectors.groupingBy(Schedule::getDate, Collectors.counting()));

            dailyCounts.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        LocalDate date = entry.getKey();
                        Long count = entry.getValue();
                        series.getData().add(new XYChart.Data<>(date
                                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), count));
                    });
        }
        areaChart.getData().add(series);
        return areaChart;
    }
    public PieChart createAndAddPieChartGender(List<Client> clients) {
        PieChart pieChart = new PieChart();

        Map<String, Long> genderCounts = clients.stream()
                .filter(client -> client.getSex() != null)
                .collect(Collectors.groupingBy(Client::getSex, Collectors.counting()));

        genderCounts.forEach((gender, count) -> {
            PieChart.Data slice = new PieChart.Data(gender, count);
            pieChart.getData().add(slice);
        });
        return pieChart;
    }
    public BarChart<String, Number>  createAndAddBarChartAge(List<Client> clients) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setTickUnit(1); // Устанавливает шаг тиков равным 1
        yAxis.setMinorTickCount(0); // Убирает дополнительные маленькие тики
        yAxis.setForceZeroInRange(true); // Убедиться, что 0 включен в диапазон
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        xAxis.setLabel("Возраст");
        yAxis.setLabel("Количество человек");

        Map<Integer, Long> ageCounts = clients.stream()
                .filter(client -> client.getBirthDate() != null)
                .collect(Collectors.groupingBy(client -> Period.between(client.getBirthDate(),
                        LocalDate.now()).getYears(), Collectors.counting()));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        ageCounts.forEach((age, count) -> {
            series.getData().add(new XYChart.Data<>(age.toString(), count));
        });
        barChart.getData().add(series);
        barChart.setStyle("-fx-max-width: 25px");
        return barChart;
    }
    public GridPane createHeadersForGrid(List<Schedule> schedules, boolean income){
        GridPane gridPane = new GridPane();

        // Получение заголовков колонок
        List<String> columnHeaders = getColumnHeaders(schedules, income);
        for (int i = 0; i < columnHeaders.size(); i++) {
            Label label = new Label(columnHeaders.get(i));
            label.setStyle("-fx-font-size: 13px; -fx-pref-width: 120px;" +
                    " -fx-pref-height: 50px;-fx-wrap-text: true; -fx-padding: 0 0 0 4");
            gridPane.add(label, i + 1, 0);
        }

        // Заполнение левой колонки (названия услуг)
        List<String> serviceNames = getServiceNames(schedules);
        for (int i = 0; i < serviceNames.size(); i++) {
            Label label = new Label(serviceNames.get(i));
            label.setStyle("-fx-font-size: 13px; -fx-pref-width: 120px; " +
                    "-fx-pref-height: 50px;-fx-wrap-text: true; -fx-padding: 0 0 0 4");
            gridPane.add(label, 0, i + 1);
        }
        return gridPane;
    }

    public void createAndAddBarChart(List<Schedule> schedules) {
        LocalDate today = LocalDate.now();
        schedules = schedules.stream()
                .filter(schedule -> schedule.getDate().isBefore(today))
                .collect(Collectors.toList());
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        xAxis.setLabel("Услуга");
        yAxis.setLabel("Общая прибыль");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Map<String, BigDecimal> serviceProfits = new HashMap<>();
        schedules.stream()
                .filter(schedule -> {
            if ("Год".equals(period.getText())) { return isWithinPeriod(schedule.getDate(), false);
            } else { return isWithinPeriod(schedule.getDate(), true);
            }
        }) .forEach(schedule -> serviceProfits.merge(schedule.getServiceName(), schedule.getPrice(), BigDecimal::add));
        serviceProfits.forEach((serviceName, totalProfit) -> { series.getData()
                .add(new XYChart.Data<>(serviceName, totalProfit)); });
        barChart.getData().add(series);
        barChart.setStyle("-fx-max-width: 40px");
        basicView.getChildren().add(barChart);
    }
    public void showAlert(String message){
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
        alert.show();
    }
    private List<String> getColumnHeaders(List<Schedule> schedules, boolean income) {
        Set<String> headers = new LinkedHashSet<>();
////////////////////////////////////////////////////////////
        if ("Год".equals(period.getText())) {
            for (Month month : Month.values()) {
                headers.add(month.toString());}
        } else {
            // Добавление конкретных дат в зависимости от выбранного периода
            schedules.stream()
                    .map(Schedule::getDate)
                    .distinct()
                    .sorted()
                    .filter(date -> isWithinPeriod(date, income))
                    .forEach(date -> headers.add(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));}
        headers.add("Общее кол-во");
        if(income) headers.add("Общая прибыль");
        return new ArrayList<>(headers);
    }
    private boolean isWithinPeriod(LocalDate date, boolean income) {
        LocalDate startDate;
        LocalDate endDate = LocalDate.now();
        int currentYear = endDate.getYear();
        endDate = switch (period.getText()) {
            case "Месяц" -> {
                startDate = endDate.withDayOfMonth(1);
                yield income ? endDate : endDate.withDayOfMonth(endDate.lengthOfMonth()); //позволяет возвращать значения из switch выражения.
            }
            case "Неделя" -> {
                startDate = endDate.with(ChronoField.DAY_OF_WEEK, 1);
                yield income ? endDate : startDate.plusDays(6);
            }
            case "Год" -> {
                startDate = LocalDate.of(currentYear, 1, 1);
                yield LocalDate.of(currentYear, 12, 31);
            }
            default -> {
                startDate = LocalDate.MIN;
                yield LocalDate.MAX;
            }
        };
        return (date.isEqual(startDate) || date.isAfter(startDate)) && (date.isEqual(endDate) || date.isBefore(endDate));
    }

    private List<String> getServiceNames(List<Schedule> schedules) {
        return schedules.stream() .map(Schedule::getServiceName) .distinct() .collect(Collectors.toList()); }
    private String getDateOrMonth(LocalDate date) {
        if ("Год".equals(period.getText())) {
            return date.getMonth().toString();
        } else { return date.toString();} }
}
