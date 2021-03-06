package core.constants;

public enum ImageFileDimensions
{

	TRASH(1024,128),
	CHECK(),
	SAVE(),
	INCWIDTH(),
	DECWIDTH(),
	INCHEIGHT(),
	DECHEIGHT(),
	TORCH(),
	CHEST(),
	GRAY_BRICK(),
	BROWN_BRICK(),
	STONE(),
	WOOD(),
	RAINBOW(64,64),
	PRESSURE_PLATE_SPIKES(),
	SPIKES(),
	DELETE_ALL(),
	CLONE(),
	STATE_ADDER();
	
	ImageFileDimensions(int x,int y)
	{
		this.x=x;
		this.y=y;
	}
	
	private ImageFileDimensions()
	{
		this(32,32);
	}
	
	public int x,y;

}
