package single_run.single_run;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class UrlBatchCheck {
	public static Workbook getWb(String path) {
		try {
			return WorkbookFactory.create(new File(path));
		} catch (Exception e) {
			throw new RuntimeException("读取EXCEL文件出错", e);
		}
	}

	public static Sheet getSheet(Workbook wb, int sheetIndex) {
		if (wb == null) {
			throw new RuntimeException("工作簿对象为空");
		}
		int sheetSize = wb.getNumberOfSheets();
		if (sheetIndex < 0 || sheetIndex > sheetSize - 1) {
			throw new RuntimeException("工作表获取错误");
		}
		return wb.getSheetAt(sheetIndex);
	}

	public static List<List<String>> getExcelRows(Sheet sheet, int startLine, int endLine) {
		List<List<String>> list = new ArrayList<List<String>>();
		// 如果开始行号和结束行号都是-1的话，则全表读取
		if (startLine == -1)
			startLine = 0;
		if (endLine == -1) {
			endLine = sheet.getLastRowNum() + 1;
		} else {
			endLine += 1;
		}
		for (int i = startLine; i < endLine; i++) {
			Row row = sheet.getRow(i);
			if (row == null) {
				System.out.println("该行为空，直接跳过");
				continue;
			}
			int rowSize = row.getLastCellNum();
			List<String> rowList = new ArrayList<String>();
			for (int j = 0; j < rowSize; j++) {
				Cell cell = row.getCell(j);
				String temp = "";
				if (cell == null) {
					System.out.println("该列为空，赋值双引号");
					temp = "NULL";
				} else {
					int cellType = cell.getCellType();
					switch (cellType) {
					case Cell.CELL_TYPE_STRING:
						temp = cell.getStringCellValue().trim();
						temp = StringUtils.isEmpty(temp) ? "NULL" : temp;
						break;
					case Cell.CELL_TYPE_BOOLEAN:
						temp = String.valueOf(cell.getBooleanCellValue());
						break;
					case Cell.CELL_TYPE_FORMULA:
						temp = String.valueOf(cell.getCellFormula().trim());
						break;
					case Cell.CELL_TYPE_NUMERIC:
						if (HSSFDateUtil.isCellDateFormatted(cell)) {

						} else {
							temp = new DecimalFormat("#.######").format(cell.getNumericCellValue());
						}
						break;
					case Cell.CELL_TYPE_BLANK:
						temp = "NULL";
						break;
					case Cell.CELL_TYPE_ERROR:
						temp = "ERROR";
						break;
					default:
						temp = cell.toString().trim();
						break;
					}
				}
				rowList.add(temp);
			}
			list.add(rowList);
		}
		return list;
	}

	private static Map<String, Integer> statistics = new HashMap<String, Integer>();
	private static ConcurrentHashMap<String, String> urlCheckInfo = new ConcurrentHashMap<String, String>();
	private static volatile LongAdder la = new LongAdder();
	private static volatile long counter = 0;
	private static List<String> urlList = new ArrayList<String>();
	
	public static String urlIsReachable(String str) {
		URL url = null;
		String res = "";
		try {
			url = new URL(str);
			HttpURLConnection co = (HttpURLConnection) url.openConnection();
			co.setConnectTimeout(1000);
			co.connect();
			int code = co.getResponseCode();
			System.out
					.println("THREAD: " + Thread.currentThread().getName() + " ; URL: " + url + " ; HTTP 返回码: " + code);
			res = code + "";
		} catch (Exception e) {
			System.out.println("THREAD: " + Thread.currentThread().getName() + " ; URL: " + url + " ; 异常: " + e);
			res = e.toString();
		}
		if (statistics.containsKey(res)) {
			statistics.put(res, statistics.get(res) + 1);
		} else {
			statistics.put(res, +1);
		}
		urlCheckInfo.put(str, res);
		la.increment();
		//System.out.println("LA计数器："+la.longValue()+" ; checkedList 大小：" + checkedList.size());
		if (la.longValue() >= counter-5) {
			System.out.println("统计信息");
			System.out.println(statistics);
			System.out.println("每个网站的检查明细信息");
			System.out.println(urlCheckInfo);
		}
		if (la.longValue() >= counter) {
			System.out.println("执行结束，退出");
			System.exit(0);
		}
		return res;
	}

	public static void main(String args[]) {
		String path = "config/url.xlsx";
		Workbook wb = getWb(path);
		int sheetNum = wb.getNumberOfSheets();
		System.out.println("sheet num:" + sheetNum);
		String url = "";
		for (int a = 0; a < sheetNum; a++) {
			List<List<String>> list = getExcelRows(getSheet(wb, a), -1, -1);
			for (int i = 1; i < list.size(); i++) {
				List<String> row = list.get(i);
				url = StringUtils.lowerCase(row.get(1) + "://" + row.get(0));
				// System.out.println(url);
				if (!urlList.contains(url)) {
					urlList.add(url);
				}
			}
		}
		counter = urlList.size();
		//System.out.println("counter:" + counter);
		System.out.println("urlList:" + urlList);
		ThreadPoolExecutor tpe = new ThreadPoolExecutor(32, 64, 1000, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {

					@Override
					public Thread newThread(Runnable r) {
						// TODO Auto-generated method stub
						Thread t = new Thread(r, "MyThread-" + r.hashCode());
						//t.setDaemon(true);
						return t;
					}
				});

		// 开始检测
		for (int i = 0; i < urlList.size(); i++) {
			String temp = urlList.get(i);
			tpe.submit(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					urlIsReachable(temp);
				}
			});
		}

	}
}
