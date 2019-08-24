package com.youz.media;

import com.google.common.collect.Lists;
import com.youz.media.model.MediaInfo;
import com.youz.media.util.JsonUtil;
import com.youz.media.util.VideoUtil;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Controller {

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 50, 300, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    @FXML
    private TextArea mediaInfoTextArea;
    @FXML
    private TextField startInterceptVal;
    @FXML
    private TextField endInterceptVal;
    @FXML
    private TextField interceptRateVal;
    @FXML
    private ProgressBar progressBar;
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
    TableColumn<MediaInfo, String> schedule;

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
        int startTime = NumberUtils.toInt(startInterceptVal.getText());
        int endTime = NumberUtils.toInt(endInterceptVal.getText());
        int cycle = NumberUtils.toInt(interceptRateVal.getText());

        List<MediaInfo> list = mediTable.getItems();
        double count = list.size();
        AtomicInteger current = new AtomicInteger();
        for (MediaInfo mediaInfo : list) {
            executor.execute(() -> {
                File file = new File(mediaInfo.getFilePath());
                VideoUtil.interceptVodTime(mediaInfo.getFilePath(), "" + file.getName(), mediaInfo.getDuration(), startTime, endTime, cycle);
                mediaInfo.setSchedule("已完成");
                progressBar.setProgress(current.incrementAndGet() / count);

                if (current.get() == count) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.titleProperty().set("提示");
                            alert.headerTextProperty().set("操作成功");
                            alert.showAndWait();
                        }
                    });

                }
            });
        }
    }

    private void loadTableItem(File file) {
        if (file != null) {
            List<MediaInfo> list = Lists.newArrayList();
            List<MediaInfo> mediaInfoList = VideoUtil.scan(file, list);
            System.out.println(JsonUtil.toJson(mediaInfoList));
            id.setCellValueFactory(new PropertyValueFactory<MediaInfo, Integer>("id"));
            fileName.setCellValueFactory(new PropertyValueFactory<MediaInfo, String>("fileName"));
            fileSize.setCellValueFactory(new PropertyValueFactory<MediaInfo, Long>("fileSize"));
            durationFormat.setCellValueFactory(new PropertyValueFactory<MediaInfo, String>("durationFormat"));
            schedule.setCellValueFactory(new PropertyValueFactory<MediaInfo, String>("schedule"));
            ObservableList<MediaInfo> data = FXCollections.observableArrayList(mediaInfoList);
            mediTable.setItems(data);
            //设置行选中时间
            mediTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                mediaInfoTextArea.clear();
                mediaInfoTextArea.appendText(newValue.buildTextAreaContent());
            });
        }
    }

}
