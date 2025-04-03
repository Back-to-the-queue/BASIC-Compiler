package BASIC;
import java.util.*;

/**
 * A {@code Parser} that translates the lexemes using syntactic analysis and recursive descent
 * @author Nicolas Quesada (nquesada@albany.edu)
 */
public class Parser {
    LinkedList<Token> token; //The list of tokens to be parsed through
    private final String[] expVal = {"LESSTHAN", "GREATERTHAN", "LESSEQUAL", "GREATEREQUAL", "EQUAL", "NOTEQUAL"}; //List of possible conditionals
    private final String[] functions = {"MID", "RANDOM", "LEFT", "RIGHT", "NUM", "VAL"};
    TokenManager tokenM; //Member of @{code TokenManager} that controls the stream of tokens

    /**
     * Adds all the tokens into the new LinkedList
     *
     * @param tokens Gets list of parsed tokens
     */
    public Parser(LinkedList<Token> tokens) {
        this.token = tokens;
        this.tokenM = new TokenManager(token);
    }

    /**
     * Parses through all the StatementNode's
     *
     * @return what is returned from the statements() function call
     */
    public StatementsNode parse() throws Exception {
        if (!tokenM.moreTokens()) {
            throw new IllegalStateException("No tokens to parse");
        }
        return (StatementsNode) statements();
    }

    /**
     * Expression uses term() and matchAndRemove to parse through any possible expressions
     *
     * @return a partial answer if parsing is incomplete
     * @throws Exception
     */
    private Node expression() throws Exception {
        var left = term();
        while (tokenM.moreTokens()) {
            Optional<Token.TokenType> op = tokenM.peek(0);
            if (op.isEmpty()) break;

            Token.TokenType eToken = op.get();
            switch (eToken) {
                case PLUS:
                case MINUS:
                    tokenM.matchAndRemove(eToken);
                    var right = term();
                    left = new MathOpNode(
                            eToken == Token.TokenType.PLUS ? MathOpNode.OperationType.ADD : MathOpNode.OperationType.SUBTRACT,
                            left,
                            Optional.ofNullable(right)
                    );
                    break;
                default:
                    return left;
            }
        }
        return left;
    }

    /**
     * Uses factor() and matchAndRemove
     *
     * @return a partial answer if parsing is incomplete
     * @throws Exception
     */
    private Node term() throws Exception {
        Node left = factor();
        while (tokenM.moreTokens()) {
            Optional<Token.TokenType> op = tokenM.peek(0);
            if (op.isEmpty()) break;
            Token.TokenType tToken = op.get();
            switch (tToken) {
                case TIMES:
                case DIVIDE:
                    tokenM.matchAndRemove(tToken);
                    var right = factor();
                    left = new MathOpNode(
                            tToken == Token.TokenType.TIMES ? MathOpNode.OperationType.MULTIPLY : MathOpNode.OperationType.DIVIDE,
                            left,
                            Optional.of(right)
                    );
                    break;
                default:
                    return left;
            }
        }
        return left;
    }

    /**
     * @return a partial answer if parsing is incomplete
     * @throws IllegalStateException
     */
    private Node factor() throws Exception {
        Token.TokenType fToken = tokenM.peek(0).orElseThrow();
        if (fToken == Token.TokenType.WORD) {
            return new VariableNode(token.get(0).getValue());
        }
        if (!tokenM.moreTokens()) throw new IllegalStateException("Less tokens than expected in factor");
        Optional<Token.TokenType> op = tokenM.peek(0);
        if (op.isEmpty()) throw new IllegalStateException("Unexpected token in factor");
        if (fToken == Token.TokenType.NUMBER) {
            if (token.get(0).getValue().contains(".")) {
                var floats = Float.parseFloat(token.get(0).getValue());
                tokenM.matchAndRemove(Token.TokenType.NUMBER);
                return new FloatNode(floats);
            } else {
                var integer = Integer.parseInt(token.get(0).getValue());
                tokenM.matchAndRemove(Token.TokenType.NUMBER);
                return new IntegerNode(integer);
            }
        }
        throw new IllegalStateException("Unexpected token type in factor");
    }

