import com.mysql.cj.jdbc.MysqlDataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public class MainWindowController {
    @FXML private TableView tableViewPerson;
    @FXML private TableColumn<Person, Integer> tableColumnId;
    @FXML private TableColumn<Person, String> tableColumnFirstName;
    @FXML private TableColumn<Person, String> tableColumnLastName;
    @FXML private TableColumn<Person, Date> tableColumnBirthDate;
    @FXML private Button buttonLoadEverything;
    @FXML private DatePicker datePickerFrom;
    @FXML private DatePicker datePickerTo;

    private Connection connection;

    public void initialize() throws SQLException {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));

        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/database_19_01_2020?useUnicode=true&" +
                "useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                "admin", "admiN!2345");


        datePickerFrom.setValue(LocalDate.now());
        datePickerTo.setValue(LocalDate.now());
    }

    public void actionLoadEverything() throws SQLException {
        ArrayList<Person> arrayListResult = new ArrayList<>();

        String queryModel = "select id, first_name, last_name, birth_date from Person";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(queryModel);

        while(rs.next()){
            Person person = new Person(rs.getInt("id"), rs.getString("first_name"),
                    rs.getString("last_name"), rs.getDate("birth_date"));
            arrayListResult.add(person);
        }

        ObservableList<Person> observableListPerson = FXCollections.observableList(arrayListResult);
        tableViewPerson.setItems(observableListPerson);
    }

    public void actionLoadWithDates() throws SQLException {
        ArrayList<Person> arrayListResult = new ArrayList<>();

        PreparedStatement statement = connection.prepareStatement("select id, first_name, last_name, birth_date " +
                "from Person where birth_date > ? and birth_date < date_add(?, interval 2 day);");
        statement.setDate(1, Date.valueOf(datePickerFrom.getValue()));
        statement.setDate(2, Date.valueOf(datePickerTo.getValue()));
        ResultSet rs = statement.executeQuery();

        while(rs.next()){
            Person person = new Person(rs.getInt("id"), rs.getString("first_name"),
                    rs.getString("last_name"), rs.getDate("birth_date"));
            arrayListResult.add(person);
        }

        ObservableList<Person> observableListPerson = FXCollections.observableList(arrayListResult);
        tableViewPerson.setItems(observableListPerson);
    }
}
