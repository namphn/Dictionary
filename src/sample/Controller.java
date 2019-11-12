package sample;


import com.mysql.jdbc.PreparedStatement;
import com.sun.speech.freetts.VoiceManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.Window;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller {

    @FXML
    TextField inPut, inputword, inputexplain, inputdelete, inputeditword, inputeditexplain, english;
    @FXML
    Button translate, translateonline;
    @FXML
    Button search, insertbutton, speakonline;
    @FXML
    ListView listView,listViewdelete;
    @FXML
    WebView webView,vietnamese,webViewdelete;
    @FXML
    AnchorPane pane, translateonlinepane, edittabpane,startpane;
    @FXML
    Button Speak, deletebutton, edittab,Start;
    @FXML
    Button Translate,Insert, Delete, Edit;
    @FXML
    AnchorPane panetranslate, paneinsert, paneedit, panedelete;

    /*
        các Phương thức
    */

    public void setKeyBoard() {
        // set sự kiện cho phím Enter
        inPut.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                String text = inPut.getText();
                text = text.toLowerCase();
                if ("".equals(text)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("THÔNG BÁO");
                    alert.setHeaderText("                       TỪ CHƯA ĐƯỢC NHẬP!");
                    alert.setContentText("*WARNING: FBI");
                    alert.show();
                }else {
                    try {
                        if (timTu(text).equals("")) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("THÔNG BÁO");
                            alert.setHeaderText("                TỪ VỪA NHẬP KHÔNG HỢP LỆ!");
                            alert.setContentText("*ERROR: 404");
                            alert.show();
                        }
                        else webView.getEngine().loadContent(timTu(text));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }   // set sự kiện cho phím Enter

    public void searchWord() throws SQLException {
            List DS = new ArrayList();
            setArray(DS);            // mảng chưa các từ tiếng Anh

            ObservableList<String> listWord = FXCollections.observableArrayList(DS);
            // nạp vào ObservableList DS chứa các từ tiếng anh   (ObservableList cho phép theo dõi những thay đổi khi chúng diễn ra)

            FilteredList<String> filteredData = new FilteredList<>(listWord, s -> true);
            //FilteredList dùng để lọc, các thay đổi trong  ObservableList được truyền ngay đến FilteredList

            listView.setItems(filteredData);
            // hiện lên listview

            inPut.textProperty().addListener((observable, oldValue, newValue) -> {    // những thay đổi trong input
                filteredData.setPredicate(s -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String tolower = newValue.toLowerCase();
                    if (s.toLowerCase().startsWith(tolower)) {
                        return true;
                    }
                    return false;
                });
                listView.setItems(filteredData);
            });
    }   //hàm xử lí khi click vào search button, hiện các từ gọi ý trong listview được lấy từ ArrayList

    public void setKeyPressed() throws SQLException{
        //  bắt Mouse Event khi click vào listView
        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent click) {

                if (click.getClickCount() == 2) {
                    String text = (String) listView.getSelectionModel().getSelectedItem();
                    try {
                        inPut.setText(text);
                        webView.getEngine().loadContent(timTu(text));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }   //bắt sự kiện đúp chuột

    public void searchWordDelete() throws SQLException {
        List DS = new ArrayList();
        setArray(DS);            // mảng chưa các từ tiếng Anh
        ObservableList<String> listWord = FXCollections.observableArrayList(DS);               // nạp vào ObservableList DS1 chứa các từ tiếng anh
        /**
         * FilteredList dùng để lọc, các thay đổi trong  ObservableList được truyền ngay đến FilteredList
         */
        FilteredList<String> filteredData = new FilteredList<>(listWord, s -> true);
        listViewdelete.setItems(filteredData); // hiện lên listview

        inputdelete.textProperty().addListener((observable, oldValue, newValue) -> {              // những thay đổi trong input
            filteredData.setPredicate(s -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String tolower = newValue.toLowerCase();
                if (s.toLowerCase().startsWith(tolower)) {
                    return true;
                }
                return false;
            });
            listViewdelete.setItems(filteredData);
        });
    }   // in ra Danh sach cac từ trong phần delete

    public void setKeyPressedDelete() throws SQLException{

        listViewdelete.setOnMouseClicked(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent click) {

                if (click.getClickCount() == 2) {
                    //Use ListView's getSelected Item
                    //currentItemSelected = listView.getSelectionModel().getSelectedItem();//use this to do whatever you want to. Open Link etc.
                    String text = (String) listViewdelete.getSelectionModel().getSelectedItem();
                    try {
                        inputdelete.setText(text);
                        webViewdelete.getEngine().loadContent(timTu(text));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });}   // bắt sự kiện đúp chuột
    public void SetDeleteButton(ActionEvent event) throws SQLException {    // chọn nút để xóa từ
        Statement statement = MySQLConnUtils.getJDBCConnection().createStatement();
        String word = inputdelete.getText();

        if(CheckWordInDataBase(word)) {
            try {
                String sql = "DELETE  FROM tbl_edict WHERE word = '"+ word+"'";
                Alert alert = new Alert(Alert.AlertType.NONE, "BẠN CÓ CHẮC MUỐN XÓA TỪ!", ButtonType.YES, ButtonType.NO);
                if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                    statement.executeUpdate(sql);
                }
                Alert delete = new Alert(Alert.AlertType.WARNING);
                delete.setTitle("THÔNG BÁO");
                delete.setHeaderText("               ĐÃ XÓA!");
                delete.setContentText("*WARNING: FBI");
                delete.show();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("THÔNG BÁO");
            alert.setHeaderText("               TỪ KHÔNG CÓ SẴN!");
            alert.setContentText("*WARNING: FBI");
            alert.show();
        }
    }   // bắt sự kiên xóa từ
    public void setSearchDelete() throws SQLException {
        setKeyPressedDelete();
        searchWordDelete();
    }

    public void Submit(ActionEvent event) throws SQLException {
        // xử lí 2 nút search và translate
        String text;
        if (event.getSource() == translate) {
            // Xử lý translate
            text = inPut.getText();
            text = text.toLowerCase();      // chuyển về chữ thường

            if ("".equals(text)) {           // nếu chưa nhập vào
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("THÔNG BÁO");
                alert.setHeaderText("                       TỪ CHƯA ĐƯỢC NHẬP!");
                alert.setContentText("*WARNING: FBI");
                alert.show();
            } else if (timTu(text).equals("")) {    // nếu nhập sai
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("THÔNG BÁO");
                alert.setHeaderText("                TỪ VỪA NHẬP KHÔNG HỢP LỆ!");
                alert.setContentText("*ERROR: 404");
                alert.show();
            } else webView.getEngine().loadContent(timTu(text));
        }
        if(event.getSource() == search){
            setKeyPressed();
            searchWord();
    }

    }

    public void Click(ActionEvent event){
        if(event.getSource() == Translate) {
            panetranslate.toFront();
        }
        else if(event.getSource() == Insert ) {
            paneinsert.toFront();
        }
        else if(event.getSource() == Edit ) {
            paneedit.toFront();
        }
        else if(event.getSource() == Delete ) {
            panedelete.toFront();
        }
        else if(event.getSource() == translateonline ) {
            translateonlinepane.toFront();
        }
        else if(event.getSource()== edittab) {
            edittabpane.toFront();
        }
        else if(event.getSource() == Start){
            startpane.toFront();
        }

    }

    public boolean CheckWordInDataBase(String word) throws SQLException {
        //String c="";
        Statement statement = MySQLConnUtils.getJDBCConnection().createStatement();
        String Sql = "SELECT * FROM tbl_edict WHERE word = '"+word+"'";
        ResultSet rs = statement.executeQuery(Sql);
        if(rs.next())
            return true;
        else return false;
    }       // check từ nhập vào xem đã tồn tại chưa

    public void SetInsertButton(ActionEvent event) throws SQLException {
        Statement statement = MySQLConnUtils.getJDBCConnection().createStatement();

        String word = inputword.getText();
        String detail = inputexplain.getText();
        word.toLowerCase();
        detail.toLowerCase();
        System.out.println(word);
        if(!CheckWordInDataBase(word)) {
            try {
                String sql = "INSERT INTO tbl_edict(word, detail) VALUES ('"+word+"', '"+detail+"')";
                Alert alert = new Alert(Alert.AlertType.NONE, "BẠN CÓ CHẮC THÊM TỪ!", ButtonType.YES, ButtonType.NO);
                if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                    statement.executeUpdate(sql);

                    Alert confirm  = new Alert(Alert.AlertType.INFORMATION);
                    confirm.setTitle("THÔNG BÁO");
                    confirm.setHeaderText("                     ĐÃ THÊM");
                    confirm.setContentText("*WARNING: FBI");
                    confirm.show();
                }
            }
            catch (Exception e){
                e.printStackTrace(); }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("THÔNG BÁO");
            alert.setHeaderText("                       TỪ BẠN NHẬP ĐÃ TỒN TẠI!");
            alert.setContentText("*WARNING: FBI");
            alert.show();
        }
    }       //bắt sự kiện cho nút insert

    public void SetEditButton(ActionEvent event) throws SQLException {
            Statement statement = MySQLConnUtils.getJDBCConnection().createStatement();
            String word = inputeditword.getText();
            String detail = inputeditexplain.getText();

            if(CheckWordInDataBase(word)){
                String sql = "UPDATE tbl_edict set detail = '"+detail+"' WHERE word = '"+word+"'";
                Alert alert = new Alert(Alert.AlertType.NONE, "BẠN CÓ SỬA TỪ!", ButtonType.YES, ButtonType.NO);
                if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
                    statement.executeUpdate(sql);
                    Alert delete = new Alert(Alert.AlertType.WARNING);
                    delete.setTitle("THÔNG BÁO");
                    delete.setHeaderText("               ĐÃ SỬA!");
                    delete.setContentText("*WARNING: FBI");
                    delete.show();
                }

            }
            else{
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("THÔNG BÁO");
                alert.setHeaderText("               TỪ KHÔNG CÓ SẴN!");
                alert.setContentText("*WARNING: FBI");
                alert.show();
            }
        }          // bắt sự kiện cho nút edit

    public void setTranslateOnline(ActionEvent event) throws IOException {
        String englishword = english.getText();
        String tran = GoogleAPI.Connect(englishword);
        vietnamese.getEngine().loadContent(tran);
    }       // bắt sự kiện cho TranslateOnline

    public void setButtonSpeakOnline(ActionEvent event){
        VoiceManager voiceManager = VoiceManager.getInstance();

        com.sun.speech.freetts.Voice syntheticVoice = voiceManager.getVoice("kevin16");
        syntheticVoice.allocate();
        String text = english.getText();
        syntheticVoice.speak(text);
        syntheticVoice.deallocate();
    }                       // phương thức để speak English Word  (trong phấn TranslateOnline)

    public void  handle(ActionEvent event )  {
        VoiceManager voiceManager = VoiceManager.getInstance();

        com.sun.speech.freetts.Voice syntheticVoice = voiceManager.getVoice("kevin16");
        syntheticVoice.allocate();
        String text = inPut.getText();
        syntheticVoice.speak(text);
        syntheticVoice.deallocate();
    }                                  // phương thức để speak English Word


    /*
    Các hàm bổ sung cho phần bên trên
    */
    public void initialize(URL location, ResourceBundle resources) {
        setKeyBoard();
    }

    public void setArray(List DS) throws SQLException {
        Statement statement = MySQLConnUtils.getJDBCConnection().createStatement();
        String Sql = "SELECT * FROM tbl_edict";
        ResultSet rs = statement.executeQuery(Sql);
        while(rs.next()){
            DS.add(rs.getString("word"));
        }
    }
    public String timTu(String a) throws SQLException {
        String c="";
        Statement statement = MySQLConnUtils.getJDBCConnection().createStatement();
        String Sql = "SELECT * FROM tbl_edict WHERE word = '"+a+"'";
        ResultSet rs = statement.executeQuery(Sql);
        if(rs.next())
            return (rs.getString("detail"));
        else return "";
    }
    public void close(ActionEvent event ){

        Alert alert = new Alert(Alert.AlertType.NONE, "Bạn có chắc là muốn thoát chương trình?", ButtonType.YES, ButtonType.NO);
        if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            Platform.exit();
        }
    }

}



