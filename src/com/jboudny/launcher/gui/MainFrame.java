package com.jboudny.launcher.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicProgressBarUI;

import com.jboudny.launcher.Launcher;
import com.jboudny.launcher.localization.LocalizationHelper;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = -6724401659166549898L;

	BorderLayout bl = new BorderLayout();
	LogoPanelLogin lp;
	JProgressBar progressBar;
	Point mouseDownCompCoords;
	MainFrame frame;
	
	public Point pos = new Point(0, 0);
	
	public MainFrame() {
		super(Launcher.APP_NAME + " launcher v" + Launcher.version);
		
		UIManager.put("ProgressBar.background", Color.GRAY);
		UIManager.put("ProgressBar.foreground", Color.DARK_GRAY);
		UIManager.put("ProgressBar.selectionBackground", Color.WHITE);
		UIManager.put("ProgressBar.selectionForeground", Color.WHITE);
		UIManager.put("ProgressBar.repaintInterval", new Integer(20));
		
		this.setUndecorated(true);
		this.setSize(600, 320);
		this.setPreferredSize(new Dimension(600, 320));
		this.setLocationRelativeTo(null);
		this.setBackground(Color.WHITE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(bl);
		
		frame = this;
		
		this.addMouseListener(new MouseListener(){
		    public void mouseReleased(MouseEvent e) {
		    mouseDownCompCoords = null;
		    }
		    public void mousePressed(MouseEvent e) {
		    mouseDownCompCoords = e.getPoint();
		    lp.mousePress(pos);
		    }
		    public void mouseExited(MouseEvent e) {
		    }
		    public void mouseEntered(MouseEvent e) {
		    }
		    public void mouseClicked(MouseEvent e) {
		    }
		    });
		     
		    this.addMouseMotionListener(new MouseMotionListener(){
		    public void mouseMoved(MouseEvent e) {
				pos.x = e.getX();
				pos.y = e.getY();
				
				lp.repaint();
		    }
		     
		    public void mouseDragged(MouseEvent e) {
		    Point currCoords = e.getLocationOnScreen();
		    frame.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
		    }
		    });
	}
	
	public void useLoginPanel(Launcher launcher, String user, String password) {
		this.lp = new LogoPanelLogin(launcher, user, password, this);
		
		lp.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.BLACK));
		this.add(lp, BorderLayout.CENTER);
		this.invalidate();
		this.repaint();
	}
	
	public LogoPanel getLogoPanel() {
		return this.lp;
	}
	
	public void setControlsEnabled(boolean enabled) {
		if(enabled)
			lp.enableControls();
		else
			lp.disableControls();
	}
	
	public void initControls() {
		this.setControlsEnabled(false);
		this.progressBar = new JProgressBar(0, 100);
		this.progressBar.setStringPainted(true);
		this.progressBar.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.BLACK));
		this.progressBar.setUI(new BasicProgressBarUI());
		this.progressBar.setValue(0);
		this.progressBar.setFont(lp.infoFont);	
		this.add(this.progressBar, BorderLayout.SOUTH);		
		this.progressBar.setString(LocalizationHelper.getBestLocalization().lookingForUpdates());
		this.progressBar.setIndeterminate(true);
	}

	public void setProgressBarText(String text) {
		this.progressBar.setString(text);
	}

	public JProgressBar getProgressBar() {
		return this.progressBar;
	}

	public void setBarText(String errorText) {
		this.getProgressBar().setIndeterminate(false);
		this.setProgressBarText(errorText);
		this.getProgressBar().setValue(100);
	}
}
