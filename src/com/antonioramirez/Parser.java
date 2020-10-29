/*
 * Filename: Parser.java
 * Date: 15 Sep 19
 * Author: Antonio Ramirez
 * Purpose: Takes a lexer as a parameter and receives tokens and parses the corresponding GUI element as a result
 */
package com.antonioramirez;

import javax.swing.*;
import java.awt.*;
import java.io.*;

class Parser {
    private Lexer lexer;
    private Token currentToken;
    private JFrame jFrame;
    private String string;
    private ButtonGroup buttonGroup;
    private Token expectedToken;

    Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    //Starting method for the class
    void parse() throws IOException{
        //Checking if GUI builds successfully at which point it will be displayed.
        if (buildGUI()) {
            jFrame.setVisible(true);
        } else {
            //GUI did not parse completely, display error message.
            System.out.println("Problem building GUI -> " + expectedToken + " Check document structure");
        }
    }

    //Called by parse(), calls all subsequent methods
    //Creates the JFrame, main JPanel, and calls the other parse methods to create the GUI
    private boolean buildGUI() throws IOException{
        //Variables used for window/panel height and width
        int width;
        int height;
        //Grabbing the initial currentToken, rest of grabs occur within the checkToken() switch
        currentToken = lexer.getNextToken();
        //Window requires title string before being parsed
        if (checkToken(Token.WINDOW)){
            //Window title string check
            if (checkToken(Token.STRING)){
                //New window with the String as the title
                jFrame = new JFrame(string);
                jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                //Creating a main panel for the window
                JPanel mainPanel = new JPanel();
                //Checking for window dimensions
                if (checkToken(Token.L_PARENTHESIS)) {
                    if (checkToken(Token.NUMBER)) {
                        //Storing number currentToken as the width
                        width = (int) lexer.getNVal();
                        currentToken = lexer.getNextToken();
                        if (checkToken(Token.COMMA)) {
                            if (checkToken(Token.NUMBER)) {
                                //Storing number currentToken as height
                                height = (int) lexer.getNVal();
                                currentToken = lexer.getNextToken();
                                if (checkToken(Token.R_PARENTHESIS)) {
                                    //assigning dimensions to the window and main panel
                                    jFrame.setSize(width,height);
                                    mainPanel.setSize(width,height);
                                    jFrame.add(mainPanel);
                                    //Calling the parseLayout() method fot the main panel layout
                                    if (parseLayout(mainPanel)) {
                                        //Calling the parseWidgets() recursive method to populate panel with components
                                        if (parseWidgets(mainPanel)) {
                                            //Checking for end of GUI
                                            if (checkToken(Token.END)) {
                                                //Returns true, displays GUI in parse()
                                                return checkToken(Token.PERIOD);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //If any of the previous check fail, return false to parse() method, and display an error
        return false;
    }

    //Method called in buildGUI() method, grabs next currentToken or takes necessary actions before moving on.
    //Ensures currentToken passed from the
    private boolean checkToken(Token nextToken) throws IOException{
        if (currentToken == nextToken) {
            switch (currentToken) {
                //These cases need no further action at the moment, move on to the next currentToken
                case BUTTON:
                case END:
                case FLOW:
                case GRID:
                case LABEL:
                case LAYOUT:
                case PANEL:
                case RADIO:
                case TEXTFIELD:
                case WINDOW:
                case COMMA:
                case COLON:
                case L_PARENTHESIS:
                case R_PARENTHESIS:
                case SEMICOLON:
                    currentToken = lexer.getNextToken();
                    break;
                case GROUP:
                    //Creating a new ButtonGroup before moving on, used for the radio buttons
                    buttonGroup = new ButtonGroup();
                    currentToken = lexer.getNextToken();
                    break;
                case STRING:
                    //Storing the value in the class variable for use in other methods before moving on
                    string = lexer.getSVal();
                    currentToken = lexer.getNextToken();
                    break;
            }
            //currentToken passed the check, return true
            return true;
        } else {
            //Used for error message generation to pinpoint where the issue is within the document
            expectedToken = nextToken;
            //Returns false if check fails
            return false;
        }
    }

    //Used to add the correct layout to the window/panel
    private boolean parseLayout(Container panel) throws IOException{
        int rows;
        int cols;
        int hgap;
        int vgap;
        //Check for Layout keyword
        if (checkToken(Token.LAYOUT)) {
            //determine if it is a flow or grid layout
            if (checkToken(Token.FLOW)) {
                //Add flow layout to the panel
                panel.setLayout(new FlowLayout());
                //Next token is a colon, run check, should return true
                return checkToken(Token.COLON);
                //Grid is the other layout option
            } else if (checkToken(Token.GRID)) {
                //Read dimensions, similar to the window methodology
                if (checkToken(Token.L_PARENTHESIS)) {
                    if (checkToken(Token.NUMBER)) {
                        rows = (int) lexer.getNVal();
                        currentToken = lexer.getNextToken();
                        if (checkToken(Token.COMMA)) {
                            if (checkToken(Token.NUMBER)) {
                                cols = (int) lexer.getNVal();
                                currentToken = lexer.getNextToken();
                                //Checking for the optional hgap and vgap numbers
                                if (checkToken(Token.R_PARENTHESIS)) {
                                    panel.setLayout(new GridLayout(rows, cols));
                                    return checkToken(Token.COLON);
                                    //If the optional numbers are there, process them
                                    //Similar to the previous dimension methods
                                } else if (checkToken(Token.COMMA)) {
                                    if (checkToken(Token.NUMBER)) {
                                        hgap = (int) lexer.getNVal();
                                        currentToken = lexer.getNextToken();
                                        if (checkToken(Token.COMMA)) {
                                            if (checkToken(Token.NUMBER)) {
                                                vgap = (int) lexer.getNVal();
                                                currentToken = lexer.getNextToken();
                                                if (checkToken(Token.R_PARENTHESIS)) {
                                                    panel.setLayout(new GridLayout(rows, cols, hgap, vgap));
                                                    return checkToken(Token.COLON);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //Return false if any check fails, eventually creating an error message
        return false;
    }

    //Uses recursion to ensure all panel widgets are built
    private boolean parseWidgets(Container panel) throws IOException{
        if (createWidget(panel)) {
            if (parseWidgets(panel)) {
                return true;
            }
            return true;
        }
        return false;
    }

    //Adds the correct component to the panel, or creates a new panel and goes through the entire layout process again
    private boolean createWidget(Container panel) throws IOException{
        //Used for TextField size
        int columns;
        //Creates a button
        if (checkToken(Token.BUTTON)) {
            if (checkToken(Token.STRING)) {
                if (checkToken(Token.SEMICOLON)) {
                    panel.add(new JButton(string));
                    return true;
                }
            }
        }
        //Creates a ButtonGroup and calls the parseRadioButtons() helper method
        else if (checkToken(Token.GROUP)) {
            if (parseRadioButtons(panel)) {
                if (checkToken(Token.END)) {
                    return checkToken(Token.SEMICOLON);
                }
            }
        }
        //Creates a JLabel and adds it to the panel
        else if (checkToken(Token.LABEL)) {
            if (checkToken(Token.STRING)) {
                if (checkToken(Token.SEMICOLON)) {
                    panel.add(new JLabel(string));
                    return true;
                }
            }
        }
        //Creates a new panel
        else if (checkToken(Token.PANEL)) {
            //Adding the panel to our main/original panel
            panel.add(panel = new JPanel());
            //Adding a layout using the same method as before
            if (parseLayout(panel)) {
                //Adding panel specific widgets
                if (parseWidgets(panel)) {
                    //End of panel widgets
                    if (checkToken(Token.END)) {
                        return checkToken(Token.SEMICOLON);
                    }
                }
            }
        }
        //Creates a Textfield using the columns variable
        else if (checkToken(Token.TEXTFIELD)) {
            if (checkToken(Token.NUMBER)) {
                columns = (int) lexer.getNVal();
                currentToken = lexer.getNextToken();
                if (checkToken(Token.SEMICOLON)) {
                    panel.add(new JTextField(columns));
                    return true;
                }
            }
        }
        //If widget is not in the correct format, relies on checks
        return false;
    }

    //Same methodology as the widgets recursion
    private boolean parseRadioButtons(Container panel) throws IOException{
        if (createRadioButton(panel)) {
            if (parseRadioButtons(panel)) {
                return true;
            }
            return true;
        }
        return false;
    }

    //Creates the button and adds it to the ButtonGroup
    private boolean createRadioButton(Container panel) throws IOException{
        if (checkToken(Token.RADIO)) {
            if (checkToken(Token.STRING)) {
                if (checkToken(Token.SEMICOLON)) {
                    JRadioButton rButton = new JRadioButton(string);
                    panel.add(rButton);
                    buttonGroup.add(rButton);
                    return true;
                }
            }
        }
        return false;
    }
}