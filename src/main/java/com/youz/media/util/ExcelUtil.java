package com.youz.media.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.youz.media.model.ExcelModel;
import com.youz.media.model.MediaInfo;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class ExcelUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MediaFileInfoParse.class);

    public static void exportExcel(List<MediaInfo> mediaInfoList, Boolean mergeSheetExcel, String path) {
        Map<String, List<ExcelModel>> map = buildExcelMap(mergeSheetExcel,mediaInfoList);
        String excelPath = path + File.separator + new Date().getTime() + ".xls";
        String[] head = {"视频名称", "专辑名称", "时长(秒)", "时长(00:00:00)", "比特率", "编码格式", "媒体类型", "分辨率", "编码格式", "频率", "比特率", "大小", "大小(MB)", "描述"};
        FileOutputStream os = null;
        try {
            //创建工作簿
            HSSFWorkbook workbook = new HSSFWorkbook();
            //列标题样式
            HSSFCellStyle style_header = createCellStyle(workbook, (short) 13);
            HSSFCellStyle style_row = createCellStyle(workbook, (short) 10);
            for (Map.Entry<String, List<ExcelModel>> excelModel : map.entrySet()) {
                //创建工作表
                String sheetName = "".equals(excelModel.getKey()) ? "无专辑分类" : excelModel.getKey();
                HSSFSheet sheet = workbook.createSheet(sheetName);
                //加载合并单元格对象
                //设置默认列宽
                sheet.setDefaultColumnWidth(20);
                //创建列标题行；并且设置列标题
                HSSFRow row2 = sheet.createRow(0);
                for (int i = 0; i < head.length; i++) {
                    HSSFCell cell2 = row2.createCell(i);
                    //加载单元格样式
                    cell2.setCellStyle(style_header);
                    cell2.setCellValue(head[i]);
                }
                //操作单元格；将用户列表写入excel
                List<ExcelModel> excelModelList = excelModel.getValue();
                if (excelModelList != null && excelModelList.size() > 0) {
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
                            cell.setCellValue(propertyValue);
                            cell.setCellStyle(style_row);
                        }
                    }
                }
            }
            //输出
            os = new FileOutputStream(excelPath);
            workbook.write(os);
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("导出EXCEL编单错误", e);
            }
        } finally {
            if (os != null) {
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
        style.setAlignment(HorizontalAlignment.CENTER);//水平居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        //创建字体
        HSSFFont font = workbook.createFont();
//        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//加粗字体
        font.setFontHeightInPoints(fontSize);
        //加载字体
        style.setFont(font);
        return style;
    }

    public static Map<String, List<ExcelModel>> buildExcelMap(Boolean mergeSheetExcel, List<MediaInfo> list) {
        Map<String, List<ExcelModel>> map = Maps.newHashMap();
        for (MediaInfo mediaInfo : list) {
            ExcelModel excelModel = new ExcelModel();
            excelModel.setName(mediaInfo.getFileName());
            excelModel.setAlbumName(mediaInfo.getFolderName());
            if (mediaInfo.getDuration() != null) {
                excelModel.setDuration(mediaInfo.getDuration());
            }
            if (mediaInfo.getDurationFormat() != null) {
                excelModel.setDurationFormat(mediaInfo.getDurationFormat());
            }
            if (mediaInfo.getBitRate() != null) {
                excelModel.setBitRate(mediaInfo.getBitRate());
            }
            if (mediaInfo.getVideoCodeType() != null) {
                excelModel.setVideoCodeType(mediaInfo.getVideoCodeType());
            }
            if (mediaInfo.getVideoType() != null) {
                excelModel.setVideoType(mediaInfo.getVideoType());
            }
            if (mediaInfo.getVideoPixel() != null) {
                excelModel.setVideoPixel(mediaInfo.getVideoPixel());
            }
            if (mediaInfo.getAudioBitRate() != null) {
                excelModel.setAudioBitRate(mediaInfo.getAudioBitRate());
            }
            if (mediaInfo.getAudioCodeType() != null) {
                excelModel.setAudioCodeType(mediaInfo.getAudioCodeType());
            }
            if (mediaInfo.getAudiofreq() != null) {
                excelModel.setAudiofreq(mediaInfo.getAudiofreq());
            }
            excelModel.setFileSize(mediaInfo.getFileSize() + "");
            excelModel.setFileSizeMB(new BigDecimal(mediaInfo.getFileSize()).divide(new BigDecimal(1048576), 2, RoundingMode.HALF_UP) + "MB");

            if (mergeSheetExcel) {
                List<ExcelModel> excelModelList = map.get("sheet1");
                if (excelModelList == null) {
                    excelModelList = Lists.newArrayList();
                }
                excelModelList.add(excelModel);
                map.put("sheet1",excelModelList);
            } else {
                List<ExcelModel> excelModelList = map.get(excelModel.getAlbumName());
                if (excelModelList == null) {
                    excelModelList = Lists.newArrayList();
                }
                excelModelList.add(excelModel);
                map.put(excelModel.getAlbumName(),excelModelList);
            }

        }
        return map;
    }
}