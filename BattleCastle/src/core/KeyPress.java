package core;

public enum KeyPress {
	
	LEFT_D("*left-down*"),
	RIGHT_D("*right_down*"),
	JUMP_D("*jump_down*"),
	DOWN_D("*down_down*"),
	LEFT_U("*left-up*"),
	RIGHT_U("*right-up*"),
	JUMP_U("*jump-up*"),
	DOWN_U("*down-up*");
	
	private String s;
	
	KeyPress(String s)
	{
		this.s = s;
	}
	
	public String getText()
	{
		return s;
	}
	
}
