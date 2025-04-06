package BASIC;
import java.util.LinkedList;
import java.util.Optional;

/**
 * A {@code TokenManager} holds methods that help manage the token stream
 * @author Nicolas Quesada (nquesada@albany.edu)
 */
public class TokenManager {
    private LinkedList<Token> tokens = new LinkedList<>(); //

    /**
     *
     * @param token
     */
    public TokenManager(LinkedList<Token> token){
        this.tokens = token;
        //System.out.println(tokens);
    }

    /**
     *
     * @return
     */
    public boolean moreTokens(){
        return !tokens.isEmpty();
    }

    /**
     *
     * @param tokenNum
     * @return
     */
    public Optional<Token.TokenType> peek(int tokenNum){
        if (tokenNum < tokens.size()) {
            Token.TokenType token = tokens.get(tokenNum).getTokenValue();
            return Optional.ofNullable(token);
        }
        return Optional.empty();
    }

    /**
     *
     * @param t
     * @return
     */
    public Optional<Token.TokenType> matchAndRemove(Token.TokenType t){
        if ((!tokens.isEmpty()) && (tokens.get(0).getTokenValue().equals(t))) {
            tokens.remove(0);
            //System.out.println(tokens);
            return Optional.of(t);
        }
        return Optional.empty();
    }
}
