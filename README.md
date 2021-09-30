# Water-Meter-Reader-V2.0

**Note: The essential improvement brought to the program in this version is the addition of the graphical user interface (GUI), which was done by using JavaFX.**

Living in an apartment building or in a condominium means certain obligations for the residents, including presenting the monthly water consumption to the administrator of the owners' association and keeping the record of the water consumption.

The following program comes in the aid of this category of persons, in the sense that it offers them the possibility to indefinitely store water meter readings and, most importantly, to calculate at any time the current consumption of cold and hot water and to generate a ready-to-print flyer so that it can be handed immediately over to the building administration.

This way, the residents will no longer need to keep records on paper, to manually calculate the monthly water consumption and to write by hand each month the flyer for the administrator.

## Usage Features
1. Easilly adding new water meter readings;
2. The history of water meter records is saved forever and is accessible anytime;
3. Displaying the current water consumption;
4. Generating a ready-to-print text file (flyer) containing the curent water consumption and details about the current water meter reading and the last one;
5. The ability to delete the last water meter reading saved in case the user made mistakes regarding the date of the reading or the level indicated by the water meter;
6. The option to clear the whole list of recorded water indexes.

## Technical Features

#### About the option to add a new water meter reading:
1. Adding a new water meter reading requires the user to enter valid values for the date, the cold and hot water levels.
2. For easier use by the user, the input for the date is done by implementing a DatePicker. Because in real life the history of water meter readings needs to be ordered chronologically, **the user is restricted to pick a date between the one of the last saved water meter reading and the date of today**. As an exception, it is possible to enter any date from the past when the registry of readings is empty (e.g. when the user adds for the first time a reading or when he cleares the history of readings). This was done bysetting the day cell factory of the DatePicker.
3. For the values of cold and hot water levels to be valid, they must meet two conditions: firstly, the values must be of type double and, secondly, they need to be higher than the ones of the previous reasing.
4. Entering invalid values for cold and hot water levels causes the following: **raising a warning for the user about his mistake in real time, while he is typing** and **disabling the ok button**. This features were achieved by adding change listeners to the input text boxes, which observ modifications of the user's input and set the warning message accordingly, and by using a boolean binding to enable/disable the OK button.
5. Entering a valid water meter reading is confirmed to the user by **showing a message that which disappears automatically after 3 seconds if the user does not close it manually**.

#### About the option to show the current water consumption:
1. Selecting this menu item will open a new window containing either information about the current water consumption, either a message that informs the user he needs to enter at least two water meter readings to determine the water consumption.

#### About the feature to generate a ready to print text file (flyer) containing details about the current water consumption:
1. The Program has the ability to generate a ready to print text file containing information about the apartment number (optionally), the date it was generated, detailes about the last and the penultimate water meter readings (date of the readings, cold and hot water levels) and the current cold and hot water consumption.
2. When generating the flyer, **the user has the option to enter the number of its apartment if he decides so**. Therefore, the program checks if the input for the apartment number is valid, meaning that the text box is either empty, or it contains an integer number between 0 and 1000. Similar to the option of adding a new reading, incorrect input causes a warning to be displayed and the ok button to be deactivated.
3. **The successfully generated text file will open automatically after the user presses the ok button**.

#### About the feature to delete the last item:
1. **The program allows the user to delete the last recorded water meter reading only once**, until a new water meter reading is added. The reason for this restriction is that, in real lfe, a data registry in which all of the entries could be modified one by one would no longer be reliable or trusty.
2. **The choice to delete the last element of the water meter readings history is saved in a text file and reloaded each time the application is restarted, so the "deleting the last element only once" feature can not be fooled by exiting and restarting the program**.
3. Before proceeding to the deletion of the last entry, **the program displays a confirmation alert** for the user to confirm its intention.

