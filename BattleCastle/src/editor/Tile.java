package editor;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;
import org.json.simple.JSONValue;

import core.constants.ImageFilePaths;
import game.physics.Polygon;
import game.physics.Vector;
import utility.Utility;

public class Tile extends Polygon implements JSONStreamAware, Comparable<Tile>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5649265101080536323L;
	
	protected String picText,statesText;
	protected BufferedImage[] pics;
	protected ArrayList<State> states;
	protected int animation,imageX,imageY,currentState;
	protected int speed = 3;
	protected boolean animateIffMouseOver,mouseIsOver,reverseAnimate,currentlyReversed;
	protected boolean statesActive=true;
	private String ID;
	
	private static BufferedImage[] check;
	//private static State[] still;
	
	//TODO: REMOVE THESE
	//private int width, height, x, y;

	static
	{
		check= new BufferedImage[]{(BufferedImage)Utility.loadImage(ImageFilePaths.CHECK)};
		//still=new State[]{new State()};
	}

	public static Tile decodeJSON(JSONObject j)
	{
		if(j.size()==8)
			return new Tile((int)(long)j.get("x"),(int)(long)j.get("y"),(int)(long)j.get("width"),(int)(long)j.get("height"),(String)j.get("picText"),(int)(long)j.get("imageX"),(int)(long)j.get("imageY"),(String)j.get("statestext"));
		return null;
	}

	public Tile(int x,int y,int width,int height)
	{
		this(x,y,width,height,"",32,32,"");

		picText="";
		statesText="";
	}
	
	public Tile(int x,int y,int width,int height,String picText,int imageX,int imageY,String statesText,boolean noWrap)
	{
		this(x,y,width,height,picText,imageX,imageY,statesText);
		this.noWrap=noWrap;
	}
	
	public Tile(int x,int y,int width,int height,String picText,int imageX,int imageY,String statesText,boolean noWrap,boolean animateIffMouseOver)
	{
		this(x,y,width,height,picText,imageX,imageY,statesText,noWrap);
		this.animateIffMouseOver=animateIffMouseOver;
	}
	
	public Tile(int x,int y,int width,int height,String picText,int imageX,int imageY,String statesText,boolean noWrap,boolean animateIffMouseOver,boolean reverseAnimate)
	{
		this(x,y,width,height,picText,imageX,imageY,statesText,noWrap,animateIffMouseOver);
		this.reverseAnimate=reverseAnimate;
	}

	public Tile(int x,int y,int width,int height,String picText,int imageX,int imageY,String statesText)
	{
		super(x,y,width,height);
//		this.x=x;
//		this.y=y;
//		this.width=width;
//		this.height=height;
		this.picText=picText;
		this.imageX=imageX;
		this.imageY=imageY;
		if(picText!=null&&!picText.equals(""))
		{
			this.pics=Utility.loadBufferedArray(picText, imageX, imageY);
		}
		else
			this.pics=check;
		this.statesText=statesText;
		this.states=createStates(statesText);
		currentState=0;
		ID = Utility.generateID(x,y,width,height);
	}

	/*public Tile(int x,int y,int width,int height,Image pic,State[] states)
	{
		this(x,y,width,height,new Image[]{pic},states);
	}*/

	protected ArrayList<State> createStates(String s)
	{
		if(s==null||s.equals(""))
		{
			ArrayList<State> temp = new ArrayList<State>();
			temp.add(new State(0,0));
			return temp;
		}
		String[] text=s.split(",");
		ArrayList<State> temp=new ArrayList<State>();
		for(int i = 0; i<text.length;i++)
		{
			String[] data=text[i].split("\\+");
			if(data.length==1)
				temp.add(new State(Integer.parseInt(data[0])));
			else if(data.length==2)
			{
				temp.add(new State(Integer.parseInt(data[0]),Integer.parseInt(data[1])));
			}
			else
			{
				temp.add(new State(Integer.parseInt(data[0]),Integer.parseInt(data[1]),Integer.parseInt(data[2]),Integer.parseInt(data[3])));
			}
		}
		return temp;
	}
	
	public String makeStatesText()
	{
		String temp="";
		for(State s:states)
			temp+=s.stringify()+",";
		return temp.substring(0, temp.length()-1);
	}

	public void setStates(ArrayList<State> s)
	{
		states=s;
	}
	
	public void addState(State s)
	{
		states.add(s);
	}
	
	public State removeState(int index)
	{
		return states.remove(index);
	}
	
	public State removeLastState()
	{
		return states.remove(states.size()-1);
	}
	
	public ArrayList<State> getStates()
	{
		return states;
	}
	
	public void setToLastState()
	{
		for(;currentState<states.size();currentState++)
		{
			states.get(currentState).moveToEndPos(this);
		}
	}
	
	public void resetStates()
	{
		//some more code based on position?????
		states.get(currentState).reset();
		currentState=0;
	}
	
	public boolean getStatesActive()
	{
		return statesActive;
	}
	
	public void setStatesActive(boolean statesActive)
	{
		this.statesActive=statesActive;
	}
	
	public void setMouseIsOver(boolean mouse)
	{
		this.mouseIsOver=mouse;
	}

	public boolean getMouseIsOver()
	{
		return mouseIsOver;
	}
	
	public void setReverseAnimate(boolean reverseAnimate)
	{
		if(!this.reverseAnimate&&reverseAnimate)
			animation<<=1;
		else if(this.reverseAnimate&&!reverseAnimate)
			animation>>=1;
		this.reverseAnimate=reverseAnimate;
		
	}

	public int getWidth()
	{
		return getCorners()[1].XPoint()-getCorners()[0].XPoint();
	}
	
	public int getHeight()
	{
		return Math.abs(getCorners()[3].YPoint() - getCorners()[0].YPoint());
	}
	
	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void setWidth(int width)
	{
		getCorners()[1].setX(getCorners()[0].XPoint() + width);
		getCorners()[2].setX(getCorners()[0].XPoint() + width);
	}
	
	public void setHeight(int height)
	{
		getCorners()[2].setY(getCorners()[0].YPoint() + height);
		getCorners()[3].setY(getCorners()[0].YPoint() + height);
	}
	
	public int getX()
	{
		return getCorners()[0].XPoint();
	}
	
	public int getY()
	{
		return getCorners()[0].YPoint();
	}
	
	public String getID() {return ID;}
	
	public void draw(Graphics g)
	{
		g.drawImage(pics[animation/speed], getX(), getY(), getWidth() , getHeight(), null);
//		System.out.println(picText + " " + animation + " " + currentState + " " + x + " " + y);
	}
	
	public void tick()
	{
		super.tick();
		if(animation!=0||!animateIffMouseOver||(animateIffMouseOver&&mouseIsOver))
		{
			if(reverseAnimate&&currentlyReversed)
			{
				if(--animation==0)
					currentlyReversed=false;
			}
			else if(animation++>=(pics.length-1)*speed)
			{
				if(reverseAnimate)
					currentlyReversed=true;
				else
					animation=0;
			}
			if(statesActive&&states.get(currentState).increment(this))
				currentState=(++currentState)%states.size();
		}
	}

	public Tile copy()
	{
//		return new Tile(0, 0, width, height, picText, imageX, imageY, statesText);
		return new Tile(0, 0, getWidth(), getHeight(), picText, imageX, imageY, statesText);
	}

	public String stringify()
	{
		return String.format("X:%d,Y:%d,W:%d,H:%d,A:%d",
				getX(),getY(),getWidth(),getHeight(),animation);
	}

	public void execute(String s)
	{
		String[] items = s.split(",");
		for(String item : items)
		{
			String[] key_value = item.split(":");
			switch(key_value[0])
			{
			case "X":  moveTo(Integer.parseInt(key_value[1]),getY());
			break;
			case "Y":  moveTo(getX(),Integer.parseInt(key_value[1]));
			break;
			case "W": setWidth(Integer.parseInt(key_value[1]));
			break;
			case "H": setHeight(Integer.parseInt(key_value[1]));
			break;
			case "A": this.animation=Integer.parseInt(key_value[1]);
			break;

			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void writeJSONString(Writer out) throws IOException {
		LinkedHashMap obj=new LinkedHashMap();
		obj.put("x",new Integer(getX()));
		obj.put("y",new Integer(getY()));
		obj.put("width",new Integer(getWidth()));
		obj.put("height",new Integer(getHeight()));
		obj.put("picText",picText);
		obj.put("imageX",imageX);
		obj.put("imageY", imageY);
		obj.put("statestext",makeStatesText());
		JSONValue.writeJSONString(obj, out);
	}

	@Override
	public String toString() {
		return stringify();
	}

	public Vector getStateVel() {
		if(states.size() != 0)
			return states.get(currentState).getV();
		return null;
	}

	@Override
	public int compareTo(Tile o) {
		return this.ID.compareTo(o.ID);
	}
	
	
	
}
