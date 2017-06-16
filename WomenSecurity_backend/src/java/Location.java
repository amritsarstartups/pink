
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Location extends HttpServlet 
{

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            
    {
        String imei,lat,lng,city,time;
        Date d=new Date();
        
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) 
        {
            time=String.valueOf(d.getTime());
        imei=request.getParameter("imei");
        lat=request.getParameter("lat");
        lng=request.getParameter("lng");
        city=request.getParameter("city");
        city=city.replace("%5F"," ");
                       Class.forName("com.mysql.jdbc.Driver");
                       Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/women_security", "root", "system");
                       Statement stm = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                       ResultSet rs=stm.executeQuery("select * from locations_helper");
                       rs.moveToInsertRow();
                       rs.updateString("imei",imei);
                       rs.updateString("time",time);
                       rs.updateString("lat", lat);
                       rs.updateString("lng", lng);
                       rs.updateString("city", city);
                       rs.insertRow();
                       Statement stm1 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                       ResultSet rs1=stm1.executeQuery("select * from locations_helper where imei="+imei+" order by id desc");
                       while(rs1.next())
                       {
                           System.out.println("IN WHILE"); 
                         if(rs1.getInt("near_by")!=0)
                         {
                             int id=rs1.getInt("near_by");
                             System.out.println("IN IF");
                            double la=Double.parseDouble(rs1.getString("lat"));
                            double ln=Double.parseDouble(rs1.getString("lng"));
                            double dis=distance(la, ln, Double.parseDouble(lat), Double.parseDouble(lng));
                            if(dis*1000<500)
                            {
                                System.out.println("IN INNER IF");
                               Statement stm2 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                               ResultSet rs2=stm2.executeQuery("select * from locations_victim where id="+id);
                               rs2.next();
                               String llat=rs2.getString("lat");
                               String llng=rs2.getString("lng");
                               out.println("needhelp_"+llat+"_"+llng);
                               break;
                            }
                                
                            
                         }
                       }
                       
                       
                                    
                       
       }catch(Exception e){e.printStackTrace();}
  
    }
        
    
    @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    
    @Override
    public String getServletInfo()
    {
        return "Short description";
   
    }
    private double distance(double lat1, double lon1, double lat2, double lon2) {
    double theta = lon1 - lon2;
    double dist = Math.sin(deg2rad(lat1)) 
                    * Math.sin(deg2rad(lat2))
                    + Math.cos(deg2rad(lat1))
                    * Math.cos(deg2rad(lat2))
                    * Math.cos(deg2rad(theta));
    dist = Math.acos(dist);
    dist = rad2deg(dist);
    dist = dist * 60 * 1.1515*1.609344;
    return (dist);
}

private double deg2rad(double deg) {
    return (deg * Math.PI / 180.0);
}

private double rad2deg(double rad) {
    return (rad * 180.0 / Math.PI);
}

}
