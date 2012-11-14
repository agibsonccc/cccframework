package com.ccc.util.swing.popup;

import java.awt.BorderLayout;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;


public class ProgressDialog extends JDialog  {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ProgressDialog(JFrame parentFrame,int min,int max,Performer performer) {
		this.parentFrame=parentFrame;
		this.min=min;
		this.max=max;
		this.performer=performer;
		if(performer!=null)
			statusLabel = new JLabel(performer.status());
		jl = new JLabel("Count: " + 0);
		dpb.setStringPainted(true);
		ProgressDialog.runningDialogs.add(this);
	}



	public Performer getPerformer() {
		return performer;
	}



	public void setPerformer(Performer performer) {
		this.performer = performer;
		if(performer!=null)
			statusLabel = new JLabel(performer.status());
	}



	public  void showGUI() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				add(BorderLayout.CENTER, dpb);
				add(BorderLayout.NORTH, statusLabel);
				add(BorderLayout.SOUTH,jl);

				setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				setSize(300, 75);
				setLocationRelativeTo(parentFrame);
				dpb.setVisible(true);
				statusLabel.setVisible(true);
				setVisible(true);
				validate();

			}
		});
		t.start();

		//for (int i = 0; i <= 500; i++) {
		while((performer.progressSoFar() < 100 || !performer.doneYet()) && !stop) {
			jl.setText("Count : " + performer.progressSoFar());
			dpb.setValue(performer.progressSoFar());
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					dpb.setString(String.valueOf(dpb.getValue()));

					dpb.updateUI();
				}
			});
			statusLabel.setText(performer.status());
			validate();
			if(performer.progressSoFar() == 100 || performer.doneYet() && !stop){
				setVisible(false);
				parentFrame.setVisible(false);
				parentFrame.dispose();
				this.dispose();
				break;
			}
			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		//}
	}
	public void stop() {
		stop=true;
		parentFrame.setVisible(false);
		parentFrame.dispose();
		dpb.setVisible(false);
		this.dispose();
		dispose();
	}

	private int min;
	private int max;
	private JProgressBar dpb = new JProgressBar(min,max);
	private JFrame parentFrame;
	private JLabel jl;
	private Performer performer;
	private JLabel statusLabel;
	private boolean stop=false;
	public static List<ProgressDialog> runningDialogs;
	static {
		if(runningDialogs==null) runningDialogs = new CopyOnWriteArrayList<ProgressDialog>();
	}
}
