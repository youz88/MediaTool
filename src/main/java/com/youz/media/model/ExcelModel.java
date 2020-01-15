package com.youz.media.model;

import lombok.Data;

@Data
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

}
