package com.journaldev.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@WebServlet("/UploadDownloadFileServlet")
public class UploadDownloadFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private ServletFileUpload uploader = null;
	@Override
	public void init() throws ServletException{
		DiskFileItemFactory fileFactory = new DiskFileItemFactory();
		File filesDir = (File) getServletContext().getAttribute("FILES_DIR_FILE");
		fileFactory.setRepository(filesDir);
		this.uploader = new ServletFileUpload(fileFactory);
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fileName = request.getParameter("fileName");
		if(fileName == null || fileName.equals("")){
			throw new ServletException("File Name can't be null or empty");
		}
		File file = new File(request.getServletContext().getAttribute("FILES_DIR")+File.separator+fileName);
		if(!file.exists()){
			throw new ServletException("File doesn't exists on server.");
		}
		System.out.println("File location on server::"+file.getAbsolutePath());
		ServletContext ctx = getServletContext();
		InputStream fis = new FileInputStream(file);
		String mimeType = ctx.getMimeType(file.getAbsolutePath());
		response.setContentType(mimeType != null? mimeType:"application/octet-stream");
		response.setContentLength((int) file.length());
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		
		ServletOutputStream os       = response.getOutputStream();
		byte[] bufferData = new byte[1024];
		int read=0;
		while((read = fis.read(bufferData))!= -1){
			os.write(bufferData, 0, read);
		}
		os.flush();
		os.close();
		fis.close();
		System.out.println("File downloaded at client successfully");
	}

	public enum UploadType
	{
		kittify,
		npc,
		banner
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(!ServletFileUpload.isMultipartContent(request)){
			throw new ServletException("Content type is not multipart/form-data");
		}
		String type = request.getParameter("type");
		try
		{
			UploadType.valueOf(type);
		}
		catch(Exception e)
		{
			throw new RuntimeException("Invalid type specified: "+type);
		}
		
		
//		PrintWriter out = response.getWriter();
//		out.write("<html><head></head><body>");
		try {
			List<FileItem> fileItemsList = uploader.parseRequest(request);
			Iterator<FileItem> fileItemsIterator = fileItemsList.iterator();
			while(fileItemsIterator.hasNext()){
				FileItem fileItem = fileItemsIterator.next();
				System.out.println("FieldName="+fileItem.getFieldName());
				System.out.println("FileName="+fileItem.getName());
				System.out.println("ContentType="+fileItem.getContentType());
				System.out.println("Size in bytes="+fileItem.getSize());
				
				String basePath = System.getProperty("overrideImageFolder");
				if (basePath==null)
					basePath = new File("anything").getAbsolutePath().replace(File.separator+"config"+File.separator+"anything",  "")+File.separator+"docroot";
				
				if (type.equals("banner"))
				{
					File file = new File(basePath+File.separator+"images"+File.separator+type+File.separator+"sourceimage.jpg");
					file.getParentFile().mkdirs();
					fileItem.write(file);
					
					executeDroplet("fullscreen-banner1", file);
				}
				else if (type.equals("kittify"))
				{
					File file = new File(basePath+File.separator+"images"+File.separator+type+File.separator+"sourceimage.png");
					file.getParentFile().mkdirs();
					fileItem.write(file);
					
					executeDroplet("kittify1", file);
					executeDroplet("kittify2", file);
					executeDroplet("kittify3", file);
					executeDroplet("kittify4", file);
					executeDroplet("kittify6", file);
					executeDroplet("kittify7", file);
					executeDroplet("kittify5", file);
				}
				else
					throw new RuntimeException("Unhandled type.");
				
				
				
				
				response.sendRedirect("index.html");
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
//			out.write("Exception in uploading file.");
		} catch (Exception e) {
			e.printStackTrace();
//			out.write("Exception in uploading file.");
		}
//		out.write("</body></html>");
	}
	public void executeDroplet(String dropletFilenamePart, File file)
	{
		String line = "C:\\Initium\\Initium-ImageProcessor\\droplets\\"+dropletFilenamePart+".exe";
		CommandLine cmdLine = new CommandLine(line);
		cmdLine.addArgument(file.getAbsolutePath());
		
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
			e.printStackTrace();
		}
		System.out.println("Exit value: "+exitValue);
		
		try
		{
			synchronized(this)
			{
				this.wait(1000);
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

}
