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

    public LoginForm(JFrame parent){
        super(parent);
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(450,474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mobile = txtFldMobile.getText();
                String pin = String.valueOf(pwdFldPin.getPassword());

                user = getAuthenticatedUser(mobile, pin);

                if(user != null){
                    dispose();
                    loginPanel.setVisible(false);
                    if(e.getSource()==btnOk){
                        DashboardForm myDashboardForm = new DashboardForm();
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
                dispose();
            }
        });



        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource()==registerButton){
                    RegistrationForm myRegistrationForm = new RegistrationForm(null);
                    myRegistrationForm.setVisible(true);
                    loginPanel.setVisible(false);
                    dispose();
                }
            }
        });
        setVisible(true);
    }

    public User user;

    private User getAuthenticatedUser(String mobile, String pin){
        User user = null;

        final String DB_URL = "jdbc:mysql://localhost:3306/gcashapp";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM users WHERE mobile=? AND pin=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, mobile);
            preparedStatement.setString(2, pin);

            ResultSet resultSet = preparedStatement.executeQuery();

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



