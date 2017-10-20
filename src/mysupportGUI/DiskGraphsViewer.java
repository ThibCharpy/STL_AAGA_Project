package mysupportGUI;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class DiskGraphsViewer
{
private static int    width    = 0;
private static int    height   = 0;
private static String title    = "DiskGraphs";
private static String filename = "input.points";
private static FramedGUI framedGUI;
private static int     nbPoints      = 100;
private static int     edgeThreshold = 80;
private static boolean proxyPPTI     = false;

public DiskGraphsViewer ()
{
}

public static void main (String[] args)
{
	for (int i = 0; i < args.length; ++i)
	{
		boolean argExp = true;
		if (args[i].charAt(0) == '-')
		{
			String var3 = args[i];
			byte   var4 = -1;
			switch (var3.hashCode())
			{
				case -2118335772:
					if (var3.equals("-nbPoints"))
					{
						var4 = 0;
					}
					break;
				case -729327018:
					if (var3.equals("-proxyPPTI"))
					{
						var4 = 2;
					}
					break;
				case 862202913:
					if (var3.equals("-edgeThreshold"))
					{
						var4 = 1;
					}
			}

			switch (var4)
			{
				case 0:
					try
					{
						nbPoints = Integer.parseInt(args[i + 1]);
						break;
					} catch (Exception var10)
					{
						System.err.println("Invalid argument for option " + args[i] + ": Integer type expected");
						return;
					}
				case 1:
					try
					{
						edgeThreshold = Integer.parseInt(args[i + 1]);
						break;
					} catch (Exception var9)
					{
						System.err.println("Invalid argument for option " + args[i] + ": Integer type expected");
						return;
					}
				case 2:
					proxyPPTI = true;
					argExp = false;
					break;
				default:
					System.err.println("Unknown option " + args[i]);
					return;
			}

			if (argExp && args[i + 1].charAt(0) == '-')
			{
				System.err.println("Option " + args[i] + " expects an argument but received none");
				return;
			}

			++i;
		}
	}

	SwingUtilities.invokeLater(new Runnable()
	{
		public void run ()
		{
			DiskGraphsViewer.framedGUI = new FramedGUI(DiskGraphsViewer.width,
			                                           DiskGraphsViewer.height,
			                                           DiskGraphsViewer.title,
			                                           DiskGraphsViewer.nbPoints,
			                                           DiskGraphsViewer.edgeThreshold,
			                                           DiskGraphsViewer.proxyPPTI);
		}
	});
	Object var11 = Variables.lock;
	synchronized (Variables.lock)
	{
		try
		{
			Variables.lock.wait();
		} catch (InterruptedException var7)
		{
			var7.printStackTrace();
		}
	}

	readFile();
}

public static void readFile ()
{
	ArrayList points = new ArrayList();

	try
	{
		BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));

		try
		{
			String line;
			while ((line = input.readLine()) != null)
			{
				String[] coordinates = line.split("\\s+");
				points.add(new Point(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])));
			}

			ArrayList<Point> hitPoints = new ArrayList();

			for (int i = points.size() / 20; i < 2 * points.size() / 20; ++i)
			{
				hitPoints.add((Point) ((Point) points.get(i)).clone());
			}

			framedGUI.drawPoints(points, hitPoints);
			Object var21 = Variables.lock2;
			synchronized (Variables.lock2)
			{
				Variables.lock2.notify();
			}
		} catch (IOException var18)
		{
			System.err.println("Exception: interrupted I/O.");
		} finally
		{
			try
			{
				input.close();
			} catch (IOException var16)
			{
				System.err.println("I/O exception: unable to close " + filename);
			}

		}
	} catch (FileNotFoundException var20)
	{
		System.err.println("Input file not found.");
	}

}
}
