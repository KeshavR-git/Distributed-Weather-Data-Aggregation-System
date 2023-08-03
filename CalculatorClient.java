import java.rmi.*;

public class CalculatorClient {
    public static void main(String[] args) {
        try {
            // We lookup the remote object with the name specified in RMI registry
            // We also cast it Calculator interface to obtain a reference to remote object
            Calculator stub = (Calculator)Naming.lookup("rmi://localhost:8000/testing");
            // store the number in CL argument in a variable called clientNumber
            int clientNumber = Integer.parseInt(args[0]);
            // Client 1
            if (clientNumber == 1) {
                stub.pushValue(5);
                stub.pushValue(6);
                stub.pushOperation("max");
            }
            // Client 2
            else if (clientNumber == 2) {
                stub.pushValue(5);
                stub.pop();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
