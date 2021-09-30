package sample;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class WaterIndex {

    private SimpleObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private SimpleDoubleProperty cold = new SimpleDoubleProperty();
    private SimpleDoubleProperty hot = new SimpleDoubleProperty();


    public WaterIndex(LocalDate date, double cold, double hot) {

        this.date.set(date);
        this.cold.set(cold);
        this.hot.set(hot);
    }

    public LocalDate getDate() {
        return date.get();
    }

    public double getCold() {
        return cold.get();
    }

    public double getHot() {
        return hot.get();
    }


    public String formatterToString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate newDate = date.get();
        String text = newDate.format(formatter);

        return text;
    }


    @Override
    public String toString() {
        double coldWater = cold.getValue();
        double hotWater = hot.getValue();
        return  "Date Of The Reading: " + formatterToString() +
                "\nCold Water Index: " + coldWater + " m\u00B3" +
                "\nHot Water Index: " + hotWater + " m\u00B3";
    }
}
