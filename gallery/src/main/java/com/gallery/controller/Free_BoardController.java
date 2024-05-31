package com.gallery.controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import com.gallery.annotation.Controller;
import com.gallery.annotation.RequestMapping;
import com.gallery.annotation.RequestMethod;
import com.gallery.dao.Free_BoardDAO;
import com.gallery.domain.Free_BoardDTO;
import com.gallery.domain.SessionInfo;
import com.gallery.servlet.ModelAndView;
import com.gallery.util.MyUtil;
import com.gallery.util.MyUtilBootstrap;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class Free_BoardController {
	// 리스트
	@RequestMapping(value = "/free_board/list")
	public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		ModelAndView mav = new ModelAndView("free_board/list");
		
		Free_BoardDAO dao = new Free_BoardDAO();
		MyUtil util = new MyUtilBootstrap();
		
		try {
			String page = req.getParameter("page");
			int current_page = 1;
			
			if(page != null) {
				current_page = Integer.parseInt(page);
			}
			
			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");
			if(schType == null) {
				schType = "all";
				kwd = "";
			}
			if(req.getMethod().equals("GET")) { // get 방식일 때 디코딩 
				kwd = URLDecoder.decode(kwd,"utf-8");
			}
			
			String pageSize = req.getParameter("size");
			int size = pageSize == null ? 10 : Integer.parseInt(pageSize);
			
			int dataCount, total_page;
			
			if(kwd.length() != 0) {
				dataCount = dao.dataCount(schType, kwd);
			} else {
				dataCount = dao.dataCount();
			}
			total_page = util.pageCount(dataCount, size);
			
			int offset = (current_page - 1) * size;
			if(offset < 0) offset = 0;
			
			List<Free_BoardDTO> list;
			if(kwd.length() == 0) {
				list = dao.listFree_Board(offset, size);
			} else {
				list = dao.listFree_Board(offset, size, schType, kwd);
			}
			/*
			List<Free_BoardDTO> listNotice = null;
			if(current_page ==1) {
				listNotice = dao.list
			}
			*/
		} catch (Exception e) {
			
		}
		
		return mav;
		
		  
	}

	
	// 글 쓰기
	@RequestMapping(value = "/free_board/write", method = RequestMethod.GET)
	public ModelAndView writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		ModelAndView mav = new ModelAndView("free_board/write");
		mav.addObject("mode", "write");
		
		return mav;
		
	}
	
	// 글 저장
	@RequestMapping(value = "/free_board/write", method = RequestMethod.POST)
	public ModelAndView writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Free_BoardDAO dao  = new Free_BoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		try {
			Free_BoardDTO dto = new Free_BoardDTO();
			
			dto.setMember_id(info.getUserId());
			
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			dao.insertFree_Board(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return new ModelAndView("redirect:/free_board/list");
	}
	
	// 글 보기
	
	
}
