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
    private JButton cashTransferButton;
    private JButton viewTransactionButton;

    private JTable table1;
    private JButton allTransactionsButton;
    private JButton userSTransactionsButton;

    private String name;


    public DashboardForm(String name){
        this.name = name;

        setTitle("Dashboard");
        setContentPane(dashboardPanel);
        setMinimumSize(new Dimension(450, 500));
        setSize(450,500);
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
                                            JOptionPane.showMessageDialog(null, "Your current balance is: Php " + amount);
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

        cashTransferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                final String DB_URL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12666768";
                final String USERNAME = "sql12666768";
                final String PASSWORD = "YxDac3ZBu9";

                String mobile = JOptionPane.showInputDialog("Please input mobile where to transfer cash:");

                if(!mobile.toString().matches("^\\d+$")){
                    JOptionPane.showMessageDialog(null,"Mobile must contain valid numbers only. Thanks.", "Try again", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if(mobile.length()!=11){
                    JOptionPane.showMessageDialog(null,"Mobile requires eleven(11) valid set of numbers. Thanks.", "Try again", JOptionPane.ERROR_MESSAGE);
                    return;
                }


                try(Connection conToCheckIfReceiverMobileIsSameWithSender = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                    PreparedStatement psToCheckIfReceiverMobileIsSameWithSender = conToCheckIfReceiverMobileIsSameWithSender.prepareStatement("SELECT mobile FROM users WHERE name=?");
                    psToCheckIfReceiverMobileIsSameWithSender.setString(1,name);
                    ResultSet rsToCheckIfReceiverMobileIsSameWithSender = psToCheckIfReceiverMobileIsSameWithSender.executeQuery();
                    if(rsToCheckIfReceiverMobileIsSameWithSender.next()){
                        String mobileOfSender = rsToCheckIfReceiverMobileIsSameWithSender.getString(1);
                        if(!mobile.toString().matches(mobileOfSender)){
                            try(Connection conToQueryIfMobileOfReceiverExists = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                                PreparedStatement psQueryMobileOfReceiverIfExisting = conToQueryIfMobileOfReceiverExists.prepareStatement("SELECT mobile FROM users WHERE mobile=?");
                                psQueryMobileOfReceiverIfExisting.setString(1,mobile);
                                ResultSet rsToQueryMobileOfReceiverIfExisting = psQueryMobileOfReceiverIfExisting.executeQuery();
                                if(rsToQueryMobileOfReceiverIfExisting.next()){
                                    String mobileOfReceiver = rsToQueryMobileOfReceiverIfExisting.getString(1);

                                    String amount = JOptionPane.showInputDialog("Please input cash amount that will be transferred:\n(If the desired amount doesn't contain cents then\nkindly add .00 at the end. Thanks)");

                                    if(!amount.toString().matches("^[\\+\\-]{0,1}[0-9]+[\\.\\,][0-9]+$")){
                                        JOptionPane.showMessageDialog(null,"Please input a valid number for the cash amount. Thanks.", "Try again", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }

                                    if(Double.parseDouble(amount)<=100.00){
                                        JOptionPane.showMessageDialog(null,"Please input a valid number higher than 100 for amount. Thanks.", "Try again", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }

                                    try(Connection conToQueryIdOfSender = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                                        PreparedStatement psQueryIdOfSender = conToQueryIdOfSender.prepareStatement("SELECT id FROM users WHERE name=?");
                                        psQueryIdOfSender.setString(1,name);
                                        ResultSet rsToQueryIdOfSender = psQueryIdOfSender.executeQuery();
                                        if(rsToQueryIdOfSender.next()){
                                            int idOfSender = rsToQueryIdOfSender.getInt(1);
                                            try(Connection conToQueryBalanceAmountOfSender = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                                                PreparedStatement psToQueryBalanceAmountOfSender = conToQueryBalanceAmountOfSender.prepareStatement("SELECT amount FROM balance WHERE user_id=?");
                                                psToQueryBalanceAmountOfSender.setInt(1,idOfSender);
                                                ResultSet rsToQueryBalanceAmountOfSender = psToQueryBalanceAmountOfSender.executeQuery();
                                                if(rsToQueryBalanceAmountOfSender.next()){
                                                    double balanceAmountOfSender = rsToQueryBalanceAmountOfSender.getDouble(1);
                                                    if(balanceAmountOfSender>Double.parseDouble(amount)){
                                                        try(Connection conToDeductTransferAmountToBalanceAmountOFSender = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                                                            PreparedStatement psQueryToDeductTransferAmountToBalanceAmountOFSender = conToDeductTransferAmountToBalanceAmountOFSender.prepareStatement("UPDATE balance SET amount = ? where user_id = ?");
                                                            psQueryToDeductTransferAmountToBalanceAmountOFSender.setDouble(1,balanceAmountOfSender-Double.parseDouble(amount));
                                                            psQueryToDeductTransferAmountToBalanceAmountOFSender.setInt(2, idOfSender);
                                                            psQueryToDeductTransferAmountToBalanceAmountOFSender.executeUpdate();
                                                            try(Connection conToQueryIdOfReceiver = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                                                                PreparedStatement psQueryIdOfReceiver = conToQueryIdOfReceiver.prepareStatement("SELECT id FROM users WHERE mobile=?");
                                                                psQueryIdOfReceiver.setString(1,mobile);
                                                                ResultSet rsToQueryIdOfReceiver = psQueryIdOfReceiver.executeQuery();
                                                                if(rsToQueryIdOfReceiver.next()){
                                                                    int idOfReceiver = rsToQueryIdOfReceiver.getInt(1);
                                                                    try(Connection conToQueryBalanceAmountOfReceiver = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                                                                        PreparedStatement psQueryBalanceAmountOfReceiver = conToQueryBalanceAmountOfReceiver.prepareStatement("SELECT amount FROM balance WHERE user_id=?");
                                                                        psQueryBalanceAmountOfReceiver.setInt(1,idOfReceiver);
                                                                        ResultSet rsToQueryBalanceAmountOfReceiver = psQueryBalanceAmountOfReceiver.executeQuery();
                                                                        if(rsToQueryBalanceAmountOfReceiver.next()){
                                                                            double balanceAmountOfReceiver = rsToQueryBalanceAmountOfReceiver.getDouble(1);
                                                                            try(Connection conToAddTransferAmountToBalanceAmountOfReceiver = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                                                                                PreparedStatement psToAddTransferAmountToBalanceAmountOfReceiver = conToAddTransferAmountToBalanceAmountOfReceiver.prepareStatement("UPDATE balance SET amount = ? WHERE user_id = ?");
                                                                                psToAddTransferAmountToBalanceAmountOfReceiver.setDouble(1,balanceAmountOfReceiver + Double.parseDouble(amount));
                                                                                psToAddTransferAmountToBalanceAmountOfReceiver.setInt(2,idOfReceiver);
                                                                                psToAddTransferAmountToBalanceAmountOfReceiver.executeUpdate();
                                                                                try(Connection conToQueryNameOfReceiver = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                                                                                    PreparedStatement psQueryNameOfReceiver = conToQueryNameOfReceiver.prepareStatement("SELECT name FROM users WHERE mobile=?");
                                                                                    psQueryNameOfReceiver.setString(1,mobileOfReceiver);
                                                                                    ResultSet rsQueryNameOfReciever = psQueryNameOfReceiver.executeQuery();
                                                                                    if(rsQueryNameOfReciever.next()){
                                                                                        String nameOfReceiver = rsQueryNameOfReciever.getString(1);
                                                                                        try(Connection conToInsertThisTransactionToTransactionTable = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                                                                                            PreparedStatement psToInsertThisTransactionToTransactionTable = conToInsertThisTransactionToTransactionTable.prepareStatement("INSERT INTO transaction(amount,name,account_id,date,transfertoid,transferfromid) values (?,?,?,?,?,?)");
                                                                                            psToInsertThisTransactionToTransactionTable.setDouble(1, Double.parseDouble(amount));
                                                                                            psToInsertThisTransactionToTransactionTable.setString(2,nameOfReceiver);
                                                                                            psToInsertThisTransactionToTransactionTable.setInt(3,idOfReceiver);
                                                                                            psToInsertThisTransactionToTransactionTable.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                                                                                            psToInsertThisTransactionToTransactionTable.setInt(5,idOfReceiver);
                                                                                            psToInsertThisTransactionToTransactionTable.setInt(6,idOfSender);
                                                                                            psToInsertThisTransactionToTransactionTable.executeUpdate();
                                                                                            try(Connection conToGetUserId = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                                                                                                PreparedStatement psQueryToGetUserId = conToGetUserId.prepareStatement("SELECT id FROM users WHERE name=?");
                                                                                                psQueryToGetUserId.setString(1,name);
                                                                                                ResultSet rsToGetUserId = psQueryToGetUserId.executeQuery();
                                                                                                if(rsToGetUserId.next()){
                                                                                                    int userId = rsToGetUserId.getInt(1);
                                                                                                    try(Connection conToGetAmountFromBalanceOfUser = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                                                                                                        PreparedStatement psQueryToGetAmountFromBalanceOfUser = conToGetAmountFromBalanceOfUser.prepareStatement("SELECT amount FROM balance WHERE user_id=?");
                                                                                                        psQueryToGetAmountFromBalanceOfUser.setInt(1, userId);
                                                                                                        ResultSet rsToGetAmountFromBalanceOfUser = psQueryToGetAmountFromBalanceOfUser.executeQuery();
                                                                                                        if(rsToGetAmountFromBalanceOfUser.next()){
                                                                                                            double amountAfterTransferCash = rsToGetAmountFromBalanceOfUser.getDouble(1);
                                                                                                            //update this
                                                                                                            try(Connection conToGetTime = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                                                                                                                PreparedStatement psQueryTime = conToGetTime.prepareStatement("SELECT MAX(date) FROM transaction WHERE name=?");
                                                                                                                psQueryTime.setString(1,nameOfReceiver);
                                                                                                                ResultSet rsToGetTime = psQueryTime.executeQuery();
                                                                                                                if(rsToGetTime.next()){
                                                                                                                    String date = rsToGetTime.getString(1);
                                                                                                                    JOptionPane.showMessageDialog(null, "Transfer Cash Successfull! \nTransfer Details:\nAmount Transferred: Php" + amount + "\nRecipient's Name: " +nameOfReceiver+"\nAccount ID of Recipient: " + idOfReceiver+"\nTransfer Date: " + date+"\nYour updated balance now is: Php " + (balanceAmountOfSender-Double.parseDouble(amount)));

                                                                                                                    //update
                                                                                                                }
                                                                                                            }catch(Exception e1){
                                                                                                                e1.printStackTrace();
                                                                                                            }

                                                                                                            lblAmount.setText("Your current balance is: Php " + amountAfterTransferCash);
                                                                                                        }
                                                                                                    }catch(Exception e1){
                                                                                                        e1.printStackTrace();
                                                                                                    }
                                                                                                }
                                                                                            }catch(Exception e1){
                                                                                                e1.printStackTrace();
                                                                                            }
                                                                                        }catch(Exception e1){
                                                                                            e1.printStackTrace();
                                                                                        }
                                                                                    }
                                                                                }catch(Exception e1){
                                                                                    e1.printStackTrace();
                                                                                }

                                                                            }catch(Exception e1){
                                                                                e1.printStackTrace();
                                                                            }
                                                                        }
                                                                    }catch(Exception e1){
                                                                        e1.printStackTrace();
                                                                    }
                                                                }
                                                            }catch(Exception e1){
                                                                e1.printStackTrace();
                                                            }
                                                        }catch(Exception e1){
                                                            e1.printStackTrace();
                                                        }
                                                    }
                                                    else {
                                                        JOptionPane.showMessageDialog(null,"Your current balance amount is not enough to transfer your desired amount. Thanks.", "Try again", JOptionPane.ERROR_MESSAGE);
                                                        return;
                                                    }
                                                }
                                            }catch(Exception e1){
                                                e1.printStackTrace();
                                            }
                                        }
                                    }catch(Exception e1){
                                        e1.printStackTrace();
                                    }




                                }
                                else {
                                    JOptionPane.showMessageDialog(null,"The mobile where to transfer cash does not exists. Thanks.", "Try again", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }
                            }catch(Exception e1){
                                e1.printStackTrace();
                            }
                        }
                        else{
                            JOptionPane.showMessageDialog(null,"Mobile you have entered is the same as your own mobile. Please proceed with cash in instead. Thanks.", "Try again", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                    }
                }catch(Exception e1){
                    e1.printStackTrace();
                }



            }
        });
        viewTransactionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                final String DB_URL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12666768";
                final String USERNAME = "sql12666768";
                final String PASSWORD = "YxDac3ZBu9";

                String date = JOptionPane.showInputDialog("Please input the particular date you need to track:");

                if(!date.toString().matches("^\\d{4}-\\d{2}-\\d{2} ([0-1]?\\d|2[0-3])(?::([0-5]?\\d))?(?::([0-5]?\\d))?$")){
                    JOptionPane.showMessageDialog(null,"Please input a valid YYYY-MM-DD HH:MM:SS. Thanks.", "Try again", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try(Connection conToQueryIdOfRequester = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                    PreparedStatement psQueryIdOfRequester = conToQueryIdOfRequester.prepareStatement("SELECT id FROM users WHERE name=?");
                    psQueryIdOfRequester.setString(1, name);
                    ResultSet rsToQueryIdOfRequester = psQueryIdOfRequester.executeQuery();
                    if(rsToQueryIdOfRequester.next()){
                        int idOfRequester = rsToQueryIdOfRequester.getInt(1);
                        try(Connection conToQueryDateRequested = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                            PreparedStatement psToQueryDateRequested = conToQueryDateRequested.prepareStatement("SELECT amount,name,account_id,date,transfertoid,transferfromid FROM transaction where date=? AND transferfromid=?");
                            psToQueryDateRequested.setString(1,date);
                            psToQueryDateRequested.setInt(2,idOfRequester);
                            ResultSet rsToQueryDateRequested = psToQueryDateRequested.executeQuery();
                            if(rsToQueryDateRequested.next()){
                                double amount = rsToQueryDateRequested.getDouble(1);
                                String name = rsToQueryDateRequested.getString(2);
                                int account_id = rsToQueryDateRequested.getInt(3);
                                String transaction_date = rsToQueryDateRequested.getString(4);
                                int transfertoid = rsToQueryDateRequested.getInt(5);
                                int transferfromid = rsToQueryDateRequested.getInt(6);
                                JOptionPane.showMessageDialog(null, "Transaction Details: Php" + amount + "\nRecipient's Name: " +name+"\nAccount ID of Recipient: " + account_id+"\nTransfer Date: " + transaction_date);

                            }else{
                                JOptionPane.showMessageDialog(null,"You got no transaction on that particular date you have requested. Thanks.", "Try again", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }catch(Exception e1){
                            e1.printStackTrace();
                        }
                    }
                }catch(Exception e1){
                    e1.printStackTrace();
                }


            }
        });
        allTransactionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViewAll myViewAll = new ViewAll(name);
                myViewAll.setVisible(true);
            }
        });
        userSTransactionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ViewUsersAll myViewUsersAll = new ViewUsersAll(name);
                myViewUsersAll.setVisible(true);
            }
        });
    }



    private boolean connectToDatabase(){
        boolean hasRegisteredUser = false;
        final String DB_URL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12666768";
        final String USERNAME = "sql12666768";
        final String PASSWORD = "YxDac3ZBu9";
        User user = new User();
        lblName.setText("Hello " + user.name+ "!");
        try(Connection conToGetUserId = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
            PreparedStatement psQueryToGetUserId = conToGetUserId.prepareStatement("SELECT id FROM users WHERE name=?");
            psQueryToGetUserId.setString(1,name);
            ResultSet rsToGetUserId = psQueryToGetUserId.executeQuery();
            if(rsToGetUserId.next()){
                int userId = rsToGetUserId.getInt(1);
                try(Connection conToGetAmountFromBalanceOfUser = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);){
                    PreparedStatement psQueryToGetAmountFromBalanceOfUser = conToGetAmountFromBalanceOfUser.prepareStatement("SELECT amount FROM balance WHERE user_id=?");
                    psQueryToGetAmountFromBalanceOfUser.setInt(1, userId);
                    ResultSet rsToGetAmountFromBalanceOfUser = psQueryToGetAmountFromBalanceOfUser.executeQuery();
                    if(rsToGetAmountFromBalanceOfUser.next()){
                        double amount = rsToGetAmountFromBalanceOfUser.getDouble(1);
                        lblAmount.setText("Your current balance is: Php " + amount);

                    }
                }catch(Exception e1){
                    e1.printStackTrace();
                }
            }
        }catch(Exception e1){
            e1.printStackTrace();
        }

        return hasRegisteredUser;
    }




    public static void main(String[] args) {

        DashboardForm myForm = new DashboardForm("");



    }


}