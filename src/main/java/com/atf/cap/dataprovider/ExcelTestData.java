/**
 * 
 */
package com.atf.cap.dataprovider;
import java.io.*;
import java.util.ArrayList;

import jxl.*;
import jxl.read.biff.BiffException;

/**
 * @author zwxu
 *
 */
public class ExcelTestData {
	String defaultPath = new File("src/test/resources/ui/").getAbsolutePath() + "\\";
    
    public Object[][] getData(String caseName, String dataFile){
        return getData(caseName, dataFile, 0);
    }

    public Object[][] getData(String caseName, String dataFile, int rowNum)  {
        Object[][] data = null;
        try {
            data = addList(caseName, dataFile, rowNum);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return data;
    }
   
    public Object[][] getData(String caseName, String dataFile, int beginRowNum, int endRowNum) {
        Object[][] data = null;            
                try {
                    data = addList2(caseName, dataFile, beginRowNum, endRowNum);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        return data;
    }
   
   
    private Object[][] addList(String caseName,String dataFile,int rowNum) throws FileNotFoundException{
          ArrayList<Object> list = new ArrayList<Object>();
            // 文件路径
            InputStream is = new FileInputStream(defaultPath + dataFile);
            Object[][] data = null;
            try {
                Workbook wb = Workbook.getWorkbook(is);
                Sheet rs = wb.getSheet(caseName);
                //获取表格总行数
                 int rsRows = rs.getRows();
                //获取表格总列数
                 int rsColumns = rs.getColumns();

                 if(rs!=null){                     
                         for (int i = 1; i <= rsRows-1; i++){
                                for (int j=0; j <= rsColumns-1; j++){
                                    Cell c = rs.getCell(j, i);
                                    String cz = c.getContents();
                                    list.add(cz);
                                }
                            }
                            //System.out.println(list);   
                    }
                 
                    if (rowNum <= 0 || rowNum >= rsRows) {
                         data = new Object[rsRows-1][rsColumns];
                        int k = -1;
                        for (int i = 0; i < rsRows-1; i++) {
                            for (int j = 0; j < rsColumns; j++) {
                                if (k < list.size())
                                    k++;
                                data[i][j] = list.get(k);
                                //System.out.println("i="+i+","+"j="+j+","+data[i][j]);
                            }
                        }
                    } else {
                        int k = -1;
                        data = new Object[rowNum][rsColumns];
                        for (int i = 0; i < rowNum; i++) {
                            for (int j = 0; j < rsColumns; j++) {
                                if (k < list.size())
                                    k++;
                                if (i <= (rowNum - 1)) {
                                    data[i][j] = list.get(k);
                                }
                            }
                        }
                    }   
                    wb.close();
            } catch (BiffException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return data;       
        }


   
    private Object[][] addList2(String caseName, String dataFile, int beginNum,
            int endNum) throws FileNotFoundException {
       
          ArrayList<Object> list = new ArrayList<Object>();
            // 文件路径
            InputStream is = new FileInputStream(defaultPath + dataFile);
            Object[][] data = null;
            try {
                Workbook wb = Workbook.getWorkbook(is);
                Sheet rs = wb.getSheet(caseName);
                //获取表格总行数
                 int rsRows = rs.getRows();
                //获取表格总列数
                 int rsColumns = rs.getColumns();

                 if(rs!=null){                     
                         for (int i = 1; i <= rsRows-1; i++){
                                for (int j=0; j <= rsColumns-1; j++){
                                    Cell c = rs.getCell(j, i);
                                    String cz = c.getContents();
                                    list.add(cz);
                                }
                            }
                            //System.out.println(list);   
                    }
                 
                    int sub = (endNum - beginNum) + 1;
                    data = new Object[sub][rsColumns];
                    if (beginNum <= 0 || endNum > rsRows) {
                        if(beginNum <= 0&&endNum > rsRows){
                            beginNum=0;
                            endNum=rsRows;
                            data = new Object[rsRows][rsColumns];
                            for (int i = 0; i < sub; i++) {
                                for (int j = 0; j < rsColumns; j++) {
                                    if (beginNum < rsRows*rsColumns)
                                        data[i][j] = list.get(beginNum);
                                    beginNum++;
                                }
                            }
                        }else if (beginNum <= 0&&endNum <= rsRows){       
                            beginNum=0;
                            sub=(endNum - beginNum);
                            data = new Object[endNum][rsColumns];
                            for (int i = 0; i < sub; i++) {
                                for (int j = 0; j < rsColumns; j++) {
                                    if (beginNum < sub * rsColumns)
                                        data[i][j] = list.get(beginNum);
                                    beginNum++;
                                }
                            }
                        }else {
                            endNum=rsRows;
                            sub=(endNum - beginNum) + 1;
                            data = new Object[sub][rsColumns];
                            for (int i = 0; i < sub; i++) {
                                for (int j = 0; j < rsColumns; j++) {
                                    if (beginNum <=sub * rsColumns)
                                        data[i][j] = list.get(beginNum-1);
                                    beginNum++;
                                }
                            }
                           
                        }
                    } else {
                        int k = 0;
                        int a=beginNum*rsColumns;
                        for (int i = 0; i < sub; i++) {
                            for (int j = 0; j < rsColumns; j++) {
                                if (k < sub * rsColumns)
                                        data[i][j] = list.get(a+k-2);
                                        k++;

                            }
                        }
                    }   
                    wb.close();
            } catch (BiffException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return data;       
        }
}
