package com.gallery.controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import com.gallery.annotation.Controller;
import com.gallery.annotation.RequestMapping;
import com.gallery.annotation.RequestMethod;
import com.gallery.dao.QnaDAO;
import com.gallery.domain.QnADTO;
import com.gallery.domain.SessionInfo;
import com.gallery.servlet.ModelAndView;
import com.gallery.util.MyUtil;
import com.gallery.util.MyUtilBootstrap;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class QnaController {

	@RequestMapping(value = "/qna/list")
	public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("qna/list");
		
		QnaDAO dao = new QnaDAO();
		MyUtil util = new MyUtilBootstrap();
		
		try {
			List<QnADTO> list = null;
			
			String page = req.getParameter("page");
			String kwd = req.getParameter("kwd");
			String query = "";
			
			int size = 10;
			int dataCount = 0;
			int offset = 0;
			int current_page = 1;
			int total_page = 0;

			
			if(kwd != null && req.getMethod().equals("GET")) {
				kwd = URLDecoder.decode(kwd, "utf-8");
			}

			if(kwd == null) {
				kwd = "";
				dataCount = dao.dataCount();
				
				offset = (current_page -1) * size;
				if(offset < 0) offset = 0;
				
				list = dao.listQuestion(offset, size);
			} else {
				dataCount = dao.dataCount(kwd);
				
				offset = (current_page -1) * size;
				if(offset < 0) offset = 0;
				
				list = dao.listQuestion(offset, size, kwd);
			}
			
			if(page != null) {
				current_page = Integer.parseInt(page);
			}
			
			total_page = util.pageCount(dataCount, size);
			if (current_page > total_page) {
				current_page = total_page;
			}
			
			if (kwd.length() != 0) {
				query += "kwd=" + URLEncoder.encode(kwd, "utf-8");
			} 

			String cp = req.getContextPath();
			String listUrl = cp + "/qna/list";
			String articleUrl = cp + "/qna/article?page=" + current_page;
			if (query.length() != 0) {
				listUrl += "?" + query;
				articleUrl += "&" + query;
			}
			
			String paging = util.paging(current_page, total_page, listUrl);
			
			mav.addObject("list", list);
			mav.addObject("page", current_page);
			mav.addObject("total_page", total_page);
			mav.addObject("dataCount", dataCount);
			mav.addObject("size", size);
			mav.addObject("articleUrl", articleUrl);
			mav.addObject("paging", paging);
			mav.addObject("kwd", kwd);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mav;
	
	}
	@RequestMapping(value = "/qna/write", method = RequestMethod.GET)
	public ModelAndView writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("qna/write");
		
		mav.addObject("mode", "write");
		
		return mav;
	}
	
	@RequestMapping(value = "/qna/write", method = RequestMethod.POST)
	public ModelAndView writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		QnaDAO dao = new QnaDAO();
		
		try {
			QnADTO dto = new QnADTO();
			
			dto.setMember_id(info.getUserId());
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));

			dao.insertQuestion(dto);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/qna/list");
	}
	
	@RequestMapping(value = "/qna/article", method = RequestMethod.GET)
	public ModelAndView article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		QnaDAO dao = new QnaDAO();
		
		String page = req.getParameter("page");
		String query = "page="+page;
		try {
			long num = Long.parseLong(req.getParameter("num"));
			String kwd = req.getParameter("kwd");
			
			if (kwd == null) {
				kwd = "";
			}
			
			kwd = URLDecoder.decode(kwd, "utf-8");

			if (kwd.length() != 0) {
				query += "&kwd=" + URLEncoder.encode(kwd, "UTF-8");
			}
			
			QnADTO dto = dao.findById(num);
			
			if (dto == null) {
				return new ModelAndView("redirect:/qna/list?" + query);
			}
			
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			if(dto.getAnswer() != null) {
				dto.setAnswer(dto.getAnswer().replaceAll("\n", "<br>"));
			}
			QnADTO prevDto = dao.findByPrev(num, kwd);
			QnADTO nextDto = dao.findByNext(num, kwd);
			
			ModelAndView mav = new ModelAndView("qna/article");
			
			mav.addObject("dto", dto);
			mav.addObject("page", page);
			mav.addObject("query", query);
			mav.addObject("prevDto", prevDto);
			mav.addObject("nextDto", nextDto);

			return mav;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/qna/list?"+query);
	}
	
	@RequestMapping(value = "/qna/answer", method = RequestMethod.POST)
	public ModelAndView answerSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		QnaDAO dao = new QnaDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		if (info.getUserRole() != 0) {
			return new ModelAndView("redirect:/qna/list");
		}
		
		String page = req.getParameter("page");
		try {
			QnADTO dto = new QnADTO();
			
			dto.setNum(Long.parseLong(req.getParameter("num")));
			dto.setAnswer(req.getParameter("answer"));
			dto.setAnswer_id(info.getUserId());

			dao.updateAnswer(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ModelAndView("redirect:/qna/list?page=" + page);
	}
	
	@RequestMapping(value = "/qna/update", method = RequestMethod.GET)
	public ModelAndView updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		QnaDAO dao = new QnaDAO();
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String page = req.getParameter("page");

		try {
			long num = Long.parseLong(req.getParameter("num"));
			QnADTO dto = dao.findById(num);

			if (dto == null) {
				return new ModelAndView("redirect:/qna/list?page=" + page);
			}

			if (! dto.getMember_id().equals(info.getUserId())) {
				return new ModelAndView("redirect:/qna/list?page=" + page);
			}

			ModelAndView mav = new ModelAndView("qna/write");
			
			mav.addObject("dto", dto);
			mav.addObject("page", page);
			mav.addObject("mode", "update");

			return mav;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ModelAndView("redirect:/qna/list?page=" + page);
	}
	
	@RequestMapping(value = "/qna/update", method = RequestMethod.POST)
	public ModelAndView updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		QnaDAO dao = new QnaDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String page = req.getParameter("page");
		try {
			QnADTO dto = new QnADTO();
			
			dto.setNum(Long.parseLong(req.getParameter("num")));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));

			dto.setMember_id(info.getUserId());

			dao.updateQuestion(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ModelAndView("redirect:/qna/list?page=" + page);
	}
	
	@RequestMapping(value = "/qna/delete", method = RequestMethod.GET)
	public ModelAndView delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		QnaDAO dao = new QnaDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String page = req.getParameter("page");
		String query = "page=" + page;

		try {
			long num = Long.parseLong(req.getParameter("num"));
			String mode = req.getParameter("mode");
			
			String kwd = req.getParameter("kwd");
			if (kwd == null) {
				kwd = "";
			}
			kwd = URLDecoder.decode(kwd, "utf-8");

			if (kwd.length() != 0) {
				query += "&kwd=" + URLEncoder.encode(kwd, "UTF-8");
			}
			
			QnADTO dto = dao.findById(num);
			
			if(mode.equals("answer") && info.getUserId().equals(dto.getAnswer_id())) {
				dto.setNum(num);
				dto.setAnswer("");
				dto.setAnswer_id("");

				dao.updateAnswer(dto);
			} else if(mode.equals("question") && (info.getUserId().equals(dto.getMember_id()) || info.getUserRole() == 0)) {
				dao.deleteQuestion(num);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ModelAndView("redirect:/qna/list?" + query);
	}
}
