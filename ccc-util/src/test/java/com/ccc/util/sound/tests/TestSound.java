package com.ccc.util.sound.tests;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import junit.framework.TestCase;

import org.junit.Test;
import org.springframework.util.Assert;

import com.ccc.util.sound.SoundUtils;

public class TestSound  extends TestCase {
	@Test
	public void testSound() throws LineUnavailableException, URISyntaxException, IOException, UnsupportedAudioFileException, InterruptedException {
		URL url=new URI("file:SOUND136.WAV").toURL();
		SoundUtils.playClip(url);
		
	}
	
	@Test
	public void testSoundINputStream() throws LineUnavailableException, URISyntaxException, IOException, UnsupportedAudioFileException, InterruptedException {
		File f= new File("SOUND136.WAV");
		Assert.notNull(f);
		SoundUtils.playClip(f);
	}
}
