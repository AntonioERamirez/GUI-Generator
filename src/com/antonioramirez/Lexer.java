/*
 * Filename: Lexer.java
 * Date: 15 Sep 19
 * Author: Antonio Ramirez
 * Purpose: Uses the StreamTokenizer to create tokens from the file that is passed from the JFileReader. Tokens are
 *          ultimately passed to the parser to be parsed.
 */
package com.antonioramirez;

import java.io.*;

//Relies on the SteamTokenizer class
//See: https://docs.oracle.com/javase/7/docs/api/java/io/StreamTokenizer.html#sval
class Lexer {
    private StreamTokenizer tokenizer;
    //String of the separators within the grammar
    //Will be broken down by char since they can easily be compared to tokens
    private String separators = ",:;.()";
    //Corresponding Token enums to the separators
    private Token[] separatorTokens = {Token.COMMA, Token.COLON, Token.SEMICOLON, Token.PERIOD, Token.L_PARENTHESIS, Token.R_PARENTHESIS};

    //Constructor that takes a filename, reads it, and tokenizes it
    public Lexer(String fileName) throws FileNotFoundException {
        //Reading the output of the JFileChooser, creates tokens from the file
        tokenizer = new StreamTokenizer(new FileReader(fileName));
        //Specifies that the character argument is "ordinary" in this tokenizer.
        tokenizer.ordinaryChar('.');
        //Specifies that matching pairs of this character delimit string constants in this tokenizer.
        tokenizer.quoteChar('"');
    }

    //Grabs the next token in the file, converts to one of the predefined enums
    public Token getNextToken() throws IOException {
        int token = tokenizer.nextToken();

        //Switch statement to evaluate which Token enum to return
        switch (token){
            case StreamTokenizer.TT_NUMBER:
                return Token.NUMBER;
            case StreamTokenizer.TT_WORD:
                //Iterates through all token enums until a match is found, returns that enum
                for (Token value : Token.values()){
                    //If the value matches the current token, return that enum
                    if (value.name().equals(tokenizer.sval.toUpperCase())){
                        return value;
                    }
                }
                //If a valid enum is not found, print error message
                System.out.println("Token not accepted: " + getSVal());
            case StreamTokenizer.TT_EOF:
                //A constant indicating that the end of the stream has been read.
                //Returns our enum
                return Token.EOF;
            case '"':
                return Token.STRING;
            default:
                //Evaluates the remainder "separator" tokens
                //A string with the charAt() method since int to char is comparable
                //See: https://www.javatpoint.com/java-char-to-int#targetText=Java%20Convert%20char%20to%20int&targetText=If%20we%20direct%20assign%20char,.valueOf(char)%20method.
                for (int i = 0; i < separators.length(); i++) {
                    if (token == separators.charAt(i)) {
                        return separatorTokens[i];
                    }
                }
        }
        return Token.EOF;
    }

    //If the current token is a word token, this field contains a string giving the characters of the word token
    public String getSVal()
    {
        return tokenizer.sval;
    }

    //If the current token is a number, this field contains the value of that number.
    public double getNVal()
    {
        return tokenizer.nval;
    }
}
