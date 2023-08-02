import java.rmi.*;

public class CalculatorClient {
    public static void main(String[] args) {
        try {
            Calculator stub = (Calculator)Naming.lookup("rmi://localhost:8000/testing");
            stub.pushValue(20);
            stub.pushValue(40);
            stub.pushValue(30);
            stub.pushValue(10);
            stub.pushOperation("max");
            System.out.println(stub.see());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
