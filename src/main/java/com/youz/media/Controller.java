package com.youz.media;

import com.google.common.collect.Lists;
import com.youz.media.model.MediaInfo;
import com.youz.media.util.ExcelUtil;
import com.youz.media.util.VideoUtil;
import javafx.application.Platform;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Controller {

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 50, 300, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    @FXML
    private TextArea mediaInfoTextArea;
    @FXML
    private TextField sourcePath;
    @FXML
    private TextField targetPath;
    @FXML
    private TextField startInterceptVal;
    @FXML
    private TextField endInterceptVal;
    @FXML
    private TextField interceptRateVal;
    @FXML
    private TextField imageWidth;
    @FXML
    private TextField imageHeight;
    @FXML
    private TextField imageNum;
    @FXML
    private CheckBox switchExportExcel;
    @FXML
    private CheckBox mergeSheetExcel;
    @FXML
    private TextField watermarkX;
    @FXML
    private TextField watermarkY;
    @FXML
    private TextField watermarkWidth;
    @FXML
    private TextField watermarkHeight;
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
    private void chooseSourcePath(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(new Stage());
        if (file != null) {
            sourcePath.clear();
            sourcePath.appendText(file.getPath());
            loadTableItem(file);
        }
    }

    @FXML
    private void chooseTargetPath(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(new Stage());
        if (file != null) {
            targetPath.clear();
            targetPath.appendText(file.getPath());
        }
    }

    @FXML
    private void openFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            loadTableItem(file);
        }
    }

    @FXML
    private void showDonation(ActionEvent event) {
        ImageView logo = new ImageView(getClass().getClassLoader().getResource("images/logo.png").toString());
        Stage window = new Stage();
        window.setTitle("o(*￣▽￣*)o");
        window.initModality(Modality.APPLICATION_MODAL);
        window.setResizable(Boolean.FALSE);
        BorderPane root = new BorderPane();
        root.setCenter(logo);
        Scene scene = new Scene(root);
        window.setScene(scene);
        window.showAndWait();
    }

    @FXML
    private void start(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.titleProperty().set("提示");
        if (StringUtils.isBlank(targetPath.getText())) {
            alert.headerTextProperty().set("保存路径不能为空");
            alert.showAndWait();
            return;
        }
        File file = new File(targetPath.getText().trim());
        if (!file.exists() || !file.isDirectory()) {
            alert.headerTextProperty().set("保存路径不存在");
            alert.showAndWait();
            return;
        }

        start();
    }

    private void loadTableItem(File file) {
        if (file != null) {
            List<MediaInfo> list = Lists.newArrayList();
            List<MediaInfo> mediaInfoList = VideoUtil.scan(file, list);
            id.setCellValueFactory(new PropertyValueFactory<MediaInfo, Integer>("id"));
            fileName.setCellValueFactory(new PropertyValueFactory<MediaInfo, String>("fileName"));
            fileSize.setCellValueFactory(new PropertyValueFactory<MediaInfo, Long>("fileSize"));
            durationFormat.setCellValueFactory(new PropertyValueFactory<MediaInfo, String>("durationFormat"));
            schedule.setCellValueFactory(new PropertyValueFactory<MediaInfo, String>("schedule"));
            ObservableList<MediaInfo> data = FXCollections.observableArrayList(mediaInfoList);
            mediTable.setItems(data);
            //设置行选中事件
            mediTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                mediaInfoTextArea.clear();
                mediaInfoTextArea.appendText(newValue.buildTextAreaContent());
            });

            mediTable.setRowFactory(tv -> {
                TableRow<MediaInfo> row = new TableRow<MediaInfo>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                        MediaInfo mediaInfo = row.getItem();
                    }
                });
                return row;
            });
        }
    }

    private void start() {
        int startTime = NumberUtils.toInt(startInterceptVal.getText());
        int endTime = NumberUtils.toInt(endInterceptVal.getText());
        int cycle = NumberUtils.toInt(interceptRateVal.getText());
        String basePath = targetPath.getText().trim() + File.separator;
        //文件列表
        List<MediaInfo> list = mediTable.getItems();
        //初始化进度
        double count = list.size();
        AtomicInteger current = new AtomicInteger();

        for (MediaInfo mediaInfo : list) {
            executor.execute(() -> {
                File file = new File(mediaInfo.getFilePath());
                //截取视频
                VideoUtil.interceptVodTime(mediaInfo.getFilePath(), basePath + file.getName(), mediaInfo.getDuration(), startTime, endTime, cycle);
                //截图
                VideoUtil.randomScreenshot(mediaInfo.getFilePath(),basePath,mediaInfo.getDuration(),imageWidth.getText(),imageHeight.getText(),NumberUtils.toInt(imageNum.getText()));
                //去水印
                if (StringUtils.isNotBlank(watermarkX.getText()) && StringUtils.isNotBlank(watermarkY.getText()) &&
                     StringUtils.isNotBlank(watermarkWidth.getText()) &&StringUtils.isNotBlank(watermarkHeight.getText())) {
                    Integer[] xArr = {NumberUtils.toInt(watermarkX.getText())};
                    Integer[] yArr = {NumberUtils.toInt(watermarkY.getText())};
                    Integer[] wArr = {NumberUtils.toInt(watermarkWidth.getText())};
                    Integer[] hArr = {NumberUtils.toInt(watermarkHeight.getText())};
                    VideoUtil.removeWatermark(mediaInfo,basePath + file.getName(),xArr,yArr,wArr,hArr);
                }
                mediaInfo.setSchedule("已完成");
                progressBar.setProgress(current.incrementAndGet() / count);

                if (current.get() == count) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            //导出Excel
                            if (switchExportExcel.isSelected()) {
                                ExcelUtil.exportExcel(list,mergeSheetExcel.isSelected(),basePath);
                            }
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
}
