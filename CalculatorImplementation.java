import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class CalculatorImplementation extends UnicastRemoteObject implements Calculator {
    Stack<Integer>st;
    ArrayList<Integer>arr;
    CalculatorImplementation() throws RemoteException {
        super();
        st = new Stack<>();
        arr = new ArrayList<>();
    }
    public int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }
    public void pushValue(int val) {
        st.push(val);
    }
    public void pushOperation(String operator) {
        while (!st.isEmpty()) {
            arr.add(st.pop());
        }
        if (operator.equals("min")) {
            st.push(Collections.min(arr));
        }
        if (operator.equals("max")) {
            st.push(Collections.max(arr));
        }
        if (operator.equals("lcm")) {
            int lcm = 1;
            for (int i = 0; i < arr.size(); i++) {
                lcm = (lcm * arr.get(i)) / gcd(lcm, arr.get(i));
            }
            st.push(lcm);
        }
        if (operator.equals("gcd")) {
            int gcd = arr.get(0);
            for (int i = 1; i < arr.size(); i++) {
                gcd = gcd(gcd, arr.get(i));
            }
            st.push(gcd);
        }
    }
    public int pop() {
        return st.pop();
    }
    public boolean isEmpty() {
        return st.isEmpty();
    }
    public int see() {
        if (!st.isEmpty()) {
            return st.peek();
        }
        return 0;
    }
    public int delayPop(int millis) {
        try {
            Thread.sleep(millis);
            return st.pop();
        } catch (Exception e) {
            return 0;
        }
    }
}