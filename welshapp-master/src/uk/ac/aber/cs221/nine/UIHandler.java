/*
 * @(#) UIHandler.java 1.0 2020/04/30
 *
 * Copyright (c) 2020 Aberystwyth University.
 * All rights reserved.
 *
 */


package uk.ac.aber.cs221.nine;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import uk.ac.aber.cs221.nine.dictionary.Dictionary;
import uk.ac.aber.cs221.nine.dictionary.PracticeList;
import uk.ac.aber.cs221.nine.quizzes.Flashcard;
import uk.ac.aber.cs221.nine.quizzes.Quiz;
import uk.ac.aber.cs221.nine.word.EnglishComparator;
import uk.ac.aber.cs221.nine.word.WelshComparator;
import uk.ac.aber.cs221.nine.word.Word;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static uk.ac.aber.cs221.nine.Settings.setFontSize;

/**
 * This is the UI Handler class which handles the entire User Interface
 *
 * @author Dixon[dkc2], Taavi[tak16]
 * @version 1.0
 *
 *  Copyright (c) 2020 Aberystwyth University.
 *  * All rights reserved.
 */
public class UIHandler {

   // /////////////// //
   // Class Variables //
   // /////////////// //

   public static Stage primaryScene;
   public static Parent root;

   public static ImageView ukFlag;
   public static ImageView welshFlag;
   public static Stage messageBoxStage;
   public static Stage addWordStage;
   private static String currentScene;
   private static boolean isEnglish;
   private static int lastMessageBox;

   // /////// //
   // METHODS //
   // /////// //

   /**
    * Get the current scene
    *
    * @return current scene
    */
   public static String getCurrentScene() {
      return currentScene;
   }

   /**
    * Set the current scene
    *
    * @param sceneName the scene user are currently at
    */
   public static void setCurrentScene(String sceneName) {
      currentScene = sceneName;
   }

   /**
    * Load the dictionary and display all the words into the user interface
    */
   private static void loadDictionaryIntoUI() {
      TableView tableView = setTableViewProperties();

      // Go through all the words, adding them into the table
      for (Object word : Dictionary.getDictionary()) {
         tableView.getItems().add(word);
      }

      sortTableInUI();
      changeFontSizeInUI();
   }

   /**
    * Get the table view from the user interface
    *
    * @return table view
    */
   private static TableView getTableFromUI() {
      return ((TableView) getUIElement("dictionaryTableView"));
   }

   /**
    * Returns a JavaFX Object in the main scene by its FX:id
    *
    * @return JavaFX Object
    */
   public static Object getUIElement(String name) {
      return primaryScene.getScene().lookup("#" + name);
   }

   /**
    * Load all favourite words into practice table view
    */
   private static void loadFavoritesIntoUI() {
      TableView tableView = setTableViewProperties();

      // Go through all of the words in the practice list, adding them into the table
      for (Word word : PracticeList.getPracticedWords()) {
         tableView.getItems().add(word);
      }

      sortTableInUI();
      changeFontSizeInUI();
   }

