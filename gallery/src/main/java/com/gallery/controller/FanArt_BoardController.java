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
			
			int offset = (current_page - 1) * size;
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
			if(req.getParameter("notice") != null && info.getUserRole() == 0) {
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
		
		return new ModelAndView("redirect:/fanArt_board/list");
	}
	
	@RequestMapping(value = "/fanArt_board/article")
	public ModelAndView article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("fanArt_board/article");
		
		FanArtDAO dao = new FanArtDAO();
		
		try {
			String page = req.getParameter("page");
			
			long num = Long.parseLong(req.getParameter("num"));
			
			FanArtDTO dto = dao.findById(num);
			if(dto == null) {
				return new ModelAndView("redirect:/fanArt_board/list?page="+page);
			}
			
			mav.addObject("dto", dto);
			mav.addObject("page", page);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mav;
	}
	
	@RequestMapping(value = "/fanArt_board/update", method = RequestMethod.GET)
	public ModelAndView updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("fanArt_board/write");
		
		FanArtDAO dao = new FanArtDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String page = req.getParameter("page");
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			
			FanArtDTO dto = dao.findById(num);
			if(dto == null || ! dto.getMember_id().equals(info.getUserId())) {
				return new ModelAndView("redirect:/fanAat_borad/list?page="+page);
			}
			
			mav.addObject("dto", dto);
			mav.addObject("page", page);
			mav.addObject("mode", "update");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mav;
	}
	
	@RequestMapping(value = "/fanArt_board/update", method = RequestMethod.POST)
	public ModelAndView updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		FanArtDAO dao = new FanArtDAO();
		
		String root = req.getServletContext().getRealPath("/");
		String path= root + "uploads" + File.separator +"fanArt";
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		FileManager fileManager = new FileManager();
		
		String page = req.getParameter("page");
		
		
		try {
			FanArtDTO dto = new FanArtDTO();
			
			dto.setNum(Long.parseLong(req.getParameter("num")));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			int notice = 0;
			if(req.getParameter("notice") != null && info.getUserRole() == 0) {
				notice = Integer.parseInt(req.getParameter("notice"));
			}
			dto.setNotice(notice);
			String img = req.getParameter("img");
			dto.setImg(img);
			
			Part p = req.getPart("selectFile");
			MyMultipartFile multipart = fileManager.doFileUpload(p, path);
			
			if(multipart != null) {
				fileManager.doFiledelete(path, img);
				
				String filename = multipart.getSaveFilename();
				dto.setImg(filename);
			}
			
			dao.updateFanArt(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/fanArt_board/list?page="+page);
	}
	
	@RequestMapping(value = "/fanArt_board/delete", method = RequestMethod.GET)
	public ModelAndView delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		FanArtDAO dao = new FanArtDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		FileManager fileManager = new FileManager();
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads"+File.separator+"photo";
		String page = req.getParameter("page");
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			
			FanArtDTO dto = dao.findById(num);
			if(dto == null) {
				return new ModelAndView("redirect:/photo/list?page="+page);
			}
			
			if(!dto.getMember_id().equals(info.getUserId()) && !info.getUserId().equals("admin")) {
				return new ModelAndView("redirect:/photo/list?page="+page);
			}
			
			fileManager.doFiledelete(pathname, dto.getImg());
			dao.deleteFanArt(num);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return new ModelAndView("redirect:/fanArt_board/list?page="+page);
	}
}
