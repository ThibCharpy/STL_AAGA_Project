package mysupportGUI;

import algorithms.DefaultTeam;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class TestBedEvaluator
{
private static String  testFolder    = "/files/archives/aaga2015/dominatingSetDG/tests";
private static int     edgeThreshold = 55;
private static boolean proxyPPTI     = false;
private static double result;
private static int    fails;

public TestBedEvaluator ()
{
}

public static void main (String[] args)
{
	for (int i = 0; i < args.length; ++i)
	{
		if (args[i].charAt(0) == '-')
		{
			if (args[i + 1].charAt(0) == '-')
			{
				System.err.println("Option " + args[i] + " expects an argument but received none");
				return;
			}

			String var2 = args[i];
			byte   var3 = -1;
			switch (var2.hashCode())
			{
				case -729327018:
					if (var2.equals("-proxyPPTI"))
					{
						var3 = 2;
					}
					break;
				case 45114943:
					if (var2.equals("-test"))
					{
						var3 = 0;
					}
					break;
				case 862202913:
					if (var2.equals("-edgeThreshold"))
					{
						var3 = 1;
					}
			}

			switch (var3)
			{
				case 0:
					try
					{
						testFolder = args[i + 1];
						break;
					} catch (Exception var6)
					{
						System.err.println("Invalid argument for option " + args[i] + ": TestBed folder expected");
						return;
					}
				case 1:
					try
					{
						edgeThreshold = Integer.parseInt(args[i + 1]);
						break;
					} catch (Exception var5)
					{
						System.err.println("Invalid argument for option " + args[i] + ": Integer type expected");
						return;
					}
				case 2:
					proxyPPTI = true;
					break;
				default:
					System.err.println("Unknown option " + args[i]);
					return;
			}

			++i;
		}
	}

	evalFiles(proxyPPTI);
}

protected static double getResult ()
{
	return result;
}

protected static int getFails ()
{
	return fails;
}

protected static void evalFiles (boolean proxyPPTI)
{
	result = 0.0D;
	fails = 0;

	for (int index = 0; index < 100; ++index)
	{
		ArrayList points = new ArrayList();

		try
		{
			if (proxyPPTI)
			{
				System.setProperty("http.proxyHost", "proxy.ufr-info-p6.jussieu.fr");
				System.setProperty("http.proxyPort", "3128");
				System.setProperty("https.proxyHost", "proxy.ufr-info-p6.jussieu.fr");
				System.setProperty("https.proxyPort", "3128");
			}

			InputStream url = (new URL(
					  "https://www-apr.lip6.fr/~buixuan" + testFolder + "/input" + index + ".points")).openStream();
			BufferedReader input = new BufferedReader(new InputStreamReader(url));

			try
			{
				String line;
				while ((line = input.readLine()) != null)
				{
					String[] coordinates = line.split("\\s+");
					points.add(new Point(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])));
				}

				System.out.println("Input " + index + " successfully read. Computing...");
				ArrayList<Point> pts = (new DefaultTeam()).calculConnectedDominatingSet(points, edgeThreshold);
				System.out.println("   >>> Computation completed. Evaluating... ");
				if (!Evaluator.isValide(pts, points, edgeThreshold))
				{
					++fails;
				}
				else
				{
					result += Evaluator.score(pts);
				}

				System.out.println("   >>> Evaluation completed. Fails: " + fails);
				System.out.println("   >>> Average score (excluding fails): " + result / (double) (index + 1 - fails));
			} catch (IOException var17)
			{
				System.err.println("Exception: interrupted I/O.");
			} finally
			{
				try
				{
					input.close();
				} catch (IOException var16)
				{
					System.err.println("I/O exception: unable to close files.");
				}

			}
		} catch (Exception var19)
		{
			++fails;
			if (!(var19 instanceof FileNotFoundException) && !(var19 instanceof IOException))
			{
				System.err.println("Computation aborted with an exception." + var19);
			}
			else
			{
				System.err.println("Input file not found.");
			}
		}
	}

	System.out.println("--------------------------------------");
	System.out.println("");
	System.out.println("Total fails: " + fails);
	System.out.println("Average score: " + result / (double) (100 - fails));
}
}