#### Other technical features:
1. **The user's input is checked for every feature of the application, so that any possible exception that could crush the program is avoided (e.g. NumberFormatException, DateTimeException etc.).**
2. **The datbase of water meter readings is saved in a text file in key moments in the life cycle of the application** - e.g. after adding a new reading, after deleting an old one, before quiting the program etc.
3. As I wrote above, the user cand delete the last entry only once, until he enters a new one. So, **the availability of this option is expressed in the code by using a variable of type int, which holds the value 0 when the user is allowed to delete the last entry and the value 1 when he isn't allowed. This value is written in a text file and reloaded each time the aplication is started**. This way the program keeps track of a possible deletion, even if the program has been shut down.
4. The program contains blocks of code that **verifies if one of the above text files exists, before loading them, so that a possible FileNotFoundEception is avoided** (for the situation where the user accidentally or intentionally deletes one or both files).
5. **The dates displayed by the program are formatted to be shown in the European format (dd-MM-yyyy)**.
6. The numeric values of the cold and hot water levels are **rounded to two decimals** and displayed accordingly.
7. Displaying the date in the European format and the numeric values with to decimals in the TableView were achieved by **setting the cell factory of the coresponding table columns**.


5. The choice to delete the last element of the water meter readings history is saved in a text file and reloaded each time the application is restarted, so the "deleting the last element only once" feature can not be fooled by exiting and restarting the program.


1. The user's input is checked whenever he gives a command, so that any possible exception that could crush the program are avoided (e.g. NumberFormatException, DateTimeException etc.).
2. When adding a new water meter reading, the program does the following verifications:
    1) It checkes if the input for date is in the format "dd MM yyyy". Even though it's not showed in the app text, to be more user friendly, the program allows intuitive input in the format "d MM yyyy" or "dd M yyyy". Inputs of other formats aren't allowed, the user being asked to enter the right format.
    2) If the date entered by the user respects one of the formats listed above, than it is compared to the last date saved in the history of water meter readings, if it exists. For logical reasons, a valid new date needs to be set in time after the moment of the previous water meter reading.
    3) Regarding the input for the water meter values, firstly the program verifies if the input from the user is of type double.
    4) Secondly, for logical considerations, the program tests if the new water meter index is higher than the previous one recorded. If the test is negative, that means that maybe the input from the user is wrong or the last recorded water meter reading is incorrect. The application offers solutions for both of the cases - the user can reenter the correct input or delete the last recorded water meter index.
3. All the data entered by the user is saved in a text file in key moments of the life cycle of the application - e.g. after adding a new reading, after deleting an old one, before quiting the program etc.
4. The program permits the user to delete the last recorded water meter reading only once, until a new water meter reading is added. The reason for this restriction is that, in real lfe, a data registry in which all of the entries could be modified one by one would no longer be reliable or trusty.
5. The choice to delete the last element of the water meter readings history is saved in a text file and reloaded each time the application is restarted, so the "deleting the last element only once" feature can not be fooled by exiting and restarting the program.
6. The program contains blocks of code that verifies if one of the above text files (the one used for the database for the watermeter records and the other one used to store the choice of deleting the last element of the list of readings) exists, before loading them, so that a possible FileNotFoundEception is avoided (for the situation when the user   manually deletes accidentaly or intentionaly one of the two files or both of them).
7. The program has the option to generate a text file containing details about the current water consumption, inlcuding the possibility to enter the apartment number.
8. The dates displayed by the program are formatted to be shown in the European format (dd-MM-yyyy).

## Screenshots
Adding new water meter readings:\
![wm1](https://user-images.githubusercontent.com/90447306/135104862-81f0d966-1433-4ec1-b6fc-65d9b437446d.jpg)

Printing the history of water meter readings, diplaying the current water consumption, deleting the last entry and generating the consumption flyer:\
![wm2](https://user-images.githubusercontent.com/90447306/135104865-23462b3e-0d16-4317-95d1-26ad232c3e22.jpg)

The consumption flyer:\
![wm3](https://user-images.githubusercontent.com/90447306/135104869-29e92f2a-ab30-4f82-bfa1-75d95b785b7f.jpg)

## How to Download
To run the program please download the .jar file uploaded in the repository.

## Future Features
- Adding a GUI to the application.


