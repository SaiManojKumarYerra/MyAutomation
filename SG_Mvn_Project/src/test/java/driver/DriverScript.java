package driver;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import methods.AppDependentMethods;
import methods.AppIndependentMethods;
import methods.Datatable;
import methods.TaskModuleMethods;
import methods.UserModuleMethods;
import reports.ReportUtils;
import testScripts.TaskModuleScripts;
import testScripts.UserModuleScripts;

public class DriverScript {
	public static AppIndependentMethods appInd = null;
	public static AppDependentMethods appDep = null;
	public static Datatable datatable = null;
	public static TaskModuleMethods taskMethods = null;
	public static UserModuleMethods userMethods = null;
	public static ReportUtils reports = null;
	public static TaskModuleScripts taskScripts = null;
	public static UserModuleScripts userScripts = null;
	public static ExtentReports extent = null;
	public static ExtentTest test = null;
	public static String screenshotLocation = null;
	public static String strModuleName = null;
	public static String controller = null;
	
	
	
	
	@BeforeSuite
	public void loadClasses() {
		
		try {
			appInd = new AppIndependentMethods();
			appDep = new AppDependentMethods();
			datatable = new Datatable();
			taskMethods = new TaskModuleMethods();
			userMethods = new UserModuleMethods();
			reports = new ReportUtils();
			taskScripts = new TaskModuleScripts();
			userScripts = new UserModuleScripts();
			extent = reports.startExtentReport("TestReports", appInd.readPropFile("config.properties", "BuildName"));
			controller = System.getProperty("user.dir")+"\\Controller\\ExecutionController.xlsx";
			
		}catch(Exception e)
		{
			System.out.println("Exception in loadClasses() : "+e);
		}
			
	}
	
	@Test
	public void Execution(){
		int mRows = 0;
		int tcRows = 0;
		String executeModule = null;
		String executeTest = null;
		Class<?> cls = null;
		Object obj = null;
		Method objTest = null;
		int ModulesCount = 0;
		int testScriptCount = 0;
		try {
		
			mRows = datatable.getRowCount(controller, "Modules", test);
			for(int m=1; m<=mRows; m++)
			{
				
				executeModule = datatable.getCellData(controller, "Modules", "ExecuteModule", m);
				
				if(executeModule.equalsIgnoreCase("Yes")) {
					ModulesCount++;
					strModuleName = datatable.getCellData(controller, "Modules", "ModuleName", m);
					tcRows = datatable.getRowCount(controller, strModuleName, test);
					for(int t=1; t<=tcRows; t++)
					{
						executeTest=datatable.getCellData(controller, strModuleName, "ExecuteTest", t);
						if(executeTest.equalsIgnoreCase("Yes")) {
							testScriptCount++;
							String scriptname = datatable.getCellData(controller, strModuleName, "ScriptName", t);
							String classname = datatable.getCellData(controller, strModuleName, "ClassName", t);
							
							cls = Class.forName(classname);
							obj = cls.newInstance();
							objTest = obj.getClass().getMethod(scriptname);
							String status = String.valueOf(objTest.invoke(obj));
							
							if(status.equalsIgnoreCase("True")) {
								datatable.setCellData(controller, strModuleName, "Result", t, "PASS");
							}else {
								datatable.setCellData(controller, strModuleName, "Result", t, "FAIL");
							}
						}
					
					}
				}
			}
			
			if(ModulesCount==0) {
				System.out.println("No module is selected for execution. "
						+ "Please select atleast one module for execution under ExecutionController.xlsx");
			}else if(testScriptCount==0) {
				System.out.println("No testScript is selected for execution. "
						+ "Please select atleast one testScript for execution under ExecutionController.xlsx");
			}
			

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			executeModule = null;
			executeTest = null;
			cls = null;
			obj = null;
			objTest = null;
		}
	}
	
}
