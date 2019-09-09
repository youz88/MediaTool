package com.youz.media.util;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import com.youz.media.model.MediaInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static com.youz.media.util.MediaFileInfoParse.FFMPEG_PATH;

public class VideoUtil {
    private static final Random RANDOM = new Random();

    public static void screenshot(String sourcePath, String targetPath, Integer duration, String pixel) {
        File folderFile = new File(targetPath.substring(0,targetPath.lastIndexOf(File.separator)));
        if (!folderFile.exists()) {
            //创建图片目录
            folderFile.mkdirs();
        }

        List<String> commands = new ArrayList<String>();
        commands.add(FFMPEG_PATH);
        commands.add("-ss");
        commands.add(duration.toString());
        commands.add("-i");
        commands.add(sourcePath);
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
        commands.add("-q:v");//提高图片画质
        commands.add("2");
        commands.add(targetPath);
        InputStream in =  null;
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            in = process.getInputStream();
            byte[] bytes = new byte[1024];
            while (in.read(bytes) != -1) {
//                        System.out.println("...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

        /****
         * 随机截取图片
         * @param sourcePath:视频路径
         * @param targetPath:图片保存基本路径
         * @param duration:总时长
         * @param widthStr:宽度
         * @param heightStr:高度
         * @param num:数量
         */
    public static void randomScreenshot(String sourcePath, String targetPath, Integer duration, String widthStr, String heightStr, Integer num) {
        //ffmpeg.exe -ss 00:45:30 -i C:\yz\file_server\video\CSGO\123.ts  -vf select='eq(pict_type\,I)' -vsync 2 -s 1920*1080 -f image2 C:\yz\file_server\video\CSGO\a.jpg
        if (StringUtils.isNotBlank(widthStr) && StringUtils.isNotBlank(heightStr) && num != null && num > 0) {
            int width = NumberUtils.toInt(widthStr);
            int height = NumberUtils.toInt(heightStr);
            String pixel = widthStr + "x" +heightStr;

            //构造图片文件夹
            File sourceFile = new File(sourcePath);
            String folderPath = targetPath + sourceFile.getParent().substring(sourceFile.getParent().lastIndexOf(File.separator));

            for (int i=0;i<num;i++) {
                String imagePath = folderPath + sourcePath.substring(sourcePath.lastIndexOf(File.separator),sourceFile.getName().lastIndexOf(".")) + "_" + (i+1) + ".jpg";

                int time = RANDOM.nextInt(duration) + 1;
                if (width < height) {
                    //竖图
                    int newWidth = Double.valueOf(16 / 9.0 * height).intValue();
                    pixel = newWidth + "x" + height;
                    //先截取和高度成比例的图片
                    VideoUtil.screenshot(sourcePath,imagePath,time,pixel);
                    //再从图片中间截取
                    cropImage(imagePath,imagePath,newWidth/2-width/2,0,width,height,"jpg");
                } else {
                    VideoUtil.screenshot(sourcePath,imagePath,time,pixel);
                }
            }
        }
    }

    /****
     * 截取视频时长
     * @param sourcePath:源视频路径
     * @param targetPath:目标视频路径
     * @param duration:总时长
     */
    public static void interceptVodTime(String sourcePath, String targetPath, Integer duration, Integer startTime, Integer endTime, Integer cycle) {
//        ffmpeg  -i ./我与精彩集锦只差一个走位的距离.ts -vcodec copy -acodec copy -ss 00:00:00 -to 00:06:45 ./我与 精彩集锦只差1一个走位的距离.ts -y

        if (startTime > 0 || endTime > 0 || cycle > 0) {
            File file = new File(targetPath);
            String suffix = file.getName().substring(file.getName().lastIndexOf("."));
            File directory = new File(file.getParent());
            if (!directory.exists()) {
                directory.mkdirs();
            }

            endTime = duration - endTime;
            String start;
            String end;

            int index = 1;
            if (cycle > 0) {
                for (int i = startTime; i < endTime; i = i + cycle) {
                    String filePath = file.getParent() + File.separator + file.getName().substring(0,file.getName().lastIndexOf(".")) + "_" + index + suffix;
                    start = durationFormat(i);
                    if(i+cycle < endTime){
                        end = durationFormat(i+cycle);
                    }else {
                        end = durationFormat(endTime);
                    }

                    List<String> commands = new ArrayList<String>();
                    commands.add(FFMPEG_PATH);
                    commands.add("-ss");
                    commands.add(start);
                    commands.add("-to");
                    commands.add(end);
                    commands.add("-accurate_seek");
                    commands.add("-i");
                    commands.add(sourcePath);
                    commands.add("-codec");
                    commands.add("copy");
                    commands.add("-avoid_negative_ts");
                    commands.add("1");
                    commands.add(filePath);
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
                    }
                    index++;
                }
            }else {
                String filePath = file.getParent() + File.separator + index + "_" + file.getName();
                start = durationFormat(startTime);
                end = durationFormat(endTime);

                List<String> commands = new ArrayList<String>();

                commands.add(FFMPEG_PATH);
                commands.add("-ss");
                commands.add(start);
                commands.add("-to");
                commands.add(end);
                commands.add("-accurate_seek");
                commands.add("-i");
                commands.add(sourcePath);
                commands.add("-codec");
                commands.add("copy");
                commands.add("-avoid_negative_ts");
                commands.add("1");
                commands.add(filePath);
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
                }
            }
        }
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

    public static String durationFormat(Integer duration) {
        StringBuffer buffer = new StringBuffer();
        int h = duration / 3600;
        if (h > 0) {
            buffer.append(String.format("%02d", h)).append(":");
        } else {
            buffer.append(String.format("%02d", 0)).append(":");
        }
        int time = duration % 3600;
        if (time > 0) {
            int m = time / 60;
            int s = time % 60;
            buffer.append(String.format("%02d", m)).append(":");
            buffer.append(String.format("%02d", s));
        } else {
            buffer.append("00:00");
        }
        return buffer.toString();
    }

    /** 图片裁剪 */
    public static boolean cropImage(String sourcePath,String targetPath,int x,int y,int w,int h,String sufix) {
        InputStream is = null;
        try {
            is = new FileInputStream(sourcePath);
            BufferedImage bufferedImage = ImageIO.read(is);
            bufferedImage = bufferedImage.getSubimage(x,y,w,h);
            return ImageIO.write(bufferedImage,sufix,new File(targetPath));
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
