package game.physics;

public class Edge
{
	
	private Vector max;
	private Vector edge;
	
	public Edge()
	{
		
	}

	public Vector getMax() {
		return max;
	}

	public Vector getEdge() {
		return edge;
	}
	
	public void set(Vector max,Vector edge)
	{
		this.max=max;
		this.edge=edge;
	}

}
