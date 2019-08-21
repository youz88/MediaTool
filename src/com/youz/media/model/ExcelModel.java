package com.youz.media.model;

public class ExcelModel {

    /** 正题名 */
    private String name = "";

    /** 系列题名 */
    private String albumName = "";

    /** 时长(300) */
    private Integer duration = 0;

    /** 时长(00:00:00) */
    private String durationFormat = "";

    /** 比特率(333) */
    private Integer bitRate = 0;

    /** 编码格式：(mpeg4) */
    private String videoCodeType = "";

    /** 媒体类型:(720p) */
    private String videoType = "";

    /** 分辨率(1024x768) */
    private String videoPixel = "";

    /** 编码格式：(ac3) */
    private String audioCodeType = "";

    /** 频率(3200)HZ */
    private String audiofreq = "";

    /** 比特率(333) */
    private String audioBitRate = "";

    /** 文件大小 */
    private String fileSize = "";

    /** 文件大小(MB) */
    private String fileSizeMB = "";

    /** 描述 */
    private String description = "";


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
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

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileSizeMB() {
        return fileSizeMB;
    }

    public void setFileSizeMB(String fileSizeMB) {
        this.fileSizeMB = fileSizeMB;
    }
}
