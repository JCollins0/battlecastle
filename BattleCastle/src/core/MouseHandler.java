package core;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import core.menu_object.MapSelectionObject;
import core.menu_object.MenuButton;
import core.menu_object.MenuButtonType;
import core.menu_object.MenuTextField;
import core.menu_object.MenuTextFieldType;
import game.Game;
import game.player.BattleCastleUser;

public class MouseHandler implements MouseMotionListener, MouseListener {

	private BattleCastleCanvas canvasref;
	private static Point mouse;
	
	public MouseHandler(BattleCastleCanvas canvasref){
		this.canvasref = canvasref;
		mouse = new Point(-1,-1);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		switch( canvasref.getCurrentState())
		{
		case MAIN_MENU:
			
			ArrayList<MenuButton> buttonList = canvasref.getMenuButtons();
			
			for(MenuButton button : buttonList)
			{
				if (button.getBounds().contains(mouse))
				{
					if (button.getButtonType() == MenuButtonType.HOST_GAME)
					{
						canvasref.setGame(true);
						
						canvasref.setCurrentState(GameState.INPUT_USER_NAME);
						
					}else if(button.getButtonType() == MenuButtonType.JOIN_GAME)
					{
						canvasref.setGame(false);
						
						canvasref.setCurrentState(GameState.JOIN_SERVER);
					}
					
				}
			}
			
			break;
		case JOIN_SERVER:
			

			ArrayList<MenuTextField> menuTextFieldList = canvasref.getMenuTextFields();
			for (MenuTextField field : menuTextFieldList)
			{
				if (field.getBounds().contains(mouse))
				{
					field.setSelected(true);
				}else
					field.setSelected(false);
			}
			
			buttonList = canvasref.getMenuButtons();
			
			for(MenuButton button : buttonList)
			{
				if (button.getBounds().contains(mouse))
				{
					if (button.getButtonType() == MenuButtonType.CONNECT_TO_IP)
					{
						canvasref.setCurrentState(GameState.GAMEPLAY);
						MenuTextField field = canvasref.getTextFieldByID(MenuTextFieldType.SERVER_IP_FIELD);
						canvasref.getGame().setServerIP(field.getText());
						MenuTextField name = canvasref.getTextFieldByID(MenuTextFieldType.USERNAME_FIELD);
						
						try {
							BattleCastleUser user = new BattleCastleUser(name.getText(),InetAddress.getLocalHost(),Game.CLIENT_PORT);
						//	System.out.println(user.getAddress());
							canvasref.getGame().sendUserData(user);
						} catch (UnknownHostException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
			
			break;
		case INPUT_USER_NAME:
			
			menuTextFieldList = canvasref.getMenuTextFields();
			for (MenuTextField field : menuTextFieldList)
			{
				if (field.getBounds().contains(mouse))
				{
					field.setSelected(true);
				}else
					field.setSelected(false);
			}
			
			buttonList = canvasref.getMenuButtons();
			for(MenuButton button : buttonList)
			{
				if (button.getBounds().contains(mouse))
				{
					if (button.getButtonType() == MenuButtonType.CONTINUE_TO_GAME)
					{
						MenuTextField name = canvasref.getTextFieldByID(MenuTextFieldType.USERNAME_FIELD);

						try {
							BattleCastleUser user = new BattleCastleUser(name.getText(),InetAddress.getLocalHost(),Game.CLIENT_PORT);
							//System.out.println(user.getAddress());
							canvasref.getGame().setServerIP(InetAddress.getLocalHost());
							canvasref.getGame().sendUserData(user);
							canvasref.getGame().getMyUser().setConnected(true);
						} catch (UnknownHostException e1) {
							e1.printStackTrace();
						}
						
						if(canvasref.getGame().getHostType() == HostType.SERVER)
						{
							canvasref.setCurrentState(GameState.SELECT_MAP);
						}else
						{
							canvasref.setCurrentState(GameState.GAMEPLAY);
							
						}
						
					}
				}
			}
			
			break;
		case GAMEPLAY:
			
			//handle player mouse clicks
			
			
			break;
		case SELECT_MAP:
			
			buttonList = canvasref.getMenuButtons();
			
			for(MenuButton button : buttonList)
			{
				if (button.getBounds().contains(mouse))
				{
					if (button.getButtonType() == MenuButtonType.SELECT_MAP)
					{
						MapSelectionObject map = (MapSelectionObject) button;
						
						//Send which map 
						canvasref.getGame().sendMapChoice(map.getMapType());
						
						canvasref.setCurrentState(GameState.GAMEPLAY);
					}
				}
			}
			
			break;
		
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouse = e.getPoint();
	}
	
}
