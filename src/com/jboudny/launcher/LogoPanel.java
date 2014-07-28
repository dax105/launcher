package com.jboudny.launcher;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

public class LogoPanel extends JPanel {

	public String logoText = "Order of the stone";

	public String launcherVersion = "" + Launcher.version;
	public String appVersion = "N/A";

	private static final long serialVersionUID = 1L;

	public Font logoFont = null;
	public Font infoFont = null;

	int steps = 48;
	int step = 0;

	public LogoPanel() {
		try {
			logoFont = Font.createFont(Font.TRUETYPE_FONT,
					LogoPanel.class.getResourceAsStream("font.ttf"));
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		logoFont = logoFont.deriveFont(62f);
		infoFont = logoFont.deriveFont(12f);

		float timeSec = 0.8f;
		float steptime = timeSec / (float) steps;

		Timer t = new Timer();

		t.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {

				step++;
				repaint();

				if (step >= steps) {
					cancel();
				}

			}

		}, 0, (int) (steptime * 1000f));

		setBackground(Color.WHITE);
	}

	public float calculateExpScale(float x) {
		x = 1 - x;

		float v = (float) ((Math.exp(2.77258872 * x) - 1f) / 15f);

		return 1 - v;
	}

	@Override
	public void paint(Graphics gd) {
		super.paint(gd);

		Graphics2D g = (Graphics2D) gd;

		g.setFont(logoFont);

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		FontMetrics fml = g.getFontMetrics(logoFont);

		float stepMult = step / (float) steps;
		float stepMultExp = calculateExpScale(stepMult);

		int x = this.getWidth() / 2 - fml.stringWidth(logoText) / 2;
		int y = (int) (this.getHeight() / 2 + fml.getHeight() / 4 - (70 * (1 - stepMultExp)));

		g.setColor(new Color(0, 0, 0, stepMult));

		g.drawString(logoText, x, y);

		//g.setColor(Color.BLACK);

		g.setFont(infoFont);

		FontMetrics fmi = g.getFontMetrics(infoFont);

		String lv = "Launcher version: " + launcherVersion;
		String av = "App version: " + appVersion;

		g.drawString(lv, 6 - (100 * (1 - stepMultExp)),
				this.getHeight() - 6);
		g.drawString(av, this.getWidth() - fmi.stringWidth(av) - 6 + (100 * (1 - stepMultExp)),
				this.getHeight() - 6);

	}

}
