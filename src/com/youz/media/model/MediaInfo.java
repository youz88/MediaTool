package com.youz.media.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

public class MediaInfo {

    /** 时长(300) */
    private Integer duration;

    /** 时长(00:00:00) */
    private String durationFormat;
    
    /** 比特率(333) */
    private Integer bitRate;

    /** 编码格式：(mpeg4) */
    private String videoCodeType;

    /** 媒体类型:(720p) */
    private String videoType;

    /** 分辨率(1024x768) */
    private String videoPixel;

    /** 编码格式：(ac3) */
    private String audioCodeType;

    /** 频率(3200)HZ */
    private String audiofreq;

    /** 比特率(333) */
    private String audioBitRate;

    //额外添加属性
    /** 唯一标识 */
    private Integer id;

    /** 文件名称 */
    private String fileName;

    /** 进度 */
    private StringProperty schedule = new SimpleStringProperty("未完成");

    /** 文件路径 */
    private String filePath;

    /** 文件路径 */
    private Long fileSize;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSchedule() {
        return schedule.get();
    }

    public StringProperty scheduleProperty() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule.set(schedule);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getDurationFormat() {
        return durationFormat;
    }

    public void setDurationFormat(String durationFormat) {
        this.durationFormat = durationFormat;
    }

    public Integer getBitRate() {
        return bitRate;
    }

    public void setBitRate(Integer bitRate) {
        this.bitRate = bitRate;
    }

    public String getVideoCodeType() {
        return videoCodeType;
    }

    public void setVideoCodeType(String videoCodeType) {
        this.videoCodeType = videoCodeType;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getVideoPixel() {
        return videoPixel;
    }

    public void setVideoPixel(String videoPixel) {
        this.videoPixel = videoPixel;
    }

    public String getAudioCodeType() {
        return audioCodeType;
    }

    public void setAudioCodeType(String audioCodeType) {
        this.audioCodeType = audioCodeType;
    }

    public String getAudiofreq() {
        return audiofreq;
    }

    public void setAudiofreq(String audiofreq) {
        this.audiofreq = audiofreq;
    }

    public String getAudioBitRate() {
        return audioBitRate;
    }

    public void setAudioBitRate(String audioBitRate) {
        this.audioBitRate = audioBitRate;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}
