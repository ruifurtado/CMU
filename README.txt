###########  README   ####################

ESTE FICHEIRO DESCREVE TODAS AS TAREFAS E DUVIDAS RELACIONADAS COM O PROJECTO "UBIBIKE" :


##SHARING POINTS AND EXCHANGING TEXT MESSAGES --> WIFI DIRECT
##UBIBIKE APP INFORMS THE SERVER WHENEVER A BIKE ARRIVES OR LEAVES THE STATION 

####----> SEMANA 17-23 Abril : tirar duvida + fazer o 3, com base no lab 4 (termites), é só implementar no projecto em background e acrescentar o código nos sitios certos : POR FAZER


1. GUI DESIGN AND IMPLEMENTATION
	
	- layouts : check
	
	- toda a interação entre actividades, botões etc : check

	DUVIDA: Na actividades das estacões, quando reservamos uma bike, pode ser como estamos a fazer? ("book a bike at specific station, while showing their location on a map"  : PERGUNTAR

2. CENTRAL SERVER + UBIBIKE
	
	- SIGN IN + SIGN UP : check
	
	- STATIONS + BOOK A BIKE (apenas a visualização das estacões com bikes disponíveis com todos os dados relacionados, e o decremento de uma bike quando ha uma reserva) : check
	
	- GET LIST OF STATIONS WITH AVAILABLE BIKES TO BOOK : check

	- BOOK BIKE AT SPECIFIC SATION (while showing their location on a map *DUVIDA 1.*)  : check

	- GET USER INFORMATION (including current score and trajectories) : check apenas os scores
 
	- SEND NEW TRAJECTORY : POR FAZER
	
	- SHOW MOST RECENT AND PAST TRAJECTORIES ON A MAP : POR FAZER
	
3. BETWEEN MOBILE DEVICES (using WIFI Direct)

	- SEND AND RECEIVE POINTS : POR FAZER

	- SEND AND RECEIVE TEXT MESSAGES : POR FAZER

4. BETWEEN  MOBILE DEVICES, BLE BEACONS, AND THE CENTRAL SERVER

	- NOTIFY BIKE PICK UP : POR FAZER

	- NOTIFY BIKE DROP OFF : POR FAZER

5. BETWEEN THE USER AND THE BIKE BEING USER (using BLE)

	- DETECT WHAT BIKE IS BEING USED BY WHICH USER : POR FAZER

6. ADVANCED FEATURES

	- SECURITY: POR FAZER
		- MESSAGE REPLICATION AND TEMPERING 
			EXAMPLE: if someone eavesdrops a message containing  points, it must be impossible to: i) resend the message multiple times to the same user or to different users, or ii)  tamper with the data in the message (for example, increase the number of points in the message)

	- ROBUSTNESS : POR FAZER
		- REORDERING OF EVENTS
			EXAMPLE:  imagine  that  user  A  cycles  to  gain  points  and  then  sends  all  the  points  to  user  B.  Now, user B updates the central server before user A reports that it gained points by cycling. When such situations  occur, the system must be able to: i) accept the points given to user B, and ii) update the score of user A. In other  words, the server must always know how users earned their points


