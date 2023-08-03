compile:
	javac *.java

registry:
	rmiregistry $$(read -p "Enter port number: " port; echo $$port)

server:
	java CalculatorServer 

client:
	java CalculatorClient $$(read -p "Enter client number: " clientNumber; echo $$clientNumber)