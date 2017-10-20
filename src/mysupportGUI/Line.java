package mysupportGUI;

import java.awt.*;

public class Line
{
private Point p;
private Point q;
private Color c;

protected Line (Point p, Point q, Color c)
{
	this.p = p;
	this.q = q;
	this.c = c;
}

public Line (Point p, Point q)
{
	this.p = p;
	this.q = q;
	this.c = Color.RED;
}

protected static double distance (Point p, Point q)
{
	return Math.sqrt(Math.pow(p.getX() - q.getX(), 2.0D) + Math.pow(p.getY() - q.getY(), 2.0D));
}

public Point getP ()
{
	return this.p;
}

protected void setP (Point p)
{
	this.p = p;
}

public Point getQ ()
{
	return this.q;
}

protected void setQ (Point q)
{
	this.q = q;
}

protected Color getColor ()
{
	return this.c;
}

protected void setColor (Color c)
{
	this.c = c;
}

protected double distance ()
{
	return Math.sqrt(Math.pow(this.p.getX() - this.q.getX(), 2.0D) + Math.pow(this.p.getY() - this.q.getY(), 2.0D));
}
}
