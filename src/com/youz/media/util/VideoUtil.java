package com.youz.media.util;

import com.youz.media.model.MediaInfo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class VideoUtil {

    public static SimpleDateFormat HH_MM_SS = new SimpleDateFormat("HH:mm:ss");

    /****
     * 获取指定时间内的图片
     * @param videoPath:视频路径
     * @param imagePath:图片保存路径
     * @param hour:指定时
     * @param min:指定分
     * @param sec:指定秒
     */
    public static Boolean getThumb(String ffmpegPath, String videoPath, String imagePath, String hour, String min, String sec, String pixel) {
        //ffmpeg.exe -i C:\yz\file_server\video\CSGO\123.ts -ss 00:00:20 -r 1 -q:v 2 -f image2 C:\yz\file_server\video\CSGO\a.jpeg
        //ffmpeg.exe -ss 02:00:20 -i D:\file_server\132.mkv -y -f image2 D:\file_server\a.jpg
        //ffmpeg.exe -ss 00:45:30 -i C:\yz\file_server\video\CSGO\123.ts  -vf select='eq(pict_type\,I)' -vsync 2 -s 1920*1080 -f image2 C:\yz\file_server\video\CSGO\a.jpg
        List<String> commands = new ArrayList<String>();

        commands.add(ffmpegPath);
        commands.add("-ss");
        commands.add(hour + ":" + min + ":" + sec);
        commands.add("-i");
        commands.add(videoPath);

        commands.add("-vf");
        commands.add("select='eq(pict_type\\,I)'");
        commands.add("-vsync");
        commands.add("2");

        commands.add("-y");
        commands.add("-f");
        commands.add("image2");
        commands.add("-s");
        commands.add(pixel); //这个参数是设置截取图片的大小
        commands.add("-b:v");
        commands.add("2000k");
//        commands.add("-q:v");//提高图片画质
//        commands.add("2");
        commands.add(imagePath);
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            InputStream in = process.getInputStream();
            byte[] bytes = new byte[1024];
            while (in.read(bytes) != -1) {
//                System.out.println("...");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /****
     * 改变图片大小
     * @param sourcePath:源图片路径
     * @param toImagePath:图片保存路径
     * @param pixel:图片尺寸
     */
    public static Boolean changePixel(String ffmpegPath, String sourcePath, String toImagePath, String pixel) {
//        ffmpeg.exe -i C:\\yz\\1.jpg -s 267x151 -y -f image2 C:\\yz\\a.jpg
        List<String> commands = new ArrayList<String>();

        commands.add(ffmpegPath);
        commands.add("-i");
        commands.add(sourcePath);
        commands.add("-s");
        commands.add(pixel); //这个参数是设置截取图片的大小
        commands.add("-y");
        commands.add("-f");
        commands.add("image2");
        commands.add(toImagePath);
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            InputStream in = process.getInputStream();
            byte[] bytes = new byte[1024];
            while (in.read(bytes) != -1) {
//                System.out.println("...");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /****
     * 截取视频时长
     * @param sourcePath:源视频路径
     * @param targetPath:目标视频路径
     * @param durationFormat:总时长
     */
    public static Boolean interceptVodTime(String ffmpegPath, String sourcePath, String targetPath, String durationFormat, Integer startTime, Integer endTime) {
//        ffmpeg  -i ./我与精彩集锦只差一个走位的距离.ts -vcodec copy -acodec copy -ss 00:00:00 -to 00:06:45 ./我与 精彩集锦只差1一个走位的距离.ts -y        List<String> commands = new ArrayList<String>();

        File file = new File(targetPath);
        File directory = new File(file.getParent());
        if(!directory.exists()){
            directory.mkdirs();
        }

        String[] split = durationFormat.split(":");

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.HOUR,0);
        startCalendar.set(Calendar.MINUTE,0);
        startCalendar.set(Calendar.SECOND,0);
        startCalendar.add(Calendar.SECOND,startTime);
        String start = String.format("%02d",startCalendar.get(Calendar.HOUR)) + ":" + String.format("%02d",startCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d",startCalendar.get(Calendar.SECOND));

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.HOUR,Integer.valueOf(split[0]));
        endCalendar.set(Calendar.MINUTE,Integer.valueOf(split[1]));
        endCalendar.set(Calendar.SECOND,Integer.valueOf(split[2].substring(0,split[2].lastIndexOf("."))));
        endCalendar.add(Calendar.SECOND,-endTime);
        String end = String.format("%02d",endCalendar.get(Calendar.HOUR)) + ":" + String.format("%02d",endCalendar.get(Calendar.MINUTE)) + ":" + String.format("%02d",endCalendar.get(Calendar.SECOND));

        List<String> commands = new ArrayList<String>();

        commands.add(ffmpegPath);
        commands.add("-i");
        commands.add(sourcePath);
        commands.add("-vcodec");
        commands.add("copy");
        commands.add("-acodec");
        commands.add("copy");
        commands.add("-ss");
        commands.add(start);
        commands.add("-to");
        commands.add(end);
        commands.add(targetPath);
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            InputStream in = process.getInputStream();
            byte[] bytes = new byte[1024];
            while (in.read(bytes) != -1) {
//                System.out.println("...");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static void trim(String scourcePath, String createPath, Integer x, Integer y, Integer width, Integer height) {
        try {
            String formatName = scourcePath.substring(scourcePath.lastIndexOf(".") + 1);
            BufferedImage bufferedImage = ImageIO.read(new FileInputStream(scourcePath));
            bufferedImage = bufferedImage.getSubimage(x, y, width, height);
            File file = new File(createPath);
            ImageIO.write(bufferedImage, formatName, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copy(String scourcePath, String createPath) throws Exception {
        File source = new File(scourcePath);
        File dest = new File(createPath);
        File parentPath = new File(dest.getParent());
        if(!parentPath.exists()){
            parentPath.mkdirs();
        }
        FileChannel sourceCh = null;
        FileChannel destCh = null;
        try {
            FileInputStream fis = new FileInputStream(source);
            FileOutputStream fos = new FileOutputStream(dest);
            sourceCh = fis.getChannel();
            destCh = fos.getChannel();
            destCh.transferFrom(sourceCh, 0, sourceCh.size());
        }finally {
            if(sourceCh != null){
                sourceCh.close();
            }
            if(destCh != null){
                destCh.close();
            }
        }
    }

    public static List<MediaInfo> scan(File file, List<MediaInfo> list) {
        if (file.isFile()) {
            MediaInfo mediaInfo = MediaFileInfoParse.parse(file.getPath());
            mediaInfo.setId(list.size() + 1);
            mediaInfo.setFileSize(file.length());
            list.add(mediaInfo);
        } else if(file.isDirectory()) {
            for (File f:file.listFiles()) {
                scan(f,list);
            }
        }
        return list;
    }
}
