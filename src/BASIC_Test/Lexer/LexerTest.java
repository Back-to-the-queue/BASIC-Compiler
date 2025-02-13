package BASIC_Test.Lexer;
import BASIC.*;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LexerTest {
    private String readTestFile(String filePath) throws Exception {
        System.out.println("Looking for file at: " + Path.of(filePath).toAbsolutePath());
        return Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
    }

    @Test
    public void TestProcessWord() throws Exception {
        String testInput = readTestFile("/Users/nicolasquesada/Documents/BASICpt2/src/BASIC_Test/Lexer/ProcessWordTest");
        Lexer lexer = new Lexer(testInput);
        lexer.lex();
        java.util.LinkedList<BASIC.Token> tokens = lexer.tokenList;


    }

    public void TestProcessNumber() throws Exception {
        String testInput = readTestFile("/Users/nicolasquesada/Documents/BASICpt2/src/BASIC_Test/Lexer/ProcessNumberTest");
        Lexer lexer = new Lexer(testInput);
        lexer.lex();
        java.util.LinkedList<BASIC.Token> tokens = lexer.tokenList;

    }

    public void TestProcessSymbol() throws Exception {
        String testInput = readTestFile("/Users/nicolasquesada/Documents/BASICpt2/src/BASIC_Test/Lexer/ProcessSymbolTest");
        Lexer lexer = new Lexer(testInput);
        lexer.lex();
        java.util.LinkedList<BASIC.Token> tokens = lexer.tokenList;


    }
    public void TestHandleStringLiteral() throws Exception {
        String testInput = readTestFile("/Users/nicolasquesada/Documents/BASICpt2/src/BASIC_Test/Lexer/ProcessStringLiteralTest");
        Lexer lexer = new Lexer(testInput);
        lexer.lex();
        java.util.LinkedList<BASIC.Token> tokens = lexer.tokenList;

    }
}