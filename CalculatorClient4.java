import java.rmi.*;

public class CalculatorClient4 {
    public static void main(String[] args) {
        try {
            Calculator stub = (Calculator)Naming.lookup("rmi://localhost:8000/testing");
            stub.pushValue(10000);
            stub.pushValue(40000);
            stub.pushValue(30000);
            stub.pushValue(10000);
            stub.pushOperation("max");
            System.out.println(stub.see());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
