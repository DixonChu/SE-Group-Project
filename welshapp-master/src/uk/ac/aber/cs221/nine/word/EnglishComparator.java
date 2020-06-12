/*
 * @(#) EnglishComparator.java 1.0 2020/04/30
 *
 * Copyright (c) 2020 Aberystwyth University.
 * All rights reserved.
 *
 */

package uk.ac.aber.cs221.nine.word;

import java.util.Comparator;

/**
 * This is a comparator class for English
 *
 * @author Dixon[dkc2], Taavi[tak16]
 * @version 1.0
 */
public class EnglishComparator
implements Comparator<Word> {

   /**
    * Compare method for two English words
    *
    * @param word  First word object
    * @param word2 Second word object
    * @return -1, 0, 1
    */
   @Override
   public int compare(Word word, Word word2) {
      // If either is a verb, we have to make sure we compare the stems, without the "to " prefix
      if (word.getWordType().equalsIgnoreCase("verb") || word2.getWordType().equalsIgnoreCase("verb")) {
         int substring1 = 0;
         int substring2 = 0;
         if (word.getWordType().equalsIgnoreCase("verb")) {
            substring1 = 3;
         }
         if (word2.getWordType().equalsIgnoreCase("verb")) {
            substring2 = 3;
         }
         return word.getEnglish().substring(substring1).toLowerCase().compareTo(word2.getEnglish().substring(substring2).toLowerCase());
      // otherwise just compare the words normally
      } else {
         return word.getEnglish().toLowerCase().compareTo(word2.getEnglish().toLowerCase());
      }
   }
}
