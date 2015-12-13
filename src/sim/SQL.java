package sim;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
public class SQL {

    Connection conn;
    PreparedStatement preparedStmt;
    int simno;

    public SQL() {

        try {
            String myDriver = "com.mysql.jdbc.Driver";
            String db = "jdbc:mysql://localhost/traffic_sim";
            Class.forName(myDriver);
            conn = DriverManager.getConnection(db, "Kahveci", "123456");
            simno = 0;
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void Vehicle_Insert(String VType, String VName) {
        try {

            String query = "Insert Into vehicle (VType,VName,SimId) Values (?, ?,?)";

            preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, VType);
            preparedStmt.setString(2, VName);
            preparedStmt.setInt(3, simno);
            preparedStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void Sim_Control() {
        try {

            String query1 = "SELECT SimId FROM node ORDER BY SimId DESC LIMIT 1";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query1);
            if (rs.next() == false) {
                simno = 1;

            } else {
                simno = rs.getInt("SimId") + 1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void Node_Insert(String NName) {
        try {
            String query = " Insert Into node (NodeName,SimId) Values (?,?)";
            preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, NName);
            preparedStmt.setInt(2, simno);
            preparedStmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void Time_Insert_V_N(int time, String nodeName, String vehicleName) {
        try {
            String query = "SELECT NodeId FROM node WHERE node.NodeName='" + nodeName + "' and node.SimId=" + simno + "";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            int nodeId = -1;

            while (rs.next()) {
                nodeId = rs.getInt("NodeId");
            }
            String query1 = "SELECT VehicleId FROM vehicle WHERE vehicle.VName=" + "'" + vehicleName + "'";
            Statement st1 = conn.createStatement();
            ResultSet rs1 = st1.executeQuery(query1);

            int vehicleId = -1;

            while (rs1.next()) {
                vehicleId = rs1.getInt("VehicleId");
            }

            String query2 = " Insert Into time (Time,NodeId,VehicleId) Values (?,?,?)";

            preparedStmt = conn.prepareStatement(query2);
            preparedStmt.setInt(1, time);
            preparedStmt.setInt(2, nodeId);
            preparedStmt.setInt(3, vehicleId);
            preparedStmt.execute();

            String queryt = "SELECT TimeId FROM time ORDER BY TimeId DESC LIMIT 1";
            Statement stt = conn.createStatement();
            ResultSet rst = stt.executeQuery(queryt);

            int timeId = -1;

            while (rst.next()) {
                timeId = rst.getInt("TimeId");
            }


            String querys = " Insert Into simulation (SimNo,TimeId) Values (?,?)";
            preparedStmt = conn.prepareStatement(querys);
            preparedStmt.setInt(1, simno);
            preparedStmt.setInt(2, timeId);
            preparedStmt.execute();

            String queryss = "SELECT SimId FROM simulation ORDER BY SimId DESC LIMIT 1";
            Statement stss = conn.createStatement();
            ResultSet rsss = stt.executeQuery(queryss);

            int simId = -1;

            while (rsss.next()) {
                simId = rsss.getInt("SimId");
            }
            String Date = now("dd-MM-yyyy");
            String queryd = " Insert Into date (Date,SimId) Values (?,?)";
            preparedStmt = conn.prepareStatement(queryd);
            preparedStmt.setString(1,Date);
            preparedStmt.setInt(2, simId);
            preparedStmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

//SELECT vehicle.VName,vehicle.VType,node.NodeName from date left JOIN simulation on simulation.SimId=date.SimId LEFT JOIN time on time.TimeId=simulation.SimId LEFT JOIN vehicle on vehicle.VehicleId=time.VehicleId LEFT join node on node.NodeId=time.NodeId where date.Date='12-12-2015' and simulation.SimNo=2 and time.Time=0
    }


    public void Time_Select(int time) {
        try {
            String query1 = "SELECT NodeName,VType,VName from time "
                    + "left join node on node.NodeId=time.NodeId "
                    + "left join vehicle on vehicle.VehicleId=time.VehicleId "
                    + "where Time.Time=" + time + "";
            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery(query1);

            while (rs.next()) {

                String node_name = rs.getString("NodeName");
                String vtype = rs.getString("VType");
                String vname = rs.getString("VName");
                System.out.println(node_name + " " + vtype + " " + vname);


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public  String now(String dateFormat)
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(cal.getTime());
    }

}
