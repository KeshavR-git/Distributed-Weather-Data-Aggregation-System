import java.rmi.*;

public class CalculatorClient3 {
    public static void main(String[] args) {
        try {
            Calculator stub = (Calculator)Naming.lookup("rmi://localhost:8000/testing");
            stub.pushValue(1000);
            stub.pushValue(3000);
            stub.pushValue(4000);
            stub.pushValue(2000);
            stub.pushOperation("gcd");
            System.out.println(stub.see());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

