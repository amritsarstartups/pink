
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Location_Victim extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Date d=new Date();
            String time=String.valueOf(d.getTime());
            String imei=request.getParameter("imei");
            String lat=request.getParameter("lat");
            String lng=request.getParameter("lng");
            System.out.println("Latitude"+lat);
            System.out.println("Longitude"+lng);
            String city=request.getParameter("city");
            city=city.replace("%5F"," ");
                  
            Class.forName("com.mysql.jdbc.Driver");
                       Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/women_security", "root", "system");
                       Statement stm = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                       ResultSet rs=stm.executeQuery("select * from locations_victim");
                       rs.moveToInsertRow();
                       rs.updateString("imei",imei);
                       rs.updateString("time",time);
                       rs.updateString("lat", lat);
                       rs.updateString("lng", lng);
                       rs.updateString("city", city);
                       rs.insertRow();
                       
                       
                       Statement stm1 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                       long l1=d.getTime();
                       double dlat=Double.parseDouble(lat);
                       double dlong=Double.parseDouble(lng);
                       ResultSet rs1=stm1.executeQuery("select * from locations_helper where city='"+city+"'");
                       while(rs1.next())
                       {
                          
                          long dat= Long.parseLong(rs1.getString("time"));
                          if(l1-dat<30000)
                          {
                           
                           System.out.println("IN TIME");
                           double ddlat=Double.parseDouble(rs1.getString("lat"));
                           double ddlng=Double.parseDouble(rs1.getString("lng"));
                           double dis=distance(ddlat, ddlng,dlat,dlong);
                           long di=(long)dis;
                              System.out.println(di);
                           if(di*1000<500)
                           {
                              System.out.println("IN DISTANCE");   
                              Statement stm3 = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                              ResultSet rs3=stm3.executeQuery("select * from locations_victim order by id desc");
                              rs3.next();
                              int id=rs3.getInt("id");
                              rs1.updateInt("near_by", id);
                              rs1.updateRow();
                              
                           }
                          }
                       }
                       
                       
            
           
            
            
        }catch(Exception e){e.printStackTrace();}
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
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
