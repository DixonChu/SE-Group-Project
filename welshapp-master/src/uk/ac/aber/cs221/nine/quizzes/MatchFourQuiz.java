/*
 * @(#) MatchFourQuiz.java 1.0 2020/04/30
 *
 * Copyright (c) 2020 Aberystwyth University.
 * All rights reserved.
 *
 */


package uk.ac.aber.cs221.nine.quizzes;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import uk.ac.aber.cs221.nine.UIHandler;
import uk.ac.aber.cs221.nine.word.Word;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This is the matching word class for the quiz
 *
 * @author Dixon[dkc2], Taavi[tak16]
 * @version 1.0
 *
 */
public abstract class MatchFourQuiz {

   // /////////////// //
   // Class Variables //
   // /////////////// //

   private static String language;
   private static List<Word> answers;

   // /////// //
   // METHODS //
   // /////// //

   /**
    * Setting up the quiz
    *
    * @param words    Words being quizzed on
    * @param language English, Welsh
    */
   public static void setupTest(List<Word> words, String language) {
      MatchFourQuiz.language = language;
      MatchFourQuiz.answers = words;

      // Setup UI for progress bar and progress.
      ((Label) UIHandler.getUIElement("currentScoreLabel")).setText("Correct: \n" + Quiz.getCurrentScore() + " / " + (Quiz.getCurrentQuestion() - 1) + " \nQuiz progress:");
      ((ProgressBar) UIHandler.getUIElement("progressBar")).setProgress((((double) Quiz.getCurrentQuestion() - (double) 1) / (double) Quiz.getMaximumAmountOfQuestions()));

      // If the language we're quizzing in is English...
      if (language.equalsIgnoreCase("English")) {
         // .. loop through our choiceBoxes and their corresponding labels..
         for (int i = 1; i < 5; i++) {
            // .. setting the label's text (the word we want them to find the translation for) ..
            ((Label) UIHandler.getUIElement("choiceLabel" + i)).setText(words.get(i - 1).getEnglish());
            // .. and for all for words we have to choose from ..
            for (Word w : words) {
               // .. add them into the the choice box for each label ..
               ((ChoiceBox) UIHandler.getUIElement("choiceBox" + i)).getItems().add(w.getWelsh());
            }
            // .. shuffling the choices so they would be random ..
            Collections.shuffle(((ChoiceBox) UIHandler.getUIElement("choiceBox" + i)).getItems());
            // and set the first word in the choiceBox as the default value.
            ((ChoiceBox) UIHandler.getUIElement("choiceBox" + i)).setValue(((ChoiceBox) UIHandler.getUIElement("choiceBox" + i)).getItems().get(0));
         }
      } else {
         // Do the same as above, but for Welsh
         for (int i = 1; i < 5; i++) {
            ((Label) UIHandler.getUIElement("choiceLabel" + i)).setText(words.get(i - 1).getWelsh());
            for (Word w : words) {
               ((ChoiceBox) UIHandler.getUIElement("choiceBox" + i)).getItems().add(w.getEnglish());
            }
            Collections.shuffle(((ChoiceBox) UIHandler.getUIElement("choiceBox" + i)).getItems());
            ((ChoiceBox) UIHandler.getUIElement("choiceBox" + i)).setValue(((ChoiceBox) UIHandler.getUIElement("choiceBox" + i)).getItems().get(0));
         }
      }

      // Set the submit button to call our checkAnswer() function.
      ((Button) UIHandler.getUIElement("submitButton")).setOnAction(e -> {
         try {
            checkAnswer();
         } catch (IOException ioException) {
            ioException.printStackTrace();
         }
      });

   }

   /**
    * Check answer method
    *
    * @throws IOException carried over from nextQuestion, as it accesses Scene files to load the specific test
    */
   private static void checkAnswer() throws IOException {

      int rightAnswers = 0;
      LinkedList<String> wrongAnswers = new LinkedList<>();

      // If we're quizzing in English ..
      if (MatchFourQuiz.language.equals("English")) {
         // go through each choiceBox and check its value ..
         for (int i = 1; i < 5; i++) {
            // if it matches the corresponding word's translation
            if (((ChoiceBox) UIHandler.getUIElement("choiceBox" + i)).getValue().equals(MatchFourQuiz.answers.get(i - 1).getWelsh())) {
               // and increase the amount of right answers we have
               rightAnswers++;
            } else {
               // otherwise add the original word they got wrong into our list of incorrect answers
               wrongAnswers.add(MatchFourQuiz.answers.get(i - 1).getEnglish());
            }
         }
      // Do the above, but for Welsh
      } else {
         for (int i = 1; i < 5; i++) {
            if (((ChoiceBox) UIHandler.getUIElement("choiceBox" + i)).getValue().equals(MatchFourQuiz.answers.get(i - 1).getEnglish())) {
               rightAnswers++;
            } else {
               wrongAnswers.add(MatchFourQuiz.answers.get(i - 1).getWelsh());
            }
         }
      }

      // If we got all the answers right, we passed successfully
      if (rightAnswers == 4) {
         Quiz.nextQuestion(true);
         UIHandler.showNotification("Correct!", 1, false);
      // otherwise display all the incorrect answers in a notification
      } else {
         String wrong = "";
         for (String s : wrongAnswers) {
            wrong += s + ", ";
         }
         Quiz.nextQuestion(false);
         UIHandler.showNotification("Wrong answers for: " + wrong, 2, true);
      }
   }
}
