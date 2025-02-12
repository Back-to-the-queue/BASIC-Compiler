package BASIC_Test;
import BASIC.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LexerTest {
    @Test
    public void TestLine1() throws Exception {
        Lexer lexer = new Lexer("Queso:");
        lexer.lex();
        java.util.LinkedList<BASIC.Token> tokens = lexer.tokenList;
        assertEquals(Token.TokenType.PLUS, tokens.get(0));
        assertEquals(Token.TokenType.MINUS, tokens.get(1));
        assertEquals(Token.TokenType.FUNCTION, tokens.get(3));
    }
}