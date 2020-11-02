package assignement;

//the list of imports
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.io.File;

import logist.LogistSettings;
import logist.Measures;
import logist.behavior.AuctionBehavior;
import logist.behavior.CentralizedBehavior;
import logist.agent.Agent;
import logist.config.Parsers;
import logist.simulation.Vehicle;
import logist.plan.Plan;
import logist.task.Task;
import logist.task.TaskDistribution;
import logist.task.TaskSet;
import logist.topology.Topology;
import logist.topology.Topology.City;

public class CentralizedAssignement implements CentralizedBehavior{
	
	private Topology topology;
	private TaskDistribution distribution;
	private Agent agent;
	private long timeout_setup;
	private long timeout_plan;
	private Random randomGenerator;
	private double probability;
	private List<PTask> pickups;
	private List<PTask> deliveries;
	private List<Constraint> constraints;
	
	@Override
	public void setup(Topology topology, TaskDistribution distribution, Agent agent) {
		
		// this code is used to get the timeouts
        LogistSettings ls = null;
        try {
            ls = Parsers.parseSettings("config" + File.separator + "settings_default.xml");
        }
        catch (Exception exc) {
            System.out.println("There was a problem loading the configuration file.");
        }
        
     // the setup method cannot last more than timeout_setup milliseconds
        timeout_setup = ls.get(LogistSettings.TimeoutKey.SETUP);
        // the plan method cannot execute more than timeout_plan milliseconds
        timeout_plan = ls.get(LogistSettings.TimeoutKey.PLAN);
        
        this.topology = topology;
        this.distribution = distribution;
        this.agent = agent;
        this.randomGenerator = new Random(12345);
        this.probability = 0.4;
	}
	
	public List<Plan> plan(List<Vehicle> vehicles, TaskSet tasks) {
		List<Plan> plans = new ArrayList<Plan>();
		
		System.out.println("Building Tasks");
		this.pickups = convertPickup(tasks);
		this.deliveries = convertDeliveries(tasks);
		System.out.println("Done !");
		
		System.out.println("Building Constraints");
		this.constraints = new ArrayList<Constraint>();
		for(Vehicle vehicle: vehicles) 
			this.constraints.add(new CapacityConstraint(vehicle));
		for (int i = 0; i < this.pickups.size(); i++) {
			this.constraints.add(new SameVehicle(this.pickups.get(i), this.deliveries.get(i)));
			this.constraints.add(new OrderConstraint(this.pickups.get(i), this.deliveries.get(i)));
		}
		System.out.println("Done !");
		
		System.out.println("Building first Guess");
		Solution guess = new Solution();
		List<Solution> neighbours = new ArrayList<Solution>();
		guess.firstGuess(vehicles, this.pickups, this.deliveries);
		System.out.println("Done !");
		
		System.out.println("Building plan. Time allowed = " + (this.timeout_plan/3600));
		long time_start = System.currentTimeMillis();
		while(System.currentTimeMillis() - time_start <= this.timeout_plan) {
			neighbours = guess.neighbours(constraints);
			guess = this.localChoice(guess, neighbours, this.probability);
		}
		
		for (Vehicle v: vehicles) {
			City currentCityV = v.getCurrentCity();
			Plan planV = new Plan(currentCityV);
			PTask currentTask = guess.getNextTask_v().get(v);
			
			while (currentTask != null) {
				planV.appendMove(currentTask.getCity());
				Task temp;
				for (Task task: tasks) {
					if (task.id == currentTask.getID())
						temp = task;
				}
				if (currentTask.getPickup())
					planV.appendPickup(temp);
				else
					planV.appendDelivery(temp);
			}
		}
		
		return plans;
	}
	
	private List<PTask> convertPickup(TaskSet tasks){
		List<PTask> pTasks = new ArrayList<PTask>();
		for (Task task: tasks) {
			pTasks.add(new PTask(task.id, task.pickupCity, true, task.weight));
		}
		return pTasks;
	}	
	
	private List<PTask> convertDeliveries(TaskSet tasks){
		List<PTask> deliveries = new ArrayList<PTask>();
		for (Task task: tasks)
			deliveries.add(new PTask(task.id, task.deliveryCity, false, task.weight));
		return deliveries;
	}
							
	private Solution localChoice(Solution currentGuess, List<Solution> currentNeighbours, double probability) {
		Collections.shuffle(currentNeighbours);
		Solution max = Collections.max(currentNeighbours);
		
		if (this.randomGenerator.nextDouble() <= this.probability)
			return currentGuess;
		else
			return max;
	}
	

}


