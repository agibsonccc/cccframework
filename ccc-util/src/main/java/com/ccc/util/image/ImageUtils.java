package com.ccc.util.image;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;

import com.ccc.util.filesystem.FileMoverUtil;
import com.ccc.util.filesystem.PathManipulator;
/**
 * This is a class that handles various operations on images.
 * @author Adam Gibson
 *
 */
public class ImageUtils {

	/**
	 * This will convert an icon to an image.
	 * @param icon the icon to convert
	 * @return an equivalent image for the given icon.
	 */
	public static Image iconToImage(Icon icon) {
		if (icon instanceof ImageIcon) {
			return ((ImageIcon)icon).getImage();
		} else {
			int w = icon.getIconWidth();
			int h = icon.getIconHeight();
			GraphicsEnvironment ge =
					GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gd = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gd.getDefaultConfiguration();
			BufferedImage image = gc.createCompatibleImage(w, h);
			Graphics2D g = image.createGraphics();
			icon.paintIcon(null, g, 0, 0);
			g.dispose();
			return image;
		}
	}//end iconToImage 

	/**
	 * This will return the image format based on the passed in string
	 * @param format the format to test for
	 * @return the image format for the given string or null if it doesn't exist
	 */
	public static ImageFormat formatFromString(File file) {
		String format=PathManipulator.getFormat(file);
		return formatFromString(format);
	}//end formatFromString

	/**
	 * This will return the image format based on the passed in string
	 * @param format the format to test for
	 * @return the image format for the given string or null if it doesn't exist
	 */
	public static ImageFormat formatFromString(String format) {
		int dotIndex=format.indexOf('.');
		if(dotIndex >=0) {
			format=format.substring(dotIndex+1);
		}
		for(ImageFormat image : formats) {
			if(image.name.toLowerCase().contains(format.toLowerCase()))
				return image;
		}
		return null;
	}
	/**
	 * THis will convert the given image and write to the specified path
	 * @param fileName the name of the file to write
	 * @param newFile the new file to write to
	 * @return true if the file was written, false otherwise
	 * @throws ImageReadException
	 * @throws IOException
	 * @throws ImageWriteException
	 */
	public static boolean convertImage(String fileName,String newFile) throws ImageReadException, IOException, ImageWriteException {
		File f = new File(fileName);
		BufferedImage from=Sanselan.getBufferedImage(f);
		String toFormat=PathManipulator.getFormat(newFile);
		File toFile  = new File(newFile);
		FileMoverUtil.createFile(toFile,false);
		ImageFormat getFormat=formatFromString(toFormat);
		//no image format found
		if(getFormat==null)
			return false;
		Sanselan.writeImage(from, toFile, getFormat,null);
		return true;
	}//end convertImage
	
	public static ImageFormat[] formats=ImageFormat.getAllFormats();
}//end ImageUtils
