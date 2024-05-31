package com.gallery.controller;

import java.io.File;
import java.io.IOException;

import com.gallery.annotation.Controller;
import com.gallery.annotation.RequestMapping;
import com.gallery.annotation.RequestMethod;
import com.gallery.dao.Art_BoardDAO;
import com.gallery.domain.Art_BoardDTO;
import com.gallery.domain.SessionInfo;
import com.gallery.servlet.ModelAndView;
import com.gallery.util.FileManager;
import com.gallery.util.MyMultipartFile;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@Controller
public class Art_BoardController {
	@RequestMapping(value = "/art_board/list")
	public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("art_board/list");

		
		return mav;
	}
	
	
	@RequestMapping(value = "/art_board/write", method = RequestMethod.GET)
	public ModelAndView writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("art_board/write");
		mav.addObject("mode", "write");
		return mav;
	}
	
	
	@RequestMapping(value = "/art_board/write", method = RequestMethod.POST)
	public ModelAndView writSubmit(HttpServletRequest req, HttpServletResponse resq) throws ServletException, IOException {
		// 글 저장 
		Art_BoardDAO dao = new Art_BoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		FileManager fileManager = new FileManager();
		
		// 파일 저장 경로
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "art_board";
		
		try {
			Art_BoardDTO dto = new Art_BoardDTO();
			// userId는 세션에 저장된 정보
			dto.setMember_id(info.getUserId());
			
			// 파라미터
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			Part p = req.getPart("selectFile");
			MyMultipartFile multFile = fileManager.doFileUpload(p, pathname);
			if(multFile != null) {
				String saveFilename = multFile.getSaveFilename();
				String uploadfilename = multFile.getSaveFilename();
				long size = multFile.getSize();
				dto.setSaveFilename(saveFilename);
				dto.setUploadfilename(uploadfilename);
				dto.setFileSize(size);
				
				dao.insertAar_Board(dto,"write");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/art_board/list");
	}
	
	
}



	