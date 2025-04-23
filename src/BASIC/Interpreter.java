package BASIC;
import java.util.*;

//Replace queue with stack and work on flow control
//pushing onto the stack and then popping off
//focus on if and for repeating statements

/**
 * A {@code Interpreter} that handles the AST of Nodes created by the parser
 * @author Nicolas Quesada (nquesada@albany.edu)
 */
public class Interpreter {
    public static Random randomNum = new Random();
    public List<Node> dataStatements = new ArrayList<>();
    public HashMap<String, LabeledStatementNode> labels = new HashMap<>();
    public HashMap<String, String> stringVars = new HashMap<>();
    public HashMap<String, Integer> intVars = new HashMap<>();
    public HashMap<String, Float> floatVars = new HashMap<>();
    Optional<StatementNode> currentStatement;
    Optional<StatementNode> skippedStatement;
    Queue<Optional<StatementNode>> queue = new LinkedList<>();
    private boolean loop = true;
    private boolean isTrue = false;
    private int forStart = 0;
    private String forVar = null;
    private Optional<StatementNode> nextNode = Optional.empty();

    public Interpreter(StatementsNode statements) {
        dataSearch(statements);
        labelSearch(statements);
        if (!statements.getStatements().isEmpty()) {
            Optional<StatementNode> first = statements.getStatements().get(0);
            first.get().buildList(statements);
        }

    }

