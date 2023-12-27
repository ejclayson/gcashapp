import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewUsersAll extends JFrame{
    private JTable table1;
    private JPanel panel1;

    public ViewUsersAll(String name) {
        setTitle("View All");
        setContentPane(panel1);
        setMinimumSize(new Dimension(500, 550));
        setSize(500, 550);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);



        final String DB_URL = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12666768";
        final String USERNAME = "sql12666768";
        final String PASSWORD = "YxDac3ZBu9";

        try (Connection conToQueryIdOfRequester = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);) {
            PreparedStatement psQueryIdOfRequester = conToQueryIdOfRequester.prepareStatement("SELECT id FROM users WHERE name=?");
            psQueryIdOfRequester.setString(1, name);
            ResultSet rsToGetUserIdOfRequester = psQueryIdOfRequester.executeQuery();
            if (rsToGetUserIdOfRequester.next()) {
                int userIdOfRequester = rsToGetUserIdOfRequester.getInt(1);
                try (Connection conToQueryDetailsRequested = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);) {
                    PreparedStatement psToQueryDetailsRequested = conToQueryDetailsRequested.prepareStatement("select DATE,NAME,AMOUNT from transaction where transferfromid=?");
                    //PreparedStatement psToQueryDetailsRequested = conToQueryDetailsRequested.prepareStatement("select DATE,NAME,AMOUNT from transaction where transfertoid=? OR transferfromid=?");
                    psToQueryDetailsRequested.setInt(1, userIdOfRequester);
                    //psToQueryDetailsRequested.setInt(2, userIdOfRequester);
                    ResultSet rsToQueryDetailsRequested = psToQueryDetailsRequested.executeQuery();
                    table1.setModel(DbUtils.resultSetToTableModel(rsToQueryDetailsRequested));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    public static void main(String[] args) {

        ViewUsersAll myViewUsersAll = new ViewUsersAll("");



    }
}
