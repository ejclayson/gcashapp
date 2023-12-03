import com.mysql.cj.log.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginForm extends JDialog{
    private JTextField txtFldMobile;
    private JPasswordField pwdFldPin;
    private JButton btnOk;
    private JButton btnCancel;
    private JPanel loginPanel;
    private JButton registerButton;

    private User user;

    public LoginForm(JFrame parent){
        super(parent);
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(450,474));
        setModal(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);


        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mobile = txtFldMobile.getText();
                String pin = String.valueOf(pwdFldPin.getPassword());

                user = getAuthenticatedUser(mobile, pin);

                if(user != null){

                    if(e.getSource()==btnOk){
                        loginPanel.setVisible(false);

                        dispose();
                        DashboardForm myDashboardForm = new DashboardForm(user.name);
                        myDashboardForm.setVisible(true);
                    }
                }else{
                    JOptionPane.showMessageDialog(LoginForm.this, "Mobile or Pin Invalid", "Try again", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
                dispose();
            }
        });



        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource()==registerButton){
                    loginPanel.setVisible(false);
                    dispose();
                    RegistrationForm myRegistrationForm = new RegistrationForm(null);
                    myRegistrationForm.setVisible(true);
                }
            }
        });
        setVisible(true);
    }

//    public User user;

    private User getAuthenticatedUser(String mobile, String pin){
        User user = null;

        final String DB_URL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12666768";
        final String USERNAME = "sql12666768";
        final String PASSWORD = "YxDac3ZBu9";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM users WHERE mobile=? AND pin=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, mobile);
            preparedStatement.setString(2, pin);
            ResultSet resultSet = preparedStatement.executeQuery();
            //System.out.println(resultSet.next());
            if (resultSet.next()) {
                user = new User();
                user.name = resultSet.getString("name");
                user.email = resultSet.getString("email");
                user.mobile = resultSet.getString("mobile");
                user.pin = resultSet.getString("pin");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return user;

    }

    public static void main(String[] args) {
        LoginForm loginForm = new LoginForm(null);
        User user = loginForm.user;

        if(user != null){
            System.out.println("Successfully Authentication of: " + user.name);
            System.out.println("        Email: " + user.email);
            System.out.println("        Mobile: " + user.mobile);
        }else {
            System.out.println("Authentication cancelled");
        }

    }

}