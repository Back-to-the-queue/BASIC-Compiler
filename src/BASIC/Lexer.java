package BASIC;

import javax.print.Doc;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Nicolas Quesada (nquesada@albany.edu) 01/17/2024
 * {@code Lexer} goes through the document character by character
 * and passes them to {@code Token} to create the lexemes
 */

public class Lexer {
    private int line = 1; //holds the line number
    private int position = 0; //holds the position on the line
    private String processedString = ""; //holds the string created by processWord & processNumber
    private final CodeHandler Document; //holds the document
    public static LinkedList<Token> tokenList = new LinkedList<>(); //holds the list of lexemes
    private char currentChar; //holds the current character that is being looked at
    private int qCount; //holds the number of quotes in a literal
    HashMap<String, Token.TokenType> keyWords = new HashMap<>(); //Hashmap to store our keywords
    HashMap<String, Token.TokenType> functions = new HashMap<>(); //Hashmap to store builtin functions
    HashMap<String, Token.TokenType> twoCharExp = new HashMap<>(); //Hashmap to store our two character expressions
    HashMap<String, Token.TokenType> oneCharExp = new HashMap<>(); //Hashmap to store our one character expressions
    final String[] FUNCTIONS = {"random", "left", "right", "mid", "num", "val"};
    final String[] DEFINEDWORDS = {"read", "print", "input", "data", "gosub", "for", "to", "step", "next",
            "return", "if", "then", "function", "while", "end", "random"};
    /**
     *Constructor passes the document to CodeHandler
     * @param document takes in the document and sets it to the variable
     */
    public Lexer(String document){
        this.Document = new CodeHandler(document);
        for(var mapWord: DEFINEDWORDS) {
            keyWords.put(mapWord, Token.TokenType.valueOf(mapWord.toUpperCase()));
        }
        for(var i : FUNCTIONS){
            functions.put(i, Token.TokenType.valueOf(i.toUpperCase()));
        }
        twoCharExp.put("==", Token.TokenType.EQUALS);
        twoCharExp.put("<=", Token.TokenType.LESSEQUAL);
        twoCharExp.put("--", Token.TokenType.DECREMENT);
        twoCharExp.put("++", Token.TokenType.INCREMENT);
        twoCharExp.put(">=", Token.TokenType.GREATEREQUAL);
        twoCharExp.put("<>", Token.TokenType.NOTEQUAL);
        twoCharExp.put("^=", Token.TokenType.EXPONENTEQUAL);
        twoCharExp.put("%=", Token.TokenType.MODEQUAL);
        twoCharExp.put("*=", Token.TokenType.TIMESEQUAL);
        twoCharExp.put("/=", Token.TokenType.DIVIDEEQUAL);
        twoCharExp.put("+=", Token.TokenType.PLUSEQUAL);
        twoCharExp.put("-=", Token.TokenType.MINUSEQUAL);
        twoCharExp.put("!~", Token.TokenType.NOMATCH);
        twoCharExp.put("", Token.TokenType.EQUALS);
        twoCharExp.put("&&", Token.TokenType.AND);
        twoCharExp.put(">>", Token.TokenType.APPEND);
        twoCharExp.put("||", Token.TokenType.OR);

        oneCharExp.put("{", Token.TokenType.LCBRACE);
        oneCharExp.put("}", Token.TokenType.RCBRACE);
        oneCharExp.put("[", Token.TokenType.LBRACE);
        oneCharExp.put("]", Token.TokenType.RBRACE);
        oneCharExp.put("(", Token.TokenType.LPAREN);
        oneCharExp.put(")", Token.TokenType.RPAREN);
        oneCharExp.put("~", Token.TokenType.MATCH);
        oneCharExp.put("=", Token.TokenType.EQUAL);
        oneCharExp.put("<", Token.TokenType.LESSTHAN);
        oneCharExp.put(">", Token.TokenType.GREATERTHAN);
        oneCharExp.put("!", Token.TokenType.NOT);
        oneCharExp.put("+", Token.TokenType.PLUS);
        oneCharExp.put("^", Token.TokenType.EXPONENT);
        oneCharExp.put("-", Token.TokenType.MINUS);
        oneCharExp.put("?", Token.TokenType.TERNARY);
        oneCharExp.put(":", Token.TokenType.COLON);
        oneCharExp.put("*", Token.TokenType.TIMES);
        oneCharExp.put("/", Token.TokenType.DIVIDE);
        oneCharExp.put("%", Token.TokenType.MOD);
        oneCharExp.put(";", Token.TokenType.SEMICOLON);
        oneCharExp.put("|", Token.TokenType.LINE);
        oneCharExp.put(",", Token.TokenType.COMMA);

    }

    /**
     *Parses through the entire document and adds the Lexemes to the list of Tokens
     * @throws Exception when an unexpected character is seen
     */
    public void lex() throws Exception {
        System.out.println("Document: " + Document.remainder());
        while(!Document.isDone()){
            currentChar = Document.getChar(); //sets the current character to the
            Token processedToken; //holds the token created by processWord & processNumber
            if(Character.isWhitespace(currentChar)){
                position++;
            } else if(currentChar == '\n'){
                line++;
                position = 0;
                tokenList.add(new Token(Token.TokenType.SEPARATOR, ""));
            } else if(currentChar == '\r'){
                position++;
                Document.swallow(1);
            } else if(Character.isLetter(currentChar)){
                processedToken = processWord();
                tokenList.add(processedToken);
                processedString = "";
            } else if(Character.isDigit(currentChar)){
                processedToken = processNumber();
                tokenList.add(processedToken);
                processedString = "";
            } else if(currentChar == '"'){
                processedToken = handleStringLiteral();
                tokenList.add(processedToken);
                processedString = "";
            } else if(oneCharExp.containsKey(String.valueOf(currentChar))){
                processedToken = processSymbol();
                tokenList.add(processedToken);
                processedString = "";
            }
            System.out.println("TokenList: " + tokenList);
        }
    }

    /**
     *Processes the current String and creates a token of it
     * @return a token that contains the WORD type and the string of the word
     * @throws Exception when an unexpected character is seen
     */
    private Token processWord() throws Exception{
        while(!Character.isWhitespace(currentChar)){
            if(Character.isLetter(currentChar) || Character.isDigit(currentChar) || currentChar == '_'){
                processedString += currentChar;
                position++;
                System.out.println(Document.peek(1));
            }
            if(Document.peek(1) == ':'){
                currentChar = Document.getChar();
                position++;
                processedString += currentChar;
                Document.swallow(1);
                return new Token(Token.TokenType.LABEL, processedString);
            }
            else if (keyWords.containsKey(processedString) && ((Character.isWhitespace(currentChar) || currentChar == '\n'))){
            return new Token(String.valueOf(keyWords.get(processedString.toLowerCase())));
            }
            else if(Document.isDone()){
                break;
            }
            else if(!Character.isLetter(currentChar) && !Character.isDigit(currentChar) && currentChar != '_' && !Character.isWhitespace(currentChar)){
                throw new Exception("Invalid character @ " + line + ":" + position);
            }
            currentChar = Document.getChar();
        }
        return new Token(Token.TokenType.WORD, processedString);
    }

    /**
     * Processes the current String and creates a token for it
     * @return a token that contains the NUMBER type and the string of the number
     * @throws Exception when an unexpected character is seen
     */
    private Token processNumber() throws Exception{
        int decimal = 0;
        while (!Character.isWhitespace(currentChar) && !Document.isDone()) {
            if (Character.isDigit(currentChar)) {
                processedString += currentChar;
                position++;
            } else if (currentChar == '.') {
                decimal++;
                if(decimal > 1){
                    throw new Exception("Error: Too many decimal points @ " + line + ":" + position);
                }
                processedString += currentChar;
                position++;
            } else if(Document.isDone()){
                break;
            }
            currentChar = Document.getChar();
        }
        return new Token(Token.TokenType.NUMBER, processedString);
    }

    /**
     * Processes the current String and creates a token for it
     * @return a token that contains the type of symbol and the string of the symbol
     * @throws Exception when an unexpected character is seen
     */
    private Token processSymbol() throws Exception{return null;}

    /**
     * Processes the current String Literal and creates a token for it
     * @return a token that contains the STRINGLITERAL type and the string of the number
     * @throws Exception when an unexpected character is seen or if there are an uneven number of quotations
     */
    private Token handleStringLiteral() throws Exception{
        qCount++;
        while(currentChar != '"'){
            processedString += currentChar;
            currentChar = Document.getChar();
            position++;
        }
        if (qCount % 2 != 0) throw new Exception("Unfinished quote");
        return new Token(Token.TokenType.STRINGLITERAL, processedString);
    }
}