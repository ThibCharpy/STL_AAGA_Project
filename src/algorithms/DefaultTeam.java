package algorithms;

import java.awt.Point;
import java.util.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class DefaultTeam {
  public ArrayList<Point> calculConnectedDominatingSet(ArrayList<Point> points, int edgeThreshold) {
    ArrayList<Point> pointsClone = (ArrayList<Point>)points.clone();
    Map<Color, List<Point>> result = MIS(pointsClone,edgeThreshold);
    System.out.println("MIS : size="+result.get(Color.BLACK).size());
    if (isMisValid(points,result,edgeThreshold))
      System.out.println("MIS VALID");
    else
      System.out.println("MIS INVALID");
    return (ArrayList<Point>) result.get(Color.BLACK);
  }

  private Map<Color, List<Point>> initialise(ArrayList<Point> points){
    System.out.println("Graphe size="+points.size());
    Map<Color, List<Point>> tmp = new HashMap<>();
    List<Point> whiteVertices = new ArrayList<>();
    tmp.put(Color.BLACK,new ArrayList<>());
    tmp.put(Color.GREY,new ArrayList<>());
    for (Point point : points){
     whiteVertices.add(point);
    }
    tmp.put(Color.WHITE, whiteVertices);
    return tmp;
  }

  private Point getMaxDegreePointOfPointList(List<Point> set, List<Point> points, int edgeThreshold){
    if (!set.isEmpty()){
      int maxIndex = 0;
      int max = neighbor(set.get(0), (ArrayList<Point>) points,edgeThreshold).size();
      Point maxDegreePoint = set.get(0);
      if (0 < set.size()) {
        for (int i = 1; i < set.size(); i++) {
          int degree = neighbor(set.get(i), (ArrayList<Point>) points, edgeThreshold).size();
          if (max < degree) {
            max = degree;
            maxIndex++;
          }
        }
      }
      if (0 < maxIndex)
        maxDegreePoint = set.get(maxIndex);
      return maxDegreePoint;
    }
    return null;
  }

  private List<Point> getActives(List<Point> neighbors, ArrayList<Point> points,
                                 Map<Color, List<Point>> coloredPoints, int edgeThreshold){
    //Parmi les voisins je prend les voisins blanc de ses voisins
    List<Point> whitePoints = coloredPoints.get(Color.WHITE);
    List<Point> res = new ArrayList<>();
    for (Point neighbor : neighbors){
      ArrayList<Point> actives = neighbor(neighbor,points,edgeThreshold);
      for (Point active : actives){
        if (whitePoints.contains(active))
          if (!res.contains(active))
            res.add(active);
      }
    }
    return res;
  }

  private Map<Color, List<Point>> MIS(ArrayList<Point> points, int edgeThreshold){
    Map<Color, List<Point>> colorPoints = initialise(points);

    // INITIALIZE
    System.out.println("START INITIALIZE MIS");
    Random random = new Random();
    random.setSeed(System.currentTimeMillis());
    int randomLeader = random.nextInt(points.size()-1) +1;
    //TODO : essayer de gerer une selection du leader avec un algo déidié (prendre + haut degré)
    //Choix au hasard du leader
    //TODO : possibilité de choix par le plus grand degré
    List<Point> whitePoints = colorPoints.get(Color.WHITE);
    Point leader = whitePoints.get(randomLeader);
    List<Point> oldActives = new ArrayList<>();
    whitePoints.remove(leader);
    Point newLeader = null;
    System.out.println("END INITIALIZE MIS");

    // ALGORITHM
    System.out.println("START ALGO MIS");
    while (!colorPoints.get(Color.WHITE).isEmpty()){
      //System.out.println("ALGO whiteSize="+whitePoints.size());
      System.out.print(whitePoints.size()+":DEBUT+");
      // Je transforme le leader en noir
      colorPoints.get(Color.BLACK).add(leader);
      List<Point> leaderNeighbors = neighbor(leader,points,edgeThreshold);
      boolean activesEmpty = true;
      if (!leaderNeighbors.isEmpty()) {
        System.out.print("VOISIN+");
        for (Point neighbor : leaderNeighbors) {
          //je colore en gris ses voisins
          // et je les enlève de la liste des sommet blancs
          whitePoints.remove(neighbor);
          colorPoints.get(Color.GREY).add(neighbor);
        }
        List<Point> actives = getActives(leaderNeighbors, points, colorPoints, edgeThreshold);
        if (!actives.isEmpty()) {
          System.out.print("ACTIVES+");
          // Si il y a des actifs
          // Je prend l'actif de plus haud degré
          newLeader = getMaxDegreePointOfPointList(actives, points, edgeThreshold);
          actives.remove(newLeader);
          oldActives = actives;
          activesEmpty = false;
        }
      }else{
        System.out.print("PASDEVOISIN+");
        if (!oldActives.isEmpty()){
          System.out.print("ANCIENNE+");
          newLeader = getMaxDegreePointOfPointList(oldActives, points, edgeThreshold);
          oldActives.remove(newLeader);
          activesEmpty = false;
        }
      }
      if (activesEmpty){
        if (1 < whitePoints.size()) {
          System.out.print("RANDOM+");
          randomLeader = random.nextInt(whitePoints.size() - 1) + 1;
          newLeader = whitePoints.get(randomLeader);
        }else{
          newLeader = whitePoints.get(0);
        }
      }
      // J'enlève le leader de l'ensemble des points blancs
      whitePoints.remove(leader);
      // je definis mon nouveau leader
      leader = newLeader;
      System.out.println("FIN");
    }

    System.out.println("END ALGO MIS");
    return colorPoints;
  }

  private boolean isMisValid(ArrayList<Point> points, Map<Color, List<Point>> colorPoints,
                             int edgeThreshold){
    if (!colorPoints.get(Color.WHITE).isEmpty()){
      ArrayList<Point> blackPoints = (ArrayList<Point>) colorPoints.get(Color.BLACK);
      ArrayList<Point> greyPoints = (ArrayList<Point>) colorPoints.get(Color.GREY);

      //Deux à deux non voisins
      System.out.println("START TEST 1 MIS");
      ArrayList<Point> pointsClone = (ArrayList<Point>) blackPoints.clone();
      for (Point p1 : blackPoints){
        ArrayList<Point> p1Neighbors = neighbor(p1,points,edgeThreshold);
        for (Point p2 : blackPoints){
          if (pointsClone.contains(p2)) {
            if (!p1.equals(p2)) {
              if (p1Neighbors.contains(p2)) {
                return false;
              }
            }
          }
        }
        pointsClone.remove(p1);
      }
      System.out.println("END TEST 1 MIS");

      //Un sommet hors du MIS à au moins un point du MIS comme voisin
      System.out.println("START TEST 2 MIS");
      boolean containBlack = false;
      for (Point point : greyPoints){
        ArrayList<Point> neighbors = neighbor(point,points,edgeThreshold);
        containBlack = false;
        for (Point neighbor : neighbors) {
          if (blackPoints.contains(neighbor))
            containBlack = true;
        }
        if (!containBlack)
          return false;
      }
      System.out.println("END TEST 2 MIS");

      return true;
    }
    return false;
  }

  //FILE PRINTER
  private void saveToFile(String filename,ArrayList<Point> result){
    int index=0;
    try {
      while(true){
        BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(filename+Integer.toString(index)+".points")));
        try {
          input.close();
        } catch (IOException e) {
          System.err.println("I/O exception: unable to close "+filename+Integer.toString(index)+".points");
        }
        index++;
      }
    } catch (FileNotFoundException e) {
      printToFile(filename+Integer.toString(index)+".points",result);
    }
  }
  private void printToFile(String filename,ArrayList<Point> points){
    try {
      PrintStream output = new PrintStream(new FileOutputStream(filename));
      int x,y;
      for (Point p:points) output.println(Integer.toString((int)p.getX())+" "+Integer.toString((int)p.getY()));
      output.close();
    } catch (FileNotFoundException e) {
      System.err.println("I/O exception: unable to create "+filename);
    }
  }

  //FILE LOADER
  private ArrayList<Point> readFromFile(String filename) {
    String line;
    String[] coordinates;
    ArrayList<Point> points=new ArrayList<Point>();
    try {
      BufferedReader input = new BufferedReader(
              new InputStreamReader(new FileInputStream(filename))
      );
      try {
        while ((line=input.readLine())!=null) {
          coordinates=line.split("\\s+");
          points.add(new Point(Integer.parseInt(coordinates[0]),
                  Integer.parseInt(coordinates[1])));
        }
      } catch (IOException e) {
        System.err.println("Exception: interrupted I/O.");
      } finally {
        try {
          input.close();
        } catch (IOException e) {
          System.err.println("I/O exception: unable to close "+filename);
        }
      }
    } catch (FileNotFoundException e) {
      System.err.println("Input file not found.");
    }
    return points;
  }


  private static ArrayList<Point> neighbor (Point p, ArrayList<Point> vertices, int edgeThreshold)
  {
    ArrayList<Point> result = new ArrayList<Point>();

    for (Point point : vertices)
      if (point.distance(p) < edgeThreshold && !point.equals(p))
        result.add((Point) point.clone());

    return result;
  }

  private static boolean isMember (ArrayList<Point> points, Point p)
  {
    for (Point point : points)
      if (point.equals(p))
        return true;
    return false;
  }
}

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static mysupportGUI.RandomPointsGenerator.distanceToCenter;

