package BASIC_Test.Interpreter;
import BASIC.*;
import org.junit.jupiter.api.*;
import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class InterpreterTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        Lexer.tokenList.clear();
    }

    @Test
    public void testIntegerAssignmentAndPrint() throws Exception {
        Lexer lexer = new Lexer("X = 42 \n PRINT X \n END");
        lexer.lex();
        java.util.LinkedList<Token> tokens = Lexer.tokenList;
        Parser parser = new Parser(tokens);
        StatementsNode statements = parser.parse();
        Interpreter interpreter = new Interpreter(statements);
        interpreter.interpret(statements.getCurrentStatement());

        assertEquals(42, interpreter.intVars.get("X"));
        assertTrue(outputStream.toString().contains("42"));
    }

    @Test
    public void testMathOpAssignment() throws Exception {
        Lexer lexer = new Lexer("F = 10 \n C = 5 * (F - 32) / 9 \n END");
        lexer.lex();
        java.util.LinkedList<Token> tokens = Lexer.tokenList;
        Parser parser = new Parser(tokens);
        StatementsNode statements = parser.parse();
        Interpreter interpreter = new Interpreter(statements);
        interpreter.interpret(statements.getCurrentStatement());

        assertEquals(-12, interpreter.intVars.get("C"));
    }

    @Test
    public void testFloatAssignment() throws Exception {
        Lexer lexer = new Lexer("F% = 3.14 \n END");
        lexer.lex();
        java.util.LinkedList<Token> tokens = Lexer.tokenList;
        Parser parser = new Parser(tokens);
        StatementsNode statements = parser.parse();
        Interpreter interpreter = new Interpreter(statements);
        interpreter.interpret(statements.getCurrentStatement());

        assertEquals(3.14f, interpreter.floatVars.get("F%"));
    }

    @Test
    public void testStringAssignmentAndPrint() throws Exception {
        Lexer lexer = new Lexer("S$ = \"Hello\" \n PRINT S$ \n END");
        lexer.lex();
        java.util.LinkedList<Token> tokens = Lexer.tokenList;
        Parser parser = new Parser(tokens);
        StatementsNode statements = parser.parse();
        Interpreter interpreter = new Interpreter(statements);
        interpreter.interpret(statements.getCurrentStatement());

        assertEquals("Hello", interpreter.stringVars.get("S$"));
        assertTrue(outputStream.toString().contains("Hello"));
    }

    @Test
    public void testReadNodeFromData() throws Exception {
        Lexer lexer = new Lexer("DATA 99, \"WORLD\" \n READ A, B$ \n END");
        lexer.lex();
        java.util.LinkedList<Token> tokens = Lexer.tokenList;
        Parser parser = new Parser(tokens);
        StatementsNode statements = parser.parse();
        Interpreter interpreter = new Interpreter(statements);
        interpreter.interpret(statements.getCurrentStatement());
        assertEquals(99, interpreter.intVars.get("A"));
        assertEquals("WORLD", interpreter.stringVars.get("B$"));
    }

    @Test
    public void testIfNodeConditionTrue() throws Exception {
        Lexer lexer = new Lexer("""
        C = 28
        IF C < 40 THEN printCold
        Z = 63
        printCold:
        PRINT "Its cold"
        RETURN
        END
        """);
        lexer.lex();
        LinkedList<Token> tokens = Lexer.tokenList;
        Parser parser = new Parser(tokens);
        StatementsNode statements = parser.parse();
        Interpreter interpreter = new Interpreter(statements);
        interpreter.interpret(statements.getCurrentStatement());

        String output = outputStream.toString().trim();
        assertTrue(output.contains("Its cold"), "Expected output to contain 'Its cold'");
    }

    @Test
    public void testForLoop() throws Exception {
        Lexer lexer = new Lexer("""
        FOR I = 1 TO 5
            PRINT I
        NEXT I
        END
    """);
        lexer.lex();
        LinkedList<Token> tokens = Lexer.tokenList;
        Parser parser = new Parser(tokens);
        StatementsNode statements = parser.parse();
        Interpreter interpreter = new Interpreter(statements);
        interpreter.interpret(statements.getCurrentStatement());

        String output = outputStream.toString().trim();
        for (int i = 1; i <= 5; i++) {
            assertTrue(output.contains(String.valueOf(i)), "Expected output to contain " + i);
        }
        assertEquals(5, interpreter.intVars.get("I")); // Final value of loop variable
    }



    @Test
    public void testFunctionRandom() {
        int result = Interpreter.random();
        assertTrue(result >= 0 && result <= 1000);
    }

    @Test
    public void testFunctionLeftRightMid() {
        assertEquals("Hel", Interpreter.left("Hello", 3));
        assertEquals("llo", Interpreter.right("Hello", 3));
        assertEquals("ell", Interpreter.mid("Hello", 1, 3));
    }

    @Test
    public void testValNumConversion() {
        assertEquals("123", Interpreter.numInt(123));
        assertEquals("3.14", Interpreter.numFloat(3.14f));
        assertEquals(123, Interpreter.valInt("123"));
        assertEquals(3.14f, Interpreter.valFloat("3.14"));
    }
}
