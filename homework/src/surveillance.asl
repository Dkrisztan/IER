/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <-
  .print("Start up complete.").

+gate(X,Y) : true <-
  .print("gate: ",X,",",Y).

+takenparkingspot(X,Y) : true <-
  .print("A parking spot at (",X,",",Y,") has been filled.").

+carArrived(X,Y) : emptyparkingspot(U,V) <-
  .print("There is a car at (",X,",",Y,") waiting to be placed in the parking area. Calling valet...");
  !callValet(X,Y,U,V).

+carLeaving(X,Y) : gate(U,V) <-
  .print("There is a car at (",X,",",Y,") waiting to get out of the parking area. Calling valet...");
  !callValet(X,Y,U,V).

+!callValet(X,Y,U,V) : true <- 
  .print("Instructing robot to pick up the order from (",X,",",Y,") and carry it to (",U,",",V,").");
  .send(valet,achieve,carryCar(X,Y,U,V)).

+carOnValet(X,Y) : true <-
  .print("Robot is carrying order from customer ",X," with order ",Y).
