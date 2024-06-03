package com.gallery.image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Calendar;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/image/html5Upload")
public class ImageUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");

		HttpSession session = req.getSession();

		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "image";
		File f = new File(pathname);
		if (! f.exists()) {
			f.mkdirs();
		}
		
		// HTML 5
		String cp = req.getContextPath();
		
		String strUrl = "";
		try {
			
			if(!"OPTIONS".equals(req.getMethod().toUpperCase())) { // OPTIONS 가 아닌 것.
				String filename = req.getHeader("file-name");
		    	
		    	InputStream is = req.getInputStream(); // 파일 읽기. (입력스트램 쓴 것)
		    	String saveFilename = doFileUpload(is, filename, pathname);
		    	
				strUrl += "&bNewLine=true&sFileName="; // PHP 봐야함.
                strUrl += saveFilename;
                // strUrl += "&sWidth=600";
                strUrl += "&sFileURL=" + cp + "/uploads/image/" + saveFilename; 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		PrintWriter out = resp.getWriter();
		out.print(strUrl);
	}
	
	public String doFileUpload(InputStream is, String originalFilename, String pathname) throws Exception {
		String saveFilename = null;

		// 클라이언트가 업로드한 파일의 이름
		if(originalFilename == null || originalFilename.length() == 0)
			return null;
		
		// 확장자
		String fileExt = originalFilename.substring(originalFilename.lastIndexOf("."));
		if(fileExt == null || fileExt.length() == 0) {
			return null;
		}
		
		// 서버에 저장할 새로운 파일명을 만든다.
		saveFilename = String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS", 
				         Calendar.getInstance());
		saveFilename += System.nanoTime();
		saveFilename += fileExt;
		
		// 업로드할 경로가 존재하지 않는 경우 폴더를 생성 한다.
		File dir = new File(pathname);
		if(!dir.exists())
			dir.mkdirs();
		
		String fullpathname = pathname + File.separator + saveFilename;
		
		byte[] b = new byte[4096]; // 바이트 버퍼 읽음.(입력스트림에서 바이트 버퍼를 읽음.)
		int size = 0;
		FileOutputStream fos = new FileOutputStream(fullpathname);
		
		while((size = is.read(b))!=-1) {
			fos.write(b, 0, size);
		}
		
		fos.close();
		is.close();
		
		return saveFilename;
	}
}
