package com.jboudny.launcher.gui;

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

import com.jboudny.launcher.Launcher;
import com.jboudny.launcher.Version;
import com.jboudny.launcher.localization.*;

public class LogoPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public Font logoFont = null;
	public Font infoFont = null;

	int steps = 48;
	int step = 0;
	
	boolean login;
	
	protected ILocalization local;

	public LogoPanel() {
		this.local = LocalizationHelper.getBestLocalization();
		
		try {
			logoFont = Font.createFont(Font.TRUETYPE_FONT,
					Thread.currentThread().getContextClassLoader().getResourceAsStream("com/jboudny/launcher/font.ttf"));
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		logoFont = logoFont.deriveFont(62f);
		infoFont = logoFont.deriveFont(12f);
		
		Launcher.font = infoFont;

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
		login = false;
	}

	public float calculateExpScale(float x) {
		x = 1 - x;

		float v = (float) ((Math.exp(2.77258872 * x) - 1f) / 15f);

		return 1 - v;
	}
	
	public void disableControls() {
	}
	
	public void enableControls() {
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

		int x = this.getWidth() / 2 - fml.stringWidth(Launcher.APP_NAME) / 2;
		
		int cy = 0;
		
		if (!login) {
			cy = this.getHeight() / 2;
		} else {
			cy = this.getHeight() / 5;
		}
		
		int y = (int) (cy + fml.getHeight() / 4 - (70 * (1 - stepMultExp)));

		g.setColor(new Color(0, 0, 0, stepMult));

		g.drawString(this.local.applicationName(), x, y);

		//g.setColor(Color.BLACK);

		g.setFont(infoFont);

		FontMetrics fmi = g.getFontMetrics(infoFont);

		String lv = this.local.launcherVersion(Launcher.version);
		String av = this.local
				.applicationVersion(Launcher.appVersion == null ? Version
						.getNullVersion() : Launcher.appVersion);

		g.drawString(lv, 6 - (100 * (1 - stepMultExp)),
				this.getHeight() - 6);
		g.drawString(av, this.getWidth() - fmi.stringWidth(av) - 6 + (100 * (1 - stepMultExp)),
				this.getHeight() - 6);
		
		onPaint(g);

	}
	
	public void onPaint(Graphics2D g) {
		
	}

}