    /**
     *Evaluates the statement nodes
     * @param node statement node to be evaluated
     */
    public void interpret(Optional<StatementNode> node){
        currentStatement = node;
        do {
            if(currentStatement.get().getNext() != null){
                nextNode = Objects.requireNonNull(currentStatement).flatMap(StatementNode::getNext);
            } else break;
            currentStatement.ifPresent(state -> {
                if (state instanceof ReadNode readNode) {
                    if(dataStatements.isEmpty()) {
                        try {
                            throw new Exception("No data available for READ operation.");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    List<VariableNode> vars = readNode.getVars();
                    int i = -1;
                    var data = dataStatements.get(0);
                    var nodes = ((DataNode) data).getData();
                    for (VariableNode varNode : vars) {
                        String varName = varNode.getName();
                        i++;
                        var currNode = nodes.get(i);
                            if (currNode instanceof StringNode stringNode) {
                                if (varName.contains("$")){
                                    stringVars.put(varName, stringNode.getMember());
                                } else try {
                                    throw new Exception("Mismatched types in DATA and READ");
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            } else if (currNode instanceof FloatNode floatNode) {
                                if (varName.contains("%")) {
                                    floatVars.put(varName, floatNode.getNumber());
                                } else try {
                                    throw new Exception("Mismatched types in DATA and READ");
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            } else if (currNode instanceof IntegerNode integerNode){
                                intVars.put(varName, integerNode.getNumber());
                            }
                    }
                    dataStatements.remove(0);
                    //currentStatement = Optional.ofNullable(queue.poll());
                }else if (state instanceof AssignmentNode assignmentNode) {
                    var assignType = assignmentNode.getExpression();
                    var variable = assignmentNode.getVariable();
                    var name = ((VariableNode) variable).getName();
                    if (assignType instanceof IntegerNode integerNode) {
                        int num = integerNode.getNumber();
                        intVars.put(name, num);
                    }else if (assignType instanceof FloatNode floatNode) {
                        float num = floatNode.getNumber();
                        floatVars.put(name, num);
                    }else if (assignType instanceof StringNode stringNode) {
                        String val = stringNode.getMember();
                        stringVars.put(name, val);
                    }else if (assignType instanceof MathOpNode mathOp) {
                        if(mathOp.getLeft() instanceof FloatNode ||
                                mathOp.getRight().orElseThrow() instanceof FloatNode) {
                            float exp = evaluateFloat(assignType);
                            floatVars.put(name, exp);
                        }else if(mathOp.getLeft() instanceof IntegerNode ||
                                mathOp.getRight().orElseThrow() instanceof IntegerNode) {
                            int exp = evaluateInteger(assignType);
                            intVars.put(name, exp);
                        }
                    }
                    //currentStatement = Optional.ofNullable(queue.poll());
                } else if (state instanceof InputNode inputNode) {
                    List<Node> nodeList = inputNode.getInputList();
                    List<String> toBePrinted = new ArrayList<>();
                    if (nodeList.get(0) instanceof StringNode) {
                        toBePrinted.add(String.valueOf(nodeList.get(0)));
                        nodeList.remove(0);
                    }
                    System.out.println(toBePrinted);
                    toBePrinted.clear();
                    for (Node value : nodeList) {
                        if (value instanceof VariableNode variableNode) {
                            String varName = variableNode.getName();
                            Scanner scanner = new Scanner(System.in);
                            System.out.print("Insert value for: " + varName + " ");
                            var input = scanner.nextLine();
                            if (varName.contains("$")) stringVars.put(varName, input);
                            else if (varName.contains("%")) floatVars.put(varName, Float.parseFloat(input));
                            else intVars.put(varName, Integer.parseInt(input));
                        }
                    }
                    //currentStatement = Optional.ofNullable(queue.poll());
                } else if (state instanceof PrintNode printNode) {
                    List<Node> nodeList = printNode.getPrintList();
                    List<String> toBePrinted = new ArrayList<>();
                    for (var nodes : nodeList) {
                        if (nodes instanceof StringNode stringNode) {
                            toBePrinted.add(stringNode.getMember());
                        } else if (nodes instanceof MathOpNode mathNode) {
                            if (mathNode.getLeft() instanceof IntegerNode) {
                                int num = evaluateInteger(mathNode);
                                toBePrinted.add(String.valueOf(num));
                            } else if (mathNode.getLeft() instanceof FloatNode) {
                                float num = evaluateFloat(mathNode);
                                toBePrinted.add(String.valueOf(num));
                            }
                        } else if (nodes instanceof FunctionNode functionNode) {
                            var params = functionNode.getParams();
                            switch (functionNode.getName()) {
                                case "LEFT$":
                                    toBePrinted.add(left(String.valueOf(params.get(0)), Integer.parseInt(String.valueOf(params.get(1)))));
                                    break;
                                case "MID$":
                                    toBePrinted.add(mid(String.valueOf(params.get(0)), Integer.parseInt(String.valueOf(params.get(1))), Integer.parseInt(String.valueOf(params.get(2)))));
                                    break;
                                case "RIGHT$":
                                    toBePrinted.add(right(String.valueOf(params.get(0)), Integer.parseInt(String.valueOf(params.get(1)))));
                                    break;
                                case "NUM$":
                                    if (params.get(0) instanceof FloatNode floatNode)
                                        toBePrinted.add(numFloat(floatNode.getNumber()));
                                    else toBePrinted.add(numInt(Integer.parseInt(String.valueOf(params.get(0)))));
                                    break;
                                case "VAL":
                                    toBePrinted.add(String.valueOf(valInt(String.valueOf(params.get(0)))));
                                    break;
                                case "VAL%":
                                    toBePrinted.add(String.valueOf(valFloat(String.valueOf(params.get(0)))));
                                    break;
                                default:
                                    throw new RuntimeException("Unknown function: " + functionNode.getName());
                            }
                        } else if (nodes instanceof VariableNode variableNode) {
                            var variable = lookupVariable(variableNode.getName());
                            toBePrinted.add(variable);
                            if (variable == null) {
                                throw new RuntimeException("Unknown variable: " + nodes);
                            }
                        }
                    }
                    System.out.println(toBePrinted);
                    //currentStatement = Optional.ofNullable(queue.poll());
                } else if (state instanceof IfNode ifNode) {
                    var expression = ifNode.getBool();
                    expression.ifPresent(exp -> {
                        isTrue = false;
                        if (exp instanceof BooleanExpression booleanExpression) {
                            var left = booleanExpression.getLeftExpression();
                            var right = booleanExpression.getRightExpression();
                            var cond = booleanExpression.getCondition();
                            var evaluatedRight = 0.0;
                            var evaluatedLeft = 0.0;
                            if (right instanceof IntegerNode ||
                                    left instanceof IntegerNode) {
                                evaluatedRight = evaluateInteger(right);
                                evaluatedLeft = evaluateInteger(left);
                            }else if (right instanceof FloatNode ||
                                    left instanceof FloatNode) {
                                evaluatedLeft = evaluateFloat(right);
                                evaluatedRight = evaluateFloat(left);
                            } switch (cond) {
                                case LESSEQUAL:
                                    isTrue = evaluatedLeft <= evaluatedRight;
                                    break;
                                case GREATEREQUAL:
                                    isTrue = evaluatedLeft >= evaluatedRight;
                                    break;
                                case EQUALS:
                                    isTrue = evaluatedLeft == evaluatedRight;
                                    break;
                                case NOTEQUAL:
                                    isTrue = evaluatedLeft != evaluatedRight;
                                    break;
                                case LESSTHAN:
                                    isTrue = evaluatedLeft < evaluatedRight;
                                    break;
                                case GREATERTHAN:
                                    isTrue = evaluatedLeft > evaluatedRight;
                                    break;
                            }if (isTrue){
                                String label = ifNode.getEndLabel();
                                skippedStatement = Optional.ofNullable(labels.get(label));
                            }
                        }
                    });
                    currentStatement = skippedStatement;
                } else if (state instanceof GoSubNode) {
                    intVars = null;
                } else if (state instanceof ForNode forNode) {
                    int increment = evaluateInteger(forNode.getIncrement());
                    var variable = forNode.getVariable();
                    variable.ifPresent(i -> {
                        if(i instanceof AssignmentNode assignmentNode) {
                            forStart = evaluateInteger(assignmentNode.getExpression());
                            var preForVar = assignmentNode.getVariable();
                            forVar = ((VariableNode) preForVar).getName();
                            intVars.put(String.valueOf(forVar), forStart);
                        }
                    });
                    int end = evaluateInteger(forNode.getEnd());
                    var statements = forNode.getStatements();
                    while(forStart <= end) {
                        for (Optional<StatementNode> statement : statements) {
                            queue.offer(statement);
                            currentStatement.get().setNext(statement);
                        }
                        forStart += increment;
                        intVars.put(forVar, forStart);
                    }
                } else if (state instanceof WhileNode whileNode) {

                } else if (state instanceof NextNode) {
                    nextNode = queue.poll();
                } else if (state instanceof LabeledStatementNode) {
                    queue.offer(Optional.of(state));
                } else if (state instanceof ReturnNode) {
                    nextNode = queue.poll();
                } else if (state instanceof EndNode) {
                    loop = false;
                }
            });
            if(!currentStatement.equals(skippedStatement)){
                currentStatement = nextNode;
            }
        } while(loop);
    }

    /**
     *Evaluates Integer value
     * @return evaluated integer
     */
    public int evaluateInteger(Node node){
        if(node instanceof VariableNode variableNode){
            String variable = lookupVariable(variableNode.getName());
            if (variable != null) {
                return Integer.parseInt(variable);
            }
        } else if(node instanceof IntegerNode integerNode){
            return integerNode.getNumber();
        } else if(node instanceof MathOpNode mathOpNode){
            var left = evaluateInteger(mathOpNode.getLeft());
            var op = mathOpNode.getOperationType();
            var right = evaluateInteger(mathOpNode.getRight().orElseThrow());
            return switch (op) {
                case SUBTRACT -> left - right;
                case ADD -> left + right;
                case DIVIDE -> left / right;
                case MULTIPLY -> left * right;
            };
        } else if(node instanceof FunctionNode functionNode){
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
     */
    public float evaluateFloat(Node node){
        if(node instanceof VariableNode variableNode){
            return Float.parseFloat(Objects.requireNonNull(lookupVariable(variableNode.getName())));
        } else if(node instanceof FloatNode floatNode){
            return floatNode.getNumber();
        } else if(node instanceof MathOpNode mathOpNode){
            var left = evaluateFloat(mathOpNode.getLeft());
            var op = mathOpNode.getOperationType();
            var right = evaluateFloat(mathOpNode.getRight().orElseThrow());
            return switch (op) {
                case SUBTRACT -> left - right;
                case ADD -> left + right;
                case DIVIDE -> left / right;
                case MULTIPLY -> left * right;
            };
        } else if(node instanceof FunctionNode functionNode){
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
     * @param node the original node that is being passed through
     */
    public void dataSearch(StatementsNode node){
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
     * @param node the original node that is being passed through
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
