package game.physics;

public class PhysicsTriangle extends PhysicsPoly {

	public PhysicsTriangle(int[] x, int[] y, double theta, Vector velocity,
			double torque, double mass, double dragC) {
		super(x, y, theta, velocity, torque, mass, dragC);
	}
	
	public PhysicsTriangle(int x1, int y1, int x2, int y2, int x3, int y3, double theta,Vector velocity, double torque, double mass, double dragC)
	{
		this(new int[]{x1,x2,x3},new int[]{y1,y2,y3},theta,velocity,torque,mass,dragC);
	}

}
