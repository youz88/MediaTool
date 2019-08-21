package com.youz.media.util;

import com.youz.media.model.ExcelModel;
import com.youz.media.model.MediaInfo;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.hssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class ExcelUtil {

    private static final String FFMPEG_PATH = "";

    //暂时只扫描 ts 文件
    public static final Set<String> VOD_TYPE = new HashSet(){
        private static final long serialVersionUID = 1L;
        {
            add("ts");
            add("mp4");
        }
    };

    public static Map<String,List<ExcelModel>> scan(String path) {
        Map<String,List<ExcelModel>> map = null;
        File file = new File(path);
        if(file.exists() && file.isDirectory()){
            map = new HashMap();
            for(File f:file.listFiles()){
                if(f.isDirectory()){
                    String albumName = f.getName();
                    List<ExcelModel> excelModels = map.get(albumName);
                    if(excelModels == null){
                        excelModels = new ArrayList();
                    }

                    for(File child:f.listFiles()){
                        String suffix = child.getName().substring(child.getName().lastIndexOf(".") + 1);
                        if(child.isFile() && VOD_TYPE.contains(suffix)){
                            MediaInfo mediaInfo = MediaFileInfoParse.parse(child.getPath());
                            ExcelModel excelModel = new ExcelModel();
                            excelModel.setName(child.getName());
                            excelModel.setAlbumName(albumName);
                            if(mediaInfo.getDuration() != null){
                                excelModel.setDuration(mediaInfo.getDuration());
                            }
                            if(mediaInfo.getDurationFormat() != null){
                                excelModel.setDurationFormat(mediaInfo.getDurationFormat());
                            }
                            if(mediaInfo.getBitRate() != null){
                                excelModel.setBitRate(mediaInfo.getBitRate());
                            }
                            if(mediaInfo.getVideoCodeType() != null){
                                excelModel.setVideoCodeType(mediaInfo.getVideoCodeType());
                            }
                            if(mediaInfo.getVideoType() != null){
                                excelModel.setVideoType(mediaInfo.getVideoType());
                            }
                            if(mediaInfo.getVideoPixel() != null){
                                excelModel.setVideoPixel(mediaInfo.getVideoPixel());
                            }
                            if(mediaInfo.getAudioBitRate() != null){
                                excelModel.setAudioBitRate(mediaInfo.getAudioBitRate());
                            }
                            if(mediaInfo.getAudioCodeType() != null){
                                excelModel.setAudioCodeType(mediaInfo.getAudioCodeType());
                            }
                            if(mediaInfo.getAudiofreq() != null){
                                excelModel.setAudiofreq(mediaInfo.getAudiofreq());
                            }
                            if(child.exists()){
                                excelModel.setFileSize(child.length() + "");
                                excelModel.setFileSizeMB(new BigDecimal(child.length()).divide(new BigDecimal(1048576), 2, RoundingMode.HALF_UP) + "MB");
                            }
                            excelModels.add(excelModel);
                        }
                    }
                    map.put(albumName, excelModels);
                }
            }
        }
        return map;
    }

    public static void exportExcel(Map<String,List<ExcelModel>> map, String path) {

        String[] head = {"视频名称","专辑名称","时长(秒)","时长(00:00:00)","比特率","编码格式","媒体类型","分辨率","编码格式","频率","比特率","大小","大小(MB)","描述"};

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(path);
            //1、创建工作簿
            HSSFWorkbook workbook = new HSSFWorkbook();

            //1.3、列标题样式
            HSSFCellStyle style_header = createCellStyle(workbook, (short) 13);
            HSSFCellStyle style_row = createCellStyle(workbook, (short) 10);

            int sheetIndex = 0;
            for(Map.Entry<String, List<ExcelModel>> excelModel:map.entrySet()){
                //2、创建工作表
                String sheetName = "".equals(excelModel.getKey()) ? "无专辑分类" : excelModel.getKey();
                HSSFSheet sheet = workbook.createSheet(sheetName);
                //2.1、加载合并单元格对象
                //设置默认列宽
                sheet.setDefaultColumnWidth(20);

                //3.2、创建列标题行；并且设置列标题
                HSSFRow row2 = sheet.createRow(0);
                for (int i = 0; i < head.length; i++) {
                    HSSFCell cell2 = row2.createCell(i);
                    //加载单元格样式
                    cell2.setCellStyle(style_header);
                    cell2.setCellValue(head[i]);
                }

                //4、操作单元格；将用户列表写入excel
                List<ExcelModel> excelModelList = excelModel.getValue();

                int allRow = 0;
                if (excelModelList != null && excelModelList.size() > 0) {
                    int hour = 0;
                    int minute = 0;
                    int second = 0;
                    allRow = excelModelList.size();

                    for (int i = 0; i < excelModelList.size(); i++) {
                        HSSFRow row = sheet.createRow(i + 1);
                        ExcelModel model = excelModelList.get(i);
                        Class clazz = model.getClass();
                        Field[] fields = clazz.getDeclaredFields();
                        for (int j = 0; j < fields.length; j++) {
                            HSSFCell cell = row.createCell(j);
                            String propertyName = fields[j].getName();
                            String methodSuffixName = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
                            Method m = model.getClass().getMethod("get" + methodSuffixName);
                            String propertyValue = m.invoke(model).toString();
                            if (j == 3) {
                                propertyValue = propertyValue.substring(0,propertyValue.lastIndexOf("."));
                                String[] split = propertyValue.split(":");
                                hour += NumberUtils.toInt(split[0]);
                                minute += NumberUtils.toInt(split[1]);
                                second += NumberUtils.toInt(split[2]);
                            }
                            cell.setCellValue(propertyValue);
                            cell.setCellStyle(style_row);
                        }
                    }
                    if(second > 0){
                        minute += second / 60;
                        second = second % 60;
                    }
                    if(minute > 0){
                        hour += minute / 60;
                        minute = minute % 60;
                    }
                    String allTime = String.format("%02d", hour) + "." + String.format("%02d", minute) + "." + String.format("%02d", second);
                    workbook.setSheetName(sheetIndex,sheetName + "("+allRow+">"+allTime+")");
                }
                sheetIndex++;
            }

            //5、输出
            os = new FileOutputStream(path);
            workbook.write(os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 创建单元格样式
     *
     * @param workbook 工作簿
     * @param fontSize 字体大小
     * @return 单元格样式
     */
    private static HSSFCellStyle createCellStyle(HSSFWorkbook workbook, short fontSize) {
        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中
        //创建字体
        HSSFFont font = workbook.createFont();
//        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//加粗字体
        font.setFontHeightInPoints(fontSize);
        //加载字体
        style.setFont(font);
        return style;
    }

}