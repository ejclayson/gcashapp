import javax.swing.*;
import javax.xml.transform.Result;
import java.awt.*;
import java.sql.*;

public class DashboardForm extends JFrame{


    private JButton logoutButton;
    private JLabel lblName;
    private JPanel dashboardPanel;

    private String name;


    public DashboardForm(String name){
        this.name = name;

        setTitle("Dashboard");
        setContentPane(dashboardPanel);
        setMinimumSize(new Dimension(500, 429));
        setSize(1200,700);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        //boolean hasRegisteredUser = connectToDatabase();

        //lblName.setText("Hello!");
        SwingUtilities.invokeLater(()->{
            boolean hasRegisteredUser = connectToDatabase();
            lblName.setText("Hello! " + name);
        });

        setVisible(true);
    }




    private boolean connectToDatabase(){
        boolean hasRegisteredUser = false;

        final String DB_URL = "jdbc:mysql://localhost:3306/gcashapp";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try(Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
            User user = new User();
            lblName.setText("Hello " + user.name+ "!");


//            Statement stmt = conn.createStatement();
//            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS gcashapp");
//            stmt.close();
//            conn.close();
//
//            conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
//            stmt = conn.createStatement();
//            String sql = "CREATE TABLE IF NOT EXISTS users(" + "id INT(10) NOT NULL PRIMARY KEY AUTO_INCREMENT," + "name VARCHAR(200) NOT NULL," + "email VARCHAR(200) NOT NULL UNIQUE," + "mobile VARCHAR(200)," + "pin VARCHAR(200) NOT NULL" + ")";
//            stmt.executeUpdate(sql);
//
//            stmt = conn.createStatement();
//            ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) FROM users");
//
//            if(resultSet.next()){
//                int numUsers = resultSet.getInt(1);
//                if(numUsers>0){
//                    hasRegisteredUser = true;
//                }
//            }



        }catch (Exception e){
            e.printStackTrace();
        }
        return hasRegisteredUser;
    }



    public static void main(String[] args) {
        DashboardForm myForm = new DashboardForm("");
        //User user;

    }


}
