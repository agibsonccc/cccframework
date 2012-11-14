package com.ccc.util.swing.popup;

public class JFramePopupExample extends JFramePopupImplementation{
	
	JFramePopupExample(){
		super("yourmom",2);
		this.setHeight(200);
		this.setWidth(500);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String[]args) {
		JFramePopupExample t = new JFramePopupExample();
		t.popup();
	}
	
}
