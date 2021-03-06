
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class Server{

  public static String SERVERIP = "192.168.0.17";
  public static ArrayList<AccelDataObject> accelData = new ArrayList<Server.AccelDataObject>();
  public static final int SERVERPORT = 5000;
  public static boolean xMovDetect = false;
  public static boolean yMovDetect = false;
  public static boolean zMovDetect = false;
  public static double totDist = 0;
  public static boolean movementDetected = false;
  public static ArrayList<GPSDataObject> gpsData = new ArrayList<Server.GPSDataObject>();
  //private ServerSocket serverSocket;
  
  private static void openChart() {
    if (accelData != null && accelData.size() > 0) {
        double t = accelData.get(0).getTimestamp();
        XYSeries series1 = new XYSeries("X axis");
        XYSeries series2 = new XYSeries("Y Axis");
        XYSeries series3 = new XYSeries("Z Axis");
        
        int i=0;
        for(i=0;i<accelData.size();i++){
          
          double time = accelData.get(i).getTimestamp();
          double x = accelData.get(i).getX();
          if(Math.abs(x)>2) xMovDetect = true;
          
          double y = accelData.get(i).getY();
          if(Math.abs(y)>2) yMovDetect = true;
          
          double z = accelData.get(i).getZ();
          if(Math.abs(z)>2) zMovDetect = true;
          
          series1.add(time-t, x);
          series2.add(time-t, y);
          series3.add(time-t, z);
        }
        
        XYSeriesCollection dataset1 = new XYSeriesCollection();
        dataset1.addSeries(series1);
        dataset1.addSeries(series2);
        dataset1.addSeries(series3);
        JFreeChart chart = ChartFactory.createXYLineChart(
            "All Axes Accelo vs. Time",
            "Time",
            "Movement in Each Axis",
            dataset1,
            PlotOrientation.VERTICAL,  
            true,                      
            true,                      
            false                      
            );
        try {
          ChartUtilities.saveChartAsPNG(new File("chart1.png"), chart, 800, 500);
      } catch (IOException e) {
          System.err.println("Problem occurred creating chart.");
      }
        
    }
    
    if(gpsData!=null && gpsData.size()>2){
      XYSeries gpsSeries = new XYSeries("Location Tracking");
      int i=0;
      for(i=0;i<gpsData.size();i++){
        
        double lat = gpsData.get(i).getLat();
        double lng = gpsData.get(i).getLng();
        gpsSeries.add(lat, lng);
        
      }
      
      XYSeriesCollection dataset2 = new XYSeriesCollection();
      dataset2.addSeries(gpsSeries);
      
      JFreeChart chart = ChartFactory.createXYLineChart(
          "Latitude vs. Longitude Plot",
          "Latitude",
          "Longitude",
          dataset2,
          PlotOrientation.VERTICAL,  
          true,                      
          true,                      
          false                      
          );
      try {
        ChartUtilities.saveChartAsPNG(new File("chart2.png"), chart, 800, 500);
    } catch (IOException e) {
        System.err.println("Problem occurred creating chart.");
    }
      }

  }
  
  private static double getTotalDistance(){
    double totDistance = 0;
    if (gpsData != null && gpsData.size() > 1){
      int i=0;
      for(i=0;i<(gpsData.size()-1);i++){
        totDistance+=getDistance(gpsData.get(i+1).getLat(), 
            gpsData.get(i+1).getLng(), 
            gpsData.get(i).getLat(), 
            gpsData.get(i).getLng());
      }
      return totDistance;
    }
    else{
      System.out.println("Not enough GPS entries");
      return 0;
    }
      
  }
     
  private static double getDistance( double LatNew, double LongNew, double LatOld, double LongOld ){
    
    double degToRad = (Math.PI/180);
    double distance = 0;

    try{
        double deltaLong = (LongNew - LongOld) * degToRad;
        double deltaLat = (LatNew - LatOld) * degToRad;
        double a =
            Math.pow(Math.sin(deltaLat / 2.0), 2)
                + Math.cos(LatOld * degToRad)
                * Math.cos(LatNew * degToRad)
                * Math.pow(Math.sin(deltaLong / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        distance = 6367 * c;
        System.out.println("Actual distance : " + distance);
        
        if(distance*1000 < 3) distance = 0;
        
        System.out.println("Returning distance : " + distance);
        return distance;

    } catch(Exception e){
        e.printStackTrace();
    }
    
    return 0;
    
  }
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    //SERVERIP = "192.168.0.17";
    
    System.out.println("Starting");
    List<String> dataLineSet; 
    AccelDataObject accel_data_item;
    
      try {
              if (SERVERIP != null) {
                System.out.println("Creating socket");
                
                  ServerSocket serverSocket = new ServerSocket(SERVERPORT);
                  System.out.println("Socket created " + SERVERIP);
                  
                  while (true) {
                      // LISTEN FOR INCOMING CLIENTS
                      System.out.println("Listening...");
                      Socket client = serverSocket.accept();
                      System.out.println("Comes here");
                      try {
                          BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                          String line = null;
                          File file = new File("accel_log.txt");
                          if(file.exists()){
                            PrintWriter writer = new PrintWriter(file);
                            writer.print("");
                            writer.close();
                          }
                          BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
                          while ((line = in.readLine()) != null) {
                              System.out.println(line);
                              buf.append(line);
                              buf.newLine();
                              dataLineSet = new ArrayList<String>(Arrays.asList(line.split("\\s* \\s*")));
                              
                              if(dataLineSet.get(0).equals("ACCELO:")){
                                System.out.println("ACCELO Entry");
                                System.out.println(dataLineSet.get(2));
                                System.out.println(dataLineSet.get(4));
                                System.out.println(dataLineSet.get(6));
                                Double ts =Double.parseDouble(dataLineSet.get(2));
                                Double x = Double.parseDouble(dataLineSet.get(4));
                                Double y = Double.parseDouble(dataLineSet.get(6));
                                Double z = Double.parseDouble(dataLineSet.get(8));
                                
                                accel_data_item = new Server().new AccelDataObject(ts,x,y,z);
                                accelData.add(accel_data_item);
                              }
                              else
                                if(dataLineSet.get(0).equals("GPS:")){
                                  System.out.println("GPS Entry");
                                  Double ts = Double.parseDouble(dataLineSet.get(2));
                                  Double lat = Double.parseDouble(dataLineSet.get(4));
                                  Double lng = Double.parseDouble(dataLineSet.get(6));
                                  
                                  GPSDataObject gps_data_item = new Server().new GPSDataObject(ts,lat,lng);
                                  gpsData.add(gps_data_item);
                                }
                          }

                          buf.close();
                      } catch (Exception e) {
                          e.printStackTrace();
                      }
                      System.out.println("Closing client socket");
                      client.close();
                      
                      totDist = getTotalDistance()*1000;
                      openChart();
                      if(xMovDetect&&yMovDetect&&zMovDetect){
                        if(totDist>2.5){
                          movementDetected = true;
                          System.out.println("Movement Detected");
                          System.out.println("Total Distance moved is : " + totDist + " meters");
                        }
                        else {
                          System.out.println("No Movement Detected");
                        }
                      }
                      else
                      {
                        System.out.println("No Movement Detected");
                      }
                      
                      Show s = new Server().new Show(movementDetected);
                      s.setVisible(true);
                      
                      break;
                  }
                  System.out.println("Closing server socket object");
                  serverSocket.close();
              } else {
                  System.out.println("Couldn't detect internet connection!");
              }
          } catch (Exception e) {
              System.out.println("Error!");
              e.printStackTrace();
          }
      
  }
  
 public class Show extends JFrame{
   /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public Show(final boolean movementDetected){
     
     if(movementDetected){
       System.out.println("HERE TOO");
       
       setLayout(new GridLayout(1,2));
       setSize(1900, 600);
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       ImageIcon image1 = new ImageIcon("chart1.png");
       ImageIcon image2 = new ImageIcon("chart2.png");
       add(new JLabel(image1));
       add(new JLabel(image2));
       setTitle("Movement Detected : " + totDist + " meters");
       pack();
       
     }
     else {
       
       setLayout(new GridLayout(1,1));
       setSize(800, 600);
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       ImageIcon image1 = new ImageIcon("chart1.png");
       add(new JLabel(image1));
       setTitle("No Movement Detected!");
       pack();
       
     }
     
   }
 }

 
  
  public class AccelDataObject {
    private double timestamp;
    private double x;
    private double y;
    private double z;
 
    public AccelDataObject(double timestamp, double x, double y, double z) {
        this.timestamp = timestamp;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public double getTimestamp() {
        return timestamp;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
    }
}
  public class GPSDataObject {
    
    private double Lat;
    private double Lng;
    private double timestamp;
    
    public GPSDataObject(double timestamp, double Lat, double Lng) {
      this.timestamp = timestamp;
      this.Lat = Lat;
      this.Lng = Lng;
      
  }
    public double getTimestamp() {
      return timestamp;
  }
    
    public double getLat() {
      return Lat;
  }
    public double getLng() {
      return Lng;
  }
  }

}


