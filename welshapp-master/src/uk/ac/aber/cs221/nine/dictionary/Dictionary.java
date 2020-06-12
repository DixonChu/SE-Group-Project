/*
 * @(#) Dictionary.java 1.0 2020/04/30
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
 * This is the dictionary class
 *
 * @author: Dixon[dkc2], Taavi[tak16]
 * @version: 1.0
 */
public class Dictionary {

   // /////////////// //
   // Class Variables //
   // /////////////// //


   public static LinkedList<Word> words = new LinkedList<>();

   // /////// //
   // METHODS //
   // /////// //

   /**
    * Loading dictionary JSON file
    */
   public static void loadDictionaryFromFile() {
      JSONParser jsonParser = new JSONParser();

      try (FileReader reader = new FileReader("src/dictionary.json")) {
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

            Word loadedWord = new Word(extra + next.get("english").toString().strip(), next.get("welsh").toString().strip(), next.get("wordType").toString().strip());

            addWord(loadedWord, false);
         }
      } catch (ParseException | IOException e) {
         e.printStackTrace();
      }
   }

   /**
    * Saving words to dictionary JSON file
    */
   public static void saveDictionaryToFile() {
      JSONArray wordsToSave = new JSONArray();

      for (Word word : getDictionary()) {
         wordsToSave.add(word.wordToJSONObject());
      }

      try (FileWriter fileWriter = new FileWriter("src/dictionary.json")) {
         fileWriter.write(wordsToSave.toJSONString());
         fileWriter.flush();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   /**
    * Dictionary getter method
    *
    * @param word words in dictionary
    * @return word if it exists in the dictionary
    */
   public static Word getWord(Word word) {
      if (getDictionary().contains(word)) {
         return getDictionary().get(getDictionary().indexOf(word));
      }
      return null;
   }

   /**
    * Dictionary getter method
    *
    * @return words in dictionary
    */
   public static LinkedList<Word> getDictionary() {
      return words;
   }

   /**
    * Add new word into dictionary list
    *
    * @param word                word to add
    * @param addIntoPracticeList add word to practice list
    */
   public static void addWord(Word word, boolean addIntoPracticeList) {
      words.add(word);
      // If true add the word into the practice list
      if (addIntoPracticeList) {
         PracticeList.addIntoPracticedWords(word);
      }
   }

   /**
    * Remove word from the dictionary list
    *
    * @param word                   word to remove
    * @param removeFromPracticeList remove word from practice list if word to remove are in the practice list
    */
   public static void removeWord(Word word, boolean removeFromPracticeList) {
      words.remove(word);
      // If word in practice list and true, remove the word
      if (PracticeList.getPracticedWords().contains(word) && removeFromPracticeList) {
         PracticeList.removeFromPracticedWords(word);
      }
   }


}
