import java.rmi.*;

public class CalculatorClient2 {
    public static void main(String[] args) {
        try {
            Calculator stub = (Calculator)Naming.lookup("rmi://localhost:8000/testing");
            stub.pushValue(100);
            stub.pushValue(200);
            stub.pushValue(300);
            stub.pushValue(400);
            stub.pushOperation("min");
            System.out.println(stub.see());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

