package mysupportGUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class FramedGUI extends JFrame
{
private static final long serialVersionUID = 599149216192397088L;
protected RootPanel rootPanel;

public FramedGUI (int width, int height, String title, int nbPoints, int edgeThreshold, boolean proxyPPTI)
{
	super(title);
	this.setDefaultCloseOperation(3);
	this.rootPanel = new RootPanel(edgeThreshold);
	this.getContentPane().add(this.rootPanel);
	this.addKeyListener(new Keymaps(this.rootPanel, nbPoints, edgeThreshold, proxyPPTI));
	if (width >= 100 && height >= 100)
	{
		this.setSize(new Dimension(width, height));
	}
	else
	{
		this.pack();
	}

	this.setVisible(true);
	Object var7 = Variables.lock;
	synchronized (Variables.lock)
	{
		Variables.lock.notify();
	}
}

public void drawPoints (ArrayList<Point> points, ArrayList<Point> hitPoints)
{
	this.rootPanel.drawPoints(points, hitPoints);
}
}
