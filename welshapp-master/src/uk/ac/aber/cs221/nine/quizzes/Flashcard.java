/*
 * @(#) Flashcard.java 1.0 2020/04/30
 *
 * Copyright (c) 2020 Aberystwyth University.
 * All rights reserved.
 *
 */

package uk.ac.aber.cs221.nine.quizzes;

import uk.ac.aber.cs221.nine.Settings;
import uk.ac.aber.cs221.nine.UIHandler;
import uk.ac.aber.cs221.nine.dictionary.PracticeList;
import uk.ac.aber.cs221.nine.word.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * This is the flashcard class where it controls the flashcard's function
 *
 * @author: Dixon[dkc2], Taavi[tak16]
 * @version: 1.0
 */
public abstract class Flashcard {

   // /////////////// //
   // Class Variables //
   // /////////////// //

   private static ArrayList<Word> flashcardStack = new ArrayList<>();
   private static int currentCard = 0;

   // /////// //
   // METHODS //
   // /////// //

   /**
    * Get the current word from the flashcard stack
    *
    * @return current word from the practice list
    */
   public static Word getFlashcardWord() {
      return flashcardStack.get(currentCard);
   }

   /**
    * Update the text on the flashcard to the current word in the flashcard stack
    */
   public static void setFlashcardText() {
        /* If user set the language to English, set the flashcard in English from the beginning
        If user set the language to Welsh, set the flashcard in Welsh from the beginning
        User set the language to both, make it a random display */
      if (Settings.getLanguageOfQuiz().equals("English")) {
         UIHandler.getFlashcardInUI().setText(getFlashcardWord().getEnglish());
      } else if (Settings.getLanguageOfQuiz().equals("Welsh")) {
         UIHandler.getFlashcardInUI().setText(getFlashcardWord().getWelsh());
      } else {
         Random random = new Random();
         if (random.nextBoolean()) {
            UIHandler.getFlashcardInUI().setText(getFlashcardWord().getEnglish());
         } else {
            UIHandler.getFlashcardInUI().setText(getFlashcardWord().getWelsh());
         }
      }
   }

   /**
    * Shuffle flashcard stack once the user has gone through all the flashcards.
    */
   public static void resetFlashcardStack() {
      currentCard = 0;
      flashcardStack = new ArrayList<>();
      flashcardStack.addAll(PracticeList.getPracticedWords());
      Collections.shuffle(flashcardStack);
      setFlashcardText();
   }

   /**
    * Gets the next flashcard in the flashcard stack.
    */
   public static void nextFlashcard() {
      // Check if the current word is the last one in the flashcard stack, reset it if true, else go to the next word
      if (currentCard == (flashcardStack.size() - 1)) {
         Collections.shuffle(flashcardStack);
         currentCard = 0;
      } else {
         currentCard++;
      }
      setFlashcardText();
   }

   /**
    * Get the previous word from the flashcard stack, looping back to the end if you've reached the beginning.
    */
   public static void lastFlashcard() {
      // Check is the current word is the last one in the flashcard, reset it if true, else go to the previous word
      if (currentCard == 0) {
         currentCard = flashcardStack.size() - 1;
      } else {
         currentCard--;
      }
      setFlashcardText();
   }

   /**
    * Changes the flashcard's displayed word to the word in the opposite language
    */
   public static void flipFlashcard() {
      // Set the word into different language on what the current word is displaying
      if (getFlashcardWord().getEnglish().equals(UIHandler.getFlashcardInUI().getText())) {
         UIHandler.getFlashcardInUI().setText(getFlashcardWord().getWelsh());
      } else {
         UIHandler.getFlashcardInUI().setText(getFlashcardWord().getEnglish());
      }
   }

}
