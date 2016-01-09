package core;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import utility.AudioHandler;
import utility.AudioHandler.SOUND;
import core.menu_object.MapSelectionObject;
import core.menu_object.MenuButton;
import core.menu_object.MenuButtonType;
import core.menu_object.MenuSlider;
import core.menu_object.MenuTextField;
import core.menu_object.MenuTextFieldType;
import core.menu_object.ServerChoice;
import core.menu_object.ServerSelectionBox;
import editor.EditorFrame;
import game.Game;
import game.player.BattleCastleUser;

public class MouseHandler implements MouseMotionListener, MouseListener, MouseWheelListener {

	private BattleCastleCanvas canvasref;
	public static Point mouse;
	
	public MouseHandler(BattleCastleCanvas canvasref){
		this.canvasref = canvasref;
		mouse = new Point(-1,-1);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println(e.getPoint());
		AudioHandler.playSound(SOUND.MENU_SELECT);
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
					}else if(button.getButtonType() == MenuButtonType.LEVEL_EDITOR)
					{
						EditorFrame frame = new EditorFrame();
					}else if(button.getButtonType() == MenuButtonType.INFO_BUTTON)
					{
						canvasref.setCurrentState(GameState.INFO);
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
						canvasref.getGame().startClient();
						canvasref.getGame().connectToServer();

						MenuTextField name = canvasref.getTextFieldByID(MenuTextFieldType.USERNAME_FIELD);
						try {
							BattleCastleUser user = new BattleCastleUser(name.getText(),InetAddress.getLocalHost(),Game.CLIENT_PORT);
							canvasref.getGame().setMyUserUUID(user.getUUID());
							canvasref.getGame().sendUserData(user);
							
						} catch (UnknownHostException e1) {
							e1.printStackTrace();
						}
					}else if (button.getButtonType() == MenuButtonType.BACK_TO_MENU)
					{
						canvasref.setCurrentState(GameState.MAIN_MENU);
						if(canvasref.getGame() != null)
						{
							canvasref.getGame().stopServer();
							canvasref.getGame().stopClient();
						}
					
					}else if(button.getButtonType() == MenuButtonType.REFRESH_LAN_SERVERS)
					{
						if(!canvasref.isSearchingForServers())
							canvasref.searchForLanServers();
					}
				}
			}
			
			ServerSelectionBox selectionBox = canvasref.getServerSelectionBox();
			ServerChoice choice = selectionBox.getServerChoice(mouse);
			if(choice != null)
			{
				MenuTextField serverIPField = null;
				for(int i = 0; i < menuTextFieldList.size(); i++)
					if(menuTextFieldList.get(i).getID() == MenuTextFieldType.SERVER_IP_FIELD)
						serverIPField = menuTextFieldList.get(i);
				
				if(serverIPField != null)
					serverIPField.setText(choice.getAddress());
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
							canvasref.getGame().setMyUserUUID(user.getUUID());
							//System.out.println(user.getAddress());
							canvasref.getGame().setServerIP(InetAddress.getLocalHost());
							canvasref.getGame().sendUserData(user);
							
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
					else if (button.getButtonType() == MenuButtonType.BACK_TO_MENU)
					{
						canvasref.setCurrentState(GameState.MAIN_MENU);
						if(canvasref.getGame() != null)
						{
							canvasref.getGame().stopServer();
							canvasref.getGame().stopClient();
							canvasref.clearFaces();
						}
					
					}
				}
			}
			
			break;
		case GAMEPLAY:
			
			//handle player mouse clicks
			canvasref.getGame().launchArrow();
			
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
						canvasref.getGame().sendMapChoice(map.getMapType(), map.getFileName());
						//JOptionPane.showMessageDialog(null, map.getMapType() == MapType.CUSTOM);
						canvasref.setCurrentState(GameState.GAMEPLAY);
					}
					else if (button.getButtonType() == MenuButtonType.BACK_TO_MENU)
					{
						canvasref.setCurrentState(GameState.MAIN_MENU);
						if(canvasref.getGame() != null)
						{
							canvasref.getGame().stopServer();
							canvasref.getGame().stopClient();
							canvasref.clearFaces();
						}
					
					}
				}
			}
			
			break;
		case INFO:
			
			buttonList = canvasref.getMenuButtons();
			
			for(MenuButton button : buttonList)
			{
				if (button.getBounds().contains(mouse))
				{
					if (button.getButtonType() == MenuButtonType.BACK_TO_MENU)
					{
						canvasref.setCurrentState(GameState.MAIN_MENU);					
					}
				}
			}
			
			ArrayList<MenuSlider> sliderList = canvasref.getSliderBars();
			for(MenuSlider slider : sliderList)
			{
				if(slider.isHoverOver(mouse))
				{
					slider.setSelected(true);
				}else
					slider.setSelected(false);
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
		mouse = e.getPoint();
		switch( canvasref.getCurrentState())
		{
		case GAMEPLAY:
			break;
		case INFO:
			
			ArrayList<MenuSlider> sliderList = canvasref.getSliderBars();
			for(MenuSlider slider : sliderList)
			{
				if(slider.isSelected())
				{
					slider.update(mouse.x);
				}
			}
			
			break;
		case INPUT_USER_NAME:
			break;
		case JOIN_SERVER:
			break;
		case MAIN_MENU:
			break;
		case SELECT_MAP:
			break;
		default:
			break;
		
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouse = e.getPoint();
		switch( canvasref.getCurrentState())
		{
		case GAMEPLAY:
			canvasref.getGame().getMyPlayer().setMouseLocation(mouse);
			break;
		case SELECT_MAP:
			
		case INPUT_USER_NAME:
		case JOIN_SERVER:
		case MAIN_MENU:
		case INFO:	
			ArrayList<MenuButton> buttonList = canvasref.getMenuButtons();
			for(MenuButton button : buttonList)
			{
				if(button.isVisibleAtState(canvasref.getCurrentState()))
					button.isHoverOver(mouse);
			}
			
			break;
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		switch(canvasref.getCurrentState())
		{
		case GAMEPLAY:
			break;
		case INPUT_USER_NAME:
			break;
		case JOIN_SERVER:
			
			ServerSelectionBox serverSelectionBox = canvasref.getServerSelectionBox();
			serverSelectionBox.updatePositions(e.getWheelRotation());
			System.out.println("WHEEL ROTATION: " + e.getWheelRotation());
			
			break;
		case MAIN_MENU:
			break;
		case SELECT_MAP:
			break;
		case INFO:
			break;		
		}
	}
	
}
