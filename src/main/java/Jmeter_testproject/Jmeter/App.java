package Jmeter_testproject.Jmeter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

/**
 * Hello world!
 *
 */
public class App 
{
	public static StandardJMeterEngine jmeter=  new StandardJMeterEngine();
	public static String datatablePath = System.getProperty("user.dir");
	public static String datatableName = "TestData";
    public static void main( String[] args ) throws FileNotFoundException, IOException
    {
			//Set jmeter home for the jmeter utils to load
	        File jmeterHome = new File("G:\\Organization\\Leanings\\Automation\\JMeter\\apache-jmeter-4.0");
	        String slash = System.getProperty("file.separator");

	        if (jmeterHome.exists()) {
	            File jmeterProperties = new File(jmeterHome.getPath() + slash + "bin" + slash + "jmeter.properties");
	            if (jmeterProperties.exists()) {
	            	
	                //JMeter Engine
	                StandardJMeterEngine jmeter = new StandardJMeterEngine();

	                //JMeter initialization (properties, log levels, locale, etc)
	                JMeterUtils.setJMeterHome(jmeterHome.getPath());
	                JMeterUtils.loadJMeterProperties(jmeterProperties.getPath());
	                JMeterUtils.initLogging(); // you can comment this line out to see extra log messages of i.e. DEBUG level
	                JMeterUtils.initLocale();

	                // JMeter Test Plan
	                HashTree testPlanTree = new HashTree();

	                // First HTTP Sampler 
	                HTTPSamplerProxy examplecomSampler = new HTTPSamplerProxy();
	                examplecomSampler.setDomain("google.com");
	                examplecomSampler.setPort(80);
	                examplecomSampler.setPath("/");
	                examplecomSampler.setMethod("GET");
	                examplecomSampler.setName("Open google.com");
	                examplecomSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
	                examplecomSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

	                // Loop Controller
	                LoopController loopController = new LoopController();
	                loopController.setLoops(5);
	                loopController.setFirst(true);
	                loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
	                loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
	                loopController.initialize();

	                // Thread Group
	                ThreadGroup threadGroup = new ThreadGroup();
	                threadGroup.setName("Sample Thread Group");
	                threadGroup.setNumThreads(1);
	                threadGroup.setRampUp(1);
	                threadGroup.setSamplerController(loopController);
	                threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
	                threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());

	                // Test Plan
	                TestPlan testPlan = new TestPlan("Create JMeter Script From Java Code");
	                
	                testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
	                testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
	                testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

	                // Construct Test Plan from previously initialized elements
	                testPlanTree.add(testPlan);
	                HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
	                threadGroupHashTree.add(examplecomSampler);

	                // JMeter's .jmx file format
	                SaveService.saveTree(testPlanTree, new FileOutputStream("G:\\Organization\\Leanings\\Jmeter Reports\\jmeter_api_sample.jmx"));

	                //add Summarizer output
	                
	                Summariser summer = null;
	                String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
	                if (summariserName.length() > 0) {
	                    summer = new Summariser(summariserName);
	                }


	                // Store execution results
	                String reportFile = "G:\\Organization\\Leanings\\Jmeter Reports\\report.jtl";
	                String csvFile = "G:\\Organization\\Leanings\\Jmeter Reports\\report.csv";
	                ResultCollector logger = new ResultCollector(summer);
	                logger.setFilename(reportFile);
	                ResultCollector csvlogger = new ResultCollector(summer);
	                csvlogger.setFilename(csvFile);
	                testPlanTree.add(testPlanTree.getArray()[0], logger);
	                testPlanTree.add(testPlanTree.getArray()[0], csvlogger);
	                // Run Test Plan
	                jmeter.configure(testPlanTree);
	                jmeter.run();
	                
	                System.exit(0);

	            }
	        }

	        System.err.println("jmeterHome property is not set or pointing to incorrect location");
	        System.exit(1);

    }
}
