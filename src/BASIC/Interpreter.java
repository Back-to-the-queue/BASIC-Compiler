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
    private StatementNode currentStatement;
    private Stack<StatementNode> statements = new Stack<>();

    /**
     * Visitor method to set the next member for each statement in a StatementsNode.
     * @param node The StatementsNode to visit
     */
    public void setNextStatements(StatementsNode node) {
        List<Optional<StatementNode>> statements = node.getStatements();
        StatementNode previousStatement = null;
        for (Optional<StatementNode> statement : statements) {
            if (statement.isPresent()) {
                StatementNode currentStatement = statement.get();
                if (previousStatement != null) {
                    previousStatement.setNext(currentStatement);
                }
                previousStatement = currentStatement;
                if (currentStatement instanceof StatementsNode) {
                    setNextStatements((StatementsNode) currentStatement);
                }
            }
        }
    }

    /**
     *Evaluates the statement nodes
     * @param node statement node to be evaluated
     * @throws Exception
     */
    public void interpret(StatementNode node)throws Exception{
        while(loop ==true) {
            var next = currentStatement.next;
            StatementsNode statementsNode = (StatementsNode) node;
            interpret(currentStatement);
            if (node instanceof ReadNode) {
                ReadNode readNode = (ReadNode) node;
                List<VariableNode> vars = readNode.getVars();
                for (VariableNode varNode : vars) {
                    String varName = varNode.getName();
                    if (dataStatements.isEmpty()) {
                        throw new Exception("No data available for READ operation.");
                    }
                    Node dataValue = dataStatements.remove(0);
                }
            }else if (node instanceof AssignmentNode) {
                AssignmentNode assignmentNode = (AssignmentNode) node;
                if (assignmentNode.getExpression() instanceof IntegerNode) {
                    intVars.put(String.valueOf(assignmentNode.getTarget()), Integer.parseInt(String.valueOf(assignmentNode.getExpression())));
                }
                if (assignmentNode.getExpression() instanceof FloatNode) {
                    floatVars.put(String.valueOf(assignmentNode.getTarget()), Float.parseFloat(String.valueOf(assignmentNode.getExpression())));
                }
                if (assignmentNode.getExpression() instanceof StringNode) {
                    stringVars.put(String.valueOf(assignmentNode.getTarget()), String.valueOf(assignmentNode.getExpression()));
                }
                if (assignmentNode.getExpression() instanceof MathOpNode) {
                    int num = evaluateInteger(assignmentNode);
                    intVars.put(String.valueOf(assignmentNode.getTarget()), num);
                }
            }else if (node instanceof InputNode) {
                InputNode inputNode = (InputNode) node;
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
            }else if(node instanceof PrintNode) {
                PrintNode printNode = (PrintNode) node;
                List<Node> nodeList = new ArrayList<>();
                List<String> toBePrinted = new ArrayList<>();
                for (var nodes : nodeList) {
                    if (nodes instanceof StringNode) {
                        toBePrinted.add(String.valueOf(nodes));
                    }
                    if (nodes instanceof MathOpNode) {
                        MathOpNode mathNode = (MathOpNode) nodes;
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
            }else if(node instanceof IfNode){
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
                if (left instanceof MathOpNode) {
                    MathOpNode mathOpNode = (MathOpNode) left;
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
                if (isTrue == true) {
                    IfNode ifNode = (IfNode) node;
                    String str = ifNode.getEndLabel();
                    var label = labels.get(str);
                    labels.put(str, (LabeledStatementNode) currentStatement);
                }
            }else if(node instanceof GoSubNode) {

            }else if(node instanceof ForNode) {
                ForNode forNode = (ForNode) node;
                int increment = 1;
                //int start = Integer.parseInt(String.valueOf(forNode.getStart()));
                int end = Integer.parseInt(String.valueOf(forNode.getEnd()));
                var isIncrement = forNode.getIncrement();
                //if (isIncrement.isPresent()) increment = Integer.parseInt(String.valueOf(increment));
                var exists = intVars.get(forNode.getVariable());
                if (exists == null) {
                    //intVars.put(String.valueOf(forNode.getVariable()), start);
                }
                /*if (start > end) {
                    do {
                        currentStatement = currentStatement.next;
                    } while (!(node instanceof NextNode));
                } else statements.push(forNode);
                start += increment;
            } else if(node instanceof NextNode) {
                StatementNode returnStatement = statements.pop();
                returnStatement = currentStatement.next;
            }*/ else if(node instanceof ReturnNode) {
                StatementNode returnStatement = statements.pop();
                returnStatement = currentStatement.next;
            } else if(node instanceof EndNode) {
                loop = false;
            }
            currentStatement = currentStatement.next;
        }
    }}

    /**
     *Evaluates Integer value
     * @return evaluated integer
     * @throws Exception
     */
    public int evaluateInteger(Node node) throws Exception{
        if(node instanceof VariableNode){
            VariableNode variableNode = (VariableNode) node;
            String variable = lookupVariable(variableNode.getName());
            return Integer.parseInt(variable);
        } if(node instanceof IntegerNode){
            IntegerNode integerNode = (IntegerNode) node;
            int num = integerNode.getNumber();
            return num;
        } if(node instanceof MathOpNode){
            MathOpNode mathOpNode = (MathOpNode) node;
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
        } if(node instanceof FunctionNode){
            FunctionNode functionNode = (FunctionNode) node;
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
    public float evaluateFloat(Node node) throws Exception{
        if(node instanceof VariableNode){
            VariableNode variableNode = (VariableNode) node;
            return Float.parseFloat(lookupVariable(variableNode.getName()));
        }if(node instanceof FloatNode){
            FloatNode floatNode = (FloatNode) node;
            float num = floatNode.getNumber();
            return num;
        }if(node instanceof MathOpNode){
            MathOpNode mathOpNode = (MathOpNode) node;
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
        } if(node instanceof FunctionNode){
            FunctionNode functionNode = (FunctionNode) node;
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
    private void dataSearch(ProgramNode node){
        //List<Node> nodes = node.getProgramNodes();
        /*for(var data : nodes) {
            if(data instanceof DataNode) {
                DataNode dataNode = (DataNode) data;
                dataStatements.addAll(dataNode.getData());
            }
        }*/
    }

    /**
     *
     * @param node
     */
    private void labelSearch(StatementsNode node){
        List<Optional<StatementNode>> statements = node.getStatements();
        for(var statement : statements){
            statement.ifPresent(state -> {
                if(state instanceof LabeledStatementNode){
                    LabeledStatementNode label = (LabeledStatementNode) state;
                    labels.put(label.getLabel(), label);
                }
            });
        }
    }

    /**
     * returns the leftmost N characters from the string
     * @param data the string to be manipulated
     * @param characters the amount 'N' of characters
     * @return
     */
    public static String left(String data, int characters){
        String str = data.substring(0,characters);
        return str;
    }

    /**
     * returns the rightmost N characters from the string
     * @param data the string to be manipulated
     * @param characters the amount 'N' of characters
     * @return
     */
    public static String right(String data, int characters){
        String str = data.substring(data.length() - characters);
        return str;
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
        String str = data.substring(arg1,count);
        return str;
    }

    /**
     * converts a float to a string
     * @param num
     * @return the manipulated String
     */
    public static String numFloat(float num){return String.valueOf(num);}

    /**
     * converts an int to a string
     * @param num
     * @return the manipulated String
     */
    public static String numInt(int num){return String.valueOf(num);}

    /**
     * converts a string to an integer
     * @param str the integer to be converted
     * @return the String of str
     */
    public static int valInt(String str){return Integer.parseInt(str);}

    /**
     * converts a string to a float
     * @param str the float to be converted
     * @return the String of str
     */
    public static float valFloat(String str){return Float.parseFloat(str);}
}
