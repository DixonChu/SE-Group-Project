/*
 * @(#) WelshComparator.java 1.0 2020/04/30
 *
 * Copyright (c) 2020 Aberystwyth University.
 * All rights reserved.
 *
 */

package uk.ac.aber.cs221.nine.word;

import java.util.Comparator;

/**
 * This is a comparator class for Welsh
 *
 * @author Dixon[dkc2], Taavi[tak16]
 * @version 1.0
 */
public class WelshComparator
implements Comparator<Word> {

   /**
    * Compare method for two Welsh words
    *
    * @param word  First word object
    * @param word2 Second word object
    * @return -1, 0, 1
    */
   @Override
   public int compare(Word word, Word word2) {
      return word.getWelsh().toLowerCase().compareTo(word2.getWelsh().toLowerCase());
   }
}
