package sim;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class SQL {

    Connection conn;
    PreparedStatement preparedStmt;

    public SQL() throws SQLException {

        try {
            String myDriver = "com.mysql.jdbc.Driver";
            String db = "jdbc:mysql://localhost/traffic_sim";
            Class.forName(myDriver);
            conn = DriverManager.getConnection(db, "Kahveci", "123456");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void Vehicle_Insert(String VType, String VName) throws SQLException {

        String query = " Insert Into vehicle (VType,VName) Values (?, ?)";

        preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString(1, VType);
        preparedStmt.setString(2, VName);
        preparedStmt.execute();

    }

    public void Node_Insert(String NName) throws SQLException {

        String query = " Insert Into node (NodeName) Values (?)";

        preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString(1, NName);
        preparedStmt.execute();

    }

    public void Time_Insert_V_N(int time, String nodeName, String vehicleName) throws SQLException {

        String query = "SELECT NodeId FROM node WHERE node.NodeName='"+nodeName+"'";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(query);
        int nodeId = -1;

        while(rs.next()){
            nodeId=rs.getInt("NodeId");
        }
        String query1 = "SELECT VehicleId FROM vehicle WHERE vehicle.VName="+"'"+vehicleName+"'";
        Statement st1 = conn.createStatement();
        ResultSet rs1 = st1.executeQuery(query1);

        int vehicleId = -1;

        while(rs1.next()){
            vehicleId=rs1.getInt("VehicleId");
        }

        String query2 = " Insert Into time (Time,NodeId,VehicleId) Values (?,?,?)";

        preparedStmt = conn.prepareStatement(query2);
        preparedStmt.setInt(1, time);
        preparedStmt.setInt(2, nodeId);
        preparedStmt.setInt(3, vehicleId);
        preparedStmt.execute();

    }
    public void Time_Select(int time) throws SQLException
    {
        String query1 = "SELECT NodeName,VType,VName from time "
                + "left join node on node.NodeId=time.NodeId "
                + "left join vehicle on vehicle.VehicleId=time.VehicleId "
                + "where Time.Time="+time+"";
        Statement st = conn.createStatement();

        ResultSet rs = st.executeQuery(query1);

        while (rs.next())
        {

            String node_name= rs.getString("NodeName");
            String vtype= rs.getString("VType");
            String vname= rs.getString("VName");
            System.out.println(node_name+" "+vtype+" "+vname);



        }


    }

}
