package com.jboudny.launcher.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicCheckBoxUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.plaf.metal.MetalCheckBoxUI;
import javax.swing.plaf.metal.MetalIconFactory;

import com.jboudny.launcher.Launcher;
import com.sun.java.swing.plaf.windows.WindowsCheckBoxUI;

public class LogoPanelLogin extends LogoPanel {

	private static final long serialVersionUID = 1L;
	
	private Launcher launcher;
	
	private JTextField username;
	private JPasswordField password;
	private JButton loginButton;
	private JCheckBox checkBox;
	
	
	
	public LogoPanelLogin(Launcher launcher) {
		super();
		this.login = true;
		this.launcher = launcher;
		
		UIManager.put("CheckBox.focus", new Color(0,0,0,0));
		UIManager.put("CheckBox.select", Color.GRAY);
		UIManager.put("CheckBox.border", Color.GRAY);
		UIManager.put("CheckBox.interiorBackground", Color.RED);
		UIManager.put("Button.focus", new Color(0,0,0,0));
		UIManager.put("Button.select", Color.GRAY);
		
		this.username = new JTextField(20);
		GhostText gu = new GhostText(username, " Username");
		
		this.password = new JPasswordField(20);
		GhostText gp = new GhostText(password, " Password");
		
		this.loginButton = new JButton("Login");
		this.checkBox = new JCheckBox("Remember account", false);
		
		this.loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Call something in launcher to proceed
			}
		});
		
		this.setLayout(new BorderLayout());
		
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
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
		c.insets = new Insets(0,0,0,10);
		
		checkBox.setAlignmentY(JComponent.CENTER_ALIGNMENT);
		checkBox.setPreferredSize(new Dimension(300, 20));
		checkBox.setBackground(Color.WHITE);
		
		//loginButton.setUI(new BasicButtonUI());
		
		username.setUI(new BasicTextFieldUI());
		username.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		password.setUI(new BasicPasswordFieldUI());
		password.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		loginButton.setBackground(Color.LIGHT_GRAY);
		loginButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		this.add(checkBox, c);
		
		username.setFocusable(false);
		username.setFocusable(true);

	}
	
	@Override
	public void onPaint(Graphics2D g) {
		super.onPaint(g);
	}

}