   /**
    * Set the table view properties
    *
    * @return TableView with all of the relevant settings updated
    */
   private static TableView setTableViewProperties() {
      TableView tableView = getTableFromUI();

      TableColumn englishColumn = ((TableColumn) tableView.getColumns().get(0));
      TableColumn welshColumn = ((TableColumn) tableView.getColumns().get(1));
      TableColumn wordTypeColumn = ((TableColumn) tableView.getColumns().get(2));
      TableColumn favoriteColumn = ((TableColumn) tableView.getColumns().get(3));

      // Correspond each column with a variable from each Word object
      englishColumn.setCellValueFactory(new PropertyValueFactory<>("english"));
      welshColumn.setCellValueFactory(new PropertyValueFactory<>("welsh"));
      wordTypeColumn.setCellValueFactory(new PropertyValueFactory<>("wordType"));
      favoriteColumn.setCellValueFactory(new PropertyValueFactory<>("favorited"));

      // Override the boolean's default view, making it show a tick instead of true/false
      favoriteColumn.setCellFactory(col -> new TableCell<Word, Boolean>() {
         @Override
         protected void updateItem(Boolean favorited, boolean ifNull) {
            super.updateItem(favorited, ifNull);
            setText(ifNull ? null : favorited ? "✔" : "");
         }
      });

      // Add Deletion as a right click option
      MenuItem menuItemDelete = new MenuItem("Delete");
      menuItemDelete.setOnAction((ActionEvent event) -> {
         // Get selected word
         Object item = getTableFromUI().getSelectionModel().getSelectedItem();

         // Remove the word from the dictionary, also removing it from the practice list if it exists there
         Dictionary.removeWord((Word) item, true);
         getTableFromUI().getItems().clear();

         // Refresh table
         if (currentScene.equalsIgnoreCase("dictionary")) {
            loadDictionaryIntoUI();
         } else if (currentScene.equalsIgnoreCase("revisionlist")) {
            loadFavoritesIntoUI();
         }

         showNotification("DELETED: " + ((Word) item).getEnglish(), 2, true);
      });

      // Add word editing as a right click option
      MenuItem menuItemEdit = new MenuItem("Edit");
      menuItemEdit.setOnAction((ActionEvent event) -> {
         // Get selected word as an item from the table
         Object item = getTableFromUI().getSelectionModel().getSelectedItem();
         // remember its index
         int indexOfWordEdited = Dictionary.getDictionary().indexOf(item);
         int secondaryIndexOfWord = PracticeList.getPracticedWords().indexOf(item);
         // Get the corresponding word
         Word editedWord = Dictionary.getDictionary().get(indexOfWordEdited);

         Stage reviseWord = new Stage();
         Parent root = null;
         addWordStage = reviseWord;

         // Load the add word stage
         try {
            root = FXMLLoader.load(UIHandler.class.getResource("/uk/ac/aber/cs221/nine/scenes/addWordScene.fxml"));
         } catch (IOException e) {
            e.printStackTrace();
         }

         // Replace the button's text to say Revise word instead.
         reviseWord.setScene(new Scene(root));
         Button reviseWordButton = (Button) reviseWord.getScene().lookup("#addWordButton");
         reviseWordButton.setText("Revise word");



         // Pointers to our UI elements
         TextField englishTextField = ((TextField) reviseWord.getScene().lookup("#englishDefinitionBox"));
         TextField welshTextField = ((TextField) reviseWord.getScene().lookup("#welshDefinitionBox"));
         ChoiceBox wordTypeChoice = ((ChoiceBox) reviseWord.getScene().lookup("#wordtypeChoiceBox"));

         // If it's a verb, just get the word without the "to " substring
         if (((Word) item).getWordType().equalsIgnoreCase("verb")) {
            englishTextField.setText(((Word) item).getEnglish().substring(3));
         } else {
            englishTextField.setText(((Word) item).getEnglish());
         }

         welshTextField.setText(((Word) item).getWelsh());

         wordTypeChoice.getItems().addAll(new String[]{"nm - noun masculine", "nf - noun feminine", "verb", "other"});

         // Set the wordType choice to the edited word's word type
         for (Object wordType : wordTypeChoice.getItems()) {
            if (((String) wordType).substring(0, ((Word) item).getWordType().length()).equalsIgnoreCase(((Word) item).getWordType())) {
               wordTypeChoice.setValue(wordType);
            }
         }

         reviseWordButton.setOnAction(e -> {
            String type = "";

            if (((String) wordTypeChoice.getValue()).contains("noun masculine")) {
               type = "nm";
            }
            if (((String) wordTypeChoice.getValue()).contains("noun feminine")) {
               type = "nf";
            }
            if (((String) wordTypeChoice.getValue()).contains("verb")) {
               type = "verb";
            }
            if (((String) wordTypeChoice.getValue()).contains("other")) {
               type = "other";
            }

            if (type.equals("verb")) {
               editedWord.setEnglish("to " + englishTextField.getText());
            } else {
               editedWord.setEnglish(englishTextField.getText());
            }

            editedWord.setWelsh(welshTextField.getText());
            editedWord.setWordType(type);

            // Replace word being edited with our new word using its index in the dictionary
            Dictionary.getDictionary().set(indexOfWordEdited, editedWord);
            if (secondaryIndexOfWord >= 0){
               PracticeList.getPracticedWords().set(secondaryIndexOfWord, editedWord);
            }

            reviseWord.close();

            // Refresh table
            Main.uiHandler.searchButtonClickInUI();
            showNotification("Word successfully edited.", 2, false);
         });

         reviseWord.setTitle("Revise word");
         reviseWord.show();

      });

      // Add double clicking functionality to table entries to toggle if it is favorited or not
      getTableFromUI().setRowFactory(tv -> {
         TableRow<Word> row = new TableRow<>();
         row.setOnMouseClicked(e -> {
            // Double clicked twice on an entry that is not an empty line
            if (e.getClickCount() == 2 && (!row.isEmpty())) {
               Word item = row.getItem();

               // Either remove or add it into the practice list
               if (PracticeList.getPracticedWords().contains(item)) {
                  PracticeList.removeFromPracticedWords(item);
               } else {
                  PracticeList.addIntoPracticedWords(item);
               }

               // Refresh table
               Main.uiHandler.searchButtonClickInUI();
            }
         });
         return row;
      });

      // Create a context right click menu and add the right click options into it, assigning the menu to the table.
      ContextMenu menu = new ContextMenu();
      menu.getItems().add(menuItemDelete);
      menu.getItems().add(menuItemEdit);
      getTableFromUI().setContextMenu(menu);

      // Return our table with all of our settings set
      return tableView;
   }

