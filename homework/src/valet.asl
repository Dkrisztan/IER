// Agent Valet in project Garage.mas2j

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true
<-
.print("Start up complete.").


+!getRouteToCar(X,Y,U,V) : true
<-
.print("Asking for route from my position (",X,",",Y,") to the car at (",U,",",V,") from navigator.");
.send(navigator,ask,routeToCar(X,Y,U,V)).


+!getRouteToDestination(X,Y,U,V) : true 
<-
.print("Asking for route to carry the car from (",X,",",Y,") to the destination (",U,",",V,") from navigator.");
.send(navigator,ask,routeToDestination(X,Y,U,V)).

+!carryCar(X,Y,U,V) : position(A,B)
<-
.print("Received instructions to pick up car at (",X,",",Y,") and place it at (",U,",",V,").");
+carDestination(U,V);
!getRouteToCar(A,B,X,Y).


+routeToCar(X)[source(A)] : A==navigator
<-
.print("Received route to car. ",X);
!goToCar(X).


+routeToDestination(X)[source(A)] : A==navigator
<-
!goToDestination(X).


+!goToCar([H|T]): true 
<-
go(H); 
!goToCar(T).


+!goToDestination([H|T]): true
<- 
go(H); 
!goToDestination(T).


+car(X,Y) : true
<-
.send(surveillance,tell,carOnValet(X,Y)).


+!goToCar(T) : T==[] & position(X,Y) & carDestination(U,V)
<- 
pickupcar; 
.print("Picked up car!");
-carDestination(U,V);
!getRouteToDestination(X,Y,U,V).


+!goToDestination(T) : T==[]
<- 
dropcar; 
.print("Dropped car off!"). 
