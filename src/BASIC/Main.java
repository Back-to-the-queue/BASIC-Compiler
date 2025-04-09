package BASIC;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

/**
 * @author Nicolas Quesada (nquesada@albany.edu) 01/17/2024
 * {@code Main} runs the compiler
 */
public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) System.out.println("Error: incorrect number of arguments");
        Path myPath = Paths.get(args[0]); //Sets file path to the selected file
        String document = new String(Files.readAllBytes(myPath));
        LinkedList<Token> tokenList = new LinkedList<>(); //Creates an ArrayList containing the list of tokens
        LinkedList<Token> token2; //Creates a second Arraylist for the tokens

        var lexer = new Lexer(document);
        try {
            lexer.lex();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        token2 = Lexer.tokenList;
        for (Token token : token2) {
            tokenList.add(token);
        }
        for (int i = 0; i < tokenList.size(); i++) {
            System.out.print(tokenList.get(i));
        }
    }
}