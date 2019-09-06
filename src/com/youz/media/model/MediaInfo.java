package com.youz.media.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MediaInfo {

    /** 时长(300) */
    private Integer duration;

    /** 时长(00:00:00) */
    private String durationFormat;

    /** 开始时间(0.5000) */
    private String start;

    /** 比特率(333) */
    private Integer bitRate;

    /** 编码格式：(mpeg4) */
    private String videoCodeType;

    /** 媒体类型:(720p) */
    private String videoType;

    /** 分辨率(1024x768) */
    private String videoPixel;

    /** 帧率 */
    private String videoFps;

    /**  */
    private String videoTbr;

    /** 时间精度 */
    private String videoTbn;

    /**  */
    private String videoTbc;

    /** 编码格式：(ac3) */
    private String audioCodeType;

    /** 频率(3200)HZ */
    private String audiofreq;

    /**  */
    private String audioVoice;

    /**  */
    private String audioInterface;

    /** 比特率(333) */
    private String audioBitRate;

    //额外添加属性
    /** 唯一标识 */
    private Integer id;

    /** 文件名称 */
    private String fileName;

    /** 文件夹名称 */
    private String folderName;

    /** 进度 */
    private StringProperty schedule = new SimpleStringProperty("-");

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

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getVideoFps() {
        return videoFps;
    }

    public void setVideoFps(String videoFps) {
        this.videoFps = videoFps;
    }

    public String getVideoTbr() {
        return videoTbr;
    }

    public void setVideoTbr(String videoTbr) {
        this.videoTbr = videoTbr;
    }

    public String getVideoTbn() {
        return videoTbn;
    }

    public void setVideoTbn(String videoTbn) {
        this.videoTbn = videoTbn;
    }

    public String getVideoTbc() {
        return videoTbc;
    }

    public void setVideoTbc(String videoTbc) {
        this.videoTbc = videoTbc;
    }

    public String getAudioVoice() {
        return audioVoice;
    }

    public void setAudioVoice(String audioVoice) {
        this.audioVoice = audioVoice;
    }

    public String getAudioInterface() {
        return audioInterface;
    }

    public void setAudioInterface(String audioInterface) {
        this.audioInterface = audioInterface;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String buildTextAreaContent(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("|--名称:").append(fileName).append("\n")
                .append("|--视频:").append("\n")
                .append("\t|--时长:").append(durationFormat).append("\n")
                .append("\t|--开始时间:").append(start).append("\n")
                .append("\t|--比特率:").append(bitRate).append("\n")
                .append("\t|--码率:").append(videoCodeType).append("\n")
                .append("\t|--类型:").append(videoType).append("\n")
                .append("\t|--分辨率:").append(videoPixel).append("\n")
                .append("\t|--FPS:").append(videoFps).append("\n")
                .append("\t|--TBR:").append(videoTbr).append("\n")
                .append("\t|--TBN:").append(videoTbn).append("\n")
                .append("\t|--TBC:").append(videoTbc).append("\n")
                .append("|--音频:").append("\n")
                .append("\t|--编码格式:").append(audioCodeType).append("\n")
                .append("\t|--采样率:").append(audiofreq).append("\n")
                .append("\t|--语音:").append(audioVoice).append("\n")
                .append("\t|--比特率:").append(audioBitRate).append("\n")
                .append("\t|--FMT:").append(audioInterface);
        return buffer.toString();
    }
}
