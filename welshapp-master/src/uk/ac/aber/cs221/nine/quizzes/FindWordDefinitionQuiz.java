/*
 * @(#) FindWordDefinitionQuiz.java 1.0 2020/04/30
 *
 * Copyright (c) 2020 Aberystwyth University.
 * All rights reserved.
 *
 */


package uk.ac.aber.cs221.nine.quizzes;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import uk.ac.aber.cs221.nine.UIHandler;
import uk.ac.aber.cs221.nine.dictionary.Dictionary;
import uk.ac.aber.cs221.nine.word.Word;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

/**
 * This is find definition for the word class
 *
 * @author: Dixon[dkc2], Taavi[tak16]
 * @version: 1.0
 *
 *
 */
public abstract class FindWordDefinitionQuiz {

   // /////////////// //
   // Class Variables //
   // /////////////// //

   private static String language;
   private static Word answer;

   // /////// //
   // METHODS //
   // /////// //

   /**
    * Setting up the test
    *
    * @param word     Word being quizzed on
    * @param language Language the word is being quizzed from
    */
   public static void setupTest(Word word, String language) {
      LinkedList<Word> words = new LinkedList<>();
      FindWordDefinitionQuiz.language = language;
      FindWordDefinitionQuiz.answer = word;

      // Setup UI for progress bar and progress.
      ((Label) UIHandler.getUIElement("currentScoreLabel")).setText("Correct: \n" + Quiz.getCurrentScore() + " / " + (Quiz.getCurrentQuestion() - 1) + " \nQuiz progress:");
      ((ProgressBar) UIHandler.getUIElement("progressBar")).setProgress((((double) Quiz.getCurrentQuestion() - (double) 1) / (double) Quiz.getMaximumAmountOfQuestions()));

      // Find 5 other random words from the dictionary as random choices.
      for (int loop = 0; loop < 5; loop++) {
         Random random = new Random();
         int randomWordInt = random.nextInt(Dictionary.getDictionary().size());
         while (words.contains(Dictionary.getDictionary().get(randomWordInt))) {
            randomWordInt = random.nextInt(Dictionary.getDictionary().size());
         }
         words.add(Dictionary.getDictionary().get(randomWordInt));
      }

      words.add(word);
      // Shuffle, otherwise answer is always last choice
      Collections.shuffle(words);

      // Show the user what word they need to find a translation for
      if (language.equals("English")) {
         ((Label) UIHandler.getUIElement("wordToFindLabel")).setText(word.getEnglish());
      } else {
         ((Label) UIHandler.getUIElement("wordToFindLabel")).setText(word.getWelsh());
      }

      // Designate a word to each button
      for (int i = 1; i <= 6; i++) {
         if (language.equals("English")) {
            ((Button) UIHandler.getUIElement("answerChoiceButton" + i)).setText(words.get(i - 1).getWelsh());
         } else {
            ((Button) UIHandler.getUIElement("answerChoiceButton" + i)).setText(words.get(i - 1).getEnglish());
         }
         // Make each button function as answering the question
         ((Button) UIHandler.getUIElement("answerChoiceButton" + i)).setOnAction(e -> {
            try {
               FindWordDefinitionQuiz.checkAnswer(((Button) e.getSource()).getText());
            } catch (IOException ioException) {
               ioException.printStackTrace();
            }
         });
      }
   }

   /**
    * Check if the answer is correct
    *
    * @param answer answer chosen
    * @throws IOException carried over from nextQuestion, as it accesses Scene files to load the specific test
    */
   private static void checkAnswer(String answer)
   throws IOException {
      // We should check if we're quizzing in English or Welsh
      if (FindWordDefinitionQuiz.language.equals("English")) {
         // Does our chosen answer equal the welsh version of our correct answer
         if (FindWordDefinitionQuiz.answer.getWelsh().equals(answer)) {
            Quiz.nextQuestion(true);
         } else {
            Quiz.nextQuestion(false);
            UIHandler.showNotification("False! Correct: " + FindWordDefinitionQuiz.answer.getWelsh(), 2, true);
         }
      // Quizzing in Welsh
      } else {
         if (FindWordDefinitionQuiz.answer.getEnglish().equals(answer)) {
            Quiz.nextQuestion(true);
         } else {
            Quiz.nextQuestion(false);
            UIHandler.showNotification("False! Correct: " + FindWordDefinitionQuiz.answer.getEnglish(), 2, true);
         }
      }
   }

}
