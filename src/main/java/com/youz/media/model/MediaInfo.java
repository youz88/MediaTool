package com.youz.media.model;

import lombok.Data;

@Data
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
    private String schedule = "-";

    /** 文件路径 */
    private String filePath;

    /** 文件路径 */
    private Long fileSize;

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
