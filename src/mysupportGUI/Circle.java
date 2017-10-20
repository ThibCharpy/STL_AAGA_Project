package mysupportGUI;

import java.awt.*;
import java.util.ArrayList;

public class Circle
{
private Circle.DefCase type;
private Point          center;
private Point          p;
private Point          q;
private Point          r;
private int            radius;
private Color          c;

protected Circle (Point center, int r, Color c)
{
	this.type = Circle.DefCase.ONE;
	this.center = center;
	this.radius = r;
	this.p = null;
	this.q = null;
	this.r = null;
	this.c = c;
}

public Circle (Point center, int r)
{
	this.type = Circle.DefCase.ONE;
	this.center = center;
	this.radius = r;
	this.p = null;
	this.q = null;
	this.r = null;
	this.c = Color.RED;
}

public Circle (Point p, Point q)
{
	this.type = Circle.DefCase.TWO;
	this.center = null;
	this.radius = 0;
	this.p = p;
	this.q = q;
	this.r = null;
	this.c = Color.RED;
}

public Circle (Point p, Point q, Point r)
{
	this.type = Circle.DefCase.THREE;
	this.center = null;
	this.radius = 0;
	this.p = p;
	this.q = q;
	this.r = r;
	this.c = Color.RED;
}

public double squareDiam ()
{
	return (this.p.getX() - this.q.getX()) * (this.p.getX() - this.q.getX()) +
	       (this.p.getY() - this.q.getY()) * (this.p.getY() - this.q.getY());
}

private boolean outside (Point s)
{
	boolean result = true;
	double  cX;
	double  cY;
	switch (this.type)
	{
		case ONE:
		default:
			break;
		case TWO:
			cX = (this.p.getX() + this.q.getX()) / 2.0D;
			cY = (this.p.getY() + this.q.getY()) / 2.0D;
			result = 4.0D * ((cX - s.getX()) * (cX - s.getX()) + (cY - s.getY()) * (cY - s.getY())) > this.squareDiam();
			break;
		case THREE:
			double a = 2.0D * (this.q.getX() - this.p.getX());
			double b = 2.0D * (this.q.getY() - this.p.getY());
			double c = this.p.getX() * this.p.getX() + this.p.getY() * this.p.getY() - this.q.getX() * this.q.getX() -
			           this.q.getY() * this.q.getY();
			double x = 2.0D * (this.r.getX() - this.p.getX());
			double y = 2.0D * (this.r.getY() - this.p.getY());
			double z = this.p.getX() * this.p.getX() + this.p.getY() * this.p.getY() - this.r.getX() * this.r.getX() -
			           this.r.getY() * this.r.getY();
			cY = (x * c / a - z) / (y - x * b / a);
			cX = -(b * cY + c) / a;
			result = (cX - s.getX()) * (cX - s.getX()) + (cY - s.getY()) * (cY - s.getY()) >
			         (cX - this.q.getX()) * (cX - this.q.getX()) + (cY - this.q.getY()) * (cY - this.q.getY());
	}

	return result;
}

public boolean covered (ArrayList<Point> points)
{
	for (int i = 0; i < points.size(); ++i)
	{
		if (this.outside((Point) points.get(i)))
		{
			return false;
		}
	}

	return true;
}

public Point getCenter ()
{
	Point result = null;
	switch (this.type)
	{
		case ONE:
			result = this.center;
			break;
		case TWO:
			result = new Point((int) ((this.p.getX() + this.q.getX()) / 2.0D),
			                   (int) ((this.p.getY() + this.q.getY()) / 2.0D));
			break;
		case THREE:
			double a = 2.0D * (this.q.getX() - this.p.getX());
			double b = 2.0D * (this.q.getY() - this.p.getY());
			double c = this.p.getX() * this.p.getX() + this.p.getY() * this.p.getY() - this.q.getX() * this.q.getX() -
			           this.q.getY() * this.q.getY();
			double x = 2.0D * (this.r.getX() - this.p.getX());
			double y = 2.0D * (this.r.getY() - this.p.getY());
			double z = this.p.getX() * this.p.getX() + this.p.getY() * this.p.getY() - this.r.getX() * this.r.getX() -
			           this.r.getY() * this.r.getY();
			double cY = (x * c / a - z) / (y - x * b / a);
			double cX = -(b * cY + c) / a;
			result = new Point((int) cX, (int) cY);
	}

	return result;
}

public int getRadius ()
{
	int result = 0;
	switch (this.type)
	{
		case ONE:
			result = this.radius;
			break;
		case TWO:
			result = (int) Math.sqrt(this.squareDiam() / 4.0D);
			break;
		case THREE:
			double a = 2.0D * (this.q.getX() - this.p.getX());
			double b = 2.0D * (this.q.getY() - this.p.getY());
			double c = this.p.getX() * this.p.getX() + this.p.getY() * this.p.getY() - this.q.getX() * this.q.getX() -
			           this.q.getY() * this.q.getY();
			double x = 2.0D * (this.r.getX() - this.p.getX());
			double y = 2.0D * (this.r.getY() - this.p.getY());
			double z = this.p.getX() * this.p.getX() + this.p.getY() * this.p.getY() - this.r.getX() * this.r.getX() -
			           this.r.getY() * this.r.getY();
			double cY = (x * c / a - z) / (y - x * b / a);
			double cX = -(b * cY + c) / a;
			result = (int) Math.sqrt(
					  (cX - this.q.getX()) * (cX - this.q.getX()) + (cY - this.q.getY()) * (cY - this.q.getY()));
	}

	return result;
}

public Circle.DefCase getType ()
{
	return this.type;
}

protected Color getColor ()
{
	return this.c;
}

protected void setColor (Color c)
{
	this.c = c;
}

public static enum DefCase
{
	ONE, TWO, THREE;

	private DefCase ()
	{
	}
}
}
