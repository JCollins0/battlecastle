package editor;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;

import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;
import org.json.simple.JSONValue;

import core.constants.ImageFilePaths;
import utility.Utility;

public class Tile extends Rectangle implements JSONStreamAware
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5649265101080536323L;
	protected String picText,statesText;
	protected BufferedImage[] pics;
	protected State[] states;
	protected int animation,imageX,imageY,currentState;

	private static BufferedImage[] check;
	private static State[] still;

	static
	{
		check= new BufferedImage[]{(BufferedImage)Utility.loadImage(ImageFilePaths.CHECK)};
		still=new State[]{new State()};
	}

	public static Tile decodeJSON(JSONObject j)
	{
		if(j.size()==8)
			return new Tile((int)(long)j.get("x"),(int)(long)j.get("y"),(int)(long)j.get("width"),(int)(long)j.get("height"),(String)j.get("picText"),(int)(long)j.get("imageX"),(int)(long)j.get("imageY"),(String)j.get("statesText"));
		return null;
	}

	public Tile(int x,int y,int width,int height)
	{
		this(x,y,width,height,"",32,32,"");

		picText="";
		statesText="";
	}

	public Tile(int x,int y,int width,int height,String picText,int imageX,int imageY,String statesText)
	{
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
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
	}

	/*public Tile(int x,int y,int width,int height,Image pic,State[] states)
	{
		this(x,y,width,height,new Image[]{pic},states);
	}*/

	protected State[] createStates(String s)
	{
		if(s==null||s.equals(""))
			return new State[]{new State(0,0)};
		String[] text=s.split(",");
		State[] temp=new State[text.length];
		for(int i = 0; i<temp.length;i++)
		{
			String[] data=text[i].split("-");
			if(data.length==1)
				temp[i]=new State(Integer.parseInt(data[0]));
			else if(data.length==2)
			{
				temp[i]=new State(Integer.parseInt(data[0]),Integer.parseInt(data[1]));
			}
			else
			{
				temp[i]=new State(Integer.parseInt(data[0]),Integer.parseInt(data[1]),Integer.parseInt(data[2]),Integer.parseInt(data[3]));
			}
		}
		return temp;
	}

	public void setStates(State[] s)
	{
		states=s;
	}



	protected void shift(int x,int y)
	{
		this.x+=x;
		this.y+=y;
		if(this.x>1024)
			this.x=-this.width;
		else if(this.x<-this.width)
			this.x=1024;
		if(this.y>768)
			this.y=-this.height;
		else if(this.y<-this.height)
			this.y=768;
	}

	public void draw(Graphics g)
	{
		g.drawImage(pics[animation], x, y, width, height, null);
	}

	public void tick()
	{
		if(animation++==pics.length-1)
			animation=0;
		if(states[currentState].increment(this))
			currentState=(currentState+1)%states.length;
	}

	public Tile copy()
	{
//		return new Tile(0, 0, width, height, picText, imageX, imageY, statesText);
		return new Tile(0, 0, width, height, picText, imageX, imageY, "0-1");
	}

	public String stringify()
	{
		return String.format("X:%d,Y:%d,W:%d,H:%d,A:%d",
				x,y,width,height,animation);
	}

	public void execute(String s)
	{
		String[] items = s.split(",");
		for(String item : items)
		{
			String[] key_value = item.split(":");
			switch(key_value[0])
			{
			case "X": this.x = Integer.parseInt(key_value[1]);
			break;
			case "Y": this.y = Integer.parseInt(key_value[1]);
			break;
			case "W": this.width = Integer.parseInt(key_value[1]);
			break;
			case "H": this.height = Integer.parseInt(key_value[1]);
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
		obj.put("x",new Integer(x));
		obj.put("y",new Integer(y));
		obj.put("width",new Integer(width));
		obj.put("height",new Integer(height));
		obj.put("picText",picText);
		obj.put("imageX",imageX);
		obj.put("imageY", imageY);
		obj.put("statestext",statesText);
		JSONValue.writeJSONString(obj, out);
	}

	@Override
	public String toString() {
		return stringify();
	}

}
