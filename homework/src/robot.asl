/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- .print("Start up complete.").

+!getRouteToCar(X,Y,U,V) : true <-
  .print("Asking for route from my position (",X,",",Y,") to the car at (",U,",",V,") from navigator.");
  .send(navigator,ask,routeToCar(X,Y,U,V)).

+!getRouteToDestination(X,Y,U,V) : true <-
  .print("Asking for route to carry the car from (",X,",",Y,") to the destination (",U,",",V,") from navigator.");
  .send(navigator,ask,routeToDestination(X,Y,U,V)).

+!getRouteToGate(X,Y,U,V) : true <-
  .print("Asking for route from my position (",X,",",Y,") to the gate at (",U,",",V,") from navigator.");
  .send(navigator,ask,routeToGate(X,Y,U,V)).  

+!carryCar(X,Y,U,V) : position(A,B) <-
  .print("Received instructions to pick up order at (",X,",",Y,") and place it at (",U,",",V,") (bar).");
  +carDestination(U,V);
  !getRouteToCar(A,B,X,Y).

+routeToCar(X)[source(navigator)] <-
  .print("Received route to order. ",X);
  !goToCar(X).

+routeToGate(X)[source(robot)] <-
  .print("Received route to gate. ",X);
  !goToGate(X).

+routeToDestination(X)[source(navigator)] <-
  !goToDestination(X).

+!goToCar([H|T]): true <-
  go(H); 
  !goToCar(T).

+!goToGate([H|T]): true <-
  go(H);
  !goToGate(T).

+!goToDestination([H|T]): true <- 
  go(H); 
  !goToDestination(T).

+car(X,Y) : true <-
  .send(surveillance,tell,carOnrobot(X,Y)).

+!goToCar(T) : T==[] & position(X,Y) & carDestination(U,V) <- 
  pickuporder; 
  .print("Picked up order!");
  /*-carDestination(U,V);*/
  !getRouteToDestination(X,Y,U,V).

+!goToDestination(T) : T==[] & position(A,B) <- 
  !getRouteToGate(A,B,4,2);
  .print("Added order!").