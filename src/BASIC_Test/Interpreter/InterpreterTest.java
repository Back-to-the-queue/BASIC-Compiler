package BASIC_Test.Interpreter;
import BASIC.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterTest {

    private Interpreter interpreter;

    @BeforeEach
    void setUp() {
        List<Optional<StatementNode>> nodes = new ArrayList<>();
        StatementsNode statementsNode = new StatementsNode(nodes);
        interpreter = new Interpreter(statementsNode);
    }

    @Test
    void testIntegerAssignment() throws Exception {
        VariableNode var = new VariableNode("X");
        IntegerNode expr = new IntegerNode(42);
        AssignmentNode assign = new AssignmentNode(var, expr);
        interpreter.interpret(assign);

        assertEquals(42, interpreter.intVars.get("X"));
    }

    @Test
    void testFloatAssignment() throws Exception {
        VariableNode var = new VariableNode("Y%");
        FloatNode expr = new FloatNode(3.14f);
        AssignmentNode assign = new AssignmentNode(var, expr);
        interpreter.interpret(assign);

        assertEquals(3.14f, interpreter.floatVars.get("Y%"));
    }

    @Test
    void testStringAssignment() throws Exception {
        VariableNode var = new VariableNode("name$");
        StringNode expr = new StringNode("Hello");
        AssignmentNode assign = new AssignmentNode(var, expr);
        interpreter.interpret(assign);

        assertEquals("Hello", interpreter.stringVars.get("name$"));
    }

    @Test
    void testReadData() throws Exception {
        // Setup DATA node
        VariableNode var1 = new VariableNode("A");
        VariableNode var2 = new VariableNode("B$");
        ReadNode readNode = new ReadNode(List.of(var1, var2));

        // Add data to interpreter
        interpreter.dataStatements.add(new IntegerNode(123));
        interpreter.dataStatements.add(new StringNode("Hi"));

        interpreter.interpret(readNode);

        assertEquals(123, interpreter.intVars.get("A"));
        assertEquals("Hi", interpreter.stringVars.get("B$"));
    }

    @Test
    void testEndNodeStopsLoop() throws Exception {
        EndNode endNode = new EndNode();
        interpreter.loop = true;
        interpreter.interpret(endNode);
        assertFalse(interpreter.loop);
    }
}
