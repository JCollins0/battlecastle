package utility;

import java.util.ArrayList;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;

import core.constants.AudioFilePaths;

public class AudioHandler {

	
	public enum SOUND
	{
		MENU_SELECT,
		MENU_MUSIC
	}
	
	private static Clip[] gameSounds;
	private static int numberOfSoundClips = SOUND.values().length;
	
	public static boolean muted;
	
	public AudioHandler()
	{
		muted = false;
		
		loadSounds();
		init();
		//loopSound(SOUND.MENU_MUSIC);
	}
	
	private static void loadSounds()
	{
		gameSounds = new Clip[numberOfSoundClips];
		gameSounds[SOUND.MENU_SELECT.ordinal()] = Utility.loadAudio(AudioFilePaths.MENU_SELECTED);
		gameSounds[SOUND.MENU_MUSIC.ordinal()] = Utility.loadAudio(AudioFilePaths.MENU_MUSIC);
	}
		
	public static void playSound(SOUND sound)
	{
		if(sound.ordinal() >= gameSounds.length || muted)
			return;
		gameSounds[sound.ordinal()].setFramePosition(0);
		gameSounds[sound.ordinal()].start();
	}
	
	public static void loopSound(SOUND sound)
	{
		if(sound.ordinal() >= gameSounds.length || muted)
			return;
		
		gameSounds[sound.ordinal()].loop(Clip.LOOP_CONTINUOUSLY);;
	}
	
	public static void stopSound(SOUND sound)
	{
		if(sound.ordinal() >= gameSounds.length)
			return;
		
		gameSounds[sound.ordinal()].stop();
	}
	
	public static void stopAllSound()
	{
		for(int i = 0; i < gameSounds.length; i++)
		{
			gameSounds[i].stop();
		}
	}
	
	/*
	 * Volume Control stuff i found online
	 */
	/**
	 * 
	 * @param value (a number between 0 and 1)
	 */
	
	private static ArrayList<Port> speakerPortsWithVolumeControl = new ArrayList<Port>();
	
	private void init()
	{
		try {
			Mixer.Info[] infos = AudioSystem.getMixerInfo(); 
			for (Mixer.Info info: infos) 
			{ 
				Mixer mixer = AudioSystem.getMixer(info); 
				if (mixer.isLineSupported(Port.Info.SPEAKER)) 
				{ 
					Port port;

					port = (Port)mixer.getLine(Port.Info.SPEAKER);

					port.open(); 
					if (port.isControlSupported(FloatControl.Type.VOLUME)) 
					{ 
						FloatControl volume = (FloatControl)port.getControl(FloatControl.Type.VOLUME);
//						System.out.println(info); 
//						System.out.println("- " + Port.Info.SPEAKER); 
//						System.out.println("  - " + volume); 
//					
						if(!speakerPortsWithVolumeControl.contains(port))
							speakerPortsWithVolumeControl.add(port);
					} 
					port.close(); 
				} 
				
			} 
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} 
	}
	
	public static void setVolume(float volumeLevel)
	{
		//System.out.println(speakerPortsWithVolumeControl.size());
		try {
			
			for(int i = 0; i < speakerPortsWithVolumeControl.size() ;i++)
			{
				Port port = speakerPortsWithVolumeControl.get(i);
				port.open();
				if(port.isControlSupported(FloatControl.Type.VOLUME))
				{
					FloatControl volume = (FloatControl)port.getControl(FloatControl.Type.VOLUME);
					volume.setValue(volumeLevel);
				}
				port.close();

			}
		} catch (LineUnavailableException e) {
			return;
		}
	}
	
	public static float getVolume()
	{
		try {

			for(int i = 0; i < speakerPortsWithVolumeControl.size() ;i++)
			{
				Port port = speakerPortsWithVolumeControl.get(i);
				port.open();
				if(port.isControlSupported(FloatControl.Type.VOLUME))
				{
					FloatControl volume = (FloatControl)port.getControl(FloatControl.Type.VOLUME);
					return volume.getValue();
				}
				port.close();

			}
		} catch (LineUnavailableException e) {
			return 0.0f;
		}
		
		return 0.0f;
	}
	
	
	
}