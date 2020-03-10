# Book-Store
In this project we practiced concurrent programming on the Java 8 environment. We built a simple Micro-Service framework and implemented an online books store application on top of this framework. This framework was consisted of two main parts: A Message-Bus and Micro-Services.
The program recieved a JSON file as an input which specified the type and amount of Micro-Services we initialize, the books available in the inventory, the number of vehicles available for delivering the books to the costumers and the costumers' orders. 
The Micro-Services communicates with each other by sending Events/Broadcasts to the Message-Bus which responsible to assign these messages to the next Micro-Service in a round-robin manner.
