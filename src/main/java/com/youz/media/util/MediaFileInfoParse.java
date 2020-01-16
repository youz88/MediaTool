package com.youz.media.util;

import com.youz.media.model.MediaInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MediaFileInfoParse {

    private static final Logger LOGGER = LoggerFactory.getLogger(MediaFileInfoParse.class);

    public static final String FFMPEG_PATH = new File("").getAbsolutePath() + "/resources/ffmpeg/bin/ffmpeg.exe";
    //打包 public static final String FFMPEG_PATH = new File("").getAbsolutePath() + "/ffmpeg/bin/ffmpeg.exe";
    private static final String FILE_SPLIT = ".";

    public static MediaInfo parse(String filepath) {
        MediaInfo mediaInfo = null;
        String info = getInitFileInfo(filepath);
        mediaInfo = parseFileInfo(info);
        File file = new File(filepath);
        mediaInfo.setFilePath(file.getPath());
        mediaInfo.setFileName(file.getName().substring(0,file.getName().lastIndexOf(FILE_SPLIT)));
        mediaInfo.setFolderName(file.getParent().substring(file.getParent().lastIndexOf(File.separator) + 1));
        return mediaInfo;
    }

    public static String getInitFileInfo(String filepath) {
        StringBuffer sb = new StringBuffer();
        Process p = null;
        try {
            List<String> commend = new ArrayList<String>();
            commend.add(FFMPEG_PATH);
            commend.add("-i");
            commend.add(filepath);

            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            builder.redirectErrorStream(true);
            p = builder.start();

            BufferedReader buf = new BufferedReader(new InputStreamReader(p
                    .getInputStream()));

            String line = null;
            while ((line = buf.readLine()) != null) {
                sb.append(line);
            }
            p.waitFor();
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("扫描文件错误", e);
            }
        } finally {
            if (null != p) {
                p.destroy();
            }
        }
        return sb.toString();
    }

    public static MediaInfo parseFileInfo(String info) {
        MediaInfo mediaInfo = new MediaInfo();
        try {
            String regexDuration = "Duration: (.*?), start: (.*?), bitrate: (.*?) kb/s";
            String regexVideo = "Video: (.*?) .*?, (.*?)(\\(.*?\\))?, (.*?), (.*?) kb/s, (.*?) fps, (.*?) tbr, (.*?) tbn, (.*?) tbc";
            String regexAudio = "Audio: (.*?) .*?, (.*?) Hz, (.*?), (.*?), (.*?) kb/s";

            Pattern patternDuration = Pattern.compile(regexDuration);
            Matcher matcherDuration = patternDuration.matcher(info);
            if (matcherDuration.find()) {
                String durationStr = matcherDuration.group(1);
                String startStr = matcherDuration.group(2);
                String bitRateStr = matcherDuration.group(3);
                mediaInfo.setDurationFormat(durationStr);
                String[] durationArr = durationStr.split(":");
                int duration = 0;
                for (int i = 0; i < durationArr.length; i++) {
                    duration += Double.valueOf(durationArr[i]) * Math.pow(60, durationArr.length - 1 - i);
                }
                mediaInfo.setDuration(duration);
                mediaInfo.setStart(startStr);
                mediaInfo.setBitRate(Integer.valueOf(bitRateStr));
            }

            Pattern patternVideo = Pattern.compile(regexVideo);
            Matcher matcherVideo = patternVideo.matcher(info);
            if (matcherVideo.find()) {
                String videoCodeTypeStr = matcherVideo.group(1);
                String videoTypeStr = matcherVideo.group(2);
                String pixelStr = matcherVideo.group(4);
                String bitRateStr = matcherVideo.group(5);
                String videoFpsStr = matcherVideo.group(6);
                String videoTbrStr = matcherVideo.group(7);
                String videoTbnStr = matcherVideo.group(8);
                String videoTbcStr = matcherVideo.group(9);
                mediaInfo.setVideoCodeType(videoCodeTypeStr);
                mediaInfo.setVideoType(videoTypeStr);
                mediaInfo.setVideoPixel(pixelStr);
                mediaInfo.setVideoFps(videoFpsStr);
                mediaInfo.setVideoTbr(videoTbrStr);
                mediaInfo.setVideoTbn(videoTbnStr);
                mediaInfo.setVideoTbc(videoTbcStr);
            }

            Pattern patternAudio = Pattern.compile(regexAudio);
            Matcher matcherAudio = patternAudio.matcher(info);

            if (matcherAudio.find()) {
                String audioCodeTypeStr = matcherAudio.group(1);
                String audiofreqStr = matcherAudio.group(2);
                String audioVoiceStr = matcherAudio.group(3);
                String audioInterfaceStr = matcherAudio.group(4);
                String audioBitRateStr = matcherAudio.group(5);
                mediaInfo.setAudioCodeType(audioCodeTypeStr);
                mediaInfo.setAudiofreq(audiofreqStr);
                mediaInfo.setAudioVoice(audioVoiceStr);
                mediaInfo.setAudioInterface(audioInterfaceStr);
                mediaInfo.setAudioBitRate(audioBitRateStr);
            }
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("解析文件信息错误", e);
            }
        }
        return mediaInfo;
    }
}
