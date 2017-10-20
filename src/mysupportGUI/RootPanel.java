package mysupportGUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class RootPanel extends JPanel
{
private static final long       serialVersionUID = -7573425545656207548L;
protected            CardLayout layout           = (CardLayout) this.getLayout();
protected MainPanel mainPanel;

public RootPanel (int edgeThreshold)
{
	super(new CardLayout());
	this.mainPanel = new MainPanel(edgeThreshold);
	this.add(this.mainPanel, "1664");
	this.layout.show(this, "1664");
}

public void drawPoints (ArrayList<Point> points, ArrayList<Point> hitPoints)
{
	this.mainPanel.drawPoints(points, hitPoints);
}

public void shiftLeftAll ()
{
	this.mainPanel.shiftLeftAll();
}

public void shiftUpAll ()
{
	this.mainPanel.shiftUpAll();
}

public void shiftDownAll ()
{
	this.mainPanel.shiftDownAll();
}

public void shiftRightAll ()
{
	this.mainPanel.shiftRightAll();
}

public void refreshLine ()
{
	this.mainPanel.refreshLine();
}

public ArrayList<Point> getPoints ()
{
	return this.mainPanel.getPoints();
}

public ArrayList<Point> getHitPoints ()
{
	return this.mainPanel.getHitPoints();
}

public void addClusterAndT (ArrayList<ArrayList<Point>> cluster, long t)
{
	this.mainPanel.addClusterAndT(cluster, t);
}

public void addClusterAndTBudget (ArrayList<ArrayList<Point>> cluster, long t)
{
	this.mainPanel.addClusterAndTBudget(cluster, t);
}

public void addFVSAndT (ArrayList<Line> lines, long t)
{
	this.mainPanel.addFVSAndT(lines, t);
}

public void addPathsAndT (ArrayList<ArrayList<Integer>> paths, long t)
{
	this.mainPanel.addPathsAndT(paths, t);
}

public void addPathsAndT (int[][] paths, long t)
{
	this.mainPanel.addPathsAndT(paths, t);
}

public void addTSPAndT (ArrayList<Point> points, long t)
{
	this.mainPanel.addTSPAndT(points, t);
}

public void addDSAndT (ArrayList<Point> points, long t)
{
	this.mainPanel.addDSAndT(points, t);
}

public void addTNT (int fails, double average, long t)
{
	this.mainPanel.addTNT(fails, average, t);
}
}
