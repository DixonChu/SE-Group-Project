/*
 * @(#) Word.java 1.0 2020/04/30
 *
 * Copyright (c) 2020 Aberystwyth University.
 * All rights reserved.
 *
 */

package uk.ac.aber.cs221.nine.word;

import org.json.simple.JSONObject;

/**
 * This is the word class
 *
 * @author Dixon[dkc2], Taavi[tak16]
 * @version 1.0
 */
public class Word {

   // /////////////// //
   // Class Variables //
   // /////////////// //

   private String english;
   private String welsh;
   private String wordType;
   private boolean favorited;

   // /////// //
   // METHODS //
   // /////// //

   /**
    * Word constructor
    *
    * @param english word in english language
    * @param welsh   word in welsh language
    * @param wt      word type of the word
    */
   public Word(String english, String welsh, String wt) {
      this.english = english;
      this.welsh = welsh;
      this.wordType = wt;
      this.setFavorited(false);
   }

   /**
    * Default constructor
    */
   public Word() {
   }

   /**
    * Converts a Word object into a JSONObject for saving, whilst keeping in mind to remove "to " prefix from verbs
    *
    * @return Word as a JSONObject, having "to " removed for verbs
    */
   public JSONObject wordToJSONObject() {

      JSONObject wordJSON = new JSONObject();

      if (this.getWordType().equalsIgnoreCase("verb")) {
         wordJSON.put("english", getEnglish().substring(3));
      } else {
         wordJSON.put("english", getEnglish());
      }

      wordJSON.put("welsh", getWelsh());
      wordJSON.put("wordType", getWordType());

      return wordJSON;
   }

   /**
    * English word getter method
    *
    * @return english word
    */
   public String getEnglish() {
      return english;
   }

   /**
    * English word setter method
    *
    * @param english word in english
    */
   public void setEnglish(String english) {
      this.english = english;
   }

   /**
    * Welsh word getter word
    *
    * @return welsh word
    */
   public String getWelsh() {
      return welsh;
   }

   /**
    * Welsh word setter method
    *
    * @param welsh word in welsh
    */
   public void setWelsh(String welsh) {
      this.welsh = welsh;
   }

   /**
    * Word type getter method
    *
    * @return word type
    */
   public String getWordType() {
      return wordType;
   }

   /**
    * Word type setter method
    *
    * @param wordType word type of the word
    */
   public void setWordType(String wordType) {
      this.wordType = wordType;
   }

   /**
    * Favourite word getter method
    *
    * @return true or false
    */
   public boolean isFavorited() {
      return favorited;
   }

   /**
    * Favourite word setter method
    *
    * @param favorited word to practice
    */
   public void setFavorited(boolean favorited) {
      this.favorited = favorited;
   }

   /**
    * Overriding the equals method to account for verbs with the "to " prefix
    *
    * @return true or false
    */
   @Override
   public boolean equals(Object obj) {
      int substring1 = 0;
      int substring2 = 0;

      if (this.getWordType().equalsIgnoreCase("verb")) {
         substring1 = 3;
      }
      if (((Word) obj).getWordType().equalsIgnoreCase("verb")) {
         substring2 = 3;
      }

      return this.getEnglish().substring(substring1).equalsIgnoreCase(((Word) obj).getEnglish().substring(substring2)) && this.getWelsh().equalsIgnoreCase(((Word) obj).getWelsh()) && this.getWordType().equalsIgnoreCase(((Word) obj).getWordType());
   }
}
