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
        System.out.println(stateList);
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
        String testInput = readTestFile("/Users/nicolasquesada/Documents/BASICpt2/src/BASIC_Test/Parser/ParserFullProgramTest");
        Lexer lexer = new Lexer(testInput);
        lexer.lex();
        LinkedList<Token> tokens = Lexer.tokenList;

        Parser parser = new Parser(tokens);
        StatementsNode statements = parser.parse();
        List<Optional<StatementNode>> stateList = statements.getStatements();

        // Basic assertions to verify successful parsing
        assertNotNull(stateList);
        assertFalse(stateList.isEmpty());

        // Check that the first statement is a PRINT statement (example assertion)
        assertTrue(stateList.get(0).isPresent());
        assertTrue(stateList.get(0).get() instanceof PrintNode);
    }
}
