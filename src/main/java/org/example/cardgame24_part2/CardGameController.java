package org.example.cardgame24_part2;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Card Game Controller for Card Game 24
 *
 */

public class CardGameController {
    @FXML
    private ImageView card1ImageView, card2ImageView, card3ImageView, card4ImageView;
    @FXML
    private TextField expressionTextField, solutionTextField;
    @FXML
    private Button refreshButton, verifyButton, findSolutionButton;

    private List<String> deck; //hold card file names
    private List<Integer> currentCardValues; //sore numeric value of cards

    /**
     * Initialize method. initializes the game by setting up deck, shiffling the cards and assigning the cards values
     *
     */
    @FXML
    public void initialize() {
        deck = new ArrayList<>();
        String[] suits = {"hearts", "diamonds", "spades", "clubs"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king", "ace"};

        for(String rank : ranks){
            for(String suit : suits){
                deck.add(rank + "_of_" + suit + ".png");
            }
        }

        refreshCards();

        //refresh button function
        refreshButton.setOnAction(e -> refreshCards());

        //verify button function
        verifyButton.setOnAction(e -> verifyExpression());

        //find solution button
        findSolutionButton.setOnAction(e -> findSolution());

    }//end initial method

    /**
     * refreshCards() method,shuffles deck and updates the displayed cards
     */
    private void refreshCards() {
        //shuffle deck
        Collections.shuffle(deck);
        currentCardValues = new ArrayList<>();

        //update the images for dour cards
        updateCard(card1ImageView, deck.get(0));
        updateCard(card2ImageView, deck.get(1));
        updateCard(card3ImageView, deck.get(2));
        updateCard(card4ImageView, deck.get(3));

        //extract numeric card values
        currentCardValues.add(getCardValue(deck.get(0)));
        currentCardValues.add(getCardValue(deck.get(1)));
        currentCardValues.add(getCardValue(deck.get(2)));
        currentCardValues.add(getCardValue(deck.get(3)));

        //clear input fields
        expressionTextField.clear();
        solutionTextField.clear();

    }//end refresh cards method

    /**
     * updates imageviews with specified images
     * @param cardImageView imageView to be updated
     * @param cardFileName file name for card images
     */
    private void updateCard(ImageView cardImageView, String cardFileName) {
        InputStream stream = CardGameController.class.getResourceAsStream("/cardImages/" + cardFileName);

        if(stream == null){
            throw new RuntimeException("Image not found: " + "/cardImages/" + cardFileName);
        }
        Image cardImage = new Image(stream);
        cardImageView.setImage(cardImage);
    } //updateCards() end

    /**
     * gets numeric value of cards based on file name
     * @param cardFileName file name of card
     * @return numeric value of card
     */
    private int getCardValue(String cardFileName) {
        String cardname = cardFileName.split("_of_")[0];
        switch (cardname){
            case "ace": return 1;
            case "jack": return 11;
            case "queen": return 12;
            case "king": return 13;
            default: return Integer.parseInt(cardname);
        }
    }

    /**
     * verifies if user inputef an expression that evaluates to 24
     */
    private void verifyExpression(){
        String expression = expressionTextField.getText();

        //check expression contains all card values displayed
        if(!expressionContainsAll(expression)){
            showAlert("Invalid Input, expression must use all four cards");
            return;
        }

        //evaluate user expression
        try{
            double result = evaluateExpression(expression);
            if(Math.abs(result-24) < 0.0001){
                showAlert("Correct! Your expression evaluates to 24");
            }else{
                showAlert("Incorrect! Your expression does not evaluate to 24");
            }
        }catch (Exception e){
            showAlert("Invalid arithmetic expression");
        }
    }

    /**
     * displays message to user in alert box
     * @param s message to be displayed
     */
    private void showAlert(String s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }

    /**
     * checks expression contains all card values
     * @param expression users expression
     * @return true if all card values are used, false otherwise
     */
    private boolean expressionContainsAll(String expression){
        for(int value: currentCardValues){
            if(!expression.contains(String.valueOf(value))){
                return false;
            }

        }
        return true;
    }

    /**
     * evaluates the expression
     * @param expression expression to be evaluated
     * @return result of evaluation
     * @throws Exception if the expression is invalid
     */
    private double evaluateExpression(String expression) throws Exception{
        expression = expression.replaceAll("\\s+", "");
        List<String> tokens = tokenizeExpression(expression);
        return evaluateTokens(tokens);
    }

    /**
     * tokenize the given expression into a list of numbers and operators
     * @param expression expression to be tokenized
     * @return list of tokens
     * @throws Exception if expression contains invalid characters
     */
    private List<String> tokenizeExpression(String expression) throws Exception{
        List<String> tokens = new ArrayList<>();
        String number = "";

        for(char ch: expression.toCharArray()){
            if(Character.isDigit(ch) || ch == '.'){
                number += ch;
            }else if("+-*/()".indexOf(ch) != -1){
                if(!number.isEmpty()){
                    tokens.add(number);
                    number = "";
                }
                tokens.add(String.valueOf(ch));
            }else if(!Character.isWhitespace(ch)){
                throw new Exception("Invalid character in expression" + ch);
            }
        }

        if(!number.isEmpty()){
            tokens.add(number);
        }
        return tokens;
    }

    /**
     * evaluated list of tokens to compute the result
     *
     * @param tokens list of tokens to evaluate
     * @return evaluation
     * @throws Exception if expression is invalid
     */
    private double evaluateTokens(List<String> tokens) throws Exception{
        while (tokens.contains("(")) {
            int openIndex = tokens.lastIndexOf("(");
            int closeIndex = tokens.subList(openIndex, tokens.size()).indexOf(")") + openIndex;


            if (closeIndex == openIndex || closeIndex == -1) {
                throw new Exception("Mistmatch parenthesis");
            }

            double value = evaluateTokens(tokens.subList(openIndex + 1, closeIndex));

            for (int i = 0; i <= closeIndex - openIndex; i++) {
                tokens.remove(openIndex);
            }
            tokens.add(openIndex, String.valueOf(value));
        }

            for(int i = 0; i < tokens.size(); i++){
                if(tokens.get(i).equals("*") || tokens.get(i).equals("/")){
                    double left = Double.parseDouble(tokens.get(i - 1));
                    double right = Double.parseDouble(tokens.get(i + 1));
                    double result = tokens.get(i).equals("+") ? left * right : left / right;
                    
                    tokens.set(i - 1, String.valueOf(result));
                    tokens.remove(i);
                    tokens.remove(i);
                    i--;
                }
            }
            
            for(int i = 0; i < tokens.size(); i++){
                if(tokens.get(i).equals("+") || tokens.get(i).equals("-")){
                    double left = Double.parseDouble(tokens.get(i - 1));
                    double right = Double.parseDouble(tokens.get(i + 1));
                    double result = tokens.get(i).equals("+") ? left + right : left - right;
                    
                    tokens.set(i - 1, String.valueOf(result));
                    tokens.remove(i);
                    tokens.remove(i);
                    i--;
                }
            }
            
            if(tokens.size() != 1){
                throw new Exception("Invalid expression");
            }
            return Double.parseDouble(tokens.get(0));
    }

    /**
     * find valid expression that evaluates to 24 and displays in appropriate text field
     */
    private void findSolution() {
        // Fixed card values in the displayed order
        int a = currentCardValues.get(0);
        int b = currentCardValues.get(1);
        int c = currentCardValues.get(2);
        int d = currentCardValues.get(3);

        // All possible operators
        String[] operators = {"+", "-", "*", "/"};

        // Test all combinations of operators and parentheses
        for (String op1 : operators) {
            for (String op2 : operators) {
                for (String op3 : operators) {
                    // Different groupings of parentheses
                    String[] expressions = {
                            a + op1 + b + op2 + c + op3 + d,
                            "(" + a + op1 + b + ")" + op2 + c + op3 + d,
                            "(" + a + op1 + b + op2 + c + ")" + op3 + d,
                            "(" + a + op1 + "(" + b + op2 + c + "))" + op3 + d,
                            "(" + "(" + a + op1 + b + ")" + op2 + c + ")" + op3 + d
                    };

                    // Evaluate each expression
                    for (String expression : expressions) {
                        try {
                            double result = evaluateExpression(expression); // Use your existing evaluate method
                            if (Math.abs(result - 24) < 0.0001) {
                                // Display the solution in the result field
                                solutionTextField.setText(expression);
                                return; // Stop once a solution is found
                            }
                        } catch (Exception ignored) {
                            // Ignore invalid expressions (e.g., division by zero)
                        }
                    }
                }
            }
        }

        // If no solution is found, display a message
        solutionTextField.setText("No solution found.");
    }


}