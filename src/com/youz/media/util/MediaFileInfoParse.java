package com.youz.media.util;

import com.youz.media.model.MediaInfo;
import org.apache.oro.text.regex.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MediaFileInfoParse {

    public static final String FFMPEG_PATH = new File("").getAbsolutePath() + "/ffmpeg/bin/ffmpeg.exe";
    private static final String FILE_SPLIT = ".";

    public static MediaInfo parse(String filepath) {
        MediaInfo mediaInfo = null;
        try {
            String info = getInitFileInfo(filepath);
            mediaInfo = parseFileInfo(info);
            File file = new File(filepath);
            mediaInfo.setFilePath(file.getPath());
            mediaInfo.setFileName(file.getName().substring(0,file.getName().lastIndexOf(FILE_SPLIT)));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            e.printStackTrace();
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
            PatternCompiler compiler = new Perl5Compiler();
            String regexDuration = "Duration: (.*?), start: (.*?), bitrate: (\\d*) kb\\/s";
            String regexVideo = "Video: (.*?) .*, (.*?), (.*?) .*, (.*?) fps, (.*?) tbr, (.*?) tbn, (\\w*) tbc";
            String regexAudio = "Audio: (\\w*) .*, (\\d*) Hz, (.*?), (\\w*), (\\d*) kb\\/s";
            Pattern patternDuration = compiler.compile(regexDuration,
                    Perl5Compiler.CASE_INSENSITIVE_MASK);
            PatternMatcher matcherDuration = new Perl5Matcher();
            if (matcherDuration.contains(info, patternDuration)) {
                MatchResult re = matcherDuration.getMatch();
                String durationStr = re.group(1);
                mediaInfo.setDurationFormat(durationStr);
                String[] durationArr = durationStr.split(":");
                int duration = 0;
                for (int i = 0; i < durationArr.length; i++) {
                    duration += Double.valueOf(durationArr[i])
                            * Math.pow(60, durationArr.length - 1 - i);
                }
                mediaInfo.setDuration(duration);
                mediaInfo.setStart(re.group(2));
                mediaInfo.setBitRate(Integer.valueOf(re.group(3)));
            }

            Pattern patternVideo = compiler.compile(regexVideo,
                    Perl5Compiler.CASE_INSENSITIVE_MASK);
            PatternMatcher matcherVideo = new Perl5Matcher();
            if (matcherVideo.contains(info, patternVideo)) {
                MatchResult re = matcherVideo.getMatch();
                mediaInfo.setVideoCodeType(re.group(1));
                mediaInfo.setVideoType(re.group(2));
                mediaInfo.setVideoPixel(re.group(3));
                mediaInfo.setVideoFps(re.group(4));
                mediaInfo.setVideoTbr(re.group(5));
                mediaInfo.setVideoTbn(re.group(6));
                mediaInfo.setVideoTbc(re.group(7));
            }

            Pattern patternAudio = compiler.compile(regexAudio,
                    Perl5Compiler.CASE_INSENSITIVE_MASK);
            PatternMatcher matcherAudio = new Perl5Matcher();

            if (matcherAudio.contains(info, patternAudio)) {
                MatchResult re = matcherAudio.getMatch();
                mediaInfo.setAudioCodeType(re.group(1));
                mediaInfo.setAudiofreq(re.group(2));
                mediaInfo.setAudioVoice(re.group(3));
                mediaInfo.setAudioInterface(re.group(4));
                mediaInfo.setAudioBitRate(re.group(5));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaInfo;
    }
}
