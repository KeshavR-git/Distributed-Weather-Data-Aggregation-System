import java.rmi.*;

public class CalculatorServer {
    public static void main(String[] args) {
        try {
            // Create a new instance of of the CalculatorImplementation class
            // This creates a remote object that implements Calculator interface
            Calculator skeleton = new CalculatorImplementation();
            // bind the remote object to specified name in the RMI registry
            // Clients will be able to look up this remote object by it's name in RMI registry and invoke it's methods
            Naming.rebind("rmi://localhost:8000/testing", skeleton);

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
