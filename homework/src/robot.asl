/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- .print("Start up complete.").

+!getRouteToCar(X,Y,U,V) : true <-
  .print("Asking for route from my position (",X,",",Y,") to the order at (",U,",",V,") from navigator.");
  .send(navigator,ask,routeToCar(X,Y,U,V)).

+!getRouteToDestination(X,Y,U,V) : true <-
  .print("Asking for route to carry the order from (",X,",",Y,") to the destination (",U,",",V,") from navigator.");
  .send(navigator,ask,routeToDestination(X,Y,U,V)).

+!getRouteToCarFinish(X,Y,U,V) : true <-
  .print("Asking for route from my position (",X,",",Y,") to the order at (",U,",",V,") from navigator.");
  .send(navigator,ask,routeToCarFinish(X,Y,U,V)).

+!getRouteToDestinationFinish(X,Y,U,V) : true <-
  .print("Asking for route to carry the order from (",X,",",Y,") to the destination (",U,",",V,") from navigator.");
  .send(navigator,ask,routeToDestinationFinish(X,Y,U,V)).


+!carryCar(X,Y,U,V) : position(A,B) <-
  .print("Received instructions to pick up order at (",X,",",Y,") and place it at (",U,",",V,") (bar).");
  +carDestination(U,V);
  !getRouteToCar(A,B,X,Y).

+!finishTable(X,Y,U,V) : position(A,B)<-
  .print("Received instructions to finish table at (",U,",",V,").");
  +carDestination(X,Y);
  !getRouteToCarFinish(A,B,U,V).
  
+routeToCarFinish(X)[source(navigator)] <-
  .print("Received route to order. ",X);
  !goToCarFinish(X).

+!goToCarFinish([H|T]): true <-
  go(H); 
  !goToCarFinish(T).

+!goToCarFinish(T) : T==[] & position(X,Y) & carDestination(U,V) <- 
  .print("Handed over the order!");
  -carDestination(U,V);
  leavetable;
  !getRouteToDestinationFinish(X,Y,4,2).
  
+routeToDestinationFinish(X)[source(navigator)] <-
  !goToDestinationFinish(X).

+!goToDestinationFinish([H|T]) : true <- 
  go(H);
  !goToDestinationFinish(T).
  
+!goToDestinationFinish(T) : T==[] & position(A,B)<- 
  .print("Table finished!").
  

+routeToCar(X)[source(navigator)] <-
  .print("Received route to order. ",X);
  !goToCar(X).


+routeToDestination(X)[source(navigator)] <-
  !goToDestination(X).

+!goToCar([H|T]): true <-
  go(H); 
  !goToCar(T).


+!goToDestination([H|T]) : true <- 
  go(H);
  !goToDestination(T).

+car(X,Y) : true <-
  .send(surveillance,tell,carOnrobot(X,Y)).

+!goToCar(T) : T==[] & position(X,Y) & carDestination(U,V) <- 
  pickuporder; 
  .print("Picked up order!");
  -carDestination(U,V);
  !getRouteToDestination(X,Y,U,V);
  +lastTable(X,Y).



+!goToDestination(T) : T==[] & position(A,B) & lastTable(U,V) <- 
  .print("!!!!!!Added order! robot is at (",A,",",B,") and destination is (",U,",",V,")!!!!!!");
  .send(surveillance,tell,orderAdded(A,B,U,V));
  -lastTable(C,D).
  