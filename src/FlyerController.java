package sample;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;

public class FlyerController {

    @FXML
    private DialogPane flyerDialog;

    @FXML
    private Label announceLabel1;

    @FXML
    private Label announceLabel2;

    @FXML
    private TextField inputField;

    @FXML
    private Label inputAlert;

    private BooleanBinding validation;

    public void initialize(){
        flyerDialog.setHeaderText("Water Consumption Flyer Generator");
        announceLabel1.setWrapText(true);
        announceLabel2.setWrapText(true);

        // BooleanBinding to validate the apartment number:
        validation = Bindings.createBooleanBinding(()-> {
            String input = inputField.getText();
            try {
                int value = Integer.parseInt(input);
                if (value <= 0) {
                    return false;
                } else if (value >= 1000) {
                    return false;
                } else {
                    return true;
                }

            } catch (NumberFormatException e) {
                if (input.isEmpty() || input.trim().isEmpty()) {
                    return true;
                } else {
                    return false;
                }
            }
        }, inputField.textProperty());

        //Change Listener to signal a valid apartment number:
        inputField.textProperty().addListener((observable, oldValue, newValue)->{
            try{
                int value = Integer.parseInt(newValue);
                    if (value<=0){
                        inputAlert.setText("You cannot enter a negative apartment number");
                        inputAlert.setTextFill(Paint.valueOf("red"));
                    }else if (value>=1000){
                        inputAlert.setText("The apartment number entered is too large");
                        inputAlert.setTextFill(Paint.valueOf("red"));
                    }else if (value>0 && value<1000){
                        inputAlert.setText("Valid apartment number");
                        inputAlert.setTextFill(Paint.valueOf("green"));
                    }

            }catch (NumberFormatException e){
                if (newValue.isEmpty() || newValue.trim().isEmpty()) {
                    inputAlert.setText("");
                }else {
                    inputAlert.setText("Invalid apartment number");
                    inputAlert.setTextFill(Paint.valueOf("red"));
                }
            }

        });
    }

    public Boolean getValidation() {
        return validation.get();
    }

    public BooleanBinding validationProperty() {
        return validation;
    }

    // Method to call the function to write the text file regarding the current consumption:
    public boolean generateFlyer() {
        try {
            int apartmentNo = Integer.parseInt(inputField.getText());
            if (Data.getInstance().writeFlyer(apartmentNo)) {
                System.out.println("Congrats, flyer generated!");
                return true;
            } else {
                return false;
            }
        }catch (NumberFormatException e){
            if (inputField.getText().isEmpty() || inputField.getText().trim().isEmpty()){
                int apartmentNo = -1;
                if (Data.getInstance().writeFlyer(-1)){
                    return true;
                }else{
                    return false;
                }
            }else {
                return false;
            }
        }
    }
}
