package assignement;

import logist.simulation.Vehicle;

public class CapacityConstraint extends Constraint{
	Vehicle vehicle;
	int capacity;
	
	public CapacityConstraint(Vehicle v) {
		this.vehicle = v;
		this.capacity = v.capacity();
	}
	
	public boolean compatible(Solution solution) {
		boolean res = true;
		long currentWeight = 0;
		PTask task = solution.getNextTask_v().get(this.vehicle);
		
		while (task != null) {
			if (task.getPickup())
				currentWeight += task.getWeight();
			else
				currentWeight -= task.getWeight();
			
			if (currentWeight > this.capacity)
				res = false;
			
			task = solution.getNextTask_t().get(task);
		}
		
		return res;
	}
}