import net.proteanit.sql.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ViewAll extends JFrame {

    private JTable table1;
    private JPanel panel1;

    public ViewAll(String name) {
        setTitle("View User's All");
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
                    PreparedStatement psToQueryDetailsRequested = conToQueryDetailsRequested.prepareStatement("select DATE,NAME,AMOUNT from transaction where transfertoid=? OR transferfromid=?");
                    psToQueryDetailsRequested.setInt(1, userIdOfRequester);
                    psToQueryDetailsRequested.setInt(2, userIdOfRequester);
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

        ViewAll myViewAll = new ViewAll("");



    }
}
