package mysupportGUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MainPanel extends JPanel
{
private static final long serialVersionUID = -662473925955493029L;
protected DisplayPanel displayPanel;

public MainPanel (int edgeThreshold)
{
	super(new BorderLayout());
	this.displayPanel = new DisplayPanel(edgeThreshold);
	this.add(this.displayPanel, "Center");
}

public void drawPoints (ArrayList<Point> points, ArrayList<Point> hitPoints)
{
	this.displayPanel.drawPoints(points, hitPoints);
}

public void shiftLeftAll ()
{
	this.displayPanel.shiftLeftAll();
}

public void shiftUpAll ()
{
	this.displayPanel.shiftUpAll();
}

public void shiftDownAll ()
{
	this.displayPanel.shiftDownAll();
}

public void shiftRightAll ()
{
	this.displayPanel.shiftRightAll();
}

public void refreshLine ()
{
	this.displayPanel.refreshLine();
}

public ArrayList<Point> getPoints ()
{
	return this.displayPanel.getPoints();
}

public ArrayList<Point> getHitPoints ()
{
	return this.displayPanel.getHitPoints();
}

public void addClusterAndT (ArrayList<ArrayList<Point>> cluster, long t)
{
	this.displayPanel.addClusterAndT(cluster, t);
}

public void addClusterAndTBudget (ArrayList<ArrayList<Point>> cluster, long t)
{
	this.displayPanel.addClusterAndTBudget(cluster, t);
}

public void addFVSAndT (ArrayList<Line> lines, long t)
{
	this.displayPanel.addFVSAndT(lines, t);
}

public void addPathsAndT (ArrayList<ArrayList<Integer>> paths, long t)
{
}

public void addPathsAndT (int[][] paths, long t)
{
	this.displayPanel.addPathsAndT(paths, t);
}

public void addTSPAndT (ArrayList<Point> points, long t)
{
	this.displayPanel.addTSPAndT(points, t);
}

public void addDSAndT (ArrayList<Point> points, long t)
{
	this.displayPanel.addDSAndT(points, t);
}

public void addTNT (int fails, double average, long t)
{
	this.displayPanel.addTNT(fails, average, t);
}
}