    /**
     * Parses through the statements
     *
     * @return the {@code Node} created by the helper methods or null
     */
    private Optional<StatementNode> statement() throws Exception {
        Optional<StatementNode> state = Optional.empty();
        acceptSeparators();
        if (token.get(0).getTokenValue().equals(Token.TokenType.LABEL)) {
            String label = token.get(0).getValue();
            tokenM.matchAndRemove(Token.TokenType.LABEL);
            state = statement();
            if (state.isPresent()) return Optional.of(new LabeledStatementNode(label, state));
        }
        if (!tokenM.moreTokens()) return Optional.empty();
        Token.TokenType sToken = token.get(0).getTokenValue();
        switch (sToken) {
            case PRINT:
                state = printStatement();
                break;
            case WORD:
                if(tokenM.peek(1).isPresent() && tokenM.peek(1).equals(Optional.of(Token.TokenType.EQUAL))){
                    state = assignment();
                }
                break;
            case READ:
                state = readStatement();
                break;
            case DATA:
                state = dataStatement();
                break;
            case INPUT:
                state = inputStatement();
                break;
            case RETURN:
                state = returnStatement();
                tokenM.matchAndRemove(Token.TokenType.RETURN);
                break;
            case END:
                state = endStatement();
                break;
            case FOR:
                state = forStatement();
                break;
            case IF:
                state = ifStatement();
                break;
            case WHILE:
                state = whileStatement();
                break;
            case GOSUB:
                state = goSubStatement();
                break;
            default:
                throw new IllegalStateException("Unexpected statement type in statement");
        }
        return state;
    }

    /**
     * Adds the Statements to the list in StatementsNode
     *
     * @return
     */
    public StatementNode statements() throws Exception {
        List<Optional<StatementNode>> statementsList = new ArrayList<>();
        while (tokenM.moreTokens()){
            Optional<Token.TokenType> op = tokenM.peek(0);
            if (op.isEmpty()) break;
            Optional<StatementNode> statement = statement();
            statementsList.add(statement);
        }
        System.out.println(statementsList);
        return new StatementsNode(statementsList);
    }

    /**
     * Accepts a statement and returns if it is a print statement
     *
     * @return a PrintNode
     * @throws Exception
     */
    private Optional<StatementNode> printStatement() throws Exception {
        if (tokenM.matchAndRemove(Token.TokenType.PRINT).equals(Optional.of(Token.TokenType.PRINT))) {
            List<Node> toPrint = printList();
            return Optional.of(new PrintNode(toPrint));
        } else return Optional.empty();
    }

    /**
     * Accepts a list of expressions to be printed
     */
    private List<Node> printList() throws Exception {
        List<Node> printList = new ArrayList<>();
        while (true) {
            Optional<Token.TokenType> pToken = tokenM.peek(0);
            if (pToken.isEmpty()) {
                break;
            }
            Token.TokenType print = pToken.get();
            Node item;
            if (print == Token.TokenType.STRINGLITERAL) {
                item = new StringNode(token.get(0).getValue());
                tokenM.matchAndRemove(Token.TokenType.STRINGLITERAL);
            } else {
                item = expression();
            }
            printList.add(item);
            if (tokenM.peek(0).isPresent() && tokenM.peek(0).get() != Token.TokenType.COMMA) {
                break;
            }
            tokenM.matchAndRemove(Token.TokenType.COMMA);
        }
        return printList;
    }

    /**
     * Parses through the assignment
     *
     * @return
     * @throws Exception
     */
    private Optional<StatementNode> assignment() throws Exception {
        var left = factor();
        var op = tokenM.matchAndRemove(Token.TokenType.EQUAL);
        if (op.isPresent()) {
            var right = expression();
            return Optional.of(new AssignmentNode(left, right));
        } else {
            return Optional.empty();
        }
    }

    /**
     * @return
     * @throws Exception
     */
    private Optional<StatementNode> readStatement() throws Exception {
        List<VariableNode> readList = new ArrayList<>();
        String varName;
        if (tokenM.matchAndRemove(Token.TokenType.READ).equals(Optional.of(Token.TokenType.READ))) {
            do {
                if (token.get(0).getTokenValue().equals(Token.TokenType.WORD)) {
                    varName = token.get(0).getValue();
                    tokenM.matchAndRemove(Token.TokenType.WORD);
                    readList.add(new VariableNode(varName));
                } else throw new Exception("Variable Not Found");
            } while (tokenM.matchAndRemove(Token.TokenType.COMMA).equals(Optional.of(Token.TokenType.COMMA)) && tokenM.moreTokens());
        } else return Optional.empty();
        return Optional.of(new ReadNode(readList));
    }

    /**
     * @return
     */
    private Optional<StatementNode> inputStatement() {
        List<Node> inputList = new ArrayList<>();
        String str;
        String varName;
        if (tokenM.matchAndRemove(Token.TokenType.INPUT).equals(Optional.of(Token.TokenType.INPUT))) {
            do {
                if (tokenM.matchAndRemove(Token.TokenType.STRINGLITERAL).equals(Optional.of(Token.TokenType.STRINGLITERAL))) {
                    str = token.get(0).getValue();
                    inputList.add(new StringNode(str));
                } else if (tokenM.matchAndRemove(Token.TokenType.WORD).equals(Optional.of(Token.TokenType.WORD))) {
                    varName = token.get(0).getValue();
                    inputList.add(new VariableNode(varName));
                }
            } while (tokenM.matchAndRemove(Token.TokenType.COMMA).equals(Optional.of(Token.TokenType.COMMA)));
        } else return Optional.empty();
        return Optional.of(new InputNode(inputList));
    }

