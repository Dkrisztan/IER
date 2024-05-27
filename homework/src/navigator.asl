// Agent Navigator in project Garage.mas2j

/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true
<-
.print("Start up complete.").


+!calculateRoute(X,Y,U,V) : true
<-
.print("Calculating route from (",X,",",Y,") to (",U,",",V,").").


//+obstacle(X,Y) : true
//<-
//.print("obstacle: ",X,",",Y).


//+~obstacle(X,Y) : true
//<-
//.print("walkable: ",X,",",Y).