   /**
    * Create message box
    *
    * @param message message to display
    * @throws IOException
    */
   public static void createMessageBox(String message) throws IOException {
      Stage primaryScene = new Stage();
      primaryScene.setTitle("Message");
      primaryScene.setMinHeight(299);
      primaryScene.setMinWidth(497);
      primaryScene.setScene(new Scene(FXMLLoader.load(UIHandler.class.getResource("/uk/ac/aber/cs221/nine/scenes/messageBoxScene.fxml"))));
      primaryScene.show();
      messageBoxStage = primaryScene;
      ((Label) messageBoxStage.getScene().lookup("#messageBoxLabel")).setText(message);
   }

   /**
    * Change font size for the table view using CSS
    */
   public static void changeFontSizeInUI() {
      getTableFromUI().getStylesheets().clear();
      switch (Settings.getFontSize()) {
         case "Medium":
            getTableFromUI().getStylesheets().add("/uk/ac/aber/cs221/nine/css/medium.css");
            break;
         case "Small":
            getTableFromUI().getStylesheets().add("/uk/ac/aber/cs221/nine/css/small.css");
            break;
         case "Large":
            getTableFromUI().getStylesheets().add("/uk/ac/aber/cs221/nine/css/big.css");
            break;
      }

   }

   /**
    * Button in the Flashcard scene, quick method to return it with its type cast
    *
    * @return flashcard button in flashcard scene
    */
   public static Button getFlashcardInUI() {
      return (Button) getUIElement("flashcard");
   }

   /**
    * Sort the table according to the currently chosen language whilst also making sure our button has the corresponding flag graphic set
    */
   private static void sortTableInUI() {
      Button languageButton = (Button) getUIElement("changeLanguageButton");
      if (isEnglish) {
         getTableFromUI().getItems().sort(new EnglishComparator());
         languageButton.setGraphic(ukFlag);
      } else {
         getTableFromUI().getItems().sort(new WelshComparator());
         languageButton.setGraphic(welshFlag);
      }
   }

