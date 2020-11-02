# Pickup and Delivery Problem implemented as a Constraint Satisfcation Problem



## Todo:

* Generalize the Algorithm for Stochastic Local Search (for the PDP as a CSP) so that it allows for multiple packets per-agent
* Implement the algorithm in logist from the skeleton files
* Quantify algorithm's performance

## Tasks (from datum):

* Implement Stochastic Local Search for PDP
* Run simulations of different environnemnent in order to observe the behavior of SLS for centralized planning
* Reflect on the *fairness* of optimal plan, optimality (in amount of packets transported per km) requires an uneven workload
* Test the programm for different task and vehicle numbers, discuss complexity

## Implementation Hints (from datum)

* You will need to implement the Centralized Behavior interface and compute the plans for several vehicles. The plan for each vehicle has to be returned as a list in which the plans appear in the same order as their corresponding vehicles in the vehicle list. 
* When you design your stopping criterion, make sure that you don’t use more time than the platform timeout (see starter code). 
* Again, if the *LogistPlatform* detects a problem in your plan, the simulation will exit and a detailed error message is displayed. Remember that you have to handle all tasks and that each tasks can only be picked up and delivered by one vehicle.
* There is a separate document that elaborates the relevant parts of the *LogistPlat- form* for this exercise in more detail.

