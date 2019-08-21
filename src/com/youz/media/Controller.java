package com.youz.media;

import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.youz.media.model.MediaInfo;
import com.youz.media.util.JsonUtil;
import com.youz.media.util.VideoUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class Controller {

    @FXML
    private TableView<MediaInfo> mediTable;
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

    private void loadTableItem(File file) {
        if (file != null) {
            List<MediaInfo> list = Lists.newArrayList();
            List<MediaInfo> mediaInfoList = VideoUtil.scan(file, list);
            id.setCellValueFactory(new PropertyValueFactory<MediaInfo,Integer>("id"));
            fileName.setCellValueFactory(new PropertyValueFactory<MediaInfo,String>("fileName"));
            fileSize.setCellValueFactory(new PropertyValueFactory<MediaInfo,Long>("fileSize"));
            durationFormat.setCellValueFactory(new PropertyValueFactory<MediaInfo,String>("durationFormat"));
            ObservableList<MediaInfo> data = FXCollections.observableArrayList(mediaInfoList);
            mediTable.setItems(data);
        }
    }
}
