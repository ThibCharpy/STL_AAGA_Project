package mysupportGUI;

import algorithms.DefaultTeam;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Keymaps implements KeyListener
{
private RootPanel rootPanel;
private int       nbPoints;
private int       edgeThreshold;
private boolean   proxyPPTI;

public Keymaps (RootPanel rootPanel, int nbPoints, int edgeThreshold, boolean proxyPPTI)
{
	this.rootPanel = rootPanel;
	this.nbPoints = nbPoints;
	this.edgeThreshold = edgeThreshold;
	this.proxyPPTI = proxyPPTI;
}

public void keyTyped (KeyEvent event)
{
	switch (event.getKeyChar())
	{
		case 'c':
			long t = System.currentTimeMillis();
			ArrayList<Point> atsp = (new DefaultTeam()).calculConnectedDominatingSet(this.rootPanel.getPoints(),
			                                                                         this.edgeThreshold);
			t = System.currentTimeMillis() - t;
			this.rootPanel.addDSAndT(atsp, t);
			break;
		case 'd':
		case 't':
			try
			{
				DefaultTeam.generateRandomGraph(this.nbPoints, 1400, 900, this.edgeThreshold);
				DiskGraphsViewer.readFile();
				this.rootPanel.refreshLine();
			} catch (Exception var7)
			{
				;
			}
			break;
		case 'y':
			try
			{
				DefaultTeam.generateRandomDiskGraph(this.nbPoints, 1400, 900, this.edgeThreshold);
				DiskGraphsViewer.readFile();
				this.rootPanel.refreshLine();
			} catch (Exception var7)
			{
				;
			}
			break;
		case 'e':
		case 'f':
		case 'g':
		case 'i':
		case 'j':
		case 'n':
		case 'h':
		case 'p':
		case 'q':
		default:
			break;
		case 'k':
			this.rootPanel.shiftLeftAll();
			break;
		case 'o':
			this.rootPanel.shiftUpAll();
			break;
		case 'l':
			this.rootPanel.shiftDownAll();
			break;
		case 'm':
			this.rootPanel.shiftRightAll();
			break;
		case 'r':
			try
			{
				RandomPointsGenerator.generate(this.nbPoints);
				DiskGraphsViewer.readFile();
				this.rootPanel.refreshLine();
			} catch (Exception var7)
			{
				;
			}
	}

}

public void keyPressed (KeyEvent arg0)
{
}

public void keyReleased (KeyEvent arg0)
{
}
}
