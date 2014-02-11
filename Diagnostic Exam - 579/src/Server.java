
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Server implements Runnable{

  /**
   * @param args
   */
//DEFAULT IP
  public static String SERVERIP = "192.168.0.17";

  // DESIGNATE A PORT
  public static final int SERVERPORT = 5000;
  private ArrayList<AccelDataObject> accelData;
  //private ArrayList gpsData;
  private ServerSocket serverSocket;
  
  public void run() {
    List<String> dataLineSet;
    
      try {
              if (SERVERIP != null) {
                  serverSocket = new ServerSocket(SERVERPORT);
                  System.out.println("Server is Listening on port : " + SERVERPORT);
                  while (true) {
                      // LISTEN FOR INCOMING CLIENTS
                      Socket client = serverSocket.accept();
                      
                      try {
                          BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                          String line = null;
                          
                          while ((line = in.readLine()) != null) {
                              System.out.println(line);
                              dataLineSet = new ArrayList<String>(Arrays.asList(line.split("\\s* \\s*")));
                              
                              if(dataLineSet.get(0).equals("ACCELO:")){
                                System.out.println("ACCELO Entry");
                                System.out.println(dataLineSet.get(2));
                                System.out.println(dataLineSet.get(4));
                                System.out.println(dataLineSet.get(6));
                                Long ts = Long.parseLong(dataLineSet.get(2));
                                Double x = Double.parseDouble(dataLineSet.get(4));
                                Double y = Double.parseDouble(dataLineSet.get(6));
                                Double z = Double.parseDouble(dataLineSet.get(8));
                                
                                AccelDataObject accel_data_item = new AccelDataObject(ts,x,y,z);
                                accelData.add(accel_data_item);
                              }
                              else
                                if(dataLineSet.get(0).equals("GPS:")){
                                  System.out.println("GPS Entry");
                                  Long ts = Long.parseLong(dataLineSet.get(2));
                                  Double lat = Double.parseDouble(dataLineSet.get(4));
                                  Double lng = Double.parseDouble(dataLineSet.get(6));
                                  
                                  //GPSDataObject gps_data_item = new GPSDataObject(ts,lat,lng);
                                  //gpsData.add(gps_data_item);
                                }
                          }
                          break;
                      } catch (Exception e) {
                          e.printStackTrace();
                      }
                  }
              } else {
                  System.out.println("Couldn't detect internet connection!");
              }
          } catch (Exception e) {
              System.out.println("Error!");
              e.printStackTrace();
          }
      
      //openChart();
      
    }

  private void openChart() {
    if (accelData != null || accelData.size() > 0) {
        long t = accelData.get(0).getTimestamp();
        XYSeries series1 = new XYSeries("XYGraph");
        int i=0;
        for(i=0;i<accelData.size();i++){
          
          long time = accelData.get(i).getTimestamp();
          double x = accelData.get(i).getX();
          series1.add(time-t, x);
        }
        
        XYSeriesCollection dataset1 = new XYSeriesCollection();
        dataset1.addSeries(series1);
        
        JFreeChart chart = ChartFactory.createXYLineChart(
            "X Axis Accelo vs. Time",
            "Time",
            "Movement in X Axis",
            dataset1,
            PlotOrientation.VERTICAL,  // Plot Orientation
            true,                      // Show Legend
            true,                      // Use tooltips
            false                      // Configure chart to generate URLs?
            );
        try {
          ChartUtilities.saveChartAsJPEG(new File("chart1.jpg"), chart, 500, 300);
      } catch (IOException e) {
          System.err.println("Problem occurred creating chart.");
      }
    }

  }
    
  // GETS THE IP ADDRESS OF YOUR PHONE'S NETWORK
  private static String getLocalIpAddress() {
      try {
          for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
              NetworkInterface intf = en.nextElement();
              for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                  InetAddress inetAddress = enumIpAddr.nextElement();
                  if (!inetAddress.isLoopbackAddress()) { return inetAddress.getHostAddress().toString(); }
              }
          }
      } catch (SocketException ex) {
          ex.printStackTrace();
      }
      return null;
  }
  
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    SERVERIP = getLocalIpAddress();
    (new Thread(new Server())).start();
  }
  
  public class AccelDataObject {
    private long timestamp;
    private double x;
    private double y;
    private double z;
 
    public AccelDataObject(long timestamp, double x, double y, double z) {
        this.timestamp = timestamp;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }
    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }
    public double getZ() {
        return z;
    }
    public void setZ(double z) {
        this.z = z;
    }
 
    public String toString()
    {
        return "t="+timestamp+", x="+x+", y="+y+", z="+z;
    }
}

}


