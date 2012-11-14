package com.ccc.util.objectserialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;
/**
 * Converts objects to byte arrays 
 * and vice versa.
 * @author Adam Gibson
 *
 */
public class ObjectConvertor {
	/**
	 * Converts an object to a byte array.
	 * @param obj the object to convert
	 * @return
	 * @throws IOException
	 */
	public static byte[] serialize(Object obj) throws IOException {
	
		 byte[] bytes = null;
		  ByteArrayOutputStream bos = new ByteArrayOutputStream();
		  try {
		    ObjectOutputStream oos = new ObjectOutputStream(bos); 
		    oos.writeObject(obj);
		    oos.flush(); 
		    oos.close(); 
		    bos.close();
		    bytes = bos.toByteArray ();
		  }
		  catch (IOException ex) {
			  ex.printStackTrace();
		  }
		  return bytes;
	}
	/**
	 * Convert a byte array to an object
	 * @param bytes the bytes to convert
	 * @return the object for this byte array
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		 Object obj = null;
		  try {
		    ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
		    ObjectInputStream ois = new ObjectInputStream (bis);
		    obj = ois.readObject();
		  }
		  catch (IOException ex) {
			  ex.printStackTrace();
		  }
		  catch (ClassNotFoundException ex) {
		   ex.printStackTrace();
		  }
		  return obj;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		String s = "Hello";
		byte[] bytes=serialize(s.getBytes());
		Object o=deserialize(bytes);
		System.out.println(new String((byte[])o));
	}
}



class Wrapper implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -13750141625418361L;

	public Wrapper(Object value) {
		this.value=value;
	}
	public Object getValue() {
		return value;
	}

	private Object value;
}