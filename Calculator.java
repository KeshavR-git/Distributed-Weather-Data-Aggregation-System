import java.rmi.*;

public interface Calculator extends Remote {
    public int gcd(int a, int b) throws RemoteException;
    public void pushValue(int val) throws RemoteException;
    public void pushOperation(String operator) throws RemoteException; 
    public int pop() throws RemoteException;
    public boolean isEmpty() throws RemoteException;
    public int delayPop(int millis) throws RemoteException; 
    public int see() throws RemoteException;
}