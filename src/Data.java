package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Data {

    private static Data instance = new Data();

    private ObservableList<WaterIndex> list;
    private int checker;

    private Data() {
        this.list = FXCollections.observableArrayList();
        File file = new File("DeletionChecker.txt");
        if ((file.exists()) && file.length()>0){
            checker = readChecker();
        }else {
            checker = 0;
        }
    }


    public static Data getInstance() {
        return instance;
    }

    public ObservableList<WaterIndex> getList() {
        return list;
    }

    public int getChecker() {
        return checker;
    }

    public void setChecker(int checker) {
        this.checker = checker;
    }

    // Method to add a new water meter reading to the list fo records:
    public void addReading(WaterIndex index){
        this.list.add(index);
    }

    // Method to determine the current water consumption:
    public String waterConsumption() {
        if (list.size() < 2) {
            return ("To determine your current water consumption you need to enter at least two water meter reading.");
        } else {
            double oldColdWater = list.get(list.size() - 2).getCold();
            double newColdWater = list.get(list.size() - 1).getCold();
            double coldWaterConsumption = newColdWater - oldColdWater;

            double oldHotWater = list.get(list.size() - 2).getHot();
            double newHotWater = list.get(list.size() - 1).getHot();
            double hotWaterConsumption = newHotWater - oldHotWater;

            return ("Current water consumption is: " + ((double)Math.round(coldWaterConsumption*100)/100) + " m\u00B3 of cold water and " +
                    String.format("%.2f", hotWaterConsumption) + " m\u00B3 of hot water.");
        }
    }

    // Method to write a text file containing the current water consumption:
    public boolean writeFlyer(int apartmentNumber) {
        try {
            if (list.size() < 2) {
                System.out.println("Can't generate the flyer because you need to enter at least two water meter readings.");
                return false;
            } else {
                System.out.println("Flyer successfully generated!");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate today = LocalDate.now();
                String dateNow = today.format(formatter);

                try (FileWriter flyer = new FileWriter("Water Consumption at " + dateNow + ".txt")) {
                    if (apartmentNumber == -1){
                        flyer.write("\n\n\t Water Consumption at " + dateNow);
                    }else {
                        flyer.write("\n\n\t Water Consumption for apartment no. " + apartmentNumber + " at " + dateNow);
                    }
                    flyer.write("\n\n\nWater reading for last month (" + list.get(list.size() - 2).formatterToString() + ")" + "\n\t Cold water: " + String.format("%.2f",list.get(list.size() - 2).getCold()) + " m\u00B3,   Hot water: " + String.format("%.2f", list.get(list.size() - 2).getHot()) + " m\u00B3.");
                    flyer.write("\n\nWater reading for the current month (" + list.get(list.size() - 1).formatterToString() + ")" + "\n\t Cold water: " + String.format("%.2f", list.get(list.size() - 1).getCold()) + " m\u00B3,   Hot water: " + String.format("%.2f", list.get(list.size() - 1).getHot()) + " m\u00B3.");
                    flyer.write("\n\n" + waterConsumption());
                    File file = new File("Water Consumption at " + dateNow + ".txt");
                    java.awt.Desktop.getDesktop().edit(file);
                    return true;
                }
            }
        }catch (IOException e){
            System.out.println("IOException raised");
            return false;
        }
    }

    // Method to delete the last entry from the water meter readings list:
    public void deleteLastElement() {
        if (!list.isEmpty()) {
            if (this.checker == 0) {

                System.out.println("The last entry has been deleted ( " + list.get(list.size() - 1).toString());
                this.checker = 1;
                this.list.remove(list.size() - 1);

            } else {
                System.out.println("The last entry has already been deleted");
            }
        }else{
            System.out.println("The list of water meter indexes is empty. It isn't possible to delete a last entry.");
        }
    }

    // Method to delete all the records from the list:
    public void clearList(){
        this.list.clear();
        System.out.println("The list of water meter readings has been successfully cleared!");
    }

    // Method to write in a text file the choice of deleting the last entry from the list (deletion checker):
    public void writeChecker() {
        try (FileWriter file = new FileWriter("DeletionChecker.txt")) {
            file.write(Integer.toString(checker));
        }catch (IOException e){
            System.out.println("Can't write DeletionChecker.txt");
        }
    }

    // Method to read the deletion checker from the text file:
    public int readChecker() {
        try (Scanner scanner = new Scanner(new FileReader("DeletionChecker.txt"))) {
            this.checker = scanner.nextInt();
            return this.checker;
        }catch (IOException e){
            System.out.println("Can't read DeletionChecker.txt");
            return -1;
        }
    }

    // Method to write a text file containing the list of water meter readings:
    public void writeDataBase() {
        try (FileWriter doc = new FileWriter("DataBase.txt")) {
            for (WaterIndex waterIndex : list) {
                doc.write(waterIndex.formatterToString() + ", " + waterIndex.getCold() + ", " + waterIndex.getHot() + "\n");
            }
        }catch (IOException e){
            System.out.println("Invalid data entered!");
            e.printStackTrace();
        }
    }

    // Method to load the water meter history from the generated text file:
    public void readData() {
        try (Scanner scanner = new Scanner(new FileReader("DataBase.txt"))) {
            scanner.useDelimiter(", ");
            int e = 0;
            while (scanner.hasNextLine()) {
                String dateString = scanner.next();
                scanner.skip(scanner.delimiter());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate date = LocalDate.parse(dateString, formatter);
                double cold = scanner.nextDouble();
                scanner.skip(scanner.delimiter());
                String hotString = scanner.nextLine();
                double hot = Double.parseDouble(hotString);
                WaterIndex temp = new WaterIndex(date, cold, hot);
                list.add(e, temp);
                e++;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

