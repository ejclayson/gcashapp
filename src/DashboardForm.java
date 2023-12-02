import javax.swing.*;
import javax.xml.transform.Result;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DashboardForm extends JFrame{


    private JButton logoutButton;
    private JLabel lblName;
    private JPanel dashboardPanel;
    private JButton changePinButton;

    private String name;


    public DashboardForm(String name){
        this.name = name;

        setTitle("Dashboard");
        setContentPane(dashboardPanel);
        setMinimumSize(new Dimension(500, 429));
        //setSize(1200,700);
        setSize(450,474);
        setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        SwingUtilities.invokeLater(()->{
            boolean hasRegisteredUser = connectToDatabase();
            lblName.setText("Hello! " + name);
        });


        changePinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String m = JOptionPane.showInputDialog("Please input new pin?");
                final String DB_URL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12666768";
                final String USERNAME = "sql12666768";
                final String PASSWORD = "YxDac3ZBu9";

                try(Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){

                    PreparedStatement preparedStatement = conn.prepareStatement("update users set pin = ? where name = ?");
                    preparedStatement.setString(1, m);
                    preparedStatement.setString(2, name);
                    preparedStatement.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Pin is now updated to " + m + "\nYou'll now be logout");
                    dashboardPanel.setVisible(false);
                    dispose();
                    LoginForm myLoginForm = new LoginForm(null);
                    myLoginForm.setVisible(true);
                }catch (SQLException e1){
                    e1.printStackTrace();
                }
            }
        });


        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
                dispose();
            }
        });
    }



    private boolean connectToDatabase(){
        boolean hasRegisteredUser = false;

        final String DB_URL = "jdbc:mysql://localhost:3306/gcashapp";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try(Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
            User user = new User();
            lblName.setText("Hello " + user.name+ "!");



        }catch (Exception e){
            e.printStackTrace();
        }
        return hasRegisteredUser;
    }



    public static void main(String[] args) {
        DashboardForm myForm = new DashboardForm("");


    }


}