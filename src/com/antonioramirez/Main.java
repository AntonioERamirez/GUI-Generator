/*
 * Filename: Main.java
 * Date: 15 Sep 19
 * Author: Antonio Ramirez
 * Purpose: Contains the main method. Reads the file with JFileChooser and builds instances of the Lexer and Parser
 */
package com.antonioramirez;

import javax.swing.*;
import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException{
        //Pulling the user selected filename
	    String file = readFile();
	    //Passing the file to the Lexer to be separated into tokens
        Lexer lexer = new Lexer(file);
        //Sending the tokens to the parser to be parsed into various GUI elements
        Parser parser = new Parser(lexer);
        parser.parse();
    }

    //Method to extract user selected filename
    private static String readFile(){
        //JFrame for the FileChooser
        JFrame fileFrame = new JFrame();
        //See: //https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
        //Defaulting to the current filepath
        JFileChooser jFileChooser = new JFileChooser(".");
        if(JFileChooser.APPROVE_OPTION == jFileChooser.showOpenDialog(null)){
            fileFrame.setVisible(false);
            //Returning the filepath in a string for the lexer
            return jFileChooser.getSelectedFile().getAbsolutePath();
        }else {
            System.exit(1);
            System.out.println("File not selected");
            return null;
        }
    }
}
