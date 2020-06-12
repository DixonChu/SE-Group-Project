/*
 * @(#) Main.java 1.0 2020/04/30
 *
 * Copyright (c) 2020 Aberystwyth University.
 * All rights reserved.
 *
 */


package uk.ac.aber.cs221.nine;

import javafx.application.Application;
import javafx.stage.Stage;
import uk.ac.aber.cs221.nine.dictionary.Dictionary;
import uk.ac.aber.cs221.nine.dictionary.PracticeList;

import java.io.IOException;

/**
 * This is the main class for the Welsh Dictionary
 *
 * @author Dixon[dkc2], Taavi[tak16]
 * @version 1.0
 */
public class Main extends Application {

   // /////////////// //
   // Class Variables //
   // /////////////// //

   public static UIHandler uiHandler;


   // /////// //
   // METHODS //
   // /////// //

   public static void main(String[] args) {
      launch(args);
   }

   @Override
   public void start(Stage primaryStage)
   throws Exception {
      // Load from files
      Settings.loadSettings();
      Dictionary.loadDictionaryFromFile();
      PracticeList.loadPracticeListFromFile();

      // Create an instance of UIHandler
      uiHandler = new UIHandler();

      // Make the application save when it is closed
      primaryStage.setOnCloseRequest(e -> {
         try {
            Settings.saveSettings();
            Dictionary.saveDictionaryToFile();
            PracticeList.savePracticeListToFile();
         } catch (IOException ioException) {
            ioException.printStackTrace();
         }
      });

      // Initialize UI
      uiHandler.initializeUI(primaryStage);

   }
}
