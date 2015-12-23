package sim;

import sun.util.locale.StringTokenIterator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public void Node_Insert(String NName, String NodeE, String NodeS, String NodeW, String NodeN) {
        try {
            String query = " Insert Into node (NodeName,SimId,East,South,West,North) Values (?,?,?,?,?,?)";
            preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, NName);
            preparedStmt.setInt(2, simno);
            preparedStmt.setString(3, NodeE);
            preparedStmt.setString(4, NodeS);
            preparedStmt.setString(5, NodeW);
            preparedStmt.setString(6, NodeN);
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
            String query1 = "SELECT VehicleId FROM vehicle WHERE vehicle.VName=" + "'" + vehicleName + "' and vehicle.SimId=" + simno + "";
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

//SELECT * from date
            //LEFT JOIN simulation on simulation.SimId=date.SimId
            // where date.Date='20-12-2015'

            String querys = " Insert Into simulation (SimNo,TimeId) Values (?,?)";
            preparedStmt = conn.prepareStatement(querys);
            preparedStmt.setInt(1, simno);
            preparedStmt.setInt(2, timeId);
            preparedStmt.execute();

            String queryss = "SELECT SimId FROM simulation ORDER BY SimId DESC LIMIT 1";
            Statement stss = conn.createStatement();
            ResultSet rsss = stss.executeQuery(queryss);

            int simId = -1;

            while (rsss.next()) {
                simId = rsss.getInt("SimId");
            }
            String Date = now("yyyy-MM-dd");
            String queryd = " Insert Into date (Date,SimId) Values (?,?)";
            preparedStmt = conn.prepareStatement(queryd);
            preparedStmt.setString(1, Date);
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

    public String now(String dateFormat) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(cal.getTime());
    }

    public void selectN(int simno, Simulation s) {
        try {

            String query = "SELECT * FROM node WHERE node.SimId=" + simno + "";//node ve komşuluklarını çekiyoruz haritaya nodeları yerleştirmek için
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            int nodeId = -1;
            s.setMap(new Map());
            while (rs.next()) {
                s.getMap().addNode(new Node(rs.getString("NodeName")));
            }
            ResultSet rs1 = st.executeQuery(query);
            while (rs1.next()) {
                if (rs1.getString("East") != "0") {
                    s.getMap().addEdge(s.getMap().getNode(rs1.getString("NodeName")), s.getMap().getNode(rs1.getString("East")), 0);
                }
                if (rs1.getString("South") != "0") {
                    s.getMap().addEdge(s.getMap().getNode(rs1.getString("NodeName")), s.getMap().getNode(rs1.getString("South")), 1);
                }
                if (rs1.getString("West") != "0") {
                    s.getMap().addEdge(s.getMap().getNode(rs1.getString("NodeName")), s.getMap().getNode(rs1.getString("West")), 2);
                }
                if (rs1.getString("North") != "0") {
                    s.getMap().addEdge(s.getMap().getNode(rs1.getString("NodeName")), s.getMap().getNode(rs1.getString("North")), 3);
                }

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<String> selectSim(String date) {
        ArrayList<String> sim = new ArrayList<>();
        try {//dateda yapılan simuasynlar gelıyor
            String query = "SELECT simulation.SimNo from date LEFT JOIN simulation on simulation.SimId=date.SimId WHERE date.Date='" + date + "'";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            int nodeId = -1;
            String b = "";
            while (rs.next()) {

                String k = Integer.toString(rs.getInt("SimNo"));

                if(!b.equals(k)) {
                    b=k;
                    sim.add(k);
                }

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sim;
    }

    public ArrayList<String> selectTime(int simno) {
        //sim yapılan timelar geliyor
        ArrayList<String> time = new ArrayList<>();
        try {
            String query = " SELECT time.Time from simulation LEFT JOIN time on time.TimeId=simulation.TimeId WHERE simulation.SimNo=" + simno + "";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(query);
            int nodeId = -1;
String b="";
            while (rs.next()) {
                String k=Integer.toString(rs.getInt("Time"));
                if(!b.equals(k)) {
                    b=k;
                    time.add(k);
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return time;
    }
    //SELECT vehicle.VType,vehicle.VName,node.NodeName from date LEFT JOIN simulation on simulation.SimId=date.SimId LEFT JOIN time on time.TimeId=simulation.TimeId LEFT JOIN vehicle on vehicle.VehicleId=time.VehicleId LEFT join node on node.NodeId=time.NodeId WHERE date.Date="21-12-2015" and simulation.SimNo=4 and time.Time=0
// hangi nodda hangi aracın bulunduğu seçilen zamanda ona gore sadece ekrana yazdırma yapılacak
}
