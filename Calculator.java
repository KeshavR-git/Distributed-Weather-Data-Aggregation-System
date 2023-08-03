import java.rmi.*;

// Define a remote interface named Calculator that extends the java.rmi.Remote interface
// All the methods can be invoked remotely
public interface Calculator extends Remote {
    // Define a method named gcd that takes two int arguments and returns an int result, it is a helper function
    public int gcd(int a, int b) throws RemoteException;
    // Define a method named pushValue that takes an int argument and pushes the value on the stack
    public void pushValue(int val) throws RemoteException;
    // Define a method named pushOperation that takes a string argument and does calculation based on that, pushing result onto stack
    public void pushOperation(String operator) throws RemoteException; 
    // pops value from stack
    public int pop() throws RemoteException;
    // checks if stack is empty
    public boolean isEmpty() throws RemoteException;
    // pops value from stack with specified delay in milliseconds
    public int delayPop(int millis) throws RemoteException; 
}