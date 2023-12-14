import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DashboardForm extends JFrame{


    private JButton logoutButton;
    private JLabel lblName;
    private JPanel dashboardPanel;
    private JButton changePinButton;
    private JLabel lblAmount;
    private JButton cashInButton;

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
            boolean hasRegisteredBalance = checkBalance();
            //boolean hasRegisteredBalance = checkBalance();
//            CheckBalance();
            lblName.setText("Hello! " + name);
            //lblAmount.setText("Your current balance is: Php ");
        });


        changePinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String m = JOptionPane.showInputDialog("Please input new pin:");
                if(!m.matches("^\\d+$")){
                    JOptionPane.showMessageDialog(null,"Pin must contain valid numbers only", "Try again", JOptionPane.ERROR_MESSAGE);
                }else if(m.length()!=4){
                    JOptionPane.showMessageDialog(null,"Pin must contain four(4) numbers", "Try again", JOptionPane.ERROR_MESSAGE);
                }else {
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
            }

        });

        cashInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Double m = Double.valueOf(JOptionPane.showInputDialog("Please input cash in amount:"));
                Double m = Double.parseDouble(JOptionPane.showInputDialog("Please input cash in amount:"));
                if(!m.toString().matches("^[\\+\\-]{0,1}[0-9]+[\\.\\,][0-9]+$")){
                    JOptionPane.showMessageDialog(null,"Please input a valid number for the amount", "Try again", JOptionPane.ERROR_MESSAGE);
                }else if(m<=100.00){
                    JOptionPane.showMessageDialog(null,"Please input a valid number higher than 100 for amount", "Try again", JOptionPane.ERROR_MESSAGE);
                } else{
                    final String DB_URL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12666768";
                    final String USERNAME = "sql12666768";
                    final String PASSWORD = "YxDac3ZBu9";
                    try (Connection con = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                        PreparedStatement ps = con.prepareStatement("SELECT id FROM users WHERE name=?");
                        ps.setString(1,name);
                        ResultSet rs = ps.executeQuery();
                        if(rs.next()){
                            String userId = rs.getString(1);
                            PreparedStatement ps2 = con.prepareStatement("SELECT amount FROM balance WHERE user_id=?");
                            ps2.setString(1,userId);
                            ResultSet rs2 = ps2.executeQuery();
                            if(rs2.next()){
                                double balanceAmount = Double.parseDouble(rs2.getString(1));
                                try(Connection con2 = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                                    PreparedStatement ps3 = con2.prepareStatement("INSERT INTO transaction (amount,name,account_id,date,transfertoid,transferfromid) values (?,?,?,?,?,?)");
                                    ps3.setDouble(1, m);
                                    ps3.setString(2, name);
                                    ps3.setString(3, userId);
                                    ps3.setString(4, String.valueOf(new Timestamp(System.currentTimeMillis())));
                                    ps3.setString(5, userId);
                                    ps3.setString(6, userId);
                                    ps3.executeUpdate();
                                    PreparedStatement ps4 = con2.prepareStatement("UPDATE balance SET amount = ? WHERE user_id = ?");
                                    ps4.setDouble(1, balanceAmount + m);
                                    ps4.setString(2, userId);
                                    ps4.executeUpdate();
                                    try(Connection con3 = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                                        PreparedStatement ps5 = con3.prepareStatement("select amount from balance where user_id=?");
                                        ps5.setString(1,userId);
                                        ResultSet rs3 = ps2.executeQuery();
                                        if(rs3.next()){
                                            String amount = rs3.getString(1);
                                            lblAmount.setText("Your current balance is: Php " + amount);
                                        }
                                    }
                                }catch (Exception e2){
                                    e2.printStackTrace();
                                }
                            }
                        }
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }



            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.exit(0);
                //dispose();
                dashboardPanel.setVisible(false);
                dispose();
                LoginForm myLoginForm = new LoginForm(null);
                myLoginForm.setVisible(true);
            }
        });

    }


    private boolean checkBalance() {
        //String name = txtFldName.getText();
        boolean hasRegisteredBalance = false;
        final String DB_URL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12666768";
        final String USERNAME = "sql12666768";
        final String PASSWORD = "YxDac3ZBu9";
        try(Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
            PreparedStatement pst = conn.prepareStatement("SELECT id FROM users WHERE name=?");
            pst.setString(1, name);
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                String userId = rs.getString(1);
                try(Connection con = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                    PreparedStatement ps = con.prepareStatement("insert into balance(amount,user_id) values (?,?)");
                    ps.setString(1, "0");
                    ps.setString(2,userId);
                    ps.executeUpdate();
                    try(Connection con2 = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                        PreparedStatement ps2 = con2.prepareStatement("select amount from balance where user_id=?");
                        ps2.setString(1,userId);
                        ResultSet rs2 = ps2.executeQuery();
                        if(rs2.next()){
                            String amount = rs2.getString(1);
                            lblAmount.setText("Your current balance is: Php " + amount);
                        }
                    }
                }catch (SQLException e){
                    if (e.getErrorCode() == 1062) {
                        try(Connection con2 = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                            PreparedStatement ps2 = con2.prepareStatement("select amount from balance where user_id=?");
                            ps2.setString(1,userId);
                            ResultSet rs2 = ps2.executeQuery();
                            if(rs2.next()){
                                String amount = rs2.getString(1);
                                lblAmount.setText("Your current balance is: Php " + amount);
                            }
                        }

                    }
                }
            }
        }catch (SQLException e1){
            e1.printStackTrace();
        }
        return false;
    }

    private boolean connectToDatabase(){
        boolean hasRegisteredUser = false;
        final String DB_URL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12666768";
        final String USERNAME = "sql12666768";
        final String PASSWORD = "YxDac3ZBu9";

        try(Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
            User user = new User();
            lblName.setText("Hello " + user.name+ "!");
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT id FROM users WHERE name=?");
            preparedStatement.setString(1, user.name);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()==true){
                int id = rs.getInt(1);
                System.out.println(id);
                try{
                    PreparedStatement pst = conn.prepareStatement("INSERT INTO balance (amount,user_id) VALUES (?,?)");
                    //INSERT INTO users (email, password, datetime_created) VALUES ("johndoe@gmail.com", "passwordE", "2021-01-01 05:00:00");
                    pst.setInt(1, id);
                    pst.executeUpdate();

                }catch (SQLException e1){
                    e1.printStackTrace();

                    //lblAmount.setText("Your current balance is: Php " + amount);
                }

            }



        }catch (Exception e){
            e.printStackTrace();
        }
        return hasRegisteredUser;
    }




    public static void main(String[] args) {
        DashboardForm myForm = new DashboardForm("");


    }


}