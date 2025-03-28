package BASIC_Test.Lexer;

import BASIC.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

public class LexerTest {

    private String readTestFile(String filePath) throws Exception {
        return Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
    }

    @BeforeEach
    public void setUp() {
        //Clears the token list before each test to prevent contamination between tests
        Lexer.tokenList.clear();
    }

    @Test
    public void testProcessWord() throws Exception {
        String testInput = "print input data";
        Lexer lexer = new Lexer(testInput);
        lexer.lex();
        LinkedList<Token> tokens = Lexer.tokenList;

        assertEquals(3, tokens.size(), "Lexer should tokenize 3 words.");
        assertEquals(Token.TokenType.PRINT, tokens.get(0).getTokenValue());
        assertEquals(Token.TokenType.INPUT, tokens.get(1).getTokenValue());
        assertEquals(Token.TokenType.DATA, tokens.get(2).getTokenValue());
    }

    @Test
    public void testProcessNumber() throws Exception {
        String testInput = "123 45.67 89";
        Lexer lexer = new Lexer(testInput);
        lexer.lex();
        LinkedList<Token> tokens = Lexer.tokenList;

        assertEquals(3, tokens.size(), "Lexer should tokenize 3 numbers.");
        assertEquals(Token.TokenType.NUMBER, tokens.get(0).getTokenValue());
        assertEquals("123", tokens.get(0).getValue());
        assertEquals("45.67", tokens.get(1).getValue());
        assertEquals("89", tokens.get(2).getValue());
    }

    @Test
    public void testProcessSymbol() throws Exception {
        String testInput = "+ - * / = == <= >=";
        Lexer lexer = new Lexer(testInput);
        lexer.lex();
        LinkedList<Token> tokens = Lexer.tokenList;

        assertEquals(8, tokens.size(), "Lexer should tokenize 7 symbols.");
        assertEquals(Token.TokenType.PLUS, tokens.get(0).getTokenValue());
        assertEquals(Token.TokenType.MINUS, tokens.get(1).getTokenValue());
        assertEquals(Token.TokenType.TIMES, tokens.get(2).getTokenValue());
        assertEquals(Token.TokenType.DIVIDE, tokens.get(3).getTokenValue());
        assertEquals(Token.TokenType.EQUAL, tokens.get(4).getTokenValue());
        assertEquals(Token.TokenType.EQUALS, tokens.get(5).getTokenValue()); // ==
        assertEquals(Token.TokenType.LESSEQUAL, tokens.get(6).getTokenValue()); // <=
    }

    @Test
    public void testHandleStringLiteral() throws Exception {
        String testInput = "\"hello world\" \"AWK Lexer\"";
        Lexer lexer = new Lexer(testInput);
        lexer.lex();
        LinkedList<Token> tokens = Lexer.tokenList;

        assertEquals(2, tokens.size(), "Lexer should tokenize 2 string literals.");
        assertEquals(Token.TokenType.STRINGLITERAL, tokens.get(0).getTokenValue());
        assertEquals("hello world", tokens.get(0).getValue());
        assertEquals("AWK Lexer", tokens.get(1).getValue());
    }

    @Test
    public void testInvalidCharacter() {
        String testInput = "@";
        Lexer lexer = new Lexer(testInput);

        Exception exception = assertThrows(Exception.class, lexer::lex);
        assertTrue(exception.getMessage().contains("Invalid character"));
    }

    @Test
    public void testUnfinishedStringLiteral() {
        String testInput = "\"Hello world";
        Lexer lexer = new Lexer(testInput);

        Exception exception = assertThrows(Exception.class, lexer::lex);
        assertTrue(exception.getMessage().contains("Unfinished quote"));
    }
    @Test
    public void testFullProgram() throws Exception {
        //Read the full test program from a file
        String testInput = readTestFile("/Users/nicolasquesada/Documents/BASICpt2/src/BASIC_Test/Lexer/LexerFullProgramTest");
        Lexer lexer = new Lexer(testInput);
        lexer.lex();
        LinkedList<Token> tokens = Lexer.tokenList;

        //Ensure that we got some tokens
        assertFalse(tokens.isEmpty(), "Lexer should produce tokens for a full program.");

        //Check that the first token is BEGIN
        assertEquals(Token.TokenType.BEGIN, tokens.get(0).getTokenValue(), "First token should be BEGIN block.");

        //Check that a function name appears correctly
        boolean foundPrintFunction = tokens.stream()
                .anyMatch(token -> token.getTokenValue() == Token.TokenType.PRINT);
        assertTrue(foundPrintFunction, "Program should contain a 'print' statement.");

        //Ensure a closing brace is found
        boolean foundClosingBrace = tokens.stream()
                .anyMatch(token -> token.getTokenValue() == Token.TokenType.RCBRACE);
        assertTrue(foundClosingBrace, "Program should contain a closing brace '}'.");

        //Prints all tokens
        System.out.println("Tokens from LexerFullProgramTest:");
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

}
