package com.youz.media.util;

import com.youz.media.Const;
import com.youz.media.model.MediaInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.youz.media.util.MediaFileInfoParse.FFMPEG_PATH;

public class VideoUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoUtil.class);
    private static final Random RANDOM = new Random();

    /**
     * 截图
     * @param sourcePath 原视频路径
     * @param targetPath 目标文件路径
     * @param duration 时长
     * @param pixel 图片尺寸
     */
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
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("截图错误", e);
            }
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

    /**
     * 随机截取图片
     * @param sourcePath 原视频路径
     * @param targetPath 目标文件路径
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
            String pixel = widthStr + Const.IMAGE_PIXEL_SPLIT +heightStr;

            //构造图片文件夹
            File sourceFile = new File(sourcePath);
            String folderPath = targetPath + sourceFile.getParent().substring(sourceFile.getParent().lastIndexOf(File.separator));

            for (int i=0;i<num;i++) {
                //图片路径
                String imagePath = folderPath + sourcePath.substring(sourcePath.lastIndexOf(File.separator),sourceFile.getName().lastIndexOf(Const.POINT_CHAR)) + Const.UNDERLINE_CHAR + (i+1) + Const.POINT_CHAR + Const.IMAGE_TYPE;

                int time = RANDOM.nextInt(duration) + 1;
                if (width < height) {
                    //竖图
                    int newWidth = Double.valueOf(16 / 9.0 * height).intValue();
                    pixel = newWidth + Const.IMAGE_PIXEL_SPLIT + height;
                    //先截取和高度成比例的图片
                    VideoUtil.screenshot(sourcePath,imagePath,time,pixel);
                    //再从图片中间截取
                    cropImage(imagePath,imagePath,newWidth/2-width/2,0,width,height, Const.IMAGE_TYPE);
                } else {
                    VideoUtil.screenshot(sourcePath,imagePath,time,pixel);
                }
            }
        }
    }

    /**
     * 截取视频
     * @param sourcePath 原视频路径
     * @param targetPath 目标文件路径
     * @param duration 总时长
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param cycle 截取频率(每cycle秒裁一段)
     */
    public static void interceptVodTime(String sourcePath, String targetPath, Integer duration, Integer startTime, Integer endTime, Integer cycle) {
//        ffmpeg  -i ./我与精彩集锦只差一个走位的距离.ts -vcodec copy -acodec copy -ss 00:00:00 -to 00:06:45 ./我与 精彩集锦只差1一个走位的距离.ts -y

        if (startTime > 0 || endTime > 0 || cycle > 0) {
            File file = new File(targetPath);
            String suffix = file.getName().substring(file.getName().lastIndexOf(Const.POINT_CHAR));
            File directory = new File(file.getParent());
            if (!directory.exists()) {
                directory.mkdirs();
            }

            endTime = duration - endTime;
            String start;
            String end;

            int index = 1;
            if (cycle != null && cycle > 0) {
                for (int i = startTime; i < endTime; i = i + cycle) {
                    String filePath = file.getParent() + File.separator + file.getName().substring(0,file.getName().lastIndexOf(Const.POINT_CHAR)) + Const.UNDERLINE_CHAR + index + suffix;
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
                        if (LOGGER.isErrorEnabled()) {
                            LOGGER.error("截取视频错误", e);
                        }
                    }
                    index++;
                }
            }else {
                String filePath = file.getParent() + File.separator + file.getName();
                start = durationFormat(startTime);
                end = durationFormat(endTime);

                List<String> commands = new ArrayList<String>();

                commands.add(FFMPEG_PATH);
                commands.add("-y");
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
                    if (LOGGER.isErrorEnabled()) {
                        LOGGER.error("截取视频错误", e);
                    }
                }
            }
        }
    }

    /**
     * 文件拷贝
     * @param sourcePath 原视频路径
     * @param targetPath 目标文件路径
     */
    public static void copy(String sourcePath, String targetPath) {
        File source = new File(sourcePath);
        File dest = new File(targetPath);
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
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("文件拷贝错误", e);
            }
        } finally {
            if(sourceCh != null){
                try {
                    sourceCh.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(destCh != null){
                try {
                    destCh.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    /**
     * 图片尺寸裁剪
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @param x x轴
     * @param y y轴
     * @param w 宽度
     * @param h 高度
     * @param sufix 文件后缀
     * @return
     */
    public static boolean cropImage(String sourcePath,String targetPath,int x,int y,int w,int h,String sufix) {
        InputStream is = null;
        try {
            is = new FileInputStream(sourcePath);
            BufferedImage bufferedImage = ImageIO.read(is);
            bufferedImage = bufferedImage.getSubimage(x,y,w,h);
            return ImageIO.write(bufferedImage,sufix,new File(targetPath));
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("图片尺寸裁剪错误", e);
            }
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

    /**
     * 去水印
     * @param mediaInfo:视频路径
     * @param targetPath:最终路径
     * @param xArr:总时长
     * @param yArr:宽度
     * @param wArr:高度
     * @param hArr:数量
     */
    public static void removeWatermark(MediaInfo mediaInfo, String targetPath, Integer[] xArr, Integer[] yArr, Integer[] wArr, Integer[] hArr) {
        //./ffmpeg.exe -i program00618829.ts -vf "delogo=x=1595:y=505:w=142:h=63,delogo=x=1715:y=1:w=203:h=25" -s 1920x1080 -vcodec h264 -c:a copy -b 6M 3.ts -y        if (StringUtils.isNotBlank(widthStr) && StringUtils.isNotBlank(heightStr) && num != null && num > 0) {
        String sourcePath = mediaInfo.getFilePath();
        File folderFile = new File(targetPath.substring(0,targetPath.lastIndexOf(File.separator)));
        if (!folderFile.exists()) {
            //创建图片目录
            folderFile.mkdirs();
        }

        List<String> commands = new ArrayList<String>();
        commands.add(FFMPEG_PATH);
        commands.add("-i");
        commands.add(sourcePath.replace("\\","/"));
        commands.add("-filter_complex");
        StringBuffer buffer = new StringBuffer();
        for (int i=0;i<xArr.length;i++) {
            buffer.append("delogo=")
                    .append("x=").append(xArr[i]).append(":")
                    .append("y=").append(yArr[i]).append(":")
                    .append("w=").append(wArr[i]).append(":")
                    .append("h=").append(hArr[i])
                    .append(",");
        }
        commands.add(""+buffer.substring(0,buffer.length()-1)+"");
        commands.add(targetPath.replace("\\","/"));
        commands.add("-y");
        InputStream in =  null;
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            in = process.getInputStream();
            byte[] bytes = new byte[1024];
            while (in.read(bytes) != -1) {
//                        System.out.println(new String(bytes));
            }
        } catch (IOException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("去水印错误", e);
            }
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

    /**
     * 合并视频文件(码率、音频要一致)
     * @param sourcePathArr:源视频路径
     * @param targetPath:最终路径
     */
    public static void mergeFile(String[] sourcePathArr, String targetPath) {
        File file = new File("merge.txt");
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new FileWriter(file,false),true);
            for (String path:sourcePathArr) {
                printWriter.println("file '" + path + "'");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }

        //./ffmpeg.exe -f concat -i filelist.txt -c copy result.ts -y
        File folderFile = new File(targetPath.substring(0,targetPath.lastIndexOf(File.separator)));
        if (!folderFile.exists()) {
            //创建图片目录
            folderFile.mkdirs();
        }

        List<String> commands = new ArrayList<String>();
        commands.add(FFMPEG_PATH);
        commands.add("-f");
        commands.add("concat");
        commands.add("-safe");
        commands.add("0");
        commands.add("-i");
        commands.add(file.getPath());
        commands.add("-c");
        commands.add("copy");
        commands.add(targetPath);
        commands.add("-y");
        InputStream in =  null;
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            in = process.getInputStream();
            byte[] bytes = new byte[1024];
            while (in.read(bytes) != -1) {
//                System.out.println(new String(bytes));
            }
        } catch (IOException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("合并视频错误", e);
            }
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
}
