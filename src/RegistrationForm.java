import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class RegistrationForm extends JDialog{
    private JTextField txtFldName;
    private JTextField txtFldEmail;
    private JTextField txtFldMobile;
    private JPasswordField pwdFldPin;
    private JPasswordField pwdFldConfirmPin;
    private JButton btnRegister;
    private JButton btnCancel;
    private JPanel registerPanel;
    private JButton btnLogin;

    public RegistrationForm(JFrame parent){
        super(parent);
        setTitle("Create a new account");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(450, 474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource()==btnLogin){
                    LoginForm myLoginForm = new LoginForm(null);
                    myLoginForm.setVisible(true);
                    registerPanel.setVisible(false);
                    dispose();
                }
            }
        });
        setVisible(true);
    }

    private void registerUser(){
        String name = txtFldName.getText();
        String email = txtFldEmail.getText();
        String mobile = txtFldMobile.getText();
        String pin = String.valueOf(pwdFldPin.getPassword());
        String confirmPin = String.valueOf(pwdFldConfirmPin.getPassword());

        if(name.isEmpty() || email.isEmpty() || mobile.isEmpty() || pin.isEmpty() || confirmPin.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter all fields", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(!pin.equals(confirmPin)){
            JOptionPane.showMessageDialog(this,"PIN number and Confirm PIN does not match", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }

        user = addUserToDatabase(name, email, mobile, pin);
        if(user != null){
            dispose();
        }
    }

    public User user;
    private User addUserToDatabase(String name, String email, String mobile, String pin) {
        User user = null;
        final String DB_URL = "jdbc:mysql://localhost:3306/gcashapp";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO users (name, email, mobile, pin)" + "VALUES (?, ?, ?, ?)";

            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, mobile);
            preparedStatement.setString(4, pin);

            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0) {
                user = new User();
                user.name = name;
                user.email = email;
                user.mobile = mobile;
                user.pin = pin;
            }

            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public static void main(String[] args) {
        RegistrationForm myForm = new RegistrationForm(null);
        User user = myForm.user;
        if(user != null){
            System.out.println("Successful registration of: " + user.name);
        }else{
            System.out.println("Registration cancelled");
        }
    }
    }
