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
	
	public ModelAndView updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/*
		FaqDAO dao = new FaqDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		*/
		try {
			ModelAndView mav = new ModelAndView("faq/write");
			
			mav.addObject("mode", "update");

			return mav;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/faq/list");

	}
}