   /**
    * Show notification
    *
    * @param text           notification message
    * @param time           the time the notification stays on the screen
    * @param isErrorColored notification colour, true for error, false for green standard
    */
   public static void showNotification(String text, int time, boolean isErrorColored) {
      Random rand = new Random();

      // give it a random ID, to stop old notification timers from clearing new notifications
      int messageID = rand.nextInt(10000);
      lastMessageBox = messageID;

      // Show the notification
      ((AnchorPane) getUIElement("notificationBoxPane")).setVisible(true);

      // Set notification colour, error colour to red and green for correct
      if (isErrorColored) {
         ((Rectangle) getUIElement("notificationBox")).setFill(Paint.valueOf("#ffc0d2"));
      } else {
         ((Rectangle) getUIElement("notificationBox")).setFill(Paint.valueOf("#d1ffef"));
      }

      ((Label) getUIElement("notificationMessageLabel")).setText(text);

      // Timer to hide the notification after the chosen time period
      new Timer().schedule(
              new TimerTask() {
                 @Override
                 public void run() {
                    if (lastMessageBox == messageID) {
                       ((AnchorPane) getUIElement("notificationBoxPane")).setVisible(false);
                    }
                 }
              }, time * 1000
      );
   }

   /**
    * Initialize the main scene by loading in the home scene, set corresponding variables and assets, etc
    *
    * @param primaryStage Main scene passed from launch
    * @throws IOException exception will throw if files, assets don't exist
    */
   public void initializeUI(Stage primaryStage)
   throws IOException {

      root = FXMLLoader.load(getClass().getResource("/uk/ac/aber/cs221/nine/scenes/homeScene.fxml"));
      primaryScene = primaryStage;
      ukFlag = new ImageView(new Image(getClass().getResourceAsStream("assets/uk.png")));
      welshFlag = new ImageView(new Image(getClass().getResourceAsStream("assets/welsh.png")));

      primaryScene.setTitle("Welsh Dictionary Tutor");
      primaryScene.setMinHeight(400);
      primaryScene.setMinWidth(640);
      primaryScene.setScene(new Scene(root));
      primaryScene.show();

      isEnglish = true;

      setCurrentScene("dictionary");
      loadDictionaryIntoUI();

   }

   /**
    * Close the message box
    */
   public void closeMessageBox() {
      messageBoxStage.close();
   }


   /**
    * Search button in user interface (which was removed), now used as a back-end method when the search bar is changed
    */
   public void searchButtonClickInUI() {
      TextField searchBar = (TextField) getUIElement("searchBar");
      LinkedList list = new LinkedList();

      // Check what scene we're on
      if (currentScene.equalsIgnoreCase("dictionary")) {
         list = Dictionary.getDictionary();
      } else if (currentScene.equalsIgnoreCase("revisionlist")) {
         list = PracticeList.getPracticedWords();
      }

      if (isEnglish) {
         list.sort(new EnglishComparator());
      } else {
         list.sort(new WelshComparator());
      }

      LinkedList newWords = new LinkedList<>();
      // If we have more than 0 letters on the search bar
      if (searchBar.getText().length() > 0) {
         // For all words we're looking through
         for (Object word : list) {
            // check if we're searching in english
            if (isEnglish) {
               int substring = 0;

               // remove the "to " prefix if it's a verb
               if (((Word) word).getWordType().equalsIgnoreCase("verb")) {
                  substring = 3;
               }

               // and if the word's length is longer than the search bar's length
               if (((Word) word).getEnglish().substring(substring).length() >= searchBar.getText().length()) {
                  // and the word's beginning is the same as the search bar's text
                  if (((Word) word).getEnglish().substring(0 + substring, substring + searchBar.getText().length()).equalsIgnoreCase(searchBar.getText())) {
                     // add it into the list of new words
                     newWords.add(word);
                  }
               }
            // searching in welsh
            } else {
               // and if the word's length is longer than the search bar's length
               if (((Word) word).getWelsh().length() >= searchBar.getText().length()) {
                  // and the word's beginning is the same as the search bar's text
                  if (((Word) word).getWelsh().substring(0, searchBar.getText().length()).equalsIgnoreCase(searchBar.getText())) {
                     // add it into the list of new words
                     newWords.add(word);
                  }
               }
            }
         }

         // Clear the table and show our newly chosen words
         getTableFromUI().getItems().clear();
         getTableFromUI().getItems().addAll(newWords);
      // nothing in the search bar, so just show all words
      } else {
         getTableFromUI().getItems().clear();
         switch (currentScene.toLowerCase()) {
            case "dictionary":
               loadDictionaryIntoUI();
               break;
            case "revisionlist":
               loadFavoritesIntoUI();
               break;
         }
      }
   }

   /**
    * Clear text in search bar
    */
   @FXML
   private void clearSearchBar() {
      ((TextField) getUIElement("searchBar")).setText("");
      searchButtonClickInUI();
   }

   /**
    * Choose a letter and filter the words in dictionary
    *
    * @throws IOException exception will throw if files, assets don't exist
    */
   @FXML
   private void openLetterSearchMenu()
   throws IOException {
      String[] welshAlphabet = {"a", "b", "c", "ch", "d", "dd", "e", "f", "ff", "g", "ng", "h", "i", "j", "l", "ll", "m", "n", "o", "p", "ph", "r", "rh", "s", "t", "th", "u", "w", "y"};
      String[] englishAlphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

      Stage primaryScene = new Stage();
      primaryScene.setTitle("Choose a letter");
      primaryScene.setMinHeight(400);
      primaryScene.setMinWidth(640);
      primaryScene.setScene(new Scene(FXMLLoader.load(getClass().getResource("/uk/ac/aber/cs221/nine/scenes/letterSelectScene.fxml"))));
      primaryScene.show();

      // Grid of the letter buttons
      GridPane buttonGrid = (GridPane) primaryScene.getScene().lookup("#letterGrid");
      int column = 0;
      int row = 0;

      String[] alphabet;

      if (isEnglish) {
         alphabet = englishAlphabet;
      } else {
         alphabet = welshAlphabet;
      }

      // For every letter in the corresponding alphabet
      for (String letter : alphabet) {
         // Move onto a new row if we've hit 6 letters in a row
         if (column == 6) {
            column = 0;
            row++;
         }

         // Create each letter button and assign it to fill in the search bar
         Button letterButton = new Button("");
         letterButton.setText(letter.toUpperCase());
         letterButton.setOnAction((ActionEvent event) -> {
            ((TextField) getUIElement("searchBar")).setText(letter.toUpperCase());
            searchButtonClickInUI();
            primaryScene.close();
         });
         letterButton.setMaxSize(500, 500);
         buttonGrid.add(letterButton, column, row);
         column++;
      }
   }

   /**
    * Change primary language user interface
    */
   public void changeLanguageInUI() {
      isEnglish = !isEnglish;
      sortTableInUI();

      Main.uiHandler.searchButtonClickInUI();
   }

   /**
    * Start quiz
    *
    * @throws IOException exception will throw if files, assets don't exist
    */
   public void startQuiz()
   throws IOException {
      // Start quiz if there are words in practice list, else display "No words in practice list!" notification.
      if (PracticeList.getPracticedWords().size() > 0) {
         Quiz.startQuiz();
      } else {
         showNotification("No words in practice list!", 3, true);
      }
   }

   /**
    * Change the current scene to flash card scene
    *
    * @throws IOException exception will throw if files, assets don't exist
    */
   public void changeToFlashcardScene() throws IOException {
      // Do we have more than 0 words in the practice list?
      if (PracticeList.getPracticedWords().size() > 0) {
         root = FXMLLoader.load(getClass().getResource("/uk/ac/aber/cs221/nine/scenes/flashcardScene.fxml"));
         primaryScene.setScene(new Scene(root));

         Flashcard.resetFlashcardStack();
      } else {
         // Less than 1 word in favorites
         showNotification("You have no words added into your practice list!", 5, true);
      }
   }

   /**
    * Change to settings scene
    *
    * @throws IOException exception will throws if files, assets are not exist
    */
   public void changeToSettingsScene()
   throws IOException {
      root = FXMLLoader.load(getClass().getResource("/uk/ac/aber/cs221/nine/scenes/settingsScene.fxml"));
      primaryScene.setScene(new Scene(root));

      int[] wordAmountChoices = new int[]{5,15,20,25,30};
      for (int amount : wordAmountChoices) {
         ((ChoiceBox) getUIElement("wordAmountChoice")).getItems().add(amount);
      }

      ((ChoiceBox) getUIElement("wordAmountChoice")).setValue(Settings.getAmountOfWordsInQuiz());

      // Change the word amount setting when the value is changed
      ((ChoiceBox) getUIElement("wordAmountChoice")).setOnAction(e -> {
         Settings.setAmountOfWordsInQuiz((int) ((ChoiceBox) getUIElement("wordAmountChoice")).getValue());
         showNotification("Set amount of words quizzed to: " + ((ChoiceBox) getUIElement("wordAmountChoice")).getValue(), 3, false);
      });

      ((ChoiceBox) getUIElement("flashcardLanguage")).getItems().addAll(new String[]{"English","Welsh","Both"});
      ((ChoiceBox) getUIElement("flashcardLanguage")).setValue(Settings.getLanguageOfQuiz());

      // Change the language setting when the value is changed
      ((ChoiceBox) getUIElement("flashcardLanguage")).setOnAction(e -> {
         Settings.setLanguageOfQuiz((String) ((ChoiceBox) getUIElement("flashcardLanguage")).getValue());
         showNotification("Set quizzed language to: " + ((ChoiceBox) getUIElement("flashcardLanguage")).getValue(), 3, false);
      });

      ((ChoiceBox) getUIElement("fontSizeDropdown")).getItems().addAll(new String[]{"Small","Medium","Large"});
      ((ChoiceBox) getUIElement("fontSizeDropdown")).setValue(Settings.getFontSize());

      // Change the font size setting when the value is changed
      ((ChoiceBox) getUIElement("fontSizeDropdown")).setOnAction(e -> {
         setFontSize((String) ((ChoiceBox) getUIElement("fontSizeDropdown")).getValue());
         showNotification("Set font to: " + ((ChoiceBox) getUIElement("fontSizeDropdown")).getValue(), 3, false);
      });

      ((Button) getUIElement("clearPracticeListButton")).setOnAction(e -> {
         // Clear practice list words
         for (int index = (PracticeList.getPracticedWords().size() - 1); index >= 0; index--) {
            PracticeList.removeFromPracticedWords(PracticeList.getPracticedWords().get(index));
         }

         showNotification("Practice list cleared!", 2, true);
      });

      // Help button action
      ((Button) getUIElement("helpButton")).setOnAction(e -> {
         try {
            createMessageBox("Welcome to the Welsh Dictionary app! \n" +
                    "To add or remove a word into/from \n your practice list, just double click it! \n" +
                    "If you want to add a new word, \n just click on the plus sign on the bottom right. \n" +
                    "You can edit a word or completely remove it \n from the dictionary by right clicking. \n");
         } catch (IOException ioException) {
            ioException.printStackTrace();
         }
      });

   }

   /**
    * Loads the corresponding scene for the quiz tests
    *
    * @param testNumber number of the scene to load
    * @throws IOException exception will throw if files, assets don't exist
    */
   public void changeToQuizScene(int testNumber)
   throws IOException {
      root = FXMLLoader.load(getClass().getResource("/uk/ac/aber/cs221/nine/scenes/quiz" + testNumber + "Scene.fxml"));
      primaryScene.setScene(new Scene(root));
   }

   /**
    * Change current scene to home scene
    *
    * @throws IOException exception will throw if files, assets don't exist
    */
   public void changeToHomeScene()
   throws IOException {
      root = FXMLLoader.load(getClass().getResource("/uk/ac/aber/cs221/nine/scenes/homeScene.fxml"));
      primaryScene.setScene(new Scene(root));
      setCurrentScene("dictionary");
      loadDictionaryIntoUI();
   }

   /**
    * Change current scene to revision(practice) list scene
    *
    * @throws IOException exception will throw if files, assets don't exist
    */
   public void changeToRevisionListScene()
   throws IOException {
      root = FXMLLoader.load(getClass().getResource("/uk/ac/aber/cs221/nine/scenes/revisionListScene.fxml"));
      primaryScene.setScene(new Scene(root));
      setCurrentScene("revisionlist");
      loadFavoritesIntoUI();
   }

   /**
    * Change current scene to revision scene
    *
    * @throws IOException exception will throw if files, assets don't exist
    */
   public void changeToRevisionScene()
   throws IOException {
      root = FXMLLoader.load(getClass().getResource("/uk/ac/aber/cs221/nine/scenes/revisionScene.fxml"));
      primaryScene.setScene(new Scene(root));
   }

   /**
    * Add word user interface
    *
    * @throws IOException exception will throw if files, assets don't exist
    */
   public void openAddWordToDictionaryUI()
   throws IOException {

      Stage primaryScene = new Stage();
      primaryScene.setTitle("Add word");
      primaryScene.setMinHeight(400);
      primaryScene.setMinWidth(640);
      primaryScene.setScene(new Scene(FXMLLoader.load(getClass().getResource("/uk/ac/aber/cs221/nine/scenes/addWordScene.fxml"))));
      primaryScene.show();
      addWordStage = primaryScene;
      Button addWordButton = ((Button) addWordStage.getScene().lookup("#addWordButton"));

      ChoiceBox choice = ((ChoiceBox) addWordStage.getScene().lookup("#wordtypeChoiceBox"));
      choice.getItems().addAll(new String[]{"nm - noun masculine", "nf - noun feminine", "verb", "other"});
      choice.setValue("nm - noun masculine");
      addWordButton.setDisable(true);
   }

   /**
    * Add new word to the dictionary, executed from the addWord user interface
    */
   public void addWord() {

      TextField englishTextField = ((TextField) addWordStage.getScene().lookup("#englishDefinitionBox"));
      TextField welshTextField = ((TextField) addWordStage.getScene().lookup("#welshDefinitionBox"));
      ChoiceBox wordTypeChoiceBox = ((ChoiceBox) addWordStage.getScene().lookup("#wordtypeChoiceBox"));

      String type = "";

      if (((String) wordTypeChoiceBox.getValue()).contains("noun masculine")) {
         type = "nm";
      }
      if (((String) wordTypeChoiceBox.getValue()).contains("noun feminine")) {
         type = "nf";
      }
      if (((String) wordTypeChoiceBox.getValue()).contains("verb")) {
         type = "verb";
      }
      if (((String) wordTypeChoiceBox.getValue()).contains("other")) {
         type = "other";
      }

      Word wordToBeAdded;

      if (type.equals("verb")) {
         wordToBeAdded = new Word("to " + englishTextField.getText(), welshTextField.getText(), type);
      } else {
         wordToBeAdded = new Word(englishTextField.getText(), welshTextField.getText(), type);
      }

      addWordStage.close();
      addWordStage = null;

      if (Dictionary.getDictionary().contains(wordToBeAdded)) {
         showNotification("Word already exists in the dictionary!", 3, true);
      } else {
         Dictionary.addWord(wordToBeAdded, true);
         showNotification("Word added successfully", 2, false);
      }

      searchButtonClickInUI();
   }

   /**
    * Check whether the word being added isn't invalid (empty boxes)
    */
   public void checkWordAddRequirements() {
      TextField englishTextField = ((TextField) addWordStage.getScene().lookup("#englishDefinitionBox"));
      TextField welshTextField = ((TextField) addWordStage.getScene().lookup("#welshDefinitionBox"));
      ChoiceBox wordTypeChoiceBox = ((ChoiceBox) addWordStage.getScene().lookup("#wordtypeChoiceBox"));
      Button addWordButton = ((Button) addWordStage.getScene().lookup("#addWordButton"));
      addWordButton.setDisable(englishTextField.getText().equalsIgnoreCase("") || welshTextField.getText().equalsIgnoreCase("") || wordTypeChoiceBox.getValue().equals(""));
   }

   /**
    * Navigate to previous flashcard
    */
   @FXML
   private void lastFlashcard() {
      Flashcard.lastFlashcard();
   }

   /**
    * Navigate to the next flashcard
    */
   @FXML
   private void nextFlashcard() {
      Flashcard.nextFlashcard();
   }

   /**
    * Get the opposite language for the current flashcard being displayed
    */
   @FXML
   private void flipFlashcard() {
      Flashcard.flipFlashcard();
   }

}
