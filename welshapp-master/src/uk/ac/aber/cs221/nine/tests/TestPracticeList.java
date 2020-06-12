package uk.ac.aber.cs221.nine.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.ac.aber.cs221.nine.dictionary.PracticeList;
import uk.ac.aber.cs221.nine.word.Word;

import java.io.IOException;
import java.util.LinkedList;

public class TestPracticeList {

   // /////////////// //
   // Class Variables //
   // /////////////// //

   private PracticeList practiceList;

   // /////// //
   // METHODS //
   // /////// //


   @BeforeEach
   public void setUP() {
      PracticeList.loadPracticeListFromFile();
   }

   // checks adding of word to practice list FR4

   @Test
   @DisplayName("PracticeList: Add word")
   public void maintainingPractice() {
      Word word1 = new Word("jellyfish", "slefrod môr", "fem");
      PracticeList.addIntoPracticedWords(word1);
      try {
         PracticeList.savePracticeListToFile();
      } catch (IOException e) {
         e.printStackTrace();
      }

      PracticeList.loadPracticeListFromFile();
      LinkedList<Word> practiceWords = PracticeList.getPracticedWords();
      Assertions.assertEquals(true, practiceWords.contains(word1));
   }

   // checks removal of word from practice list FR4

   @Test
   @DisplayName("PracticeList: remove word")
   public void removeWordFromPractice() {
      Word word1 = new Word("seahorse", "morfeirch", "fem");
      PracticeList.addIntoPracticedWords(word1);
      LinkedList<Word> practiceWordsBefore = PracticeList.getPracticedWords();
      Assertions.assertTrue(practiceWordsBefore.contains(word1));
      PracticeList.removeFromPracticedWords(word1);
      LinkedList<Word> practiceWords = PracticeList.getPracticedWords();
      Assertions.assertFalse(practiceWords.contains(word1));
   }


}
