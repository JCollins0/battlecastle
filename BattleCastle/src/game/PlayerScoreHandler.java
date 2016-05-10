package game;

import game.player.Player;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.TreeSet;

import utility.Utility;
import core.constants.ImageFilePaths;

public class PlayerScoreHandler {

	public static final BufferedImage skullEmpty = Utility.loadImage(ImageFilePaths.SKULL_DARK);
	public static final BufferedImage skullFilled = Utility.loadImage(ImageFilePaths.SKULL);
	public static final BufferedImage scoreBanner = Utility.loadImage(ImageFilePaths.SCORE_BANNER);
	public static final BufferedImage crown = Utility.loadImage(ImageFilePaths.CROWN);
	
	public static final int minKillsRequired = 10;
	public static final int startY = 256;
	public static final int crownX = 48;
	public static final int faceX = crownX+96;
	public static final int skullStartX=faceX + 128;
	public static final int spacingBetweenSkulls = 8;
	public static final int spacingBetweenPlayers = 8;
	
	private Game gameRef;
	
	private ArrayList<String> players;
	private ArrayList<Integer> winIndex;
	
	public PlayerScoreHandler(Game gameRef){
		players = new ArrayList<String>();
		winIndex = new ArrayList<Integer>();
		this.gameRef = gameRef;
	}
	
	public void registerPlayer(Player pl)
	{
		if(!players.contains(pl.getUUID()) && !(pl.getUUID().equals("")||pl.getUUID()==null))
		{	
			System.out.println("Player Score Handler Registering: " + pl.getUUID());
			players.add(pl.getUUID());
		
		}
		if(players.size() > 1)
			checkForDupes();
	}
	
	private void checkForDupes()
	{
		players = new ArrayList<String>(new TreeSet<String>(players));
	}
	
	public void render(Graphics g)
	{
		g.drawImage(scoreBanner, faceX, startY-64,840,64,null);
		for(int i = 0; i < players.size(); i++)
		{
			Player player = getPlayer(players.get(i));
			
			
			g.drawImage(gameRef.getPlayerFaceFromPlayer(player),faceX,64*i+startY,64,64,null);
			
			for(int k = 0; k < player.getScore(); k++)
			{
				g.drawImage(skullFilled, 64*(k) + spacingBetweenSkulls * k + skullStartX , i * 64+startY + i * spacingBetweenPlayers, 64,64,null);
				//System.out.println("K: " + k);
			}
			
			for(int s = Math.max(0,player.getScore()); s < minKillsRequired; s++)
			{
				//System.out.println("S: " + s);
				g.drawImage(skullEmpty, 64*(s) + spacingBetweenSkulls * s + skullStartX, i * 64+startY + i * spacingBetweenPlayers, 64,64,null);
			}
		}
		
		if(winIndex != null)
		{
			for(int i = 0; i < winIndex.size(); i++)
				g.drawImage(crown,crownX,64*winIndex.get(i)+startY,64,64,null);
		}
		
	}
	
	public void updateWinIndex()
	{
		 winIndex = getCurrentWinners();
	}
	
	private ArrayList<Integer> getCurrentWinners()
	{
		ArrayList<Integer> temp = new ArrayList<Integer>();
		int maxScore = Integer.MIN_VALUE;
		for(int i = 0; i < players.size(); i++)
		{
			Player p = getPlayer(players.get(i));
			if(p != null)
				if(p.getScore() >= maxScore)
				{
					maxScore = p.getScore();
					//System.out.println(maxScore  + " is the maxScore");
				}
		}
		
		if(maxScore <= 0) return null;
		
		for(int i = 0; i < players.size(); i++)
		{
			Player p = getPlayer(players.get(i));
			if(p.getScore() == maxScore) temp.add(i);
		}
		return temp;
	}
	
	private Player getPlayer(String uuid)
	{
		return gameRef.getPlayerFromUUID(uuid);
	}
	
	public void tick() {}
}
