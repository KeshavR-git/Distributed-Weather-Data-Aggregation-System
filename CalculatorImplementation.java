import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

// we have made all the functions synchronised which makes it thread-safe
public class CalculatorImplementation extends UnicastRemoteObject implements Calculator {
    Stack<Integer>st;
    // we use an array list to push all the values of the stack when they are popped, makes it easier to do the operations
    ArrayList<Integer>arr;
    CalculatorImplementation() throws RemoteException {
        // call the superclass constructor and initialising the stack and ArrayList to be used
        super();
        st = new Stack<>();
        arr = new ArrayList<>();
    }
    // helper function that calculates gcd
    public synchronized int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }
    // pushes value on top of stack
    public synchronized void pushValue(int val) {
        st.push(val);
    }
    public synchronized void pushOperation(String operator) {
        // pops the element of stack until it's empty, concurrently pushing each popped element onto the ArrayList
        while (!st.isEmpty()) {
            arr.add(st.pop());
        }
        if (operator.equals("min")) {
            st.push(Collections.min(arr));
        }
        if (operator.equals("max")) {
            st.push(Collections.max(arr));
        }
        // Euclidean algorithm to calculate LCM
        if (operator.equals("lcm")) {
            int lcm = 1;
            for (int i = 0; i < arr.size(); i++) {
                lcm = (lcm * arr.get(i)) / gcd(lcm, arr.get(i));
            }
            st.push(lcm);
        }
        // We calculate the gcd of each consecutive element in the ArrayList using the helper function defined above
        if (operator.equals("gcd")) {
            int gcd = arr.get(0);
            for (int i = 1; i < arr.size(); i++) {
                gcd = gcd(gcd, arr.get(i));
            }
            st.push(gcd);
        }
        arr.clear();
    }
    // pops element from top of stack
    public synchronized int pop() {
        return st.pop();
    }
    // checks if stack is empty
    public synchronized boolean isEmpty() {
        return st.isEmpty();
    }
    // Thread.sleep() function pauses the execution for specified number of milliseconds which is the parameter
    public synchronized int delayPop(int millis) {
        try {
            Thread.sleep(millis);
            return st.pop();
        } catch (Exception e) {
            return 0;
        }
    }
}