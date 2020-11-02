package assignement;

public class SameVehicle extends Constraint {
	PTask t1;
	PTask t2;
	
	public SameVehicle(PTask one, PTask two) {
		this.t1 = one;
		this.t2 = two;
	}
	
	boolean compatible(Solution solution) {
		return solution.getVehicle().get(this.t1) == solution.getVehicle().get(this.t2);
	}
}
