package sample;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Controller {

    @FXML
    private BorderPane mainWindow;

    @FXML
    private TableView<WaterIndex> readingsTable;

    @FXML
    private TableColumn<WaterIndex, LocalDate> dateColumn;

    @FXML
    private TableColumn<WaterIndex, Double> coldColumn;

    @FXML
    private TableColumn<WaterIndex, Double> hotColumn;

    private Data waterIndexData;

    File file = new File("DataBase.txt");

    File deletionChecker = new File("DeletionChecker.txt");


    public void initialize() {
        waterIndexData = Data.getInstance();

        // Checking if the text files for storing the history of water meter readings and the choice of deleting the last entry and, if the test is affirmative, loading the data from those text files:
        if ((file.exists()) && file.length()>1){
            waterIndexData.readData();
        }
        if ((deletionChecker.exists()) && deletionChecker.length()>0){
            waterIndexData.readChecker();
        }
        readingsTable.setItems(waterIndexData.getList());

        // Setting the cell factory for the dateColumn in the TableVIew to show the dates in the European format:
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        dateColumn.setCellFactory(new Callback<TableColumn<WaterIndex, LocalDate>, TableCell<WaterIndex, LocalDate>>() {
            @Override
            public TableCell<WaterIndex, LocalDate> call(TableColumn<WaterIndex, LocalDate> waterIndexLocalDateTableColumn) {
                TableCell<WaterIndex, LocalDate> cell = new TableCell<>(){
                    @Override
                    protected void updateItem(LocalDate localDate, boolean b) {
                        super.updateItem(localDate, b);
                        if (b){
                            setText(null);
                        }else{
                            setText(formatter.format(localDate));
                        }
                    }
                };
                return cell;
            }
        });

        // Setting the cell factory for the coldColumn and the hotColumn in the tableView, so it displays the values within two decimals and, if the values are lower than 0, it displays them including the integer part 0.
        DecimalFormat df = new DecimalFormat("#0.00");
        coldColumn.setCellFactory(new Callback<TableColumn<WaterIndex, Double>, TableCell<WaterIndex, Double>>() {
            @Override
            public TableCell<WaterIndex, Double> call(TableColumn<WaterIndex, Double> waterIndexDoubleTableColumn) {
                TableCell<WaterIndex,Double> cell = new TableCell<WaterIndex, Double>(){
                    @Override
                    protected void updateItem(Double coldReading, boolean b) {
                        super.updateItem(coldReading, b);
                        if (b){
                            setText(null);
                        }else{
                            setText(df.format(coldReading));
                        }
                    }
                };
                return cell;
            }
        });

        hotColumn.setCellFactory(new Callback<TableColumn<WaterIndex, Double>, TableCell<WaterIndex, Double>>() {
            @Override
            public TableCell<WaterIndex, Double> call(TableColumn<WaterIndex, Double> waterIndexDoubleTableColumn) {
                TableCell<WaterIndex, Double> cell = new TableCell<WaterIndex, Double>(){
                    @Override
                    protected void updateItem(Double hotReading, boolean b) {
                        super.updateItem(hotReading, b);
                        if (b){
                            setText(null);
                        }else{
                            setText(df.format(hotReading));
                        }
                    }
                };
                return cell;
            }
        });
    }

    // Event handler to create the dialog window for entering a new entry in the list of water meter readings and to retrieve the new entry from the dialog's controller and adding it to the list.
    @FXML
    public void showNewReadingDialog(){
        Dialog<ButtonType> dialog = new Dialog<ButtonType>();
        dialog.initOwner(mainWindow.getScene().getWindow());
        dialog.setTitle("Add New Water Meter Reading");

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("newreadingdialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException e){
            System.out.println("Could not load the dialog!");
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        NewReadingController newReadingController = fxmlLoader.getController();

        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(newReadingController.validation.not());

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK){

            WaterIndex newWaterIndex = newReadingController.getNewReading();

            if (newWaterIndex == null){
                System.out.println("You didn't enter valid data!");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.setContentText("You didn't enter valid data for the water meter reading!");
                alert.showAndWait();
                showNewReadingDialog();
            }else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.setContentText("New water meter reading successfully added!");
                PauseTransition delay = new PauseTransition(Duration.seconds(3));
                delay.setOnFinished(e -> alert.hide());
                alert.show();
                delay.play();
                waterIndexData.addReading(newWaterIndex);
                waterIndexData.writeDataBase();
                waterIndexData.setChecker(0);
                waterIndexData.writeChecker();
            }
        }
    }

    // Event handler that controls the currentConsumption MenuItem from the MenuBar, which determine and displays the current water consumption:
    @FXML
    public void currentConsumption() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Current Water Consumption");
        alert.setHeaderText(null);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        if (waterIndexData.getList().size() < 2) {
            alert.setContentText("To determine your current water consumption you need to enter at least two water meter readings before using this command.");
        } else {
            double oldCold = waterIndexData.getList().get(waterIndexData.getList().size() - 2).getCold();
            double newCold = waterIndexData.getList().get(waterIndexData.getList().size() - 1).getCold();
            double coldConsumption = newCold - oldCold;

            double oldHot = waterIndexData.getList().get(waterIndexData.getList().size() - 2).getHot();
            double newHot = waterIndexData.getList().get(waterIndexData.getList().size() - 1).getHot();
            double hotConsumption = newHot - oldHot;

            alert.setContentText("Current water consumption is: " + String.format("%.2f", coldConsumption) + " of m\u00B3 cold water and " +
                    String.format("%.2f", hotConsumption) + " m\u00B3 of hot water.");
        }

        alert.showAndWait();
    }

    // Event handler for the consumptionFlyer MenuItem which calls the function to generate a text file containing the current water consumption:
    @FXML
    public void consumptionFlyer(){
        Dialog<ButtonType> flyerDialog = new Dialog<ButtonType>();
        flyerDialog.initOwner(mainWindow.getScene().getWindow());

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("flyerdialog.fxml"));
        try {
            flyerDialog.getDialogPane().setContent(fxmlLoader.load());
        }catch (IOException e){
            System.out.println("Could not load the dialog!");
            e.printStackTrace();
        }

        flyerDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        flyerDialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        FlyerController flyerController = fxmlLoader.getController();

        flyerDialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(flyerController.validationProperty().not());

        Optional<ButtonType> result = flyerDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){

            boolean checker = flyerController.generateFlyer();
            if (checker){

            }else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(null);
                alert.setHeaderText(null);
                alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alert.setContentText("Can't generate the flyer because you need to enter at least two water meter readings.");
                alert.showAndWait();
            }
        }
    }

    // Event handler for the deleteLastEntry MenuItem which calls the function to delete the last entry:
    @FXML
    public void deleteLastEntry(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.setContentText("Are you sure you want to delete the last recorded water meter reading?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK){
            if (waterIndexData.getList().size()<1){
                Alert alertEmptyList = new Alert(Alert.AlertType.INFORMATION);
                alertEmptyList.setTitle(null);
                alertEmptyList.setHeaderText(null);
                alertEmptyList.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                alertEmptyList.setContentText("It is impossible to delete the last recorded water reading.\n There are no recorded water readings.");
                alertEmptyList.showAndWait();
            }else{
                if (waterIndexData.getChecker() == 0){
                    Alert validationAlert = new Alert(Alert.AlertType.INFORMATION);
                    validationAlert.setTitle(null);
                    validationAlert.setHeaderText(null);
                    validationAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    validationAlert.setContentText("The last recorded water meter reading has been deleted:\n" + waterIndexData.getList().get(waterIndexData.getList().size()-1).toString());
                    validationAlert.showAndWait();

                    waterIndexData.deleteLastElement();
                    waterIndexData.writeDataBase();
                    waterIndexData.writeChecker();

                }else{
                    System.out.println(waterIndexData.getChecker());
                    Alert alreadyDeleted = new Alert(Alert.AlertType.INFORMATION);
                    alreadyDeleted.setTitle(null);
                    alreadyDeleted.setHeaderText(null);
                    alreadyDeleted.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alreadyDeleted.setContentText("Can't proceed because you already deleted the last element of the list of water readings.");
                    alreadyDeleted.showAndWait();
                }
            }
        }
    }

    // Event handler for the resetAllReadings MenuItem which calls the function to clear the whole history:
    @FXML
    public void resetAllReadings(){

        if (waterIndexData.getList().isEmpty()){
            Alert alertNO = new Alert(Alert.AlertType.INFORMATION);
            alertNO.setTitle(null);
            alertNO.setHeaderText(null);
            alertNO.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alertNO.setContentText("The list of water meter readings is already empty!");
            alertNO.showAndWait();
        }else{
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle(null);
            confirmation.setHeaderText(null);
            confirmation.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            confirmation.setContentText("Are you sure you want to delete the whole history of water meter readings?");
            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Alert alertOK = new Alert(Alert.AlertType.INFORMATION);
                alertOK.setTitle(null);
                alertOK.setHeaderText(null);
                alertOK.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                waterIndexData.clearList();
                alertOK.setContentText("The history of water meter data was successfully cleared!");
                alertOK.showAndWait();
                waterIndexData.writeDataBase();
                waterIndexData.setChecker(0);
                waterIndexData.writeChecker();
            }
        }
    }

    // Event handler for the exit MenuItem which calls the method to close the application:
    @FXML
    public void exit(){
        waterIndexData.writeDataBase();
        waterIndexData.writeChecker();
        Platform.exit();
    }

}
