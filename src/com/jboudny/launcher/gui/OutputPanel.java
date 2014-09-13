package com.jboudny.launcher.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.PrintStream;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.basic.BasicScrollPaneUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.DefaultCaret;

import com.jboudny.launcher.Launcher;

public class OutputPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextArea textArea = new JTextArea();
	private TextAreaOutputStream taOutputStream = new TextAreaOutputStream(
			textArea);

	public JTextArea getTextArea() {
		return this.textArea;
	}

	public OutputPanel() {
		setLayout(new BorderLayout());
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setUI(new BasicTextAreaUI());
		textArea.setBorder(BorderFactory.createEmptyBorder());
		textArea.setBackground(Color.WHITE);
		textArea.setForeground(Color.BLACK);


		textArea.setFont(Launcher.font.deriveFont(Font.PLAIN, 10));

		JScrollPane jsp = null;

		try {
			LookAndFeel previousLF = UIManager.getLookAndFeel();
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			jsp = new JScrollPane(textArea,
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			UIManager.setLookAndFeel(previousLF);
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		} catch(InstantiationException e) {
			e.printStackTrace();
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		jsp.setUI(new BasicScrollPaneUI());
		jsp.setBorder(BorderFactory.createEmptyBorder());
		jsp.setBackground(Color.WHITE);
		jsp.setForeground(Color.BLACK);

		add(jsp);
		System.setOut(new PrintStream(taOutputStream));
		System.setErr(new PrintStream(taOutputStream));
		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}

}
