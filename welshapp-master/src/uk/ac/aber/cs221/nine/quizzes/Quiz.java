/*
 * @(#) Quiz.java 1.0 2020/04/30
 *
 * Copyright (c) 2020 Aberystwyth University.
 * All rights reserved.
 *
 */

package uk.ac.aber.cs221.nine.quizzes;

import uk.ac.aber.cs221.nine.Main;
import uk.ac.aber.cs221.nine.Settings;
import uk.ac.aber.cs221.nine.UIHandler;
import uk.ac.aber.cs221.nine.dictionary.PracticeList;
import uk.ac.aber.cs221.nine.word.Word;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

/**
 * This is the quiz class that manages an entire session of a quiz, keeping track of scoring and the words in the quiz.
 *
 * @author Dixon[dkc2], Taavi[tak16]
 * @version 1.0
 */
public abstract class Quiz {

   // /////////////// //
   // Class Variables //
   // /////////////// //

   private static int currentScore = 0;
   private static int currentQuestion = 0;
   private static int maximumAmountOfQuestions;
   private static LinkedList<Word> wordsInQuiz;
   private static String languageOfQuiz;

   // /////// //
   // METHODS //
   // /////// //

   /**
    * Start a quiz session
    *
    * @throws IOException carried over from nextQuestion, as it accesses Scene files to load the specific test
    */
   public static void startQuiz() throws IOException {
      currentScore = 0;
      currentQuestion = 0;
      maximumAmountOfQuestions = Settings.getAmountOfWordsInQuiz();
      languageOfQuiz = Settings.getLanguageOfQuiz();
      wordsInQuiz = new LinkedList<>();

      int amountOfQuestionsAdded;
      // First add the full practice list however many times we can fit it in our chosen amount of words to be quizzed.
      for (amountOfQuestionsAdded = 0; amountOfQuestionsAdded + PracticeList.getPracticedWords().size() < maximumAmountOfQuestions; amountOfQuestionsAdded += PracticeList.getPracticedWords().size()) {
         wordsInQuiz.addAll(PracticeList.getPracticedWords());
      }

      // Fill up the remainder with random words from the practice list
      Random random = new Random();
      while (amountOfQuestionsAdded < maximumAmountOfQuestions) {
         wordsInQuiz.add(PracticeList.getPracticedWords().get(random.nextInt(PracticeList.getPracticedWords().size())));
         amountOfQuestionsAdded++;
      }

      // Shuffle the words to randomise the selection being shown and move on to the first question
      Collections.shuffle(wordsInQuiz);
      nextQuestion(false);
   }

   /**
    * Getter method for current score
    *
    * @return current score
    */
   public static int getCurrentScore() {
      return currentScore;
   }

   /**
    * Getter method for total amount of questions in quiz
    *
    * @return maximum amount of question in quiz
    */
   public static int getMaximumAmountOfQuestions() {
      return maximumAmountOfQuestions;
   }

   /**
    * Go to the next question
    *
    * @param lastQuestionPassedSuccessfully get previous question's result
    * @throws IOException carried over from nextQuestion, as it accesses Scene files to load the specific test
    */
   public static void nextQuestion(boolean lastQuestionPassedSuccessfully) throws IOException {
      // Increase the score if previous question is correct
      if (lastQuestionPassedSuccessfully) {
         currentScore++;
      }

      // if we have any words left to quiz on
      if (wordsInQuiz.size() > 0) {

         boolean fourWordQuizTestEnabled = false;
         String language;

         currentQuestion++;

         // Set the word language in the quiz
         if (languageOfQuiz.equals("English")) {
            language = "English";
         } else if (languageOfQuiz.equals("Welsh")) {
            language = "Welsh";
         } else {
            // Chosen both, so we choose a random language
            Random random = new Random();
            if (random.nextBoolean()) {
               language = "English";
            } else {
               language = "Welsh";
            }
         }

         // Enable four word quiz test if there are more than 4 words in practice list
         if (PracticeList.getPracticedWords().size() > 3) {
            fourWordQuizTestEnabled = true;
         }


         Random quizRandom = new Random();
         int quizNumber = 0;

         // Whether we should be choosing from all three or the first two quizzes
         if (fourWordQuizTestEnabled) {
            quizNumber = quizRandom.nextInt(3);
         } else {
            quizNumber = quizRandom.nextInt(2);
         }

         // Random choice of quiz
         switch (quizNumber) {
            case 0:
               // 1 word - 6 matches
               Main.uiHandler.changeToQuizScene(1);
               FindWordDefinitionQuiz.setupTest(wordsInQuiz.getFirst(), language);
               wordsInQuiz.removeFirst();
               break;
            case 1:
               // Write definition quiz
               Main.uiHandler.changeToQuizScene(2);
               WriteDefinitionQuiz.setupTest(wordsInQuiz.getFirst(), language);
               wordsInQuiz.removeFirst();
               break;
            case 2:
               //4 word - 4 match
               LinkedList<Word> questionWords = new LinkedList<>();

               questionWords.add(wordsInQuiz.getFirst());

               Random random = new Random();

               // Get random words from the practice list to accompany the original chosen word
               while (questionWords.size() < 4) {
                  Word randomWord = PracticeList.getPracticedWords().get(random.nextInt(PracticeList.getPracticedWords().size()));
                  // Make sure we get unique words
                  while (questionWords.contains(randomWord)) {
                     randomWord = PracticeList.getPracticedWords().get(random.nextInt(PracticeList.getPracticedWords().size()));
                  }
                  questionWords.add(randomWord);
               }

               Main.uiHandler.changeToQuizScene(3);
               MatchFourQuiz.setupTest(questionWords, language);
               wordsInQuiz.removeFirst();
               break;
         }

         // if we answered the last question correctly, congratulate
         if (lastQuestionPassedSuccessfully) {
            UIHandler.showNotification("Correct!", 1, false);
         }

      // We don't have any words left to quiz the user on, show end results
      } else {
         showResults();
      }
   }

   /**
    * Getter method for current question
    *
    * @return current question
    */
   public static int getCurrentQuestion() {
      return currentQuestion;
   }

   /**
    * Show final result after the quiz
    *
    * @throws IOException Requires a Scene FXML file, so can throw an IOException if not present
    */
   private static void showResults() throws IOException {
      Main.uiHandler.changeToRevisionScene();
      UIHandler.createMessageBox("Quiz results: \n" +
              "You answered: " + getCurrentScore() + " question(s) correctly \n" +
              "You failed: " + (getMaximumAmountOfQuestions() - getCurrentScore()) + " questions \n" +
              "There was a total of: " + getMaximumAmountOfQuestions() + " questions \n" +
              "You scored: " + new DecimalFormat("#.##").format(((double) getCurrentScore() / (double) getMaximumAmountOfQuestions()) * 100) + "%");
   }

}
