package com.jboudny.launcher.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.plaf.basic.BasicTextFieldUI;

import com.jboudny.launcher.Launcher;
import com.jboudny.launcher.localization.*;

public class LogoPanelLogin extends LogoPanel {

	private static final long serialVersionUID = 1L;
	private final JTextField username;
	private final JPasswordField password;
	private final JButton loginButton;
	private final JCheckBox checkBox;
	private final JLabel loginText;
	
	public LogoPanelLogin(final Launcher launcher) {
		super();
		this.login = true;
		
		UIManager.put("CheckBox.focus", new Color(0,0,0,0));
		UIManager.put("CheckBox.select", Color.GRAY);
		UIManager.put("CheckBox.border", Color.GRAY);
		UIManager.put("CheckBox.interiorBackground", Color.RED);
		UIManager.put("Button.focus", new Color(0,0,0,0));
		UIManager.put("Button.select", Color.GRAY);
		
		GridBagConstraints c = new GridBagConstraints();
		
		this.username = new JTextField(20);
		new GhostText(username, " " + this.local.userName());
		
		this.password = new JPasswordField(20);
		new GhostText(password, " " + this.local.password());
		
		this.loginButton = new JButton(this.local.loginButton());
		this.checkBox = new JCheckBox(this.local.rememberCredentials(), false);
		
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
				
				if (checkBox.isSelected()) {
					launcher.getConfig().username = username.getText();
					launcher.getConfig().password = password.getText();
					launcher.getConfig().save(true);
				}
				
				if (!launcher.doLoginAndRun(username.getText(), password.getText())) {
					loginButton.setText("Login");
					loginButton.setEnabled(true);
					
					username.setEnabled(true);
					password.setEnabled(true);
					checkBox.setEnabled(true);
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
	
	@Override
	public void onPaint(Graphics2D g) {
		super.onPaint(g);
		
		//TODO Status info
		
	}

}
