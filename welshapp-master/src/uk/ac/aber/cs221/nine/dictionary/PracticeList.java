/*
 * @(#) PracticeList.java 1.0 2020/04/30
 *
 * Copyright (c) 2020 Aberystwyth University.
 * All rights reserved.
 *
 */

package uk.ac.aber.cs221.nine.dictionary;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import uk.ac.aber.cs221.nine.word.Word;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
 * This is the practice list class
 *
 * @author: Dixon[dkc2], Taavi[tak16]
 * @version: 1.0
 */
public abstract class PracticeList {

   // /////////////// //
   // Class Variables //
   // /////////////// //

   private static final LinkedList<Word> favWords = new LinkedList<>();

   // /////// //
   // METHODS //
   // /////// //

   /**
    * Practice words getter method
    *
    * @return favourite words
    */
   public static LinkedList<Word> getPracticedWords() {
      return favWords;
   }

   /**
    * Add words into practice list
    *
    * @param word words in practice list
    */
   public static void addIntoPracticedWords(Word word) {
      word.setFavorited(true);
      favWords.add(word);
      // Set the word favourite status to true in dictionary after adding the word to practice list
      if (Dictionary.getDictionary().contains(word)) {
         Dictionary.getWord(word).setFavorited(true);
      }
   }

   /**
    * Remove word from practice list
    *
    * @param word words in practice list
    */
   public static void removeFromPracticedWords(Word word) {
      favWords.remove(word);
      // Set the word favourite status to false in dictionary after removing the word in practice list
      if (Dictionary.getDictionary().contains(word)) {
         Dictionary.getWord(word).setFavorited(false);
      }
   }

   /**
    * Saving practice list into JSON file
    *
    * @throws IOException exception will throws if file does not exist
    */
   public static void savePracticeListToFile()
   throws IOException {
      JSONArray wordsToSave = new JSONArray();

      for (Word word : getPracticedWords()) {
         Word word2 = word;
         wordsToSave.add(word2.wordToJSONObject());
      }

      try (FileWriter fileWriter = new FileWriter("src/practiceList.json")) {
         fileWriter.write(wordsToSave.toJSONString());
         fileWriter.flush();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   /**
    * Loading practice list JSON file
    */
   public static void loadPracticeListFromFile() {
      JSONParser jsonParser = new JSONParser();

      try (FileReader reader = new FileReader("src/practiceList.json")) {
         // Read JSON file
         Object object = jsonParser.parse(reader);

         //Iterate over word list array
         JSONArray wordList = (JSONArray) object;
         for (Object o : wordList) {

            JSONObject next = (JSONObject) o;
            String extra = "";

            if (((String) next.get("wordType")).equalsIgnoreCase("verb")) {
               extra = "to ";
            }

            Word loadedWord = new Word(extra + next.get("english"), (String) next.get("welsh"), (String) next.get("wordType"));

            addIntoPracticedWords(loadedWord);
         }

      } catch (ParseException | IOException e) {
         System.out.println("PracticeList file not found, will be created on exit.");
      }
   }

}
