###########  README   ####################

ESTE FICHEIRO DESCREVE TODAS AS TAREFAS E DUVIDAS RELACIONADAS COM O PROJECTO "UBIBIKE" :


##SHARING POINTS AND EXCHANGING TEXT MESSAGES --> WIFI DIRECT
##UBIBIKE APP INFORMS THE SERVER WHENEVER A BIKE ARRIVES OR LEAVES THE STATION 

TO BE DONE:

-> a cena dos dos pontos do servidor da parte da segurança
-> mostrar a bike em uso
-> bikes reservadas e n reservadas, diferenciar
-> incrementar as bikes quando chegam das trajectorias e ver se foi uma reservada ou nao e consoante isso incrementar 
-> robustez 


ONGOIND TASK:

	-> TRAJECTORIES AND ALL THE FEATURES (VASCO)
	-> TRAJECTORIES AND ALL THE FEATURES  (RUI)
	-> ADVANCED FEATURE SECURITY  (BERNARDO)

1. GUI DESIGN AND IMPLEMENTATION
	
	- layouts : CHECK
	
	- toda a interação entre actividades, botões etc : CHECK

2. CENTRAL SERVER + UBIBIKE
	
	- SIGN IN + SIGN UP : CHECK
	
	- STATIONS + BOOK A BIKE (apenas a visualização das estacões com bikes disponíveis com todos os dados relacionados, e o decremento de uma bike quando ha uma reserva) : CHECK
	
	- GET LIST OF STATIONS WITH AVAILABLE BIKES TO BOOK : CHECK

	- BOOK BIKE AT SPECIFIC SATION (while showing their location on a map *DUVIDA 1.*)  : CHECK

	- GET USER INFORMATION (including current score and trajectories) : CHECK 
 
	- SEND NEW TRAJECTORY : CHECK
	
	- SHOW MOST RECENT AND PAST TRAJECTORIES ON A MAP : CHECK
	
3. BETWEEN MOBILE DEVICES (using WIFI Direct)

	- SEND AND RECEIVE POINTS : CHECK

	- SEND AND RECEIVE TEXT MESSAGES : CHECK

4. BETWEEN  MOBILE DEVICES, BLE BEACONS, AND THE CENTRAL SERVER

	- NOTIFY BIKE PICK UP : CHECK

	- NOTIFY BIKE DROP OFF : CHECK

5. BETWEEN THE USER AND THE BIKE BEING USER (using BLE)

	- DETECT WHAT BIKE IS BEING USED BY WHICH USER : POR FAZER

6. ADVANCED FEATURES

	- SECURITY: CHECK
		- MESSAGE REPLICATION AND TEMPERING 
			EXAMPLE: if someone eavesdrops a message containing  points, it must be impossible to: i) resend the message multiple times to the same user or to different users, or ii)  tamper with the data in the message (for example, increase the number of points in the message)

	- ROBUSTNESS : POR FAZER
		- REORDERING OF EVENTS
			EXAMPLE:  imagine  that  user  A  cycles  to  gain  points  and  then  sends  all  the  points  to  user  B.  Now, user B updates the central server before user A reports that it gained points by cycling. When such situations  occur, the system must be able to: i) accept the points given to user B, and ii) update the score of user A. In other  words, the server must always know how users earned their points


