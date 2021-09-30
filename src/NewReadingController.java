package sample;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class NewReadingController {

    @FXML
    private DialogPane NewReadingDialog;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField coldField;

    @FXML
    private Label coldAlert;

    @FXML
    private TextField hotField;

    @FXML
    private Label hotAlert;

    double oldCold;
    double oldHot;
    public BooleanBinding validation;
    private Data waterIndexData = Data.getInstance();


    public void initialize() {

        // Formatting the date shown after a date is picket by the user in the European format:
        datePicker.setConverter(new StringConverter<LocalDate>() {
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            @Override
            public String toString(LocalDate date) {
                if (date!=null){
                    return formatter.format(date);
                }else{
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String s) {
                if (s != null && !s.isEmpty()){
                    return LocalDate.parse(s, formatter);
                }else{
                    return null;
                }
            }
        });


        //Blocking the user to pick a date after the date of today.
        datePicker.setDayCellFactory(picker -> new DateCell(){
            @Override
            public void updateItem(LocalDate localDate, boolean b) {
                super.updateItem(localDate, b);
                LocalDate comparator = LocalDate.now();
                setDisable(b || localDate.compareTo(comparator)>0);
            }
        });

        //Blocking the user to pick a date prior to the date of the last entry registered in the water meter history.
        if (!Data.getInstance().getList().isEmpty()) {
            datePicker.setDayCellFactory(picker -> new DateCell() {
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    LocalDate comparator = waterIndexData.getList().get(waterIndexData.getList().size() - 1).getDate();
                    setDisable(empty || date.compareTo(comparator) <= 0 || date.compareTo(LocalDate.now())>0);
                }
            });
        }

        //BooleanBinding used for disabling the OK button until the user enters the correct input in the water meter text boxes, input which is of type double and, when there are prior readings registered in the app, greater than the last one enter.
        validation = Bindings.createBooleanBinding(() -> {
            String coldString = coldField.getText();
            String hotString = hotField.getText();
            try {
                double coldBrute = Double.parseDouble(coldString);
                double hotBrute = Double.parseDouble(hotString);
                double cold = Math.round(coldBrute*100.0)/100.0;
                double hot = Math.round(hotBrute*100.0)/100.0;
                if (Data.getInstance().getList().size() >= 1) {
                    oldCold = waterIndexData.getList().get(waterIndexData.getList().size()-1).getCold();
                    oldHot = waterIndexData.getList().get(waterIndexData.getList().size()-1).getHot();
                    if (cold > oldCold && hot > oldHot) {
                        return true;
                    } else {
                        return false;
                    }
                } else if (cold>0.0d && hot >0.0d){
                    return true;
                }else return false;

            } catch (NumberFormatException e) {
                return false;
            }
        }, coldField.textProperty(), hotField.textProperty());

        //Change listener for the coldField, which listens for changes in this field and it sets in realtime the text of the label coldAlert depending on the correct/incorrect input from the user.
        coldField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double newValueDoubleRaw = Double.parseDouble(newValue);
                double newValueDouble = Math.round(newValueDoubleRaw*100.0)/100.0;
                if (waterIndexData.getList().size()>=1) {
                    double oldValueDoubleRaw = waterIndexData.getList().get(waterIndexData.getList().size() - 1).getCold();
                    double oldValueDouble = Math.round(oldValueDoubleRaw*100.0)/100.0;
                    if (newValueDouble <= oldValueDouble) {
                        coldAlert.setText("Enter a value higher than the last reading (" + oldValueDouble + " m\u00B3) \nor delete the last entry in the water meter reading history.");
                        coldAlert.setTextFill(Paint.valueOf("red"));
                    } else {
                        coldAlert.setText("Valid cold water reading");
                        coldAlert.setTextFill(Paint.valueOf("green"));
                    }
                }else if (newValueDouble>0.0d){
                    coldAlert.setText("Valid cold water reading");
                    coldAlert.setTextFill(Paint.valueOf("green"));
                }else{
                    coldAlert.setText("Enter a value higher than 0.0");
                    coldAlert.setTextFill(Paint.valueOf("red"));
                }
            } catch (NumberFormatException e) {
                coldAlert.setText("Please enter a valid decimal number");
                coldAlert.setTextFill(Paint.valueOf("red"));
            }
        });

        //Change listener for the hotField, which listens for changes in this field and it sets in realtime the text of the label hotAlert depending on the correct/incorrect input from the user.
        hotField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double newValueDoubleRaw = Double.parseDouble(newValue);
                double newValueDouble = Math.round(newValueDoubleRaw*100.0)/100.0;
                if (waterIndexData.getList().size()>=1) {
                    double oldValueDoubleRaw = waterIndexData.getList().get(waterIndexData.getList().size() - 1).getHot();
                    double oldValueDouble = Math.round(oldValueDoubleRaw*100.0)/100.0;
                    if (newValueDouble <= oldValueDouble) {
                        hotAlert.setText("Enter a value higher than the last reading (" + oldValueDouble + " m³) \nor delete the last entry in the water meter reading history.");
                        hotAlert.setTextFill(Paint.valueOf("red"));
                    } else {
                        hotAlert.setText("Valid hot water reading");
                        hotAlert.setTextFill(Paint.valueOf("green"));
                    }
                }else if (newValueDouble>0.0d){
                    hotAlert.setText("Valid hot water reading");
                    hotAlert.setTextFill(Paint.valueOf("green"));
                }else {
                    hotAlert.setText("Enter a value higher than 0.0");
                    hotAlert.setTextFill(Paint.valueOf("red"));
                }
            } catch (NumberFormatException e) {
                hotAlert.setText("Please enter a valid decimal number");
                hotAlert.setTextFill(Paint.valueOf("red"));
            }
        });
    }


    //Creating a new water meter reading entry:
    public WaterIndex getNewReading() {
        if (datePicker.getValue() == null) {
            System.out.println("invalid date");
            return null;
        }

        LocalDate date = datePicker.getValue();

        if (!waterIndexData.getList().isEmpty()) {
            LocalDate oldDate = waterIndexData.getList().get(waterIndexData.getList().size() - 1).getDate();
            if (date.isBefore(oldDate) || date.isEqual(oldDate)) {
                System.out.println("Date entered is before or equal with the last one entered");
                return null;
            }
        }

        String coldString = coldField.getText();

        try {
            double coldGross = Double.parseDouble(coldString);
            double cold = Math.round(coldGross*100.00)/100.00;

            String hotString = hotField.getText();

            try {
                double hotGross = Double.parseDouble(hotString);
                double hot = Math.round(hotGross*100.00)/100.00;
                WaterIndex newReading = new WaterIndex(date, cold, hot);
                return newReading;
            } catch (NumberFormatException e) {
                System.out.println("Invalid hot");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid cold");
        }
        return null;
    }
}
