package AutoFuzzMain;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import FuzzingFunctions.FuzzingFunctionInterface;

public class test {
	public static void main(String[] args)
	{
		File file = new File("C:\\Program Files (x86)\\dpfuzzer\\FuzzingFunctions" ) ;
		try { 
			// Convert File to a URL 
			URL url = file.toURL(); // file:/c:/myclasses/ 
			URL[] urls = new URL[]{url}; // Create a new class loader with the directory 
			ClassLoader cl = new URLClassLoader(urls);
			// Load in the class; MyClass.class should be located in // the directory file:/c:/myclasses/com/mycompany 
			Class cls = cl.loadClass("FuzzingFunctionIncreaseMaxNumByOne"); 
			try {
				FuzzingFunctionInterface f = (FuzzingFunctionInterface ) cls.newInstance() ;
				System.out.println( f.toString() ) ;
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} 
		catch (MalformedURLException e) { } catch (ClassNotFoundException e) { } 
		}
}
