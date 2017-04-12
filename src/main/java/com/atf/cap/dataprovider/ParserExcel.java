/**
 * 
 */
package com.atf.cap.dataprovider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;

import jxl.*;
import jxl.read.biff.BiffException;

/**
 * @author zwxu
 *
 */
public class ParserExcel {
	private static Workbook book = null;
	private static Sheet sheet = null;
	/**
	 * * 在TestNG中由@DataProvider(dataProvider = "name")修饰的方法读取Excel时，
	 * 调用此类的构造方法(此方法会得到列名并将当前行移到下一行)执行完后， 转到TestNG自己的方法中去，然后由它们调用此类实现的
	 * hasNext()、next()方法 得到一行数据，然后返回给由@Test(dataProvider =
	 * "name")修饰的方法，如此反复到数据读完为止.
	 * 
	 * @param filepath
	 *            Excel文件名
	 * @param casename
	 *            用例名
	 */
	public ParserExcel(String filepath, String casename) {

		try {			
			InputStream is = new FileInputStream(filepath);
			book = Workbook.getWorkbook(is);
			sheet = book.getSheet(casename);
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Object[][] getTableArray(String FilePath, String SheetName)
			throws Exception {

		String[][] tabArray = null;

		try {

			// FileInputStream ExcelFile = new FileInputStream(FilePath);

			// Access the required test data sheet

			InputStream is = new FileInputStream(FilePath);
			book = Workbook.getWorkbook(is);
			sheet = book.getSheet(SheetName);

			int startRow = 1;

			int startCol = 0;

			int ci, cj;

			int totalRows = sheet.getRows();

			// you can write a function as well to get Column count

			int totalCols = sheet.getColumns();

			tabArray = new String[totalRows-1][totalCols];

			ci = 0;

			for (int i = startRow; i <totalRows; i++, ci++) {

				cj = 0;

				for (int j = startCol; j <totalCols; j++, cj++) {

					tabArray[ci][cj] = getCellData(i, j);

					System.out.println(tabArray[ci][cj]);

				}

			}

		}

		catch (FileNotFoundException e) {

			System.out.println("Could not read the Excel sheet");

			e.printStackTrace();

		}

		catch (IOException e) {

			System.out.println("Could not read the Excel sheet");

			e.printStackTrace();

		}

		return (tabArray);

	}

	// This method is to read the test data from the Excel cell, in this we are
	// passing parameters as Row num and Col num
	public static String getCellData(int RowNum, int ColNum) throws Exception {

		try {

			Cell cell = sheet.getCell(ColNum,RowNum);

			int dataType = cell.getType().hashCode();

			if (dataType == 3) {

				return "";

			} else {

				String CellData = cell.getContents();

				return CellData;
			}
		} catch (Exception e) {

			System.out.println(e.getMessage());

			throw (e);

		}
	}

}