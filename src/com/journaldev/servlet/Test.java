package com.journaldev.servlet;

import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;

public class Test
{

	public Test()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String line = "notepad.exe";
		CommandLine cmdLine = new CommandLine(line);
		cmdLine.addArgument("C:\\Users\\Owner\\Google Drive\\Initium\\Asset Collaboration\\New Icon Art\\Ava\\weird-green-thing1.png");
		
		DefaultExecutor executor = new DefaultExecutor();
		System.out.println("Executing "+cmdLine.toString());
		executor.setExitValue(1);			
		int exitValue=-100;
		try
		{
			exitValue = executor.execute(cmdLine);
		}
		catch (ExecuteException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Exit value: "+exitValue);
	}

}
