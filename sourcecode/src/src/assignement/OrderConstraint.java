package assignement;

public class OrderConstraint extends Constraint{
	PTask t1;
	PTask t2;
	
	public OrderConstraint(PTask one, PTask two) {
		this.t1 = one;
		this.t2 = two;
	}
	
	boolean compatible(Solution solution) {
		return solution.getTime().get(this.t1) < solution.getTime().get(this.t2);
	}
}