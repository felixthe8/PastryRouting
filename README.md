# Pastry Routing
CECS 327 Spring 2017
This is a repository for a CECS 327 project. The goal of this project is to implement a Pastry routing algorithm to find the Pastry ID and corresponding IP address of a node in the system. 

## Project Description
This project simulates the interactions of nodes across a distributed system. Each node has a leaf set and a routing table. The leaf set holds the information regarding the next and previous 2 nodes in the routing system. The routing table is a data table stored by a node in a distributed system to that lists the routes to a particular network destination.  
When a node attempts to find a Pastry ID and its corresponding IP address, it will first check its leaf set. If the value is not immediately found in the leaf set, the routing table is then checked. If the routing table does not have the Pastry ID, it will return the numerically closest Pastry ID to the one that is trying to be found, and continues this process until either a) the Pastry ID and its corresponding IP are found. or b) the Pasty ID is found to be nonexistent. 

## Getting Started
Make sure that you have the latest [Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) installed.  

```
git clone https://github.com/felixthe8/PastryRouting.git
```

Navigate into the working directory.   
*Compile the code*
```
javac Client.java SERVER_PROGRAM.java
```
*Run the server code*  
Originally, this project was deployed on AWS EC2 t2.micro. However, the project may have been removed due to budgetary and practical reasons (ex. the course ended, the free trial of t2.micro has ended). To test this program locally, in the Client.java change the *ip* to the localhost IP. 
```
java SERVER_PROGRAM
```
*Open up another terminal window and run the client code*
```
java Client
```

## Running the Tests
This project was tested with the SERVER_PROGRAM.java code running on an AWS EC2 t2.micro server and the Client.java code running on a local machine. The other nodes in the distributed system are also running on separate AWS instance, so if the Pastry ID cannot be found in the current leaf set or routing table, it will try and communicate with the other AWS instances. 

## Built With  
[Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)  
Amazon EC2 - as stated earlier, the EC2 instance may have been removed. Future work and testing may need to be done using a new EC2 instance or with the localhost IP. 