public class DefaultTeam
{

private static String filename = "input.points";

/* Optimization for not using Point.distance() method */
private static double distancecarree (Point p1, Point p2)
{
	double var1 = p1.getX() - p2.getX();
	double var2 = p1.getY() - p2.getY();
	return var1 * var1 + var2 * var2;
}

private static ArrayList<Point> neighbor (Point p, ArrayList<Point> vertices, int edgeThreshold)
{
	ArrayList<Point> result = new ArrayList<Point>();

	for (Point point : vertices)
		if (point.distance(p) < edgeThreshold && !point.equals(p))
			result.add((Point) point.clone());

	return result;
}

/* generate a random strongly connected graph */
public static void generateRandomGraph (int verticesNb, int xsize, int ysize, int edgeThreshold)
{
	try
	{
		PrintStream      output    = new PrintStream(new FileOutputStream(filename));
		ArrayList<Point> graph     = new ArrayList<>();
		Random           generator = new Random();
		for (int i = 0; i < verticesNb; i++)
		{
			graph.add(new Point(generator.nextInt(xsize), generator.nextInt(ysize)));
		}
		ArrayList<Point> notConnected = isConnexe(graph, edgeThreshold, xsize / 2, ysize / 2);
		int              graphSize    = notConnected.size();
		while (isConnexe(graph, edgeThreshold, xsize / 2, ysize / 2).size() != 0)
		{
			notConnected = isConnexe(graph, edgeThreshold, xsize / 2, ysize / 2);
			for (Point point : notConnected)
			{
				point.move(generator.nextInt(xsize), generator.nextInt(ysize));
			}
		}
		for (Point point : graph)
		{
			output.println(Integer.toString((int) point.getX()) + " " + Integer.toString((int) point.getY()));
		}
		output.close();
		notConnected = isConnexe(graph, edgeThreshold, xsize / 2, ysize / 2);
		System.out.println(notConnected.size());
	} catch (FileNotFoundException var6)
	{
		System.err.println("I/O exception: unable to create " + filename);
	}
}

public static void generateRandomDiskGraph (int verticesNb, int xsize, int ysize, int edgeThreshold)
{
	try
	{
		ArrayList<Point> graph     = new ArrayList<>();
		PrintStream      output    = new PrintStream(new FileOutputStream(filename));
		Random           generator = new Random();

		for (int i = 0; i < verticesNb; ++i)
		{
			int x;
			int y;
			do
			{
				x = generator.nextInt(xsize);
				y = generator.nextInt(ysize);
			}
			while (distanceToCenter(x, y) >= (double) 140 * 1.4D &&
			       (distanceToCenter(x, y) >= (double) 140 * 1.6D || generator.nextInt(5) != 1) &&
			       (distanceToCenter(x, y) >= (double) 140 * 1.8D || generator.nextInt(10) != 1) &&
			       (ysize / 5 >= x || x >= 4 * ysize / 5 || ysize / 5 >= y || y >= 4 * ysize / 5 ||
			        generator.nextInt(100) != 1));

			output.println(Integer.toString(x) + " " + Integer.toString(y));
			graph.add(new Point(x, y));
		}
		ArrayList<Point> notConnected = isConnexe(graph, edgeThreshold, xsize / 2, ysize / 2);
		int              graphSize    = notConnected.size();
		while (isConnexe(graph, edgeThreshold, xsize / 2, ysize / 2).size() != 0)
		{
			notConnected = isConnexe(graph, edgeThreshold, xsize / 2, ysize / 2);
			for (Point point : notConnected)
			{
				point.move(generator.nextInt(xsize), generator.nextInt(ysize));
			}
		}
		for (Point point : graph)
		{
			output.println(Integer.toString((int) point.getX()) + " " + Integer.toString((int) point.getY()));
		}
		output.close();
		notConnected = isConnexe(graph, edgeThreshold, xsize / 2, ysize / 2);
	} catch (FileNotFoundException var6)
	{
		System.err.println("I/O exception: unable to create " + filename);
	}
}

/* return the ArrayList of the points not connected with the center graphe, return.size()==0 if graphe is
strongly connected */
private static ArrayList<Point> isConnexe (ArrayList<Point> graphe, int edgeThreshold, int xCenter, int yCenter)
{
	ArrayList<Point> notConnected = (ArrayList<Point>) graphe.clone();
	ArrayList<Point> connected    = new ArrayList<Point>();
	Point            centre       = new Point(xCenter, yCenter);
	Point            plusProche   = graphe.get(0);
	for (Point p : graphe)
	{
		if (distancecarree(centre, p) < distancecarree(centre, plusProche))
			plusProche = p;
	}
	notConnected.remove(plusProche);
	connected.add(plusProche);
	while (true)
	{
		int              size           = notConnected.size();
		ArrayList<Point> connectedclone = (ArrayList<Point>) connected.clone();
		for (Point p : connectedclone)
		{
			for (Point q : neighbor(p, notConnected, edgeThreshold))
			{
				notConnected.remove(q);
				connected.add(q);
			}
		}
		if (size == notConnected.size())
			break;

	}
	return notConnected;
}

private static boolean isMember (ArrayList<Point> points, Point p)
{
	for (Point point : points)
		if (point.equals(p))
			return true;
	return false;
}

public ArrayList<Point> calculConnectedDominatingSet (ArrayList<Point> points, int edgeThreshold)
{
	ArrayList<Point>             pointsClone = (ArrayList<Point>) points.clone();
	Map<Color, ArrayList<Point>> result      = MIS(pointsClone, edgeThreshold);
	System.out.println("MIS : size=" + result.get(Color.BLACK).size());
	if (isMisValid(points, result, edgeThreshold))
		System.out.println("MIS VALID");
	else
		System.out.println("MIS INVALID");
	return (ArrayList<Point>) result.get(Color.BLACK);
}

private ArrayList<Point> localsearch2for1 (ArrayList<Point> dominantSet, ArrayList<Point> graphe, int edgeThreshold)
{
//	int              i      = 0, j, k;
	ArrayList<Point> rest   = (ArrayList<Point>) graphe.clone();
	double           tooFar = 3.5 * edgeThreshold * edgeThreshold;
	for (int i = 0; i < dominantSet.size(); i++)
	{
//		i++;
//		j = 0;
		Point ii = dominantSet.get(i);
		for (int j = i + 1; j < dominantSet.size(); j++)
		{
//			j++;
			Point jj = dominantSet.get(j);
			if (distancecarree(ii, jj) > tooFar)
				continue;
//			k = 0;
			for (Point kk : rest)
			{
				if (distancecarree(ii, kk) > tooFar || distancecarree(jj, kk) > tooFar)
					continue;

//				k++;
				ArrayList<Point> dsClone = (ArrayList<Point>) dominantSet.clone();
				dsClone.remove(ii);
				dsClone.remove(jj);
				dsClone.add(kk);
				if (isValid(graphe, dsClone, edgeThreshold))
				{
//					System.out.println("Valid trouvé après " + i + " x " + j + " x " + k + " itérations");
					return dsClone;
				}
			}
		}
	}
	return dominantSet;
}

private boolean isValid (ArrayList<Point> graphe, ArrayList<Point> dsClone, int edgeThreshold)
{
	return false;
}

private void saveToFile (String filename, ArrayList<Point> result)
{
	int index = 0;
	try
	{
		while (true)
		{
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(
					  filename + Integer.toString(index) + ".points")));
			try
			{
				input.close();
			} catch (IOException e)
			{
				System.err.println("I/O exception: unable to close " + filename + Integer.toString(index) + ".points");
			}
			index++;
		}
	} catch (FileNotFoundException e)
	{
		printToFile(filename + Integer.toString(index) + ".points", result);
	}
}

