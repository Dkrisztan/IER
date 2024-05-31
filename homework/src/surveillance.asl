/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <-
  .print("Start up complete.").

+bar(X,Y) : true <-
  .print("bar: ",X,",",Y).

+takentable(X,Y) : true <-
  .print("A table at (",X,",",Y,") has been filled.").

+orderAt(X,Y) : bar(U,V) <-
  .print("There is a customer at (",X,",",Y,") waiting to order. Calling robot...");
  !callrobot(X,Y,U,V).

+!callrobot(X,Y,U,V) : true <- 
  .print("Instructing robot to pick up the order from (",X,",",Y,") and carry it to (",U,",",V,").");
  .send(robot,achieve,carryOrder(X,Y,U,V)).

+orderOnrobot(X,Y) : true <-
  .print("Robot is carrying order from customer ",X," with order ",Y).

+orderAdded(X,Y,U,V) : true <-
  .send(robot,achieve,finishTable(X,Y,U,V)).