package BASIC;

import java.util.*;

/**
 * A {@code Interpreter} that handles the AST of Nodes created by the parser
 * @author Nicolas Quesada (nquesada@albany.edu)
 */
public class Interpreter {
    public static Random randomNum = new Random();
    public List<Node> dataStatements = new LinkedList<>();
    public HashMap<String, LabeledStatementNode> labels = new HashMap<>();
    public HashMap<String, String> stringVars = new HashMap<>();
    public HashMap<String, Integer> intVars = new HashMap<>();
    public HashMap<String, Float> floatVars = new HashMap<>();
    private boolean loop = true;
    private Optional<StatementNode> currentStatement;
    List<Optional<StatementNode>> statements;

    Interpreter(StatementsNode statements) {
        this.statements = statements.getStatements();
        dataSearch(statements);
        labelSearch(statements);
    }

    /**
     *Evaluates the statement nodes
     * @param node statement node to be evaluated
     * @throws Exception If the program uses incorrect syntax
     */
    public void interpret(StatementNode node)throws Exception {
        currentStatement = Optional.ofNullable(node);
        while (loop) {
            currentStatement.ifPresent(state -> {
                if (state instanceof ReadNode readNode) {
                    List<VariableNode> vars = readNode.getVars();
                    for (VariableNode varNode : vars) {
                        String varName = varNode.getName();
                        var data = dataStatements.get(0);
                        if (data instanceof StringNode) {
                            if (varName.contains("$")) {
                                stringVars.put(varName, ((StringNode) data).getMember());
                                dataStatements.remove(0);
                            } else try {
                                throw new Exception("Mismatched types in DATA and READ");
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        } else if (data instanceof FloatNode) {
                            if (varName.contains("%")) {
                                floatVars.put(varName, ((FloatNode) data).getNumber());
                                dataStatements.remove(0);
                            } else try {
                                throw new Exception("Mismatched types in DATA and READ");
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            intVars.put(varName, ((IntegerNode) data).getNumber());
                            dataStatements.remove(0);
                        }
                    }
                    if (dataStatements.isEmpty()) {
                        try {
                            throw new Exception("No data available for READ operation.");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }else if (node instanceof AssignmentNode assignmentNode) {
                    var assignType = assignmentNode.getExpression();
                    var varName = assignmentNode.getTarget();
                    if (assignType instanceof IntegerNode) {
                        intVars.put(String.valueOf(varName), Integer.parseInt(String.valueOf(assignType)));
                    }if (assignType instanceof FloatNode) {
                        floatVars.put(String.valueOf(varName), Float.parseFloat(String.valueOf(assignType)));
                    }if (assignType instanceof StringNode) {
                        stringVars.put(String.valueOf(varName), String.valueOf(assignType));
                    }if (assignType instanceof MathOpNode) {
                        int exp = evaluateInteger(assignmentNode);
                        intVars.put(String.valueOf(varName), exp);
                    }
                } else if (node instanceof InputNode inputNode) {
                    List<Node> nodeList = inputNode.getInputList();
                    List<String> toBePrinted = new ArrayList<>();
                    if (nodeList.get(0) instanceof StringNode) {
                        toBePrinted.add(String.valueOf(nodeList.get(0)));
                        nodeList.remove(0);
                    }
                    for (int i = 0; i > nodeList.size(); i++) {
                        if (nodeList.get(i) instanceof VariableNode) {
                            VariableNode variableNode = (VariableNode) nodeList.get(1);
                            String varName = variableNode.getName();
                            Scanner scanner = new Scanner(System.in);
                            System.out.println("Insert value for :" + varName);
                            var input = scanner.nextLine();
                            stringVars.put(varName, input);
                        }
                    }
                } else if (node instanceof PrintNode printNode) {
                    List<Node> nodeList = new ArrayList<>();
                    List<String> toBePrinted = new ArrayList<>();
                    for (var nodes : nodeList) {
                        if (nodes instanceof StringNode) {
                            toBePrinted.add(String.valueOf(nodes));
                        }
                        if (nodes instanceof MathOpNode mathNode) {
                            if (mathNode.getLeft() instanceof IntegerNode) {
                                int num = evaluateInteger(mathNode);
                                toBePrinted.add(String.valueOf(num));
                            } else if (mathNode.getLeft() instanceof FloatNode) {
                                float num = evaluateFloat(mathNode);
                                toBePrinted.add(String.valueOf(num));
                            }
                        }
                    }
                    System.out.println(toBePrinted);
                } else if (node instanceof IfNode) {
                    BooleanExpression booleanExpression = (BooleanExpression) node;
                    var evaluatedLeft = 0.0;
                    var evaluatedRight = 0.0;
                    boolean isTrue = false;
                    var left = booleanExpression.getLeftExpression();
                    if (left instanceof IntegerNode) {
                        evaluatedLeft = evaluateInteger(left);
                        var right = booleanExpression.getRightExpression();
                        evaluatedRight = evaluateInteger(right);
                    }
                    if (left instanceof FloatNode) {
                        evaluatedLeft = evaluateFloat(left);
                        var right = booleanExpression.getRightExpression();
                        evaluatedRight = evaluateFloat(right);
                    }
                    if (left instanceof MathOpNode mathOpNode) {
                        var leftMath = mathOpNode.getLeft();
                        if (leftMath instanceof IntegerNode) {
                            evaluatedLeft = evaluateInteger(left);
                            var right = booleanExpression.getRightExpression();
                            evaluatedRight = evaluateInteger(right);
                        }
                        if (leftMath instanceof FloatNode) {
                            evaluatedLeft = evaluateFloat(left);
                            var right = booleanExpression.getRightExpression();
                            evaluatedRight = evaluateFloat(right);
                        }
                    }
                    if (left instanceof VariableNode) {
                        evaluatedLeft = Integer.parseInt(lookupVariable(String.valueOf(left)));
                        var right = booleanExpression.getRightExpression();
                        evaluatedRight = evaluateInteger(right);
                    }
                    var conditional = booleanExpression.getCondition();
                    switch (conditional) {
                        case LESSTHAN:
                            isTrue = evaluatedLeft < evaluatedRight;
                            break;
                        case GREATERTHAN:
                            isTrue = evaluatedLeft > evaluatedRight;
                            break;
                        case LESSEQUAL:
                            isTrue = evaluatedLeft <= evaluatedRight;
                            break;
                        case GREATEREQUAL:
                            isTrue = evaluatedLeft >= evaluatedRight;
                            break;
                        case EQUAL:
                            isTrue = evaluatedLeft == evaluatedRight;
                            break;
                        case NOTEQUAL:
                            isTrue = evaluatedLeft != evaluatedRight;
                            break;
                    }
                    if (isTrue) {
                        IfNode ifNode = (IfNode) node;
                        String str = ifNode.getEndLabel();
                        var label = labels.get(str);
                        //labels.put(str, (LabeledStatementNode) currentStatement);
                    }
                } else if (node instanceof GoSubNode) {

                } else if (node instanceof ForNode forNode) {
                    /*int increment = 1;
                    //int start = Integer.parseInt(String.valueOf(forNode.getStart()));
                    int end = Integer.parseInt(String.valueOf(forNode.getEnd()));
                    var isIncrement = forNode.getIncrement();
                    //if (isIncrement.isPresent()) increment = Integer.parseInt(String.valueOf(increment));
                    var exists = intVars.get(forNode.getVariable());
                    if (exists == null) {
                        //intVars.put(String.valueOf(forNode.getVariable()), start);
                    }
                    if (start > end) {
                        do {
                            currentStatement = currentStatement.next;
                        } while (!(node instanceof NextNode));
                    } else statements.push(forNode);
                    start += increment;
                } else if (node instanceof NextNode) {
                    StatementNode returnStatement = statements.pop();
                    returnStatement = currentStatement.next;
                } else if (node instanceof ReturnNode) {
                    Optional<StatementNode> returnStatement = statements.pop();
                    returnStatement = Optional.ofNullable(currentStatement.next);
                } else if (node instanceof EndNode) {
                    loop = false;
                */}
            });
            currentStatement = statements.get(1);
        }
    }

    /**
     *Evaluates Integer value
     * @return evaluated integer
     * @throws Exception
     */
    public int evaluateInteger(Node node){
        if(node instanceof VariableNode variableNode){
            String variable = lookupVariable(variableNode.getName());
            return Integer.parseInt(variable);
        } if(node instanceof IntegerNode integerNode){
            int num = integerNode.getNumber();
            return num;
        } if(node instanceof MathOpNode mathOpNode){
            var left = evaluateInteger(mathOpNode);
            var op = mathOpNode.getOperationType();
            var right = evaluateInteger(mathOpNode);
            switch (op){
                case SUBTRACT:
                    return left - right;
                case ADD:
                    return left + right;
                case DIVIDE:
                    return left / right;
                case MULTIPLY:
                    return left * right;
            }
        } if(node instanceof FunctionNode functionNode){
            String functionName = functionNode.getName();
            List<Node> params = functionNode.getParams();
            String str = String.valueOf(params.get(0));
            if(functionName.equals("RANDOM")) return random();
            if(functionName.equals(("VAL"))) return valInt(str);
        }
        return 0;
    }

    /**
     *Evaluates Float value
     * @return evaluated float value
     * @throws Exception
     */
    public float evaluateFloat(Node node){
        if(node instanceof VariableNode variableNode){
            return Float.parseFloat(Objects.requireNonNull(lookupVariable(variableNode.getName())));
        }if(node instanceof FloatNode floatNode){
            return floatNode.getNumber();
        }if(node instanceof MathOpNode mathOpNode){
            var left = evaluateFloat(mathOpNode);
            var op = mathOpNode.getOperationType();
            var right = evaluateFloat(mathOpNode);
            switch (op){
                case SUBTRACT:
                    return left - right;
                case ADD:
                    return left + right;
                case DIVIDE:
                    return left / right;
                case MULTIPLY:
                    return left * right;
            }
        } if(node instanceof FunctionNode functionNode){
            String functionName = functionNode.getName();
            List<Node> params = functionNode.getParams();
            String str = String.valueOf(params.get(0));
            if(functionName.equals(("VAL%"))) return valFloat(str);
        }
        return 0;
    }

    /**
     *Looks up which map the variable belongs to
     * @param str variable name
     * @return the value of the variable
     */
    private String lookupVariable(String str){
        if(stringVars.containsKey(str))
            return stringVars.get(str);
        if(intVars.containsKey(str))
            return String.valueOf(intVars.get(str));
        if(floatVars.containsKey(str))
            return String.valueOf(floatVars.get(str));
        return null;
    }


    /**
     *
     * @param node
     */
    private void dataSearch(StatementsNode node){
        List<Optional<StatementNode>> statements = node.getStatements();
        for(Optional<StatementNode> statement : statements){
            statement.ifPresent(state -> {
                if(state instanceof DataNode data){
                    dataStatements.add(data);
                }
            });
        }
    }

    /**
     *
     * @param node
     */
    private void labelSearch(StatementsNode node){
        List<Optional<StatementNode>> statements = node.getStatements();
        for(Optional<StatementNode> statement : statements){
            statement.ifPresent(state -> {
                if(state instanceof LabeledStatementNode label){
                    labels.put(label.getLabel(), label);
                }
            });
        }
    }

    /**
     * returns the leftmost N characters from the string
     * @param data the string to be manipulated
     * @param characters the amount 'N' of characters
     * @return the manipulated string
     */
    public static String left(String data, int characters){
        return data.substring(0,characters);
    }

    /**
     * returns the rightmost N characters from the string
     * @param data the string to be manipulated
     * @param characters the amount 'N' of characters
     * @return the manipulated string
     */
    public static String right(String data, int characters){
        return data.substring(data.length() - characters);
    }

    /**
     * returns a random integer
     * @return a random integer from 0-1000
     */
    public static int random(){return randomNum.nextInt(1000);}

    /**
     * returns the characters of the string, starting from the 2nd argument and taking the
     * 3rd argument as the count
     * @param data the string to be manipulated
     * @param arg1 the first bound
     * @param arg2 the second bound
     * @return the manipulated String
     */
    public static String mid(String data, int arg1, int arg2){
        int count = arg1 + arg2;
        return data.substring(arg1,count);
    }

    /**
     * converts a float to a string
     * @param num takes in the parameter to be converted
     * @return the manipulated String
     */
    public static String numFloat(float num){return String.valueOf(num);}

    /**
     * converts an int to a string
     * @param num takes in the parameter to be converted
     * @return the manipulated String
     */
    public static String numInt(int num){return String.valueOf(num);}

    /**
     * converts a string to an integer
     * @param str the integer to be converted
     * @return the integer value of str
     */
    public static int valInt(String str){return Integer.parseInt(str);}

    /**
     * converts a string to a float
     * @param str the float to be converted
     * @return the float value of str
     */
    public static float valFloat(String str){return Float.parseFloat(str);}
}
