package com.ccc.util.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
/**
 * Various utils for network and sockets
 * @author Adam Gibson
 *
 */
public class NetworkUtils {
	/**
	 * This will attempt to guess the local ip, if it fails it will
	 * return null
	 * @return the local ip or null
	 */
	public static String getLocalHostIP() {
		try {
			InetAddress localhost= InetAddress.getLocalHost();
			return localhost.getHostAddress();
		} catch (UnknownHostException e) {
			return null;
		}
	}//end getLocalHostIP
	/**
	 * This will attempt to get the name of localhost
	 * @return the host name for localhost or null on failure
	 */
	public static String getLocalHostName() {
		try {
			InetAddress localhost= InetAddress.getLocalHost();
			return localhost.getHostName();
		} catch (UnknownHostException e) {
			return null;
		}
	}//end getLocalHostName

	/**
	 * This will return whether a port is take on the current system or not.
	 * @param port the string value of the port to test
	 * @return true if the port was taken, false otherwise
	 */
	public static boolean portTaken(String port) {
		int realPort=Integer.valueOf(port);
		try {
			ServerSocket s = new ServerSocket(realPort);
			s.close();
			return false;
		}
		catch(Exception e) {
			return true;
		}
	}//end portTaken
	/**
	 * This will return whether a port is take on the current system or not.
	 * @param port the integer value of the port to test
	 * @return true if the port was taken, false otherwise
	 */
	public static boolean portTaken(int port) {
		try {
			ServerSocket s = new ServerSocket(port);
			s.close();
			return false;
		}
		catch(Exception e) {
			return true;
		}
	}

	/**
	 * This returns the mac address of the local system.
	 * @return the mac address of the local system.
	 */
	public static String getMacAddress() {
		InetAddress ip=null;
		try {

			ip = InetAddress.getLocalHost();
			System.out.println("Current IP address : " + ip.getHostAddress());

			NetworkInterface network = NetworkInterface.getByInetAddress(ip);

			byte[] mac = network.getHardwareAddress();

			System.out.print("Current MAC address : ");

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));		
			}
			return sb.toString();

		} catch (UnknownHostException e1) {

			e1.printStackTrace();

		} catch (SocketException e2){

			e2.printStackTrace();

		}
		return null;

	}

}
