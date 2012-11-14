package com.ccc.util.sound;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.LineEvent.Type;

import org.springframework.util.Assert;

/**
 * This is a class for various sound operations in java.
 * @author Adam Gibson
 *
 */
public class SoundUtils {
	
	
	
	
	
	
	
	
	/**
	 * Plays the sound from the givne file
	 * @param clipFile the clipe file to play
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws LineUnavailableException
	 * @throws InterruptedException
	 */
	public static void playClip(InputStream clipFile) throws IOException, 
	  UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
	  class AudioListener implements LineListener {
	    private boolean done = false;
	    @Override public synchronized void update(LineEvent event) {
	      Type eventType = event.getType();
	      if (eventType == Type.STOP || eventType == Type.CLOSE) {
	        done = true;
	        notifyAll();
	      }
	    }
	    public synchronized void waitUntilDone() throws InterruptedException {
	      while (!done) { wait(); }
	    }
	  }
	  AudioListener listener = new AudioListener();
	  AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(clipFile);
	  try {
	    Clip clip = AudioSystem.getClip();
	    clip.addLineListener(listener);
	    clip.open(audioInputStream);
	    try {
	      clip.start();
	      listener.waitUntilDone();
	    } finally {
	      clip.close();
	    }
	  } finally {
	    audioInputStream.close();
	  }
	}//end playClip
	
	
	
	/**
	 * Plays the sound from the givne file
	 * @param clipFile the clipe file to play
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws LineUnavailableException
	 * @throws InterruptedException
	 */
	public static void playClip(URL clipFile) throws IOException, 
	  UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
	  class AudioListener implements LineListener {
	    private boolean done = false;
	    @Override public synchronized void update(LineEvent event) {
	      Type eventType = event.getType();
	      if (eventType == Type.STOP || eventType == Type.CLOSE) {
	        done = true;
	        notifyAll();
	      }
	    }
	    public synchronized void waitUntilDone() throws InterruptedException {
	      while (!done) { wait(); }
	    }
	  }
	  AudioListener listener = new AudioListener();
	  AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(clipFile);
	  try {
	    Clip clip = AudioSystem.getClip();
	    clip.addLineListener(listener);
	    clip.open(audioInputStream);
	    try {
	      clip.start();
	      listener.waitUntilDone();
	    } finally {
	      clip.close();
	    }
	  } finally {
	    audioInputStream.close();
	  }
	}//end playClip
	
	

	/**
	 * Plays the sound from the givne file
	 * @param clipFile the clipe file to play
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws LineUnavailableException
	 * @throws InterruptedException
	 */
	public static void playClip(File clipFile) throws IOException, 
	  UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
	  class AudioListener implements LineListener {
	    private boolean done = false;
	    @Override public synchronized void update(LineEvent event) {
	      Type eventType = event.getType();
	      if (eventType == Type.STOP || eventType == Type.CLOSE) {
	        done = true;
	        notifyAll();
	      }
	    }
	    public synchronized void waitUntilDone() throws InterruptedException {
	      while (!done) { wait(); }
	    }
	  }
	  AudioListener listener = new AudioListener();
	  AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(clipFile);
	  try {
	    Clip clip = AudioSystem.getClip();
	    clip.addLineListener(listener);
	    clip.open(audioInputStream);
	    try {
	      clip.start();
	      listener.waitUntilDone();
	    } finally {
	      clip.close();
	    }
	  } finally {
	    audioInputStream.close();
	  }
	}//end playClip
	/**
	 * Plays the sound from the givne file
	 * @param clipFile the clipe file to play
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 * @throws LineUnavailableException
	 * @throws InterruptedException
	 */
	public static void playClip(Class clipFile,String name) throws IOException, 
	  UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
	  class AudioListener implements LineListener {
	    private boolean done = false;
	    @Override public synchronized void update(LineEvent event) {
	      Type eventType = event.getType();
	      if (eventType == Type.STOP || eventType == Type.CLOSE) {
	        done = true;
	        notifyAll();
	      }
	    }
	    public synchronized void waitUntilDone() throws InterruptedException {
	      while (!done) { wait(); }
	    }
	  }
	  AudioListener listener = new AudioListener();
	  InputStream input=clipFile.getResourceAsStream(name);
	  Assert.notNull(input,"Input stream from class was null!");
	  AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(input);
	  try {
	    Clip clip = AudioSystem.getClip();
	    clip.addLineListener(listener);
	    clip.open(audioInputStream);
	    try {
	      clip.start();
	      listener.waitUntilDone();
	    } finally {
	      clip.close();
	    }
	  } finally {
	    audioInputStream.close();
	  }
	}//end playClip
	
	
	public static void main(String[] args) {
		//playSound("SOUND136.WAV");
	}
}//end SoundUtils
