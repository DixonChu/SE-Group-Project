/*
 * @(#) WriteDefinitionQuiz.java 1.0 2020/04/30
 *
 * Copyright (c) 2020 Aberystwyth University.
 * All rights reserved.
 *
 */

package uk.ac.aber.cs221.nine.quizzes;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import uk.ac.aber.cs221.nine.UIHandler;
import uk.ac.aber.cs221.nine.word.Word;

import java.io.IOException;

/**
 * This is writing definition class for the quiz
 *
 * @author Dixon[dkc2], Taavi[tak16]
 * @version 1.0
 */
public abstract class WriteDefinitionQuiz {

   // /////////////// //
   // Class Variables //
   // /////////////// //

   private static String language;
   private static Word answer;

   // /////// //
   // METHODS //
   // /////// //


   /**
    * Setting up the quiz
    *
    * @param word     Word being quizzed on
    * @param language English, Welsh
    */
   public static void setupTest(Word word, String language) {
      WriteDefinitionQuiz.language = language;
      WriteDefinitionQuiz.answer = word;

      // Setup UI for progress bar and progress.
      ((Label) UIHandler.getUIElement("currentScoreLabel")).setText("Correct: \n" + Quiz.getCurrentScore() + " / " + (Quiz.getCurrentQuestion() - 1) + " \nQuiz progress:");
      ((ProgressBar) UIHandler.getUIElement("progressBar")).setProgress((((double) Quiz.getCurrentQuestion() - (double) 1) / (double) Quiz.getMaximumAmountOfQuestions()));

      // Show the user what word they need to write a translation for
      if (language.equals("English")) {
         ((Label) UIHandler.getUIElement("answerWordLabel")).setText(word.getEnglish());
      } else {
         ((Label) UIHandler.getUIElement("answerWordLabel")).setText(word.getWelsh());
      }

      // Make the button call the checkAnswer method when clicked
      ((Button) UIHandler.getUIElement("answerButton")).setOnAction(e -> {
         try {
            checkAnswer(((TextField) UIHandler.getUIElement("answerTextField")).getText().toLowerCase());
         } catch (IOException ioException) {
            ioException.printStackTrace();
         }
      });

   }

   /**
    * Check answer method
    *
    * @param answer text user had entered
    * @throws IOException carried over from nextQuestion, as it accesses Scene files to load the specific test
    */
   private static void checkAnswer(String answer) throws IOException {
      Word correctAnswer = WriteDefinitionQuiz.answer;
      // Are we quizzing in English
      if (WriteDefinitionQuiz.language.equals("English")) {
         // we check if the answer is the correct translation for the original
         if (correctAnswer.getWelsh().strip().equalsIgnoreCase(answer)) {
            Quiz.nextQuestion(true);
         } else {
            // We make a new reference to the value using string builder, as the value gets changed half-way through and would otherwise be referenced to.
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(correctAnswer.getWelsh());
            Quiz.nextQuestion(false);
            UIHandler.showNotification("False! Correct: " + stringBuilder.toString(), 2, true);
         }
      // or welsh
      } else {
         // Same as above
         if (correctAnswer.getEnglish().strip().equalsIgnoreCase(answer)) {
            Quiz.nextQuestion(true);
         } else {
            // We make a new reference to the value using string builder, as the value gets changed half-way through and would otherwise be referenced to.
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(correctAnswer.getEnglish());
            Quiz.nextQuestion(false);
            UIHandler.showNotification("False! Correct: " + stringBuilder.toString(), 2, true);
         }
      }
   }

}
