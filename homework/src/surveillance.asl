// Agent Surveillance in project Garage.mas2j

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true 
<-
.print("Start up complete.").


+gate(X,Y) : true
<-
.print("gate: ",X,",",Y).


+takenparkingspot(X,Y) : true
<-
.print("A parking spot at (",X,",",Y,") has been filled.").


//+emptyparkingspot(X,Y) : true
//<-
//.print("emptyparkingspot ",X,",",Y).


+carArrived(X,Y) : emptyparkingspot(U,V)
<-
.print("There is a car at (",X,",",Y,") waiting to be placed in the parking area. Calling valet...");
!callValet(X,Y,U,V).


+carLeaving(X,Y) : gate(U,V)
<-
.print("There is a car at (",X,",",Y,") waiting to get out of the parking area. Calling valet...");
!callValet(X,Y,U,V).


+!callValet(X,Y,U,V) : true 
<- 
.print("Instructing valet to pick up the car from (",X,",",Y,") and carry it to (",U,",",V,").");
.send(valet,achieve,carryCar(X,Y,U,V)).

+carOnValet(X,Y) : true
<-
.print("Valet is carrying car from owner ",X," with licence plate number ",Y).
