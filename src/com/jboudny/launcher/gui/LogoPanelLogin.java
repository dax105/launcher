package com.jboudny.launcher.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.plaf.basic.BasicTextFieldUI;

import com.jboudny.launcher.Launcher;
import com.jboudny.launcher.localization.LocalizationHelper;

public class LogoPanelLogin extends LogoPanel {

	private static final long serialVersionUID = 1L;
	private final JTextField username;
	private final JPasswordField password;
	private final JButton loginButton;
	private final JCheckBox checkBox;
	private final JLabel loginText;
	
	private MainFrame frame;
	
	public LogoPanelLogin(final Launcher launcher, String savedUser, String savedPwd, MainFrame frame) {
		super();
		this.login = true;
		
		this.frame = frame;
		
		UIManager.put("CheckBox.focus", new Color(0,0,0,0));
		UIManager.put("CheckBox.select", Color.GRAY);
		UIManager.put("CheckBox.border", Color.GRAY);
		UIManager.put("CheckBox.interiorBackground", Color.RED);
		UIManager.put("Button.focus", new Color(0,0,0,0));
		UIManager.put("Button.select", Color.GRAY);
		
		GridBagConstraints c = new GridBagConstraints();
		this.username = new JTextField(20);
		this.username.setText(savedUser);
		new GhostText(username, " " + this.local.userName());
		
		this.password = new JPasswordField(20);
		this.password.setText(savedPwd);
		new GhostText(password, " " + this.local.password());
		
		this.loginButton = new JButton(this.local.loginButton());
		this.checkBox = new JCheckBox(this.local.rememberCredentials(), launcher.isSaved());
		
		final GridBagLayout layout = new GridBagLayout();
		
		this.loginText = new JLabel(this.local.loggingIn());
		this.loginText.setFont(this.infoFont);
		loginText.setVisible(false);
		
		this.loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//loginText.setVisible(true);
				
				loginButton.setText(local.loggingIn());
				loginButton.setEnabled(false);
				
				username.setEnabled(false);
				password.setEnabled(false);
				checkBox.setEnabled(false);
							
				paint(getGraphics());
				
				if (!launcher.doLoginAndRun(username.getText(),new String(password.getPassword()))) {
					loginButton.setText(LocalizationHelper.getBestLocalization().loginButton());
					loginButton.setEnabled(true);
					
					username.setEnabled(true);
					password.setEnabled(true);
					checkBox.setEnabled(true);
				} else {
					if (checkBox.isSelected()) {
						launcher.getConfig().username = username.getText();
						launcher.getConfig().password = new String(password.getPassword());
						launcher.getConfig().save(true);
					} else {
						launcher.getConfig().username = "";
						launcher.getConfig().password = "";
						launcher.getConfig().save(false);
					}
				}
				
			}
		});
		
		this.setLayout(new BorderLayout());
		
		c.fill = GridBagConstraints.HORIZONTAL;
		
		this.setLayout(layout);
		
		c.anchor = GridBagConstraints.CENTER;
		c.gridwidth = 1;
		c.gridy = 0;
		c.insets = new Insets(50,0,0,0);
		c.gridy = 0;
	    c.weightx = 0.0;
	    c.weighty = 0.0;
		username.setPreferredSize(new Dimension(300, 22));
		this.add(username, c);
		c.insets = new Insets(3,0,0,0);
		c.gridy = 1;
		password.setPreferredSize(new Dimension(300, 22));
		this.add(password, c);
		c.gridy = 2;
		c.insets = new Insets(3,0,0,0);
		loginButton.setPreferredSize(new Dimension(300, 22));
		this.add(loginButton, c);
		c.gridy = 3;
		checkBox.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		checkBox.setPreferredSize(new Dimension(300, 20));
		checkBox.setBackground(Color.WHITE);
		checkBox.setFont(this.infoFont);
		this.add(checkBox, c);
		c.gridy = 3;
		c.gridx = 0;
		c.insets = new Insets(0,0,0,0);
		loginText.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		this.add(loginText, c);
		
		//loginButton.setUI(new BasicButtonUI());
		
		username.setUI(new BasicTextFieldUI());
		username.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		username.setFont(this.infoFont);
		
		password.setUI(new BasicPasswordFieldUI());
		password.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		password.setFont(this.infoFont);
		
		loginButton.setBackground(Color.LIGHT_GRAY);
		loginButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		loginButton.setFont(this.infoFont);
		
		username.setFocusable(false);
		username.setFocusable(true);

	}
	
	public void setButtonEnabled(boolean enable) {
		this.loginButton.setEnabled(enable);
	}
	
	@Override
	public void disableControls() {
		this.loginButton.setEnabled(false);
	}
	
	@Override
	public void enableControls() {
		this.loginButton.setEnabled(true);
	}
	
	public int offset = 10;
	public int size = 8;
	
	@Override
	public void onPaint(Graphics2D g) {
		super.onPaint(g);
		
		int w = this.getWidth();
		
		boolean closeSelected = this.isCloseButtonSelected(this.frame.pos);
		boolean minimizeSelected = this.isMinimizeButtonSelected(this.frame.pos);
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setStroke(new BasicStroke(1.4f));
		
		g.setColor(closeSelected ? Color.GRAY : Color.LIGHT_GRAY);
		g.draw(new Line2D.Float(w-offset-size, offset+size-1, w-offset, offset-1));
		g.draw(new Line2D.Float(w-offset, offset+size-1, w-offset-size, offset-1));
		
		g.setColor(minimizeSelected ? Color.GRAY : Color.LIGHT_GRAY);
		g.draw(new Line2D.Float(w-offset*2-size*2, offset+size-1, w-offset*2-size, offset+size-1));
		
	}
	
	private boolean isCloseButtonSelected(Point pos) {
		Rectangle r = new Rectangle(this.getWidth()-offset-size, offset-1, size+1, size+1);
		return r.contains(pos);
	}
	
	private boolean isMinimizeButtonSelected(Point pos) {
		Rectangle r = new Rectangle(this.getWidth()-offset*2-size*2, offset-1, size+1, size+1);
		return r.contains(pos);
	}
	
	public void mousePress(Point pos) {
		if(isCloseButtonSelected(pos)) {
			System.exit(0);
		} else if (isMinimizeButtonSelected(pos)) {
			this.frame.setState(JFrame.ICONIFIED);
		}
	}

}
