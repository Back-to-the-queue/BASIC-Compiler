package BASIC_Test.Parser;
import BASIC.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional tests for the {@code Parser}
 * Author: Nicolas Quesada {nquesada@albany.edu}
 */
public class ParserTest {

    private String readTestFile(String filePath) throws Exception {
        return Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
    }

    @BeforeEach
    public void setUp() {
        //Clears the token list before each test to prevent contamination between tests
        Lexer.tokenList.clear();
    }

    @Test
    public void TestGosubReturn() throws Exception {
        Lexer lexer = new Lexer("GOSUB subroutine\nPRINT X\nsubroutine: RETURN");
        lexer.lex();
        java.util.LinkedList<Token> tokens = Lexer.tokenList;
        Parser parser = new Parser(tokens);
        StatementsNode statements = parser.parse();
        List<Optional<StatementNode>> stateList = statements.getStatements();
        assertEquals(3, stateList.size());
        assertTrue(stateList.get(0).isPresent());
        assertInstanceOf(GoSubNode.class, stateList.get(0).get());
        assertTrue(stateList.get(2).isPresent());
        assertInstanceOf(LabeledStatementNode.class, stateList.get(2).get());
    }

    @Test
    public void TestDataRead() throws Exception {
        Lexer lexer = new Lexer("DATA 10, 20, 30\nREAD A, B, C");
        lexer.lex();
        java.util.LinkedList<Token> tokens = Lexer.tokenList;
        Parser parser = new Parser(tokens);
        StatementsNode statements = parser.parse();
        List<Optional<StatementNode>> stateList = statements.getStatements();
        assertEquals(2, stateList.size());
        assertTrue(stateList.get(0).isPresent());
        assertInstanceOf(DataNode.class, stateList.get(0).get());
        assertTrue(stateList.get(1).isPresent());
        assertInstanceOf(ReadNode.class, stateList.get(1).get());
    }


    @Test
    public void TestPrint() throws Exception {
        Lexer lexer = new Lexer("PRINT \"Hello, World!\"");
        lexer.lex();
        java.util.LinkedList<Token> tokens = Lexer.tokenList;
        Parser parser = new Parser(tokens);
        StatementsNode statements = parser.parse();
        List<Optional<StatementNode>> stateList = statements.getStatements();

        assertEquals(1, stateList.size());
        assertTrue(stateList.get(0).isPresent());
        assertInstanceOf(PrintNode.class, stateList.get(0).get());
    }

    @Test
    public void testFullProgram() throws Exception {
        //Read the BASIC program from file
        String testInput = readTestFile("/Users/nicolasquesada/Documents/BASICpt2/src/BASIC_Test/Parser/ParserFullProgramTest");
        Lexer lexer = new Lexer(testInput);
        lexer.lex();
        LinkedList<Token> tokens = lexer.tokenList;

        Parser parser = new Parser(tokens);
        StatementsNode statements = parser.parse();
        List<Optional<StatementNode>> stateList = statements.getStatements();

        // Ensure the parsing was successful
        assertNotNull(stateList);
        assertFalse(stateList.isEmpty());

        // Expected number of parsed statements (excluding REM comments)
        assertEquals(15, stateList.size());

        // Check first statement: Variable Assignment (F = 100)
        assertTrue(stateList.get(0).isPresent());
        assertTrue(stateList.get(0).get() instanceof AssignmentNode);

        // Check subroutine call: GOSUB FtoC
        assertTrue(stateList.get(1).isPresent());
        assertTrue(stateList.get(1).get() instanceof GoSubNode);

        // Check PRINT statement: PRINT "Temperature in Celsius:", C
        assertTrue(stateList.get(2).isPresent());
        assertTrue(stateList.get(2).get() instanceof PrintNode);

        // Check IF statement: IF C < 40 THEN printCold
        assertTrue(stateList.get(3).isPresent());
        assertTrue(stateList.get(3).get() instanceof IfNode);

        // Check FOR loop: FOR X = 1 TO 5
        assertTrue(stateList.get(4).isPresent());
        assertTrue(stateList.get(4).get() instanceof ForNode);

        // Check PRINT inside FOR loop: PRINT X
        assertTrue(stateList.get(5).isPresent());
        assertTrue(stateList.get(5).get() instanceof PrintNode);

        // Check WHILE loop: WHILE Y < 3 endWhileLabel
        assertTrue(stateList.get(7).isPresent());
        assertTrue(stateList.get(7).get() instanceof WhileNode);

        // Check PRINT inside WHILE loop: PRINT "Y is", Y
        assertTrue(stateList.get(8).isPresent());
        assertTrue(stateList.get(8).get() instanceof PrintNode);

        // Check variable increment: Y = Y + 1
        assertTrue(stateList.get(9).isPresent());
        assertTrue(stateList.get(9).get() instanceof AssignmentNode);

        // Check PRINT statement with built-in functions
        assertTrue(stateList.get(10).isPresent());
        assertTrue(stateList.get(10).get() instanceof PrintNode);

        // Check RANDOM function usage: PRINT RANDOM()
        assertTrue(stateList.get(11).isPresent());
        assertTrue(stateList.get(11).get() instanceof PrintNode);

        // Check DATA statement: DATA 42, "Hello BASIC"
        assertTrue(stateList.get(12).isPresent());
        assertTrue(stateList.get(12).get() instanceof DataNode);

        // Check READ statement: READ num, greeting$
        assertTrue(stateList.get(13).isPresent());
        assertTrue(stateList.get(13).get() instanceof ReadNode);

        // Check END statement
        assertTrue(stateList.get(14).isPresent());
        assertTrue(stateList.get(14).get() instanceof EndNode);

        // Ensure the subroutine label FtoC exists in the parsed statements
        assertTrue(stateList.stream().anyMatch(opt -> opt.isPresent() && opt.get() instanceof LabeledStatementNode));
    }
}

/*
F = 100
GOSUB FtoC
PRINT "Temperature in Celsius:", C

IF C < 40 THEN printCold

PRINT "Counting from 1 to 5:"
FOR X = 1 TO 5
    PRINT X
NEXT X

WHILE Y < 3 endWhileLabel
    PRINT "Y is", Y
    Y = Y + 1
endWhileLabel:

name$ = "BASIC Compiler"
PRINT "Left 5 chars:", LEFT$(name$, 5)
PRINT "Right 4 chars:", RIGHT$(name$, 4)
PRINT "Middle part:", MID$(name$, 2, 3)

PRINT "Random number:", RANDOM()

DATA 42, "Hello BASIC"
READ num, greeting$
PRINT "Read from DATA:", num, greeting$

INPUT "Enter your name and age:", userName$, userAge
PRINT "Hi", userName$, "you are", userAge, "years old!"

FtoC:
    C = 5 * (F - 32) / 9
    RETURN

printCold:
    PRINT "It's cold!"
    END

 */