    /**
     * @return
     * @throws Exception
     */
    private Optional<StatementNode> dataStatement() throws Exception {
        List<Node> dataList = new ArrayList<>();
        String varName;
        int integer;
        float floats;
        if (tokenM.matchAndRemove(Token.TokenType.DATA).equals(Optional.of(Token.TokenType.DATA))) {
            do {
                if (token.get(0).getTokenValue().equals(Token.TokenType.WORD)) {
                    varName = token.get(0).getValue();
                    tokenM.matchAndRemove(Token.TokenType.WORD);
                    dataList.add(new VariableNode(varName));
                } else if (token.get(0).getTokenValue().equals(Token.TokenType.NUMBER)) {
                    if (token.get(0).getValue().contains(".")) {
                        floats = Float.parseFloat(token.get(0).getValue());
                        tokenM.matchAndRemove(Token.TokenType.NUMBER);
                        dataList.add(new FloatNode(floats));
                    } else {
                        integer = Integer.parseInt(token.get(0).getValue());
                        tokenM.matchAndRemove(Token.TokenType.NUMBER);
                        dataList.add(new IntegerNode(integer));
                    }
                } else throw new Exception("Variable Not Found");
            } while (tokenM.matchAndRemove(Token.TokenType.COMMA).equals(Optional.of(Token.TokenType.COMMA)));
        } else return Optional.empty();
        return Optional.of(new DataNode(dataList));
    }

    /**
     * Looks for return statement
     *
     * @return {@code ReturnNode}
     */
    private Optional<StatementNode> returnStatement() {
        if (token.get(0).getTokenValue().equals(Token.TokenType.RETURN))
            return Optional.of(new ReturnNode());
        else return Optional.empty();
    }

    /**
     * Looks for end statement
     *
     * @return {@code EndNode}
     */
    private Optional<StatementNode> endStatement() {
        if (tokenM.matchAndRemove(Token.TokenType.END).equals(Optional.of(Token.TokenType.END)))
            return Optional.of(new EndNode());
        else return Optional.empty();
    }

    /**
     * Parses through the for statement
     *
     * @return {@code ForNode}
     * @throws Exception if not implemented correctly
     */
    private Optional<StatementNode> forStatement() throws Exception {
        Optional<StatementNode> variable;
        int end;
        int increment = 1;
        boolean step = false;
        if (tokenM.matchAndRemove(Token.TokenType.FOR).equals(Optional.of(Token.TokenType.FOR))) {
            variable = assignment();
            if(tokenM.matchAndRemove(Token.TokenType.TO).equals(Optional.of(Token.TokenType.TO))) {
                end = Integer.parseInt(token.get(0).getValue());
                tokenM.matchAndRemove(Token.TokenType.NUMBER);
                if (tokenM.matchAndRemove(Token.TokenType.STEP).equals(Optional.of(Token.TokenType.STEP))) {
                    step = true;
                    increment = Integer.parseInt(token.get(0).getValue());
                    tokenM.matchAndRemove(Token.TokenType.NUMBER);
                }
            }else throw new Exception("End range not found");
        }else throw new Exception("Start range not found");
        Optional<StatementNode> state = statement();
        if(tokenM.matchAndRemove(Token.TokenType.NEXT).equals(Optional.of(Token.TokenType.NEXT))){
                Node last = expression();
                tokenM.matchAndRemove(Token.TokenType.WORD);
                return Optional.of(new ForNode(variable,new IntegerNode(end), new IntegerNode(increment), state));
        }
        return Optional.empty();
    }

    /**
     * Parses through the gosub statement
     *
     * @return {@code GoSubNode}
     * @throws Exception if not implemented correctly
     */
    private Optional<StatementNode> goSubStatement() throws Exception {
        String str;
        if (tokenM.matchAndRemove(Token.TokenType.GOSUB).equals(Optional.of(Token.TokenType.GOSUB))) {
            if (token.get(0).getTokenValue().equals(Token.TokenType.WORD)) {
                str = token.get(0).getValue();
                tokenM.matchAndRemove(Token.TokenType.WORD);
            } else throw new Exception("Invalid Input");
        } else return Optional.empty();
        return Optional.of(new GoSubNode(str));
    }

