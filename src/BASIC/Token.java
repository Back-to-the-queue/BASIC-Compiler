package BASIC;
/**
 * @author Nicolas Quesada (nquesada@albany.edu) 01/17/2024
 *{@code Token} creates lexemes for each group of strings
 */
public class Token {
    private TokenType tokenValue; //
    private int line; //
    private int linePosition; //
    private String value; //

    /**
     *
     */
    public enum TokenType{
        WORD, NUMBER, SEPARATOR, STRINGLITERAL, INPUT, PRINT, READ, DATA, GOSUB, FOR, IF, TO, STEP, RETURN, NEXT, THEN,
        FUNCTION, WHILE, END, LESSTHAN, GREATERTHAN, LESSEQUAL, GREATEREQUAL, EQUALS, DECREMENT, INCREMENT, EXPONENTEQUAL,
        MODEQUAL, TIMESEQUAL, DIVIDEEQUAL, PLUSEQUAL, MINUSEQUAL, NOMATCH, AND, APPEND, OR, NOTEQUAL, LCBRACE, RCBRACE,
        LBRACE, RBRACE, MATCH, EQUAL, NOT, EXPONENT, TERNARY, COLON, MOD, SEMICOLON, LINE, LPAREN, RPAREN, TIMES, PLUS,
        MINUS, DIVIDE, LABEL, COMMA, MID, RANDOM, LEFT, RIGHT, NUM, VAL;
    }

    /**
     *
     * @param type
     * @param Line
     * @param LinePos
     */
    public Token(TokenType type, int Line, int LinePos){
        this.tokenValue = type;
        this.line = Line;
        this.linePosition = LinePos;
    }

    /**
     *
     * @param tokenValue
     * @param value
     */
    public Token(TokenType tokenValue, String value){
        this.tokenValue = tokenValue;
        this.value = value;
    }

    /**
     * Constructor for key words to convert to Tokens
     */
    public Token(String str){
        if(TokenType.valueOf(str.toUpperCase()).toString().equals(str.toUpperCase()))
        {
            tokenValue = TokenType.valueOf(str.toUpperCase());
            value = str;
        }
    }

    /**
     *
     * @return
     */
    public TokenType getTokenValue() {return tokenValue;}

    /**
     *
     * @return
     */
    public String getValue() {return value;}

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return getTokenValue() + "(" + getValue() + ")";
    }
}
