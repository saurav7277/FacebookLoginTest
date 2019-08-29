package com.facebooklogin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;


public class FacebookLoginTest
{
	WebDriver driver;
	WebDriverWait wait;
	Workbook workbook ;
	CellStyle style;
	//method for executing setup process before executing test
	@BeforeTest
	public void setup() 
	{
		System.setProperty("webdriver.chrome.driver","D:\\QA-Testing\\chromedriver_win32\\chromedriver.exe");
		Map<String, Object> prefs = new HashMap<String, Object>();
		prefs.put("profile.default_content_setting_values.notifications", 2);
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", prefs);
		driver=new ChromeDriver(options);
		wait= new WebDriverWait(driver, 100);
		driver.get("https://www.facebook.com/");
	}
	
	//method for testing facebook login feature
	 @Test
	 public void facebookLoginTest() throws IOException, InterruptedException 
	 {
		  String filePath="D:\\";
		  String fileName="login_credentials.xlsx";
		  String sheetName="Sheet1";
		  String[][] data=readExcelData(filePath,fileName,sheetName);
		  String[] actual_result=new String[5];
		  String[] expected_result=new String[5];
		  String[] test_result=new String[5];
		  for(int i=0;i<data.length;i++)
		  {
			  String id="";
			  String paswd="";
			  for(int j=0;j<data[1].length;j++)
			  {
				  if(j==0)
				  {
					  id=data[i][j];
				  }
				  else if(j==data[1].length-2)
				  {
					  paswd=data[i][j];
				  }
				  else if(j==data[1].length-1)
				  {
					  expected_result[i]=data[i][j];
				  }
			  }
			  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"email\"]")));
			  WebElement userId=driver.findElement(By.xpath("//*[@id=\"email\"]"));
			  userId.sendKeys(id);
			  WebElement pswd=driver.findElement(By.xpath("//*[@id=\"pass\"]"));
			  pswd.sendKeys(paswd);
			  WebElement submit=driver.findElement(By.xpath("//*[@value=\"Log In\"]"));
			  submit.click();
			  Thread.sleep(100);
			  String actual_title=driver.getTitle().substring(driver.getTitle().indexOf("F"));
			  String expected_title="Facebook";
			  if(actual_title.equalsIgnoreCase(expected_title))
			  {
				  System.out.println(id+"  "+paswd+"  "+"LOGIN PASS");
				  actual_result[i]="LOGIN PASS";
				  //wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\\\"userNavigationLabel\\")));
				  WebElement logout_nav=driver.findElement(By.xpath("//*[@id=\"userNavigationLabel\"]"));
				  logout_nav.click();
				  Thread.sleep(3000);
				  WebElement logout_button=driver.findElement(By.xpath("//span[@class='_54nh'][contains(.,'Log Out')]"));
				  logout_button.click();
			  }
			  else
			  {
				  System.out.println(id+"  "+paswd+"   "+"LOGIN FAIL");
				  actual_result[i]="LOGIN FAIL";
				  driver.get("https://www.facebook.com/");
			  }
		  }
		  
		  for(int i=0;i<actual_result.length;i++)
		  {
			  if(actual_result[i].equalsIgnoreCase(expected_result[i]))
			  {
				  test_result[i]="PASS";
			  }
			  else
			  {
				  test_result[i]="FAIL";
			  }
		  }
		  
		  writeExcelData(filePath,fileName,sheetName,actual_result,test_result);
		  
	 }
	 
	 //method for terminating test and quitting driver
	 @AfterTest
	 public void tearDown()
	 {
		 driver.quit();
	 }
	 
	 //method for reading user's credentials from excel sheet
	 public String[][] readExcelData(String filePath,String fileName,String sheetName)throws  IOException
	 {
		 
		 File file =    new File(filePath+fileName);
		 FileInputStream inputStream = new FileInputStream(file);
		 workbook = new XSSFWorkbook(inputStream);
		 Sheet sheet = workbook.getSheet(sheetName);
		 int rowCount = sheet.getLastRowNum()-sheet.getFirstRowNum();
		 String[][] data=new String[rowCount][3];
		 for(int i=0;i<data.length;i++)
		 {
			 Row row = sheet.getRow(i+1);
			 for(int j=0;j<3;j++)
			 {
				 if(row.getCell(j).getCellType()==CellType.NUMERIC)
				 {
					
					 data[i][j]=NumberToTextConverter.toText(row.getCell(j).getNumericCellValue());

				 }
				 else if(row.getCell(j).getCellType()==CellType.STRING)
				 {
				 data[i][j]=String.valueOf(row.getCell(j).getStringCellValue());
				 }
				 
			 }
				 
		 }
		 return data;
	 }
	 
	 //method for writing result of test cases into excel sheet
	 public void writeExcelData(String filePath,String fileName,String sheetName,String[] actualResultToWrite,String[] testResultToWrite) throws IOException
	 {
	    File file =    new File(filePath+"\\"+fileName);
        FileInputStream inputStream = new FileInputStream(file);
		Workbook workbook = new XSSFWorkbook(inputStream);
		Sheet sheet = workbook.getSheet(sheetName);
		int rowCount = sheet.getLastRowNum()-sheet.getFirstRowNum();
		for(int i=0;i<actualResultToWrite.length;i++)
		 {
			 Row row = sheet.getRow(i+1);
		     Cell cell = row.createCell(3);
		     Cell cell1 = row.createCell(4);
		     cell.setCellValue(actualResultToWrite[i]);
		     cell1.setCellValue(testResultToWrite[i]);
		     if(testResultToWrite[i]=="PASS")
		     {
		    	 style = workbook.createCellStyle();
		    	 style.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
		    	 style.setFillPattern(FillPatternType.BIG_SPOTS);
		    	 row.setRowStyle(style);
		     }
		     else if(testResultToWrite[i]=="FAIL")
		     {
		    	 style = workbook.createCellStyle();
		    	 style.setFillBackgroundColor(IndexedColors.RED.getIndex());
		    	 style.setFillPattern(FillPatternType.BIG_SPOTS);
		    	 row.setRowStyle(style);
		     }
		     
		 }
		inputStream.close();
		FileOutputStream outputStream = new FileOutputStream(file);
		workbook.write(outputStream);
	    outputStream.close();
	 }
}
