package mysupportGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class DisplayPanel extends JPanel
{
private static final long serialVersionUID = -1401707925288150149L;
private static final int  xBorder          = 10;
private static final int  yBorder          = 10;
private static final int  xStep            = 10;
private static final int  yStep            = 10;
private static final Color[] sevenColors;

static
{
	sevenColors = new Color[] { Color.RED, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.PINK, Color.LIGHT_GRAY, Color.BLUE };
}

private int                         xModifier;
private int                         yModifier;
private Graphics2D                  g2d;
private ArrayList<Point>            points;
private ArrayList<Point>            hitPoints;
private ArrayList<Point>            domSet;
private ArrayList<Line>             lines;
private ArrayList<Circle>           circles;
private int[][]                     paths;
private long                        time;
private double                      distanceTotale;
private double                      scoreTotal;
private ArrayList<Attribut>         attributs;
private ArrayList<ArrayList<Point>> clusters;
private double                      budget;
private double                      averageScore;
private int                         hits;
private int                         edgeThreshold;
private int                         fails;
private Random                      random;
private boolean                     pathsReceived;
private boolean                     qc;

public DisplayPanel (int edgeThreshold)
{
	this.setPreferredSize(new Dimension(1500, 1000));
	this.xModifier = 10;
	this.yModifier = 10;
	this.points = new ArrayList();
	this.hitPoints = new ArrayList();
	this.lines = new ArrayList();
	this.circles = new ArrayList();
	this.attributs = new ArrayList();
	this.clusters = new ArrayList();
	this.domSet = new ArrayList();
	this.paths = new int[0][0];
	this.time = -1L;
	this.distanceTotale = -1.0D;
	this.scoreTotal = -1.0D;
	this.budget = -1.0D;
	this.hits = -1;
	this.fails = -1;
	this.averageScore = -1.0D;
	this.edgeThreshold = edgeThreshold;
	this.pathsReceived = false;
	this.qc = false;
	this.random = new Random();
}

private boolean isMember (ArrayList<Line> lines, Line l)
{
	Iterator var3 = lines.iterator();

	Line line;
	do
	{
		if (!var3.hasNext())
		{
			return false;
		}

		line = (Line) var3.next();
	}
	while (!line.getP().equals(l.getP()) || !line.getQ().equals(l.getQ()));

	return true;
}

public void paintComponent (Graphics g)
{
	super.paintComponent(g);
	this.g2d = (Graphics2D) g.create();
	this.g2d.setFont(new Font(this.g2d.getFont().getName(), 1, 18));
	this.g2d.drawString("Clavier:", 5, 20);
	this.g2d.drawString("- 'r' refresh points (Binh)", 15, 40);
	this.g2d.drawString("- 't' generate rectangle graph strongly connected", 15, 60);
	this.g2d.drawString("- 'y' generate disk graph strongly connected", 15, 80);
	this.g2d.drawString("- 'c' pour lancer calculConnectedDominatingSet", 15, 100);
	this.g2d.drawString("- 'h', 'j', 'k', 'l' pour déplacer les points", 15, 120);
	this.g2d.setFont(new Font(this.g2d.getFont().getName(), 1, 36));
	this.g2d.drawString("Connected Dominating Set Viewer", 600, 40);
	this.g2d.setColor(Color.BLUE);
	this.g2d.setStroke(new BasicStroke(6.0F, 1, 1));

	int x;
	int y;
	int i;
	for (i = 0; i < this.points.size(); ++i)
	{
		if ((x = (int) ((Point) this.points.get(i)).getX() + this.xModifier) >= 10 &&
		    (y = (int) ((Point) this.points.get(i)).getY() + this.yModifier) >= 10)
		{
			this.g2d.drawLine(x, y, x, y);
		}
	}

	int v;
	int w;
	int j;
	if (this.domSet.isEmpty())
	{
		this.g2d.setColor(Color.BLUE);
		this.g2d.setStroke(new BasicStroke(2.0F, 1, 1));

		for (i = 0; i < this.points.size(); ++i)
		{
			for (j = 0; j < this.points.size(); ++j)
			{
				if ((x = (int) ((Point) this.points.get(i)).getX() + this.xModifier) >= 10 &&
				    (y = (int) ((Point) this.points.get(i)).getY() + this.yModifier) >= 10 &&
				    (v = (int) ((Point) this.points.get(j)).getX() + this.xModifier) >= 10 &&
				    (w = (int) ((Point) this.points.get(j)).getY() + this.yModifier) >= 10 &&
				    ((Point) this.points.get(i)).distance((Point2D) this.points.get(j)) <= (double) this.edgeThreshold &&
				    !this.isMember(this.lines, new Line((Point) this.points.get(i), (Point) this.points.get(j))) &&
				    !this.isMember(this.lines, new Line((Point) this.points.get(j), (Point) this.points.get(i))))
				{
					this.g2d.drawLine(x, y, v, w);
				}
			}
		}
	}

	if (this.clusters.size() > 0)
	{
		for (i = 0; i < this.clusters.size(); ++i)
		{
			if (i < 7)
			{
				this.g2d.setColor(sevenColors[i]);
			}
			else
			{
				this.g2d.setColor(new Color(i % 256, i % 256, i % 256));
			}

			this.g2d.setStroke(new BasicStroke(6.0F, 1, 1));

			for (j = 0; j < ((ArrayList) this.clusters.get(i)).size(); ++j)
			{
				if ((x = (int) ((Point) ((ArrayList) this.clusters.get(i)).get(j)).getX() + this.xModifier) >= 10 &&
				    (y = (int) ((Point) ((ArrayList) this.clusters.get(i)).get(j)).getY() + this.yModifier) >= 10)
				{
					this.g2d.drawLine(x, y, x, y);
				}
			}

			this.g2d.drawLine((int) ((Attribut) this.attributs.get(i)).centerX - 5 + this.xModifier,
			                  (int) ((Attribut) this.attributs.get(i)).centerY - 5 + this.yModifier,
			                  (int) ((Attribut) this.attributs.get(i)).centerX + 5 + this.xModifier,
			                  (int) ((Attribut) this.attributs.get(i)).centerY + 5 + this.yModifier);
			this.g2d.drawLine((int) ((Attribut) this.attributs.get(i)).centerX - 5 + this.xModifier,
			                  (int) ((Attribut) this.attributs.get(i)).centerY + 5 + this.yModifier,
			                  (int) ((Attribut) this.attributs.get(i)).centerX + 5 + this.xModifier,
			                  (int) ((Attribut) this.attributs.get(i)).centerY - 5 + this.yModifier);
		}
	}

	this.g2d.setStroke(new BasicStroke(4.0F, 1, 1));

	for (i = 0; i < this.lines.size(); ++i)
	{
		x = (int) ((Line) this.lines.get(i)).getP().getX() + this.xModifier;
		y = (int) ((Line) this.lines.get(i)).getP().getY() + this.yModifier;
		v = (int) ((Line) this.lines.get(i)).getQ().getX() + this.xModifier;
		w = (int) ((Line) this.lines.get(i)).getQ().getY() + this.yModifier;
		this.g2d.setColor(((Line) this.lines.get(i)).getColor());
		this.g2d.drawLine(x, y, v, w);
	}

	for (int i1 = 0; i1 < this.circles.size(); ++i1)
	{
		x = (int) ((Circle) this.circles.get(i1)).getCenter().getX() + this.xModifier;
		y = (int) ((Circle) this.circles.get(i1)).getCenter().getY() + this.yModifier;
		i = ((Circle) this.circles.get(i1)).getRadius();
		this.g2d.setColor(((Circle) this.circles.get(i1)).getColor());
		Double c = new Double((double) (x - i), (double) (y - i), (double) (2 * i), (double) (2 * i));
		this.g2d.draw(c);
	}

	new Point();
	int t;
	if (this.domSet.size() > 0)
	{
		this.g2d.setColor(Color.BLUE);
		this.g2d.setStroke(new BasicStroke(2.0F, 1, 1));

		for (i = 0; i < this.points.size(); ++i)
		{
			for (t = 0; t < this.points.size(); ++t)
			{
				if ((x = (int) ((Point) this.points.get(i)).getX() + this.xModifier) >= 10 &&
				    (y = (int) ((Point) this.points.get(i)).getY() + this.yModifier) >= 10 &&
				    (v = (int) ((Point) this.points.get(t)).getX() + this.xModifier) >= 10 &&
				    (w = (int) ((Point) this.points.get(t)).getY() + this.yModifier) >= 10 &&
				    ((Point) this.points.get(i)).distance((Point2D) this.points.get(t)) <= (double) this.edgeThreshold)
				{
					this.g2d.setColor(Color.BLUE);
					if (this.domSet.contains(this.points.get(i)) || this.domSet.contains(this.points.get(t)))
					{
						this.g2d.setColor(Color.GREEN);
					}

					if (this.isDominated(this.domSet, (Point) this.points.get(i)) &&
					    this.isDominated(this.domSet, (Point) this.points.get(t)))
					{
						this.g2d.setColor(Color.GREEN);
					}

					this.g2d.drawLine(x, y, v, w);
				}
			}
		}

		this.g2d.setColor(Color.GREEN);
		this.g2d.setStroke(new BasicStroke(2.0F, 1, 1));

		for (i = 0; i < this.points.size(); ++i)
		{
			for (t = 0; t < this.points.size(); ++t)
			{
				if ((x = (int) ((Point) this.points.get(i)).getX() + this.xModifier) >= 10 &&
				    (y = (int) ((Point) this.points.get(i)).getY() + this.yModifier) >= 10 &&
				    (v = (int) ((Point) this.points.get(t)).getX() + this.xModifier) >= 10 &&
				    (w = (int) ((Point) this.points.get(t)).getY() + this.yModifier) >= 10 &&
				    ((Point) this.points.get(i)).distance((Point2D) this.points.get(t)) <= (double) this.edgeThreshold &&
				    this.domSet.contains(this.points.get(i)) && this.domSet.contains(this.points.get(t)))
				{
					this.g2d.setColor(Color.YELLOW);
					this.g2d.drawLine(x, y, v, w);
				}
			}
		}

		this.g2d.setColor(Color.YELLOW);
		this.g2d.setStroke(new BasicStroke(10.0F, 1, 1));

		for (i = 0; i < this.domSet.size(); ++i)
		{
			if ((x = (int) ((Point) this.domSet.get(i)).getX() + this.xModifier) >= 10 &&
			    (y = (int) ((Point) this.domSet.get(i)).getY() + this.yModifier) >= 10)
			{
				this.g2d.drawLine(x, y, x, y);
			}
		}

		this.g2d.setColor(Color.BLACK);
		this.g2d.setFont(new Font(this.g2d.getFont().getName(), 1, 32));
		this.g2d.drawString("Taille de l'ensemble: " + this.domSet.size(), 20, 200);
	}

	if (this.time != -1L)
	{
		this.g2d.setColor(Color.BLACK);
		this.g2d.setFont(new Font(this.g2d.getFont().getName(), 1, 32));
		this.g2d.drawString("Temps de calcul: " + Long.toString(this.time) + " ms", 20, 150);
	}

	if (this.averageScore != -1.0D)
	{
		this.g2d.setColor(Color.BLACK);
		this.g2d.setFont(new Font(this.g2d.getFont().getName(), 1, 64));
		this.g2d.drawString("Total fails: " + this.fails, 20, 250);
		this.g2d.drawString("Average score: " + this.averageScore / (double) (100 - this.fails), 20, 300);
	}

	if (this.pathsReceived)
	{
		i = this.random.nextInt(this.points.size());
		t = this.random.nextInt(this.points.size());
		this.g2d.setColor(Color.BLACK);
		this.g2d.setFont(new Font(this.g2d.getFont().getName(), 1, 32));
		this.g2d.drawString("Random displaying path between #" + i + " and #" + t, 20, 200);
		this.g2d.setColor(Color.YELLOW);
		this.g2d.setStroke(new BasicStroke(4.0F, 1, 1));

		while (this.paths[i][t] != t && this.paths[i][t] != i)
		{
			if (((Point) this.points.get(i)).distance((Point2D) this.points.get(this.paths[i][t])) >
			    (double) this.edgeThreshold)
			{
				this.g2d.setFont(new Font(this.g2d.getFont().getName(), 1, 50));
				this.g2d.drawString("ERROR DETECTED: no edge between #" + i + " and #" + this.paths[i][t], 20, 300);
				break;
			}

			x = (int) ((Point) this.points.get(i)).getX() + this.xModifier;
			y = (int) ((Point) this.points.get(i)).getY() + this.yModifier;
			v = (int) ((Point) this.points.get(this.paths[i][t])).getX() + this.xModifier;
			w = (int) ((Point) this.points.get(this.paths[i][t])).getY() + this.yModifier;
			this.g2d.drawLine(x, y, v, w);
			i = this.paths[i][t];
		}

		if (((Point) this.points.get(i)).distance((Point2D) this.points.get(t)) <= (double) this.edgeThreshold)
		{
			x = (int) ((Point) this.points.get(i)).getX() + this.xModifier;
			y = (int) ((Point) this.points.get(i)).getY() + this.yModifier;
			v = (int) ((Point) this.points.get(t)).getX() + this.xModifier;
			w = (int) ((Point) this.points.get(t)).getY() + this.yModifier;
			this.g2d.drawLine(x, y, v, w);
		}
		else
		{
			this.g2d.setFont(new Font(this.g2d.getFont().getName(), 1, 50));
			this.g2d.drawString("ERROR DETECTED: no edge between #" + i + " and #" + t, 20, 300);
		}
	}

	if (this.distanceTotale != -1.0D)
	{
		this.g2d.setColor(Color.BLACK);
		this.g2d.setFont(new Font(this.g2d.getFont().getName(), 1, 32));
		this.g2d.drawString("Score total: " + Long.toString((long) this.scoreTotal), 20, 200);
		this.g2d.drawString("dont distance: " + Long.toString((long) this.distanceTotale), 40, 250);
		if (this.qc)
		{
			this.g2d.drawString("QC: OK!", 20, 300);
		}
		else
		{
			this.g2d.drawString("QC: KO...", 20, 300);
		}
	}

	if (this.budget != -1.0D)
	{
		this.g2d.setColor(Color.BLACK);
		this.g2d.setFont(new Font(this.g2d.getFont().getName(), 1, 32));
		this.g2d.drawString("Points connectés: " + this.hits, 20, 200);
		this.g2d.drawString("Budget total: " + Long.toString((long) this.budget), 20, 250);
		if (this.qc)
		{
			this.g2d.drawString("QC: OK!", 20, 300);
		}
		else
		{
			this.g2d.drawString("QC: KO...", 20, 300);
		}
	}

	if (this.clusters.size() > 0)
	{
		this.g2d.setColor(Color.BLACK);
		this.g2d.setFont(new Font(this.g2d.getFont().getName(), 1, 18));

		for (i = 0; i < this.clusters.size(); ++i)
		{
			this.g2d.drawString("Cluster #" + i + " (population " + ((Attribut) this.attributs.get(i)).population +
			                    "): distance totale par rapport au centre " +
			                    Long.toString((long) ((Attribut) this.attributs.get(i)).distanceTotale), 20, 250 + i * 20);
		}
	}

}

public void drawPoints (ArrayList<Point> points, ArrayList<Point> hitPoints)
{
	this.lines.clear();
	this.circles.clear();
	this.clusters.clear();
	this.points = (ArrayList) points.clone();
	this.hitPoints = (ArrayList) hitPoints.clone();
	this.pathsReceived = false;
	this.repaint();
}

public void shiftLeftAll ()
{
	this.xModifier -= 20;
	this.repaint();
}

public void shiftUpAll ()
{
	this.yModifier -= 20;
	this.repaint();
}

public void shiftDownAll ()
{
	this.yModifier += 20;
	this.repaint();
}

public void shiftRightAll ()
{
	this.xModifier += 20;
	this.repaint();
}

public void refreshLine ()
{
	this.lines.clear();
	this.circles.clear();
	this.time = -1L;
	this.distanceTotale = -1.0D;
	this.budget = -1.0D;
	this.hits = -1;
	this.repaint();
}

public ArrayList<Point> getPoints ()
{
	return (ArrayList) this.points.clone();
}

public ArrayList<Point> getHitPoints ()
{
	return (ArrayList) this.hitPoints.clone();
}

private boolean isDominated (ArrayList<Point> ds, Point p)
{
	Iterator var3 = ds.iterator();

	Point q;
	do
	{
		if (!var3.hasNext())
		{
			return false;
		}

		q = (Point) var3.next();
	}
	while (p.distance(q) > (double) this.edgeThreshold);

	return true;
}

private double loadPoints (ArrayList<Point> pts)
{
	if (pts.size() < 2)
	{
		return 0.0D;
	}
	else
	{
		double d = ((Point) pts.get(pts.size() - 1)).distance((Point2D) pts.get(0));

		for (int i = 1; i < pts.size(); ++i)
		{
			d += ((Point) pts.get(i - 1)).distance((Point2D) pts.get(i));
		}

		return d;
	}
}

public void addTNT (int fails, double average, long t)
{
	this.time = t;
	this.fails = fails;
	this.averageScore = average;
	this.distanceTotale = -1.0D;
	this.budget = -1.0D;
	this.hits = -1;
	this.pathsReceived = false;
	this.repaint();
}

public void addDSAndT (ArrayList<Point> inpts, long t)
{
	this.domSet = (ArrayList) inpts.clone();
	this.time = t;
	this.pathsReceived = false;
	this.repaint();
}

public void addTSPAndT (ArrayList<Point> inpts, long t)
{
	ArrayList<Point> pts = (ArrayList) inpts.clone();

	int i;
	for (i = 1; i < pts.size() + 1; ++i)
	{
		if (((Point) pts.get(i - 1)).equals(pts.get(i % pts.size())))
		{
			pts.remove(i % pts.size());
			--i;
		}
	}

	this.lines.clear();

	for (i = 0; i < pts.size() - 1; ++i)
	{
		this.lines.add(new Line((Point) pts.get(i), (Point) pts.get(i + 1), Color.YELLOW));
	}

	this.lines.add(new Line((Point) pts.get(pts.size() - 1), (Point) pts.get(0), Color.YELLOW));
	this.scoreTotal = Evaluator.score(inpts);
	this.distanceTotale = this.loadPoints(inpts);
	this.time = t;
	this.budget = -1.0D;
	this.hits = -1;
	this.pathsReceived = false;
	this.qc = Evaluator.isValide(inpts, (ArrayList) this.hitPoints.clone(), this.edgeThreshold);
	this.repaint();
}

private double crossProd (double ux, double uy, double vx, double vy)
{
	return ux * vy - uy * vx;
}

private double dotProd (double ux, double uy, double vx, double vy)
{
	return ux * vx + uy * vy;
}

private boolean belongs (Point r, Line l)
{
	double rpx = l.getP().getX() - r.getX();
	double rpy = l.getP().getY() - r.getY();
	double rqx = l.getQ().getX() - r.getX();
	double rqy = l.getQ().getY() - r.getY();
	return Math.abs(this.crossProd(rpx, rpy, rqx, rqy)) < 0.1D && this.dotProd(rpx, rpy, rqx, rqy) <= 0.0D;
}

public void addClusterAndT (ArrayList<ArrayList<Point>> cluster, long t)
{
	this.lines.clear();
	this.circles.clear();
	this.clusters.clear();
	this.clusters = (ArrayList) cluster.clone();
	this.time = t;
	this.attributs = this.computeAttributs(cluster);
	this.distanceTotale = this.sumDist(this.attributs);
	this.budget = -1.0D;
	this.repaint();
}

private double sumDist (ArrayList<Attribut> as)
{
	double d = 0.0D;

	Attribut a;
	for (Iterator var4 = as.iterator(); var4.hasNext(); d += a.distanceTotale)
	{
		a = (Attribut) var4.next();
	}

	return d;
}

public void addClusterAndTBudget (ArrayList<ArrayList<Point>> cluster, long t)
{
	this.lines.clear();
	this.circles.clear();
	this.clusters.clear();
	this.clusters = (ArrayList) cluster.clone();
	this.time = t;
	this.attributs = this.computeAttributs(cluster);
	this.budget = this.computeBudget(this.attributs);
	this.distanceTotale = -1.0D;
	this.repaint();
}

private double[] mean (ArrayList<Point> points)
{
	double[] mean = new double[] { 0.0D, 0.0D };

	Point p;
	for (Iterator var3 = points.iterator(); var3.hasNext(); mean[1] += (double) p.y / (double) points.size())
	{
		p = (Point) var3.next();
		mean[0] += (double) p.x / (double) points.size();
	}

	return mean;
}

private double distanceFromC (ArrayList<Point> points, double[] center)
{
	double d = 0.0D;
	Point  c = new Point();
	c.setLocation(center[0], center[1]);

	Point p;
	for (Iterator var6 = points.iterator(); var6.hasNext(); d += c.distance(p))
	{
		p = (Point) var6.next();
	}

	return d;
}

private ArrayList<Attribut> computeAttributs (ArrayList<ArrayList<Point>> cluster)
{
	ArrayList<Attribut> attributs = new ArrayList();
	double[]            center    = new double[2];
	Iterator            var7      = cluster.iterator();

	while (var7.hasNext())
	{
		ArrayList<Point> c = (ArrayList) var7.next();
		center = this.mean(c);
		double   d = this.distanceFromC(c, center);
		Attribut a = new Attribut(d, center[0], center[1], c.size());
		attributs.add(a);
	}

	return attributs;
}

private double computeBudget (ArrayList<Attribut> attributs)
{
	double   b    = 0.0D;
	Iterator var4 = attributs.iterator();

	while (var4.hasNext())
	{
		Attribut a = (Attribut) var4.next();
		if (a.distanceTotale > b)
		{
			b = a.distanceTotale;
		}
	}

	return b;
}

public void addFVSAndT (ArrayList<Line> fvs, long t)
{
	this.lines.clear();
	this.lines = (ArrayList) fvs.clone();
	this.circles.clear();
	this.clusters.clear();
	this.time = t;
	this.distanceTotale = -1.0D;
	this.budget = -1.0D;
	this.repaint();
}

public void addPathsAndT (int[][] paths, long t)
{
	this.lines.clear();
	this.circles.clear();
	this.clusters.clear();
	this.paths = paths;
	this.pathsReceived = true;
	this.time = t;
	this.distanceTotale = -1.0D;
	this.budget = -1.0D;
	this.repaint();
}
}
