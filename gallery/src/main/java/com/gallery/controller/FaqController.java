package com.gallery.controller;

import java.io.IOException;
import java.util.List;

import com.gallery.annotation.Controller;
import com.gallery.annotation.RequestMapping;
import com.gallery.annotation.RequestMethod;
import com.gallery.dao.FaqDAO;
import com.gallery.domain.FAQDTO;
import com.gallery.domain.SessionInfo;
import com.gallery.servlet.ModelAndView;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class FaqController {
	
	@RequestMapping(value = "/faq/list")
	public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("faq/list");
		FaqDAO dao = new FaqDAO();
		
		try {
			List<FAQDTO> list = null;
			list = dao.listFaq();
			
			mav.addObject("list", list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mav;
	}

	@RequestMapping(value = "/faq/write", method = RequestMethod.GET)
	public ModelAndView writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		ModelAndView mav = new ModelAndView("faq/write");
		mav.addObject("mode", "write");
		return mav;
	}
	
	@RequestMapping(value = "/faq/write", method = RequestMethod.POST)
	public ModelAndView writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		FaqDAO dao = new FaqDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		try {
			FAQDTO dto = new FAQDTO();
			
			dto.setMember_id(info.getUserId());
			
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			dao.insertFaq(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/faq/list");
	}
	
	@RequestMapping(value = "/faq/update", method = RequestMethod.GET)
	public ModelAndView updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		FaqDAO dao = new FaqDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			FAQDTO dto = dao.findById(num);
			
			if(dto==null) {
				return new ModelAndView("redirect:/faq/list");
			}
			
			if(! dto.getMember_id().equals(info.getUserId())) {
				return new ModelAndView("redirect:/faq/list");
			}
			
			
			ModelAndView mav = new ModelAndView("faq/write");
			
			mav.addObject("dto", dto);
			mav.addObject("mode", "update");

			return mav;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/faq/list");

	}

	@RequestMapping(value = "/faq/update", method = RequestMethod.POST)
	public ModelAndView updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		FaqDAO dao = new FaqDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		try {
			
			FAQDTO dto = new FAQDTO();
			
			dto.setNum(Long.parseLong(req.getParameter("num")));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			dto.setMember_id(info.getUserId());
			
			dao.updateFaq(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/faq/list");
	}
	
	@RequestMapping(value = "/faq/delete", method = RequestMethod.GET )
	public ModelAndView delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		FaqDAO dao = new FaqDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			
			dao.deleteFaq(num, info.getUserId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/faq/list");
	}
}
