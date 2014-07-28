package com.jboudny.launcher.gui;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

public class DebugFrame extends JFrame {
	private static final long serialVersionUID = -5216019053132923914L;

	OutputPanel outPanel;
	
	public DebugFrame() {
		super("Output");
		
		outPanel = new OutputPanel();
		outPanel.setBorder(BorderFactory.createEmptyBorder());
		
		this.add(outPanel);
		this.setSize(600, 320);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
	}
}
