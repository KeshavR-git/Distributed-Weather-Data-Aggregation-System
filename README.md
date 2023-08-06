Calculator Program
This is a Java-based calculator program that uses Remote Method Invocation (RMI) to communicate between a client and a server.

Files
Calculator.java: This is the interface for the calculator, which defines the methods that can be called remotely.
CalculatorServer.java: This is the server-side implementation of the calculator program. It creates an instance of the CalculatorImplementation class and binds it to the RMI registry.
CalculatorClient.java: This is the client-side implementation of the calculator program. It looks up the calculator object in the RMI registry and calls its methods. We test multiple clients in this file by taking parsing numbers in command line.
CalculatorImplementation.java: This is the implementation of the Calculator interface. It extends UnicastRemoteObject and implements the methods defined in the Calculator interface.
CalculatorTest.java: This is a test class for the Calculator interface. It contains test methods that use assertions to check if the expected behavior of the Calculator methods is correct.

Usage
Compile all the .java files using javac.
Start the RMI registry by running rmiregistry on one terminal.
Run the CalculatorServer class to start the server on another terminal.
Run the CalculatorClient class to start a client on another terminal. You can specify a client number as a command-line argument.
The client can push values and operations onto a stack, pop values from the stack, and check if the stack is empty. The server performs calculations based on the pushed operations and values.
You can also run the CalculatorTest class to run tests on the Calculator interface and check if it is working correctly. The tests also checks if the program would run if multiple clients make request to server.

Note: We do testing in both the CalculatorTest file which is an additional file just for testing, and also in the CalculatorClient file