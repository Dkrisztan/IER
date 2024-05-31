/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start : true <- .print("Start up complete.").

+!calculateRoute(X,Y,U,V) : true <- .print("Calculating route from (",X,",",Y,") to (",U,",",V,").").
