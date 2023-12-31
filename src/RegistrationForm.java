import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

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
    private JTextField txtFldLName;

    public RegistrationForm(JFrame parent){
        super(parent);
        setTitle("Create a new account");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(450, 550));
        setModal(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//        LoginForm myLoginForm = new LoginForm(null);
//        myLoginForm.setVisible(false);

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
                registerPanel.setVisible(false);
                dispose();
            }
        });

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource()==btnLogin){
                    registerPanel.setVisible(false);
                    dispose();
                    LoginForm myLoginForm = new LoginForm(null);
                    myLoginForm.setVisible(true);
                }
            }
        });
        setVisible(true);


    }

    private void registerUser(){
        String fName = txtFldName.getText();
        String lName = txtFldLName.getText();
        //name.substring(0,1).toUpperCase();
        String tempEmail = txtFldEmail.getText();
        String mobile = txtFldMobile.getText();
        String pin = String.valueOf(pwdFldPin.getPassword());
        String confirmPin = String.valueOf(pwdFldConfirmPin.getPassword());
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        if(fName.isEmpty() || lName.isEmpty() || tempEmail.isEmpty() || mobile.isEmpty() || pin.isEmpty() || confirmPin.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter all fields", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(!pin.equals(confirmPin)){
            JOptionPane.showMessageDialog(this,"PIN number and Confirm PIN does not match", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(!fName.toLowerCase().matches("[a-z]+")){
            JOptionPane.showMessageDialog(this,"First Name must contain valid letters only", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(!lName.toLowerCase().matches("[a-z]+")){
            JOptionPane.showMessageDialog(this,"Last Name must contain valid letters only", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(!tempEmail.matches(emailPattern)){
            JOptionPane.showMessageDialog(this,"Email is not valid", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(mobile.length()!=9){
            JOptionPane.showMessageDialog(this,"Mobile requires nine(9) valid set of numbers", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(!mobile.matches("^\\d+$")){
            JOptionPane.showMessageDialog(this,"Mobile must contain valid numbers only", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(!pin.matches("^\\d+$")){
            JOptionPane.showMessageDialog(this,"Pin must contain valid numbers only", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if(pin.length()!=4){
            JOptionPane.showMessageDialog(this,"Pin requires four(4) digits only", "Try again", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = fName.substring(0,1).toUpperCase()+fName.substring(1)+" "+lName.substring(0,1).toUpperCase()+lName.substring(1);
        String email = tempEmail.toLowerCase();

        user = addUserToDatabase(name, email, mobile, pin);
        if(user != null){
            dispose();
        }
//        SwingUtilities.invokeLater(()->{
//            boolean hasRegisteredBalance = checkBalance();
//        });
//        else {
//            JOptionPane.showMessageDialog(this,"Name and/or Email and/or Mobile already registered\nPlease register another Name, Email, and Mobile.", "Try again", JOptionPane.ERROR_MESSAGE);
//        }
    }



    public User user;
    private User addUserToDatabase(String name, String email, String mobile, String pin) {
        User user = null;
        final String DB_URL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12666768";
        final String USERNAME = "sql12666768";
        final String PASSWORD = "YxDac3ZBu9";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            mobile = "09" + mobile;
            Statement stmt = conn.createStatement();
            String sql2 = "INSERT INTO users (name, email, mobile, pin)" + "VALUES (?, ?, ?, ?)";

            PreparedStatement preparedStatement2 = conn.prepareStatement(sql2);


            preparedStatement2.setString(1, name);
            preparedStatement2.setString(2, email);
            preparedStatement2.setString(3, mobile);
            preparedStatement2.setString(4, pin);




            int addedRows = preparedStatement2.executeUpdate();

            if (addedRows > 0) {
                user = new User();
                user.name = name;
                user.email = email;
                user.mobile = mobile;
                user.pin = pin;
                try(Connection conToGetUserId = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                    PreparedStatement psQueryToGetUserId = conToGetUserId.prepareStatement("SELECT id FROM users WHERE name=?");
                    psQueryToGetUserId.setString(1,name);
                    ResultSet rsToGetUserId = psQueryToGetUserId.executeQuery();
                    if(rsToGetUserId.next()){
                        int userId = rsToGetUserId.getInt(1);
                        try(Connection conToInsertBalanceForUser = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                            PreparedStatement psQueryToInsertBalanceForUser = conToInsertBalanceForUser.prepareStatement("INSERT INTO balance(amount,user_id) values (?,?)");
                            psQueryToInsertBalanceForUser.setDouble(1,0);
                            psQueryToInsertBalanceForUser.setInt(2,userId);
                            psQueryToInsertBalanceForUser.executeUpdate();
                        }catch(Exception e1){
                            e1.printStackTrace();
                        }
                    }
                }catch(Exception e1){
                    e1.printStackTrace();
                }
            }





            JOptionPane.showMessageDialog(null, "Registration Success! \nDetails registered are:\nName: " + user.name + "\nEmail: " +user.email+"\nMobile: " + user.mobile);
            registerPanel.setVisible(false);
            dispose();
            RegistrationForm myform = new RegistrationForm(null);
//            stmt.close();
//            conn.close();

        }catch (SQLException e){
            if (e.getErrorCode() == 1062) {
                JOptionPane.showMessageDialog(this,"Name and/or Email and/or Mobile already registered\nPlease register another Name, Email, and Mobile.", "Try again", JOptionPane.ERROR_MESSAGE);
            }
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