/*
 * @(#) Settings.java 1.0 2020/04/30
 *
 * Copyright (c) 2020 Aberystwyth University.
 * All rights reserved.
 *
 */


package uk.ac.aber.cs221.nine;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Setting class where it include all the settings function
 *
 * @author Dixon[dkc2], Taavi[tak16]
 * @version 1.0
 */
public abstract class Settings {

   // /////////////// //
   // Class Variables //
   // /////////////// //

   private static int amountOfWordsInQuiz = 5;
   private static String languageOfQuiz = "English";
   private static String fontSize = "Small";

   // /////// //
   // METHODS //
   // /////// //

   /**
    * Get the amount of words for the quiz
    *
    * @return the amount of words for the quiz
    */
   public static int getAmountOfWordsInQuiz() {
      return amountOfWordsInQuiz;
   }

   /**
    * Set the amount of words for the quiz
    *
    * @param amount number of quiz
    */
   public static void setAmountOfWordsInQuiz(int amount) {
      amountOfWordsInQuiz = amount;
   }

   /**
    * Get the language for the quiz
    *
    * @return language for the quiz
    */
   public static String getLanguageOfQuiz() {
      return languageOfQuiz;
   }

   /**
    * Set the language for the quiz
    *
    * @param language English, Welsh or both
    */
   public static void setLanguageOfQuiz(String language) {
      languageOfQuiz = language;
   }

   /**
    * Getter method for the font size
    *
    * @return font size
    */
   public static String getFontSize() {
      return fontSize;
   }

   /**
    * Set the font size for the system to display
    *
    * @param size small, medium or large
    */
   public static void setFontSize(String size) {
      fontSize = size;
   }

   /**
    * Save the settings
    */
   public static void saveSettings() {
      JSONObject settings = new JSONObject();

      settings.put("wordAmountInQuiz", getAmountOfWordsInQuiz());
      settings.put("quizLanguage", getLanguageOfQuiz());
      settings.put("fontSize", getFontSize());

      try (FileWriter fileWriter = new FileWriter("src/settings.json")) {
         fileWriter.write(settings.toJSONString());
         fileWriter.flush();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   /**
    * Load the settings
    */
   public static void loadSettings() {
      JSONParser jsonParser = new JSONParser();

      try (FileReader reader = new FileReader("src/settings.json")) {
         // Read JSON file
         JSONObject settingsObject = (JSONObject) jsonParser.parse(reader);

         setAmountOfWordsInQuiz(Math.toIntExact((Long) settingsObject.get("wordAmountInQuiz")));
         setLanguageOfQuiz((String) settingsObject.get("quizLanguage"));
         setFontSize((String) settingsObject.get("fontSize"));

      } catch (ParseException | IOException e) {
         System.out.println("Settings File not found, will be created on exit.");
      }
   }
}
