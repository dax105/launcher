package com.jboudny.launcher.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

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
	LogoPanel lp;
	JProgressBar progressBar;
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
	}
	
	public void useLoginPanel(boolean use, Launcher launcher) {
		if(use) {
			if(this.lp != null)
				this.remove(lp);
			
			this.lp = new LogoPanelLogin(launcher);
		} else {
			this.lp = new LogoPanel();
		}
		
		lp.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLACK));
		this.add(lp, BorderLayout.CENTER);
		this.invalidate();
		this.repaint();
	}
	
	public LogoPanel getLogoPanel() {
		return this.lp;
	}
	
	public void initControls() {
		this.progressBar = new JProgressBar(0, 100);
		this.progressBar.setStringPainted(true);
		this.progressBar.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1,
				Color.BLACK));
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
