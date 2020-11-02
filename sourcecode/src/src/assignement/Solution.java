package assignement;

//Java import
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.lang.Exception;

//Logist import
import logist.simulation.Vehicle;

public class Solution implements Comparable<Solution>{
	private HashMap<PTask, PTask> nextTask_t;
	private HashMap<Vehicle, PTask> nextTask_v;
	private HashMap<PTask, Integer> time;
	private HashMap<PTask, Vehicle> vehicle;
	
	public Solution() {
		this.nextTask_t = new HashMap<PTask, PTask>();
		this.nextTask_v = new HashMap<Vehicle, PTask>();
		this.time = new HashMap<PTask, Integer>();
		this.vehicle = new HashMap<PTask, Vehicle>();
	}

	public Solution(Solution solution) {
		this.nextTask_t = solution.getNextTask_t();
		this.nextTask_v = solution.getNextTask_v();
		this.time = solution.getTime();
		this.vehicle = solution.getVehicle();
	}
	
	public void firstGuess(List<Vehicle> vehicles, List<PTask> pickups, List<PTask> deliveries) {
		int t = 1;
		VehicleCapacityComparator comparator = new VehicleCapacityComparator();
		Vehicle vMax = Collections.max(vehicles, comparator);
		long capacity = vMax.capacity();
		
		for(int i = 0; i < pickups.size(); i++) {
			int test = 1;
			if (pickups.get(i).getWeight() > capacity)
				test = 0;
			
			try {
				long w = capacity/test;
			}
			
			catch (Exception e){
				System.out.println("Problem unsolvable: task weight bigger that maximum capacity.");
			}
			//Stinks as a test but I was not able to use the "throw method"
			
			this.time.put(pickups.get(i), t);
			t++;
			this.time.put(deliveries.get(i), t);
			t++;
			this.nextTask_t.put(pickups.get(i), deliveries.get(i));
			
			if (i == 0) 
				this.nextTask_v.put(vMax, pickups.get(i));
			else 
				this.nextTask_t.put(deliveries.get(i-1), pickups.get(i));
			
			this.nextTask_t.put(pickups.get(i), deliveries.get(i));
			this.vehicle.put(pickups.get(i), vMax);
			this.vehicle.put(deliveries.get(i), vMax);
			
			if (i == deliveries.size()-1)
				this.nextTask_t.put(deliveries.get(i), null);
		}
		
		for (Vehicle v: vehicles) {
			if (v != vMax)
				this.nextTask_v.put(v, null);
		}
	}
	
	public boolean valid(List<Constraint> constraints) {
		boolean res = true;
		for (Constraint constraint: constraints) {
			if (!constraint.compatible(this))
				res = false;
		}
		return res;
	}
	
	public void changeVehicle(Vehicle v1, Vehicle v2) {
		PTask firstTaskPickup = this.nextTask_v.get(v1);
		PTask justBeforeDeliver = firstTaskPickup;
		PTask firstTaskDeliver = this.nextTask_t.get(firstTaskPickup);
		int ID = firstTaskPickup.getID();
		while (ID != firstTaskPickup.getID()) {
			try {
				justBeforeDeliver = firstTaskDeliver;
				firstTaskDeliver = this.nextTask_t.get(firstTaskDeliver);
			}
			catch (Exception e) {
				System.out.println("Error changeVehicle: Delivery task not found.");
			}
		}
		
		this.nextTask_v.put(v1, this.nextTask_t.get(firstTaskPickup));
		this.nextTask_t.put(justBeforeDeliver, this.nextTask_t.get(firstTaskDeliver));
		this.nextTask_v.put(v2, firstTaskPickup);
		this.nextTask_t.put(firstTaskDeliver, this.nextTask_v.get(v2));
		this.nextTask_t.put(firstTaskPickup, firstTaskDeliver);
		
		this.updateTime(v1);
		this.updateTime(v2);
		
		this.vehicle.put(firstTaskPickup, v2);
		this.vehicle.put(firstTaskDeliver, v2);
	}
	
