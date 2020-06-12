package uk.ac.aber.cs221.nine.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.ac.aber.cs221.nine.dictionary.Dictionary;
import uk.ac.aber.cs221.nine.dictionary.PracticeList;
import uk.ac.aber.cs221.nine.word.Word;

import java.util.LinkedList;

public class TestDictionaryFunctionality {

   // /////////////// //
   // Class Variables //
   // /////////////// //

   private Dictionary dictionary;
   private PracticeList practiceList;

   // /////// //
   // METHODS //
   // /////// //


   @BeforeEach
   public void setUP() {
      Dictionary.loadDictionaryFromFile();

   }

   // tests FR5 when a word is added to dictionary it is automatically added to practice list
   @Test
   @DisplayName("Dictionary: Add word")
   public void testAddWord() {
      Word word1 = new Word("asparagus", "asbaragws", "other");
      Dictionary.addWord(word1, true);
      Assertions.assertEquals(word1, Dictionary.getWord(word1));
      LinkedList<Word> practiceCheck = PracticeList.getPracticedWords();
      Assertions.assertEquals(true, practiceCheck.contains(word1));


   }

   @Test
   @DisplayName("Dictionary: maintenance")
   public void testDictionaryMaintenance() {
      Word word1 = new Word("asparagus", "asbaragws", "other");
      Word word2 = new Word("cotton", "cotwm", "other");
      Dictionary.addWord(word1, true);
      Dictionary.addWord(word2, true);
      Dictionary.saveDictionaryToFile();
      Dictionary.loadDictionaryFromFile();
      Assertions.assertEquals(word1, Dictionary.getWord(word1));
      Assertions.assertEquals(word2, Dictionary.getWord(word2));

   }
}
