package ru.vadimka.nfswlauncher.theme;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ru.vadimka.nfswlauncher.theme.customcomponents.PaintButton;

public class NevilleGUI extends JFrame {

	private static final long serialVersionUID = -1236528929517011126L;
	
	private JPanel contentPane;

	/**
	 * test.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					NevilleGUI frame = new NevilleGUI();
					frame.setTitle("Test new frame");
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public NevilleGUI() {
		setSize(new Dimension(600, 400));
		contentPane = new MainPanel();
		setLocationRelativeTo(null);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setUndecorated(true);
		setResizable(false);
		setBackground(new Color(0,0,0,0));
		GUIMouseMovement mouseMovement = new GUIMouseMovement(this,0,0,600,40);
		addMouseListener(mouseMovement);
		addMouseMotionListener(mouseMovement);
		
		setContentPane(contentPane);
		PaintButton btnClose = new PaintButton(exitButtonStyle);
		btnClose.setBounds(570, 15, 15, 15);
		btnClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		contentPane.add(btnClose);
		
		PaintButton btnHide = new PaintButton(hideButtonStyle);
		btnHide.setBounds(545, 15, 15, 15);
		btnHide.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setExtendedState(JFrame.ICONIFIED);
			}
		});
		contentPane.add(btnHide);
		
		PaintButton btnStart = new PaintButton("ИГРАТЬ",roundRectangleButtonStyle);
		btnStart.setBounds(380, 335, 200, 50);
		contentPane.add(btnStart);
		
		PaintButton btnSettings = new PaintButton(roundButtonStyle);
		btnSettings.setBounds(338, 345, 30, 30);
		contentPane.add(btnSettings);
		
		PaintButton btnDiscord = new PaintButton(roundButtonStyle);
		btnDiscord.setBounds(15, 355, 30, 30);
		contentPane.add(btnDiscord);
		
		PaintButton btnSite = new PaintButton(roundButtonStyle);
		btnSite.setBounds(60, 355, 30, 30);
		contentPane.add(btnSite);
		
		PaintButton serverSelect = new PaintButton("World Evolved V2",serverSelectButtonStyle);
		serverSelect.setBounds(185, 40, 230, 25);
		contentPane.add(serverSelect);
	}
	
	private final PaintButton.PaintExecutor roundRectangleButtonStyle = new PaintButton.PaintExecutor() {
		@Override
		public void paint(Graphics2D g, PaintButton b) {
			if (b.isPressed())
				g.setPaint(Color.GRAY);
			else if (b.isEntered())
				g.setPaint(new Color(200,200,200));
			else
				g.setPaint(Color.WHITE);
			g.fillRoundRect(0, 0, b.getWidth()-1, b.getHeight()-1, 50, 50);
			g.setPaint(Color.BLACK);
			int yText = b.getHeight()/2+g.getFont().getSize()/2;
			int xText = b.getWidth()/2-g.getFontMetrics().stringWidth(b.getText())/2;
			g.drawString(b.getText(), xText, yText);
		}
	};
	
	private final PaintButton.PaintExecutor serverSelectButtonStyle = new PaintButton.PaintExecutor() {
		@Override
		public void paint(Graphics2D g, PaintButton b) {
			if (b.isPressed())
				g.setPaint(Color.GRAY);
			else if (b.isEntered())
				g.setPaint(new Color(200,200,200));
			else
				g.setPaint(Color.WHITE);
			g.fillRoundRect(0, 0, b.getWidth()-1, b.getHeight()-1, 30, 30);
			g.setPaint(Color.DARK_GRAY);
			g.fillOval(4, 4, b.getHeight()-8, b.getHeight()-9);
			g.setPaint(Color.WHITE);
			g.drawLine(8, 10, b.getHeight()/2, b.getHeight()/2+3);
			g.drawLine(b.getHeight()-9, 10, b.getHeight()/2, b.getHeight()/2+3);
			g.setPaint(Color.BLACK);
			int yText = b.getHeight()/2+g.getFont().getSize()/2;
			int xText = b.getWidth()/2-g.getFontMetrics().stringWidth(b.getText())/2;
			g.drawString(b.getText(), xText, yText);
		}
	};
	
	private final PaintButton.PaintExecutor roundButtonStyle = new PaintButton.PaintExecutor() {
		@Override
		public void paint(Graphics2D g, PaintButton b) {
			if (b.isPressed())
				g.setPaint(Color.GRAY);
			else if (b.isEntered())
				g.setPaint(new Color(200,200,200));
			else
				g.setPaint(Color.WHITE);
			g.fillOval(0, 0, b.getWidth()-1, b.getHeight()-1);
		}
	};
	
	private final PaintButton.PaintExecutor exitButtonStyle = new PaintButton.PaintExecutor() {
		@Override
		public void paint(Graphics2D g, PaintButton b) {
			if (b.isPressed())
				g.setPaint(Color.GRAY);
			else if (b.isEntered())
				g.setPaint(new Color(200,200,200));
			else
				g.setPaint(Color.WHITE);
			g.fillOval(0, 0, b.getWidth()-1, b.getHeight()-1);
			g.setPaint(Color.DARK_GRAY);
			g.drawLine(9, 9, b.getWidth()-11, b.getHeight()-11);
			g.drawLine(b.getWidth()-11, 9, 9 , b.getHeight()-11);
		}
	};
	
	private final PaintButton.PaintExecutor hideButtonStyle = new PaintButton.PaintExecutor() {
		@Override
		public void paint(Graphics2D g, PaintButton b) {
			if (b.isPressed())
				g.setPaint(Color.GRAY);
			else if (b.isEntered())
				g.setPaint(new Color(200,200,200));
			else
				g.setPaint(Color.WHITE);
			g.fillOval(0, 0, b.getWidth()-1, b.getHeight()-1);
			g.setPaint(Color.DARK_GRAY);
			g.drawLine(9, b.getHeight()/2, b.getWidth()-11, b.getHeight()/2);
		}
	};
	
	public static class MainPanel extends JPanel {
		private static final long serialVersionUID = 5646483001882179160L;
		@Override
		protected void paintComponent(Graphics g1) {
			Graphics2D g = (Graphics2D) g1;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
			g.setColor(Color.DARK_GRAY);
			g.fillRoundRect(0, 0, getWidth(), getHeight(), 60, 60);
		}
	}
}
