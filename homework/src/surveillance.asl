/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <-
  .print("Start up complete.").

+bar(X,Y) : true <-
  .print("bar: ",X,",",Y).

+takenparkingspot(X,Y) : true <-
  .print("A table at (",X,",",Y,") has been filled.").

// +carArrived(X,Y) : emptyparkingspot(U,V) <-
//   .print("There is a customer at (",X,",",Y,") waiting to be placed at a table. Calling robot...");
//   !callrobot(X,Y,U,V).

+orderAt(X,Y) : bar(U,V) <-
  .print("There is a customer at (",X,",",Y,") waiting to get out of the restaurant. Calling robot...");
  !callrobot(X,Y,U,V).

+!callrobot(X,Y,U,V) : true <- 
  .print("Instructing robot to pick up the order from (",X,",",Y,") and carry it to (",U,",",V,").");
  .send(robot,achieve,carryCar(X,Y,U,V)).

+carOnrobot(X,Y) : true <-
  .print("Robot is carrying order from customer ",X," with order ",Y).