	public void updateTime(Vehicle v) {
		PTask firstTask = this.nextTask_v.get(v);
		if (firstTask != null) {
			this.time.put(firstTask, 1);
			PTask nextTask = this.nextTask_t.get(firstTask);
			int count = 1;
			while (nextTask != null) {
				this.time.put(nextTask, count+1);
				firstTask = nextTask;
				nextTask = this.nextTask_t.get(nextTask);
				count++;
			}
		}
	}
	
	public void changeTaskOrder(Vehicle v, PTask t1, PTask t2) {
		PTask postT1 = this.nextTask_t.get(t1);
		PTask postT2 = this.nextTask_t.get(t2);
		
		PTask temp = this.nextTask_v.get(v);
		PTask prevTemp = null;
		while (temp != t1) {
			try {
				prevTemp = temp;
				temp = this.nextTask_t.get(temp);
			}
			catch (Exception e) {
				System.out.println("Error changeTaskOrder: first task is not part of tasks of current vehicle.");
			}
		}
		if (prevTemp == null) 
			this.nextTask_v.put(v, t2);
		else 
			this.nextTask_t.put(prevTemp, t2);
		this.nextTask_t.put(t2, postT1);
		
		while(temp != t2) {
			try {
				prevTemp = temp;
				temp = this.nextTask_t.get(temp);
			}
			catch (Exception e) {
				System.out.println("Error changeTaskOrder: second task is not part of tasks of current vehicle.");
			}
		}
		this.nextTask_t.put(prevTemp, t1);
		this.nextTask_t.put(t1, postT2);
		
		this.updateTime(v);
	}
	
	public List<Solution> neighbours(List<Constraint> constraints){
		List<Solution> solutions = new ArrayList<Solution>();
		for (Vehicle v1: this.nextTask_v.keySet()) {
			PTask currentTaskV1 = this.nextTask_v.get(v1);
			while(currentTaskV1 != null) {
				PTask nextTaskV1 = this.nextTask_t.get(currentTaskV1);
				while(nextTaskV1 != null) {
					Solution temp = new Solution(this);
					temp.changeTaskOrder(v1, currentTaskV1, nextTaskV1);
					nextTaskV1 = this.nextTask_t.get(nextTaskV1);
					if (temp.valid(constraints))
						solutions.add(temp);
				}
				nextTaskV1 = this.nextTask_t.get(nextTaskV1);
			}
			
			if (this.nextTask_v.get(v1) == null)
				break;
			
			for(Vehicle v2: this.nextTask_v.keySet()) {
				if (v2 == v1)
					break;
				Solution temp = new Solution(this);
				temp.changeVehicle(v1, v2);
				if (temp.valid(constraints))
					solutions.add(temp);
			}
		}
		return solutions;
	}
	
	public int cost() {
		int res = 0;
		for (Vehicle v : this.nextTask_v.keySet()) {
			int costV = 0;
			PTask currentTask = this.nextTask_v.get(v);
			while(currentTask != null) {
				PTask nextTask = this.nextTask_t.get(currentTask);
				if (nextTask != null)
					costV += currentTask.getCity().distanceTo(nextTask.getCity());
				currentTask = nextTask;
			}
			costV *= v.costPerKm();
			res += costV;
		}
		return res;
	}
	
	@Override
	public int compareTo(Solution other) {
		return this.cost() - other.cost();
	}
	
	public HashMap<PTask, PTask> getNextTask_t(){
		return this.nextTask_t;
	}
	
	public HashMap<Vehicle, PTask> getNextTask_v(){
		return this.nextTask_v;
	}
	
	public HashMap<PTask, Integer> getTime(){
		return this.time;
	}
	
	public HashMap<PTask, Vehicle> getVehicle(){
		return this.vehicle;
	}
}

