import java.rmi.*;
import java.util.EmptyStackException;

public class CalculatorTest {
    private Calculator stub;

    // The setUp method is used to set up the test environment before each test is run.
    // In this method, the remote Calculator object is looked up in the RMI registry and a reference to it is stored in the stub variable.
    public void setUp() throws Exception {
        stub = (Calculator)Naming.lookup("rmi://localhost:8000/testing");
    }
    // The testPushValueAndPop method tests the behavior of the pushValue and pop methods in the Calculator interface.
    // The method first ensures that the stack is empty by popping all values from it.
    // Then, it pushes a value onto the stack and checks if the value returned by the pop method is equal to the value that was pushed.
    public void testPushValueAndPop() throws Exception {
        while (!stub.isEmpty()) {
            stub.pop();
        }
        stub.pushValue(5);
        if (5 == stub.pop()) {
            System.out.println("testPushValueAndPop passed");
        } else {
            System.out.println("testPushValueAndPop failed");
        }
    }
    // The testPushOperationMax, testPushOperationMin, testPushOperationGcd, and testPushOperationLcm methods test the behavior of the pushOperation
    // method in the Calculator interface when different operations are pushed onto the stack.
    // These methods first ensure that the stack is empty, then push values onto the stack and push an operation onto the stack.
    // Finally, they check if the value returned by the pop method is equal to the expected result of the operation.

    public void testPushOperationMax() throws Exception {
        while (!stub.isEmpty()) {
            stub.pop();
        }
        stub.pushValue(5);
        stub.pushValue(6);
        stub.pushOperation("max");
        if (6 == stub.pop()) {
            System.out.println("testPushOperationMax passed");
        } else {
            System.out.println("testPushOperationMax failed");
        }
    }
    
    public void testPushOperationMin() throws Exception {
        while (!stub.isEmpty()) {
            stub.pop();
        }
        stub.pushValue(5);
        stub.pushValue(6);
        stub.pushOperation("min");
        if (5 == stub.pop()) {
            System.out.println("testPushOperationMin passed");
        } else {
            System.out.println("testPushOperationMin failed");
        }
    }
    
    public void testPushOperationGcd() throws Exception {
        while (!stub.isEmpty()) {
            stub.pop();
        }
        stub.pushValue(10);
        stub.pushValue(15);
        stub.pushOperation("gcd");
        if (5 == stub.pop()) {
            System.out.println("testPushOperationGcd passed");
        }
        else {
            System.out.println("testPushOperationGcd failed");
        }
    }
    
    public void testPushOperationLcm() throws Exception {
        while (!stub.isEmpty()) {
            stub.pop();
        }
        stub.pushValue(10);
        stub.pushValue(15);
        stub.pushOperation("lcm");
        if (30 == stub.pop()) {
            System.out.println("testPushOperationLcm passed");
        } else {
            System.out.println("testPushOperationLcm failed");
        }
    }
    // The testIsEmpty method tests the behavior of the isEmpty method in the Calculator interface.
    // The method first ensures that the stack is empty, then checks if the isEmpty method returns true.
    // Then, it pushes a value onto the stack and checks if the isEmpty method returns false.
    public void testIsEmpty() throws Exception {
        while (!stub.isEmpty()) {
            stub.pop();
        }
        if (true == stub.isEmpty()) {
            System.out.println("testIsEmpty passed (1/2)");
        } else {
            System.out.println("testIsEmpty failed (1/2)");
        }
        
        stub.pushValue(5);
        
        if (false == stub.isEmpty()) {
            System.out.println("testIsEmpty passed (2/2)");
        } else {
            System.out.println("testIsEmpty failed (2/2)");
        }
    }
    // The testDelayPop method tests the behavior of the delayPop method in the Calculator interface.
    // The method first ensures that the stack is empty, then calls the delayPop method with a delay of 1000 milliseconds.
    // It checks if at least 1000 milliseconds have passed between calling the method and receiving its result.
    public void testDelayPop() throws Exception {
        while (!stub.isEmpty()) {
            stub.pop();
        }
        long startTime = System.currentTimeMillis();
        int result = stub.delayPop(1000);
        long endTime = System.currentTimeMillis();
        
        if (endTime - startTime >= 1000) {
            System.out.println("testDelayPop passed");
        } else {
            System.out.println("testDelayPop failed");
        }
    }
    // The testPushOperationWhenStackIsEmpty method tests the behavior of the pushOperation method in the Calculator
    // interface when an operation is pushed onto an empty stack.
    // The method first ensures that the stack is empty, then pushes an operation onto the stack and checks if the stack is still empty.
    public void testPushOperationWhenStackIsEmpty() throws Exception {
        while (!stub.isEmpty()) {
            stub.pop();
        }
        stub.pushOperation("max");
        
        if (true == stub.isEmpty()) {
            System.out.println("testPushOperationWhenStackIsEmpty passed");
        } else {
            System.out.println("testPushOperationWhenStackIsEmpty failed");
        }
    }
    // The testPushOperationWhenStackHasOneElement method tests the behavior of the pushOperation method in the Calculator interface when an operation is pushed onto a stack that has one element.
    // The method first ensures that the stack is empty, then pushes a value onto the stack, pushes an operation onto the stack, and checks if the value returned by the pop method is equal to the value that was pushed.
    public void testPushOperationWhenStackHasOneElement() throws Exception {
        while (!stub.isEmpty()) {
            stub.pop();
        }
        stub.pushValue(5);
        stub.pushOperation("max");
        if (5 == stub.pop()) {
            System.out.println("testPushOperationWhenStackHasOneElement passed");
        } else {
            System.out.println("testPushOperationWhenStackHasOneElement failed");
        }
    }
    // The testPopWhenStackIsEmpty method tests how popping a value from an empty stack affects its contents.
    // The method first ensures that the stack is empty, then tries to pop a value from it and checks if an EmptyStackException is thrown.
    public void testPopWhenStackIsEmpty() throws Exception {
        while (!stub.isEmpty()) {
            stub.pop();
        }
        try {
            stub.pop();
            System.out.println("testPopWhenStackIsEmpty failed");
        } catch (EmptyStackException e) {
            System.out.println("testPopWhenStackIsEmpty passed");
        }
    }
    // The testDelayPopWithNegativeDelay and testDelayPopWithLargeDelay methods test the behavior of the delayPop method in the Calculator interface when called with negative or large delays.
    // These methods first ensure that the stack is empty, then call the delayPop method with a negative or large delay and check if at least or less than the specified delay has passed between calling the method and receiving its result.

    public void testDelayPopWithNegativeDelay() throws Exception {
        while (!stub.isEmpty()) {
            stub.pop();
        }
        long startTime = System.currentTimeMillis();
        int result = stub.delayPop(-1000);
        long endTime = System.currentTimeMillis();
        
        if (endTime - startTime < 1000) {
            System.out.println("testDelayPopWithNegativeDelay passed");
        } else {
            System.out.println("testDelayPopWithNegativeDelay failed");
        }
    }
    
    public void testDelayPopWithLargeDelay() throws Exception {
        while (!stub.isEmpty()) {
            stub.pop();
        }
        long startTime = System.currentTimeMillis();
        int result = stub.delayPop(Integer.MAX_VALUE);
        long endTime = System.currentTimeMillis();
        
        if (endTime - startTime < Integer.MAX_VALUE) {
            System.out.println("testDelayPopWithLargeDelay passed");
        } else {
            System.out.println("testDelayPopWithLargeDelay failed");
        }
    }
    // The main method creates an instance of the CalculatorTest class, calls its setUp method to set up the test environment,
    //  and then calls several test methods to run tests on the Calculator interface.
    public static void main(String[] args) throws Exception {
        CalculatorTest test = new CalculatorTest();
        test.setUp();
        test.testPushValueAndPop();
        test.testPushOperationMax();
        test.testPushOperationMin();
        test.testPushOperationGcd();
        test.testPushOperationLcm();
        test.testIsEmpty();
        test.testDelayPop();
    }
}