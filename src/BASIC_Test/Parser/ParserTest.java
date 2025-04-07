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
        for(int i = 0; i < stateList.size(); i++){
            System.out.println(i + ": " + stateList.get(i));
        }
        assertNotNull(stateList);
        assertFalse(stateList.isEmpty());
        assertEquals(16, stateList.size()); // Checks for correct size

        //Check statement 0: F = 100
        assertTrue(stateList.get(0).isPresent());
        assertTrue(stateList.get(0).get() instanceof AssignmentNode);

        //Check statement 1: GOSUB FtoC
        assertTrue(stateList.get(1).isPresent());
        assertTrue(stateList.get(1).get() instanceof GoSubNode);

        //Check statement 2: PRINT "Temperature in Celsius:", C
        assertTrue(stateList.get(2).isPresent());
        assertTrue(stateList.get(2).get() instanceof PrintNode);

        //Check statement 3: IF C < 40 THEN printCold
        assertTrue(stateList.get(3).isPresent());
        assertTrue(stateList.get(3).get() instanceof IfNode);

        //Check statement 4: PRINT "Counting from 1 to 5:"
        assertTrue(stateList.get(4).isPresent());
        assertTrue(stateList.get(4).get() instanceof PrintNode);

        //Check statement 5: FOR X = 1 TO 5
        assertTrue(stateList.get(5).isPresent());
        assertTrue(stateList.get(5).get() instanceof ForNode);

        //Check statement 6: WHILE Y < 3 endWhileLabel
        assertTrue(stateList.get(6).isPresent());
        assertTrue(stateList.get(6).get() instanceof WhileNode);

        //Check statement 7: name$ = "BASIC Compiler"
        assertTrue(stateList.get(7).isPresent());
        assertTrue(stateList.get(7).get() instanceof AssignmentNode);

        //Check statement 8: DATA 42, "Hello BASIC"
        assertTrue(stateList.get(8).isPresent());
        assertTrue(stateList.get(8).get() instanceof DataNode);

        //Check statement 9: READ num, greeting$
        assertTrue(stateList.get(9).isPresent());
        assertTrue(stateList.get(9).get() instanceof ReadNode);

        // Check statement 10: PRINT "Read from DATA:", num, greeting$
        assertTrue(stateList.get(10).isPresent());
        assertTrue(stateList.get(10).get() instanceof PrintNode);

        // Check statement 11: INPUT "Enter your name and age:", userName$, userAge
        assertTrue(stateList.get(11).isPresent());
        assertTrue(stateList.get(11).get() instanceof InputNode);

        //Check statement 12: PRINT "Hi", userName$, ...
        assertTrue(stateList.get(12).isPresent());
        assertTrue(stateList.get(12).get() instanceof PrintNode);

        //Check subroutine label: FtoC:
        assertTrue(stateList.stream().anyMatch(opt -> opt.isPresent() && opt.get() instanceof LabeledStatementNode &&
                ((LabeledStatementNode) opt.get()).getLabel().equals("FtoC")));

        //Check subroutine label: printCold:
        assertTrue(stateList.stream().anyMatch(opt -> opt.isPresent() && opt.get() instanceof LabeledStatementNode &&
                ((LabeledStatementNode) opt.get()).getLabel().equals("printCold")));

        //Check END is somewhere in the statements
        assertTrue(stateList.stream().anyMatch(opt -> opt.isPresent() && opt.get() instanceof EndNode));
    }
}