import java.rmi.*;

public class CalculatorServer {
    public static void main(String[] args) {
        try {
            Calculator stub = new CalculatorImplementation();
            Naming.rebind("rmi://localhost:8000/testing", stub);

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
