/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- .print("Start up complete.").

+!getRouteToOrder(X,Y,U,V) : true <-
  .print("Asking for route from my position (",X,",",Y,") to the order at (",U,",",V,") from navigator.");
  .send(navigator,ask,routeToOrder(X,Y,U,V)).

+!getRouteToDestination(X,Y,U,V) : true <-
  .print("Asking for route to carry the order from (",X,",",Y,") to the destination (",U,",",V,") from navigator.");
  .send(navigator,ask,routeToDestination(X,Y,U,V)).

+!getRouteToOrderFinish(X,Y,U,V) : true <-
  .print("Asking for route from my position (",X,",",Y,") to the table at (",U,",",V,") from navigator.");
  .send(navigator,ask,routeToOrderFinish(X,Y,U,V)).

+!getRouteToDestinationFinish(X,Y,U,V) : true <-
  .print("Asking for route to carry the money from (",X,",",Y,") to the destination (",U,",",V,") from navigator.");
  .send(navigator,ask,routeToDestinationFinish(X,Y,U,V)).

+!carryOrder(X,Y,U,V) : position(A,B) <-
  .print("Received instructions to pick up order at (",X,",",Y,") and place it at (",U,",",V,") (bar).");
  +orderDestination(U,V);
  !getRouteToOrder(A,B,X,Y).

+!finishTable(X,Y,U,V) : position(A,B)<-
  .print("Received instructions to finish table at (",U,",",V,").");
  +orderDestination(X,Y);
  !getRouteToOrderFinish(A,B,U,V).
  
+routeToOrderFinish(X)[source(navigator)] <-
  .print("Received route to order. ",X);
  !goToOrderFinish(X).

+!goToOrderFinish([H|T]): true <-
  go(H); 
  !goToOrderFinish(T).

+!goToOrderFinish(T) : T==[] & position(X,Y) & orderDestination(U,V) <- 
  .print("Handed over the order!");
  -orderDestination(U,V);
  leavetable;
  !getRouteToDestinationFinish(X,Y,4,2).
  
+routeToDestinationFinish(X)[source(navigator)] <-
  !goToDestinationFinish(X).

+!goToDestinationFinish([H|T]) : true <- 
  go(H);
  !goToDestinationFinish(T).
  
+!goToDestinationFinish(T) : T==[] & position(A,B)<- 
  .print("Table finished!").
  
+routeToOrder(X)[source(navigator)] <-
  .print("Received route to order. ",X);
  !goToOrder(X).

+routeToDestination(X)[source(navigator)] <-
  !goToDestination(X).

+!goToOrder([H|T]): true <-
  go(H); 
  !goToOrder(T).

+!goToDestination([H|T]) : true <- 
  go(H);
  !goToDestination(T).

+order(X,Y) : true <-
  .send(surveillance,tell,orderOnrobot(X,Y)).

+!goToOrder(T) : T==[] & position(X,Y) & orderDestination(U,V) <- 
  pickuporder; 
  .print("Picked up order!");
  -orderDestination(U,V);
  !getRouteToDestination(X,Y,U,V);
  +lastTable(X,Y).

+!goToDestination(T) : T==[] & position(A,B) & lastTable(U,V) <- 
  .print("Added order! robot is at (",A,",",B,") and destination is (",U,",",V,")");
  .send(surveillance,tell,orderAdded(A,B,U,V));
  -lastTable(C,D).
  