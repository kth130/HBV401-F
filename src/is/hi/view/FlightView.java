package is.hi.view;

import com.jfoenix.controls.*;
import is.hi.controller.DBManager;
import is.hi.controller.FlightController;
import is.hi.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class FlightView {
    @FXML private JFXComboBox cbOrigin;
    @FXML private JFXComboBox cbDestination;
    @FXML private JFXComboBox cbAirline;
    @FXML private JFXComboBox cbClass;
    @FXML private JFXComboBox cbTiming;
    @FXML private JFXSlider maxPrice;
    @FXML private JFXCheckBox oneWay;
    @FXML private JFXDatePicker departureFlight;
    @FXML private JFXDatePicker returnFlight;
    @FXML private JFXButton btnSearch;
    @FXML private Label maxPriceLabel;
    @FXML private TableView<Flight> originTable;
    @FXML private StackPane dialogWindow;
    @FXML private TableView<Flight> returnTable;

    //@FXML private TableColumn<Flight, String> originCol;
    //@FXML private TableColumn<Flight, String> destinationCol;
    //@FXML private TableColumn<Flight, String> departureCol;
    //FXML private TableColumn<Flight, String> departureTimeCol;
    //@FXML private TableColumn<Flight, String> priceCol;
    //@FXML private TableColumn<Flight, String> availableSeatsCol;

    private Query query;
    private FlightController fc;

    @FXML
    private void oneWayFlight(ActionEvent e){
        if(oneWay.isSelected())
            returnFlight.setDisable(true);
        else
            returnFlight.setDisable(false);
    }

    @FXML
    private void searchFlights(ActionEvent e){
        Query q = new Query();
        Query q1 = new Query();
        if(cbAirline.getValue() != null && !cbAirline.getValue().equals("Alveg sama") ){
            q.setAirline(String.valueOf(cbAirline.getValue()));
            q1.setAirline(String.valueOf(cbAirline.getValue()));
        }
        if(cbDestination.getValue() != null){
            q.setDestination(String.valueOf(cbDestination.getValue()));
            if(!oneWay.isSelected())
                q1.setOrigin(String.valueOf(cbDestination.getValue()));
        }
        if(cbOrigin.getValue() != null) {
            q.setOrigin(String.valueOf(cbOrigin.getValue()));
            if(!oneWay.isSelected())
                q1.setDestination(String.valueOf(cbOrigin.getValue()));
        }
        if(cbClass.getValue() != null) {
            q.setSeatingClass(String.valueOf(cbClass.getValue()));
            q1.setSeatingClass(String.valueOf(cbClass.getValue()));
        }

        if(cbTiming.getValue() != null){
            String timing = String.valueOf(cbTiming.getValue());
            switch(timing){
                case "Næturflug":
                    q.setDepartureTime(600);
                    q1.setDepartureTime(600);
                    break;
                case "Morgunflug":
                    q.setDepartureTime(1200);
                    q1.setDepartureTime(1200);
                    break;
                case "Dagsflug":
                    q.setDepartureTime(1800);
                    q1.setDepartureTime(1800);
                    break;
                case "Kvöldflug":
                    q.setDepartureTime(2400);
                    q1.setDepartureTime(2400);
            }
        }
        if(departureFlight.getValue() != null){
            int year = departureFlight.getValue().getYear();
            int month = departureFlight.getValue().getMonthValue();
            int day = departureFlight.getValue().getDayOfMonth();

            String m = "";
            String d = "";
            if(month < 10) m += "0";
            if(day < 10) d += "0";
            q.setDepartureDate(Integer.valueOf(year +m+month+d+day));
        }
        if(returnFlight.getValue() != null){
            int year = returnFlight.getValue().getYear();
            int month = returnFlight.getValue().getMonthValue();
            int day = returnFlight.getValue().getDayOfMonth();

            String m = "";
            String d = "";
            if(month < 10) m += "0";
            if(day < 10) d += "0";
            q1.setDepartureDate(Integer.valueOf(year +m+month+d+day));
        }
        q.setMaxPrice((int)Math.round(maxPrice.getValue()) * 1000);
        q1.setMaxPrice((int)Math.round(maxPrice.getValue()) * 1000);
        ArrayList<Flight> flightsOut;
        ArrayList<Flight> flightsBack;

        try {
            flightsOut = fc.searchFlights(q);
            System.out.println("Leitir: " + fc.getNrOffSearches("'2010-05-28T15:36:56+0200'", "'2020-05-28T15:36:56+0200'"));
        } catch (SQLException error){
            System.out.println("Villa við að sækja flug " + error);
            flightsOut = new ArrayList<>();
        }
        populateTable(flightsOut, originTable);


        System.out.println(oneWay.isSelected());
        flightsBack = new ArrayList<>();
        if(!oneWay.isSelected()){
            try {

                flightsBack = fc.searchFlights(q1);
                System.out.println("Leitir: " + fc.getNrOffSearches("'2010-05-28T15:36:56+0200'", "'2020-05-28T15:36:56+0200'"));

            } catch (SQLException error){
                flightsBack = new ArrayList<>();

            }



        }
        populateTable(flightsBack, returnTable);


    }

    private void populateTable(ArrayList<Flight> flights, TableView<Flight> table){
        ObservableList<Flight> items =  FXCollections.observableArrayList(flights);
        table.setItems(items);
    }

    @FXML
    private void login(ActionEvent e){
        dialogWindow.setLayoutX(50);
        dialogWindow.setLayoutY(50);
        double width = dialogWindow.getWidth();
        double height = dialogWindow.getHeight();
        dialogWindow.setMouseTransparent(false);

        dialogWindow.setPrefWidth(width/2);
        dialogWindow.setPrefHeight(height/3);
        dialogWindow.getChildren().clear();
        System.out.println("login clicked");
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("Innskráning"));
        JFXButton login = new JFXButton("Skrá inn");
        JFXButton close = new JFXButton("Hætta við");
        JFXTextField username = new JFXTextField("");
        username.setPromptText("Notandanafn");
        content.setLayoutX(50);
        content.setLayoutY(50);

        JFXPasswordField password = new JFXPasswordField();
        password.setPromptText("Lykilorð");
        password.setStyle("-fx-alignment: center");
        username.setStyle("-fx-alignment: center");
        close.setButtonType(com.jfoenix.controls.JFXButton.ButtonType.RAISED);
        close.setStyle("-fx-text-fill: red");
        login.setButtonType(com.jfoenix.controls.JFXButton.ButtonType.RAISED);
        login.setStyle("-fx-text-fill: green");
        Label error = new Label("");
        content.setActions(error,username, password, close, login);
        JFXDialog loginScreen = new JFXDialog(dialogWindow, content,JFXDialog.DialogTransition.TOP);
        content.setPadding(new Insets(5));
        login.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Parent root = null;
                String user = username.getText();
                String pass = password.getText();
                System.out.println(user + ": " + pass);

                try {
                    if(fc.checkAdmin(user, pass)) {
                        try {
                            System.out.println(getClass().getResource("login.fxml"));
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
                            root = loader.load();
                            AdminView controller = loader.getController();
                            controller.setController(fc);
                            Scene scene = new Scene(root);
                            Stage secStage = new Stage();
                            secStage.setScene(scene);
                            secStage.setResizable(false);
                            secStage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        loginScreen.close();
                    }else {
                        error.setText("Innskráning tókst ekki");
                        error.setTextFill(Color.web("#f00"));
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

            }

        });

        close.setOnAction(event -> {
            dialogWindow.setMouseTransparent(true);
            dialogWindow.setPrefWidth(width/2);
            dialogWindow.setPrefHeight(height/2);
            loginScreen.close();
        });

        loginScreen.show();

    }
    @FXML
    @SuppressWarnings("unchecked")
    private void flightSelected(MouseEvent e){
        double x = e.getX();
        double y = e.getY();
        dialogWindow.setLayoutX(x);
        dialogWindow.setLayoutY(y);
        dialogWindow.setMouseTransparent(false);

        dialogWindow.getChildren().clear();
        System.out.println("Table clicked");
        TableView<Flight> table = (TableView<Flight>)e.getSource();
        Flight f = table.getSelectionModel().getSelectedItem();
        if(f == null){
            dialogWindow.setMouseTransparent(true);
            return;
        }
        table.getSelectionModel().clearSelection();
        System.out.println(f.getFrom() + " " + f.getTo());
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text(f.getFrom() + " - " + f.getTo()));

        JFXButton book = new JFXButton("Bóka");
        JFXButton close = new JFXButton("Loka");
        book.setStyle("-fx-background-color: green");
        close.setStyle("-fx-background-color: red");
        Label message;
        System.out.println(f.getAvailableSeats().size());
        if(f.getAvailableSeats().size() <= 10)
            message = new Label("Aðeins "+ (f.getEcoCapacity()+f.getBusCapacity())+" sæti eftir");
        else
            message = new Label("");
        message.setTextFill(Color.RED);
        content.setActions(message, close, book);
        JFXDialog flightInfo = new JFXDialog(dialogWindow, content,JFXDialog.DialogTransition.TOP);
        book.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Parent root = null;
                try {
                    System.out.println(getClass().getResource("booking.fxml"));
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("booking.fxml"));
                    root = loader.load();
                    BookingView controller = loader.getController();
                    controller.setFlight(f, fc);
                    Scene scene = new Scene(root);
                    Stage secStage = new Stage();
                    secStage.setTitle("Bóka flug frá " + f.getFrom() + " til " + f.getTo());
                    secStage.setScene(scene);
                    secStage.setResizable(false);
                    secStage.show();
                    //((Node) (event.getSource())).getScene().getWindow().hide();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        close.setOnAction(event -> {
            dialogWindow.setMouseTransparent(true);
            flightInfo.close();
        });

        flightInfo.show();

    }

    @FXML
    @SuppressWarnings("unchecked")
    public void initialize() {
        ArrayList<String> a;

        fc = new FlightController();
        try{
            a = fc.runQuery("Select origin from flights WHERE ecoCapacity > 0 GROUP BY origin ORDER BY origin");
            cbOrigin.getItems().addAll(a);
            a = fc.runQuery("Select destination from flights WHERE ecoCapacity > 0 GROUP BY destination ORDER BY destination");
            cbDestination.getItems().addAll(a);
            a = fc.runQuery("Select airline from flights WHERE ecoCapacity > 0 GROUP BY airline");
            cbAirline.getItems().add("Alveg sama");
            cbAirline.getItems().addAll(a);
        } catch (SQLException error){
            System.out.println(error);
        }

        ObservableList<String> timings = FXCollections.observableArrayList("Alveg sama", "Morgunflug", "Dagsflug", "Kvöldflug", "Næturflug");
        cbTiming.getItems().addAll(timings);

        ObservableList<String> classes = FXCollections.observableArrayList("Economy", "Buisness");
        cbClass.getItems().addAll(classes);

        maxPrice.setValue(maxPrice.getMax());

        maxPriceLabel.setText((int)maxPrice.getValue() + " þús");
        maxPrice.valueProperty().addListener((ov, old_val, new_val) ->
                maxPriceLabel.setText((int)Math.round(new_val.doubleValue()) + " þús"));

        TableColumn originCol = new TableColumn("Brottfarastaður");
        originCol.setCellValueFactory(
                new PropertyValueFactory<Flight, String>("from"));
        originCol.setMinWidth(20);
        TableColumn destinationCol = new TableColumn("Áfangastaður");
        destinationCol.setCellValueFactory(
                new PropertyValueFactory<Flight, String>("to"));
        TableColumn priceCol = new TableColumn("Verð");
        priceCol.setCellValueFactory(
                new PropertyValueFactory<Flight, Integer>("ecoPrice"));
        TableColumn airlineCol = new TableColumn("Flugfélag");
        airlineCol.setCellValueFactory(
                new PropertyValueFactory<Flight, String>("airline"));
        TableColumn departureCol = new TableColumn("Brottfaratími");
        departureCol.setCellValueFactory(
                new PropertyValueFactory<Flight, Date>("departureDate"));
        TableColumn timingCol = new TableColumn("Klukkan");
        timingCol.setCellValueFactory(
                new PropertyValueFactory<Flight, Integer>("departureTime"));
        originTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        originTable.getColumns().addAll(originCol, destinationCol, priceCol, airlineCol, departureCol, timingCol);
        TableColumn originCol1 = new TableColumn("Brottfarastaður");
        originCol1.setCellValueFactory(
                new PropertyValueFactory<Flight, String>("from"));
        TableColumn destinationCol1 = new TableColumn("Áfangastaður");
        destinationCol1.setCellValueFactory(
                new PropertyValueFactory<Flight, String>("to"));
        TableColumn priceCol1 = new TableColumn("Verð");
        priceCol1.setCellValueFactory(
                new PropertyValueFactory<Flight, Integer>("ecoPrice"));
        TableColumn airlineCol1 = new TableColumn("Flugfélag");
        airlineCol1.setCellValueFactory(
                new PropertyValueFactory<Flight, String>("airline"));
        TableColumn departureCol1 = new TableColumn("Brottfaratími");
        departureCol1.setCellValueFactory(
                new PropertyValueFactory<Flight, Date>("departureDate"));
        TableColumn timingCol1 = new TableColumn("Klukkan");
        timingCol1.setCellValueFactory(
                new PropertyValueFactory<Flight, String>("departureTime"));
        returnTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        returnTable.getColumns().addAll(originCol1, destinationCol1, priceCol1, airlineCol1,departureCol1, timingCol1);
        departureFlight.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if(!empty) {
                    if(date.compareTo(LocalDate.now()) < 0) {
                        setDisable(true);
                    }
                }

            }
        });
        returnFlight.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if(!empty) {
                    if(date.compareTo(LocalDate.now()) < 0) {
                        setDisable(true);
                    }
                }

            }
        });
    }
}