private void printToFile (String filename, ArrayList<Point> points)
{
	try
	{
		PrintStream output = new PrintStream(new FileOutputStream(filename));
		int         x, y;
		for (Point p : points)
			output.println(Integer.toString((int) p.getX()) + " " + Integer.toString((int) p.getY()));
		output.close();
	} catch (FileNotFoundException e)
	{
		System.err.println("I/O exception: unable to create " + filename);
	}
}

//FILE LOADER
private ArrayList<Point> readFromFile (String filename)
{
	String           line;
	String[]         coordinates;
	ArrayList<Point> points = new ArrayList<Point>();
	try
	{
		BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		try
		{
			while ((line = input.readLine()) != null)
			{
				coordinates = line.split("\\s+");
				points.add(new Point(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])));
			}
		} catch (IOException e)
		{
			System.err.println("Exception: interrupted I/O.");
		} finally
		{
			try
			{
				input.close();
			} catch (IOException e)
			{
				System.err.println("I/O exception: unable to close " + filename);
			}
		}
	} catch (FileNotFoundException e)
	{
		System.err.println(filename + "Input file not found.");
	}
	return points;
}

private void generate100instances ()
{

}

private Map<Color, ArrayList<Point>> initialise (ArrayList<Point> points)
{
	System.out.println("Graphe size=" + points.size());
	Map<Color, ArrayList<Point>> colorMap      = new HashMap<>();
	ArrayList<Point>             whiteVertices = new ArrayList<>();
	colorMap.put(Color.BLACK, new ArrayList<>());
	colorMap.put(Color.GREY, new ArrayList<>());
	whiteVertices.addAll(points);
	colorMap.put(Color.WHITE, whiteVertices);
	return colorMap;
}

private Point getMaxDegreePointOfPointList (ArrayList<Point> set, ArrayList<Point> points, int edgeThreshold)
{
	if (!set.isEmpty())
	{
		int   maxIndex       = 0;
		int   max            = neighbor(set.get(0), (ArrayList<Point>) points, edgeThreshold).size();
		Point maxDegreePoint = set.get(0);
		if (0 < set.size())
		{
			for (int i = 1; i < set.size(); i++)
			{
				int degree = neighbor(set.get(i), (ArrayList<Point>) points, edgeThreshold).size();
				if (max < degree)
				{
					max = degree;
					maxIndex++;
				}
			}
		}
		if (0 < maxIndex)
			maxDegreePoint = set.get(maxIndex);
		return maxDegreePoint;
	}
	return null;
}

private ArrayList<Point> getActives (Point p,
                                     ArrayList<Point> points,
                                     ArrayList<Point> neighbors,
                                     Map<Color, ArrayList<Point>> coloredPoints,
                                     int edgeThreshold)
{
	//Parmi les voisins de p je prend les voisins blanc de ses voisins
	ArrayList<Point> whitePoints = coloredPoints.get(Color.WHITE);
	ArrayList<Point> res         = new ArrayList<>();
	for (Point neighbor : neighbors)
	{
		ArrayList<Point> actives = neighbor(neighbor, points, edgeThreshold);
		for (Point active : actives)
		{
			if (whitePoints.contains(active) && !res.contains(active))
				res.add(active);
		}
	}
	return res;
}

private Map<Color, ArrayList<Point>> MIS (ArrayList<Point> points, int edgeThreshold)
{
	Map<Color, ArrayList<Point>> colorPoints = initialise(points);

	// INITIALIZE
	System.out.println("START INITIALIZE MIS");
	Random random = new Random();
	random.setSeed(System.currentTimeMillis());
	int randomLeader = random.nextInt(points.size() - 1) + 1;
	//TODO : essayer de gerer une selection du leader avec un algo déidié (prendre + haut degré)
	//Choix au hasard du leader
	//TODO : possibilité de choix par le plus grand degré
	ArrayList<Point> whitePoints = colorPoints.get(Color.WHITE);
	Point            leader      = whitePoints.get(randomLeader);
	whitePoints.remove(leader);
	ArrayList<Point> oldActives = new ArrayList<>();
	System.out.println("END INITIALIZE MIS");

	// ALGORITHM
	System.out.println("START ALGO MIS");
	while (!colorPoints.get(Color.WHITE).isEmpty())
	{
		System.out.println("ALGO whiteSize=" + whitePoints.size());
		// Je transforme le leader en noir
		colorPoints.get(Color.BLACK).add(leader);
		ArrayList<Point> leaderNeighbors = neighbor(leader, points, edgeThreshold);
		ArrayList<Point> actives         = getActives(leader, points, leaderNeighbors, colorPoints, edgeThreshold);
		boolean          gotActives      = false;
		if (!actives.isEmpty())
		{
			// Si il y a des actifs
			gotActives = true;
			for (Point neighbor : leaderNeighbors)
			{
				//je colore en gris ses voisins
				whitePoints.remove(neighbor);
				colorPoints.get(Color.GREY).add(neighbor);
			}
			//j'ajoute les actifs aux anciens actifs si ils n'existent pas deja dedans
			for (Point active : actives)
			{
				if (!oldActives.contains(active))
				{
					oldActives.add(active);
				}
			}
			System.out.println("ALGO: actives size = " + oldActives.size());
			// Je prend l'actif de plus haud degré
			leader = getMaxDegreePointOfPointList(oldActives, points, edgeThreshold);
			oldActives.remove(leader);
		}
		// J'enlève le leader de l'ensemble des points blancs
		whitePoints.remove(leader);
		if (!gotActives && 0 < whitePoints.size())
		{
			//Si il n'y avait pas d'actifs
			// et que le graphe comporte toujours des blancs
			if (!oldActives.isEmpty())
			{
				// Si il y a des anciens actif
				// je selectionne celui de plus haut degré pour u'il devienne leader
				leader = getMaxDegreePointOfPointList(oldActives, points, edgeThreshold);
				// je l'enlève des anciens actifs
				oldActives.remove(leader);
			}
			else
			{
				//Si il ya plus d'ancien actifs
				if (1 < whitePoints.size())
				{
					// Si il reste au moins deux points points non traité du graphe
					// J'en selectionne un au hasard ui devient leader
					//TODO : possibilité de choisir par le degré
					randomLeader = random.nextInt(whitePoints.size() - 1) + 1;
					leader = whitePoints.get(randomLeader);
				}
				else
				{
					// Si il reste exactement un sommet je le selectionne
					leader = whitePoints.get(0);
				}
			}
			whitePoints.remove(leader);
		}
	}

	System.out.println("END ALGO MIS");
	return colorPoints;
}

private boolean isMisValid (ArrayList<Point> points, Map<Color, ArrayList<Point>> colorPoints, int edgeThreshold)
{
	if (!colorPoints.get(Color.WHITE).isEmpty())
	{
		ArrayList<Point> blackPoints = (ArrayList<Point>) colorPoints.get(Color.BLACK);
		ArrayList<Point> greyPoints  = (ArrayList<Point>) colorPoints.get(Color.GREY);

		//Deux à deux non voisins
		System.out.println("START TEST 1 MIS");
		ArrayList<Point> pointsClone = (ArrayList<Point>) blackPoints.clone();
		for (Point p1 : blackPoints)
		{
			ArrayList<Point> p1Neighbors = neighbor(p1, points, edgeThreshold);
			for (Point p2 : blackPoints)
			{
				if (pointsClone.contains(p2))
				{
					if (!p1.equals(p2))
					{
						if (p1Neighbors.contains(p2))
						{
							return false;
						}
					}
				}
			}
			pointsClone.remove(p1);
		}
		System.out.println("END TEST 1 MIS");

		//Un sommet hors du MIS à au moins un point du MIS comme voisin
		System.out.println("START TEST 2 MIS");
		boolean containBlack = false;
		for (Point point : greyPoints)
		{
			ArrayList<Point> neighbors = neighbor(point, points, edgeThreshold);
			containBlack = false;
			for (Point neighbor : neighbors)
			{
				if (blackPoints.contains(neighbor))
					containBlack = true;
			}
			if (!containBlack)
				return false;
		}
		System.out.println("END TEST 2 MIS");

		return true;
	}
	return false;
}

}