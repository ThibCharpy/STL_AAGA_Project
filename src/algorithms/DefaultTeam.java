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

  private List<Point> getActives(Point p, ArrayList<Point> points,
                                 List<Point> neighbors, Map<Color, List<Point>> coloredPoints, int edgeThreshold){
    //Parmi les voisins de p je prend les voisins blanc de ses voisins
    List<Point> whitePoints = coloredPoints.get(Color.WHITE);
    List<Point> res = new ArrayList<>();
    for (Point neighbor : neighbors){
      ArrayList<Point> actives = neighbor(neighbor,points,edgeThreshold);
      for (Point active : actives){
        if (whitePoints.contains(active) && !res.contains(active))
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
    whitePoints.remove(leader);
    List<Point> oldActives = new ArrayList<>();
    System.out.println("END INITIALIZE MIS");

    // ALGORITHM
    System.out.println("START ALGO MIS");
    while (!colorPoints.get(Color.WHITE).isEmpty()){
      System.out.println("ALGO whiteSize="+whitePoints.size());
      // Je transforme le leader en noir
      colorPoints.get(Color.BLACK).add(leader);
      List<Point> leaderNeighbors = neighbor(leader,points,edgeThreshold);
      List<Point> actives = getActives(leader,points,leaderNeighbors,
              colorPoints,edgeThreshold);
      boolean gotActives = false;
      if (!actives.isEmpty()) {
        // Si il y a des actifs
        gotActives = true;
        for (Point neighbor : leaderNeighbors) {
          //je colore en gris ses voisins
          whitePoints.remove(neighbor);
          colorPoints.get(Color.GREY).add(neighbor);
        }
        //j'ajoute les actifs aux anciens actifs si ils n'existent pas deja dedans
        for (Point active : actives){
          if (!oldActives.contains(active)){
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
      if (!gotActives && 0 < whitePoints.size()){
        //Si il n'y avait pas d'actifs
        // et que le graphe comporte toujours des blancs
        if (!oldActives.isEmpty()){
          // Si il y a des anciens actif
          // je selectionne celui de plus haut degré pour u'il devienne leader
          leader = getMaxDegreePointOfPointList(oldActives, points, edgeThreshold);
          // je l'enlève des anciens actifs
          oldActives.remove(leader);
        }else{
          //Si il ya plus d'ancien actifs
          if (1 < whitePoints.size()) {
            // Si il reste au moins deux points points non traité du graphe
            // J'en selectionne un au hasard ui devient leader
            //TODO : possibilité de choisir par le degré
            randomLeader = random.nextInt(whitePoints.size() - 1) + 1;
            leader = whitePoints.get(randomLeader);
          } else {
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
