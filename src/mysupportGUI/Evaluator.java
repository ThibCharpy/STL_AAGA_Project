package mysupportGUI;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Evaluator
{
public Evaluator ()
{
}

public static boolean isValide (ArrayList<Point> domSet, ArrayList<Point> points, int edgeThreshold)
{
	Iterator var3 = points.iterator();

	boolean isDom;
	do
	{
		if (!var3.hasNext())
		{
			return true;
		}

		Point p = (Point) var3.next();
		isDom = false;
		Iterator var6 = domSet.iterator();

		while (var6.hasNext())
		{
			Point q = (Point) var6.next();
			if (isDom = p.distance(q) <= (double) edgeThreshold)
			{
				break;
			}
		}
	}
	while (isDom);

	return false;
}

public static double score (ArrayList<Point> inpts)
{
	return (double) inpts.size();
}
}