    /**
     * Parses through the if statement
     *
     * @return {@code IfNode}
     * @throws Exception if not implemented correctly
     */
    private Optional<StatementNode> ifStatement() throws Exception {
        StatementsNode state = new StatementsNode();
        LabeledStatementNode isLabel = new LabeledStatementNode();
        if (tokenM.matchAndRemove(Token.TokenType.IF).equals(Optional.of(Token.TokenType.IF))) {
            Optional<StatementNode> condition = parseBoolean();
            if (condition.isPresent()) {
                if (!(tokenM.matchAndRemove(Token.TokenType.THEN).equals(Optional.of(Token.TokenType.THEN))))
                    throw new Exception("Expected THEN block");
                String label = token.get(0).getValue();
                List<Optional<StatementNode>> labelCheck = state.getStatements();
                for (Optional<StatementNode> statementNode : labelCheck) {
                    if(statementNode.equals(isLabel.getStatement())) {
                        if(String.valueOf(statementNode).contains(label)){
                            return Optional.of(new IfNode(condition, label));
                        }else throw new Exception("Label does not exist");
                    }else throw new Exception("Condition not found");
                }
            } else throw new Exception("Boolean does not exist");
        } else throw new Exception("If block not found");
        return Optional.empty();
    }

    /**
     * Parses through the while statement
     *
     * @return {@code WhileNode}
     * @throws Exception if not implemented correctly
     */
    private Optional<StatementNode> whileStatement() throws Exception {
        String endLabel = "";
        String currState = "";
        if (tokenM.matchAndRemove(Token.TokenType.WHILE).equals(Optional.of(Token.TokenType.WHILE))) {
            StatementsNode state = new StatementsNode();
            Optional<StatementNode> condition = parseBoolean();
            StatementNode label;
            if (condition.isPresent()) {
                if (tokenM.peek(0).equals(Optional.of(Token.TokenType.WORD))) {
                    endLabel = token.get(0).getValue();
                    tokenM.matchAndRemove(Token.TokenType.WORD);
                }
                Optional<StatementNode> loopState = statement();
                if (tokenM.peek(0).equals(Optional.of(Token.TokenType.LABEL))) {
                    label = statements();
                    if(String.valueOf(label).contains(endLabel)){
                        return Optional.of(new WhileNode(condition, loopState, Optional.ofNullable(label)));
                    } else throw new Exception("Label does not exist");
                } else throw new Exception("Condition not found");
            }
        }
        return Optional.empty();
    }

    /**
     * Parses through a boolean statement
     *
     * @return {@code BooleanExpression}
     * @throws Exception if expression is not implemented correctly
     */
    private Optional<StatementNode> parseBoolean() throws Exception {
        Node left = expression();
        Token.TokenType operand = null;
        boolean foundExp = false;
        for (var i : expVal) { //Loop through our possible boolean comparison symbols to see if any matches
            if (token.get(0).toString().equals(i)) {
                operand = token.get(0).getTokenValue();
                foundExp = true;
            }
        }
        if (!foundExp)
            throw new Exception("Invalid operator to compare expressions");
        tokenM.matchAndRemove(operand);
        Node right = expression();
        return Optional.of(new BooleanExpression(left, right, operand));
    }

    /**
     * Returns a {@code Node} for the built-in functions
     *
     * @return {@code FunctionNode}
     * @throws Exception if there is no closing parenthesis
     */
    private Optional<FunctionNode> functionInvocation() throws Exception {
        String function = null;
        String paramName;
        ArrayList<VariableNode> params = new ArrayList<>();
        for (var i : functions) {
            if (token.get(0).toString().equals(i)) {
                function = String.valueOf(token.get(0).toString().equals(i));
            }
        }
        tokenM.matchAndRemove(Token.TokenType.WORD);
        if (tokenM.matchAndRemove(Token.TokenType.LPAREN).equals(Optional.of(Token.TokenType.LPAREN))) {
            do {
                if (tokenM.matchAndRemove(Token.TokenType.WORD).equals(Optional.of(Token.TokenType.WORD))) {
                    paramName = token.get(0).getValue();
                    params.add(new VariableNode(paramName));
                } else throw new Exception("Variable Not Found");
            } while (tokenM.matchAndRemove(Token.TokenType.COMMA).equals(Optional.of(Token.TokenType.COMMA)));
        }
        if (tokenM.matchAndRemove(Token.TokenType.RPAREN).equals(Optional.of(Token.TokenType.RPAREN))) {
            return Optional.of(new FunctionNode(function, params));
        } else throw new Exception("Closing parenthesis not found");
    }

    /**
     * Accepts separators and ignores them without disturbing the parsing
     */
    public void acceptSeparators() {
        if (token.get(0).getTokenValue().equals(Token.TokenType.SEPARATOR)) {
            tokenM.matchAndRemove(Token.TokenType.SEPARATOR);
        }
    }
}