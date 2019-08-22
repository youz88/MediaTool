package com.youz.media;

import com.google.common.collect.Lists;
import com.youz.media.model.MediaInfo;
import com.youz.media.util.VideoUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.io.File;
import java.util.List;

public class Controller {

    @FXML
    private CheckBox startInterceptSwitch;
    @FXML
    private CheckBox endInterceptSwitch;
    @FXML
    private CheckBox interceptRateSwitch;
    @FXML
    private TextField startInterceptVal;
    @FXML
    private TextField endInterceptVal;
    @FXML
    private TextField interceptRateVal;
    @FXML
    private TableView<MediaInfo> mediTable;
    @FXML
    TableColumn<MediaInfo, CheckBox> checkBox;
    @FXML
    TableColumn<MediaInfo, Integer> id;
    @FXML
    TableColumn<MediaInfo, String> fileName;
    @FXML
    TableColumn<MediaInfo, Long> fileSize;
    @FXML
    TableColumn<MediaInfo, String> durationFormat;

    @FXML
    private void openDirectory(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(new Stage());
        loadTableItem(file);
    }

    @FXML
    private void openFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(new Stage());
        loadTableItem(file);
    }

    @FXML
    private void showDonation(ActionEvent event) {
        ImageView alipay = new ImageView(getClass().getClassLoader().getResource("images/alipay.png").toString());
        Stage window = new Stage();
        window.setTitle("o(*￣▽￣*)o");
        window.initModality(Modality.APPLICATION_MODAL);
        window.setResizable(Boolean.FALSE);
        BorderPane root = new BorderPane();
        root.setCenter(alipay);
        Scene scene = new Scene(root);
        window.setScene(scene);
        window.showAndWait();
    }

    @FXML
    private void start(ActionEvent event) {
        int startTime = 0;
        int endTime = 0;
        int cycle = 0;
        if (startInterceptSwitch.isSelected() && StringUtils.isNotBlank(startInterceptVal.getText())) {
            startTime = NumberUtils.toInt(startInterceptVal.getText());
        }
        if (endInterceptSwitch.isSelected() && StringUtils.isNotBlank(endInterceptVal.getText())) {
            endTime = NumberUtils.toInt(endInterceptVal.getText());
        }
        if (interceptRateSwitch.isSelected() && StringUtils.isNotBlank(interceptRateVal.getText())) {
            cycle = NumberUtils.toInt(interceptRateVal.getText());
        }
        List<MediaInfo> list = Lists.newArrayList();
        for (MediaInfo mediaInfo:list) {
            System.out.println(mediaInfo.getFilePath());
//            VideoUtil.interceptVodTime(mediaInfo.getFilePath(), "" , mediaInfo.getDuration(), startTime, endTime, cycle);
        }
    }

    private void loadTableItem(File file) {
        if (file != null) {
            List<MediaInfo> list = Lists.newArrayList();
            List<MediaInfo> mediaInfoList = VideoUtil.scan(file, list);
            checkBox.setCellValueFactory(new PropertyValueFactory<MediaInfo,CheckBox>("checkBox"));
            id.setCellValueFactory(new PropertyValueFactory<MediaInfo,Integer>("id"));
            fileName.setCellValueFactory(new PropertyValueFactory<MediaInfo,String>("fileName"));
            fileSize.setCellValueFactory(new PropertyValueFactory<MediaInfo,Long>("fileSize"));
            durationFormat.setCellValueFactory(new PropertyValueFactory<MediaInfo,String>("durationFormat"));
            ObservableList<MediaInfo> data = FXCollections.observableArrayList(mediaInfoList);
            mediTable.setItems(data);
        }
    }

}
