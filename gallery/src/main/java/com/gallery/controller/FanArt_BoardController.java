package com.gallery.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.gallery.annotation.Controller;
import com.gallery.annotation.RequestMapping;
import com.gallery.annotation.RequestMethod;
import com.gallery.dao.FanArtDAO;
import com.gallery.domain.FanArtDTO;
import com.gallery.domain.SessionInfo;
import com.gallery.servlet.ModelAndView;
import com.gallery.util.FileManager;
import com.gallery.util.MyMultipartFile;
import com.gallery.util.MyUtil;
import com.gallery.util.MyUtilBootstrap;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@Controller
public class FanArt_BoardController {
	
	@RequestMapping("/fanArt_board/list")
	public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("fanArt_board/list");
		// [페이지, -작가- 검색 타입, 검색 값,]
		
		String page = req.getParameter("page");
		
		FanArtDAO dao = new FanArtDAO();
		MyUtil util = new MyUtilBootstrap();
		
		try {
			int current_page = 1;
			if(page != null) {
				current_page = Integer.parseInt(page); 
			}
			
			int size = 10;
			
			int dataCount = dao.dataCount();
			int total_page = util.pageCount(dataCount, size);
			if(current_page > total_page) {
				current_page = total_page;
			}
			
			int offset = (dataCount -1) * size;
			if(offset < 0) offset = 0;
			
			List<FanArtDTO> list = dao.list(offset, size);
			
			String cp = req.getContextPath();
			String listUrl = cp + "/fanArt_board/list";
			String articleUrl = cp + "/fanArt_board/article?page="+current_page;
			String paging = util.paging(current_page, total_page, listUrl);
			
			mav.addObject("list", list);
			mav.addObject("dataCount", dataCount);
			mav.addObject("articleUrl", articleUrl);
			mav.addObject("page", current_page);
			mav.addObject("total_page", total_page);
			mav.addObject("paging", paging);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		return mav;
	}
	
	@RequestMapping(value = "/fanArt_board/write", method = RequestMethod.GET)
	public ModelAndView writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView model = new ModelAndView("fanArt_board/write");
		
		model.addObject("mode", "write");
		
		return model;
	}
	
	@RequestMapping(value = "/fanArt_board/write", method = RequestMethod.POST)
	public ModelAndView writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView model = new ModelAndView("fanArt_board/write");
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		FileManager fileManager = new FileManager();
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads"+File.separator+"fanArt";
		
		FanArtDAO dao = new FanArtDAO();
		
		String subject = req.getParameter("subject");
		String content = req.getParameter("content");
		
		try {
			FanArtDTO dto = new FanArtDTO();
			
			int notice = 0;
			if(req.getParameter("notice") != null) {
				notice = Integer.parseInt(req.getParameter("notice"));
			}
			
			dto.setMember_id(info.getUserId());
			dto.setSubject(subject);
			dto.setContent(content);
			dto.setNotice(notice);
			
			String filename = null;
			Part p = req.getPart("selectFile");
			MyMultipartFile multipart = fileManager.doFileUpload(p, pathname);
			
			if(multipart != null) {
				filename = multipart.getSaveFilename();
			}
			
			if(filename != null) {
				dto.setImg(filename);
				
				dao.insertFanArt(dto);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return model;
	}
}
