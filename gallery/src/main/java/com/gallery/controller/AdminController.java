package com.gallery.controller;

import java.io.IOException;
import java.util.List;

import com.gallery.annotation.Controller;
import com.gallery.annotation.RequestMapping;
import com.gallery.dao.MemberDAO;
import com.gallery.domain.MemberDTO;
import com.gallery.servlet.ModelAndView;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AdminController {
	@RequestMapping("/admin")
	public ModelAndView main(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("admin/main");
		return mav;
	}
	
	@RequestMapping("/admin/member")
	public ModelAndView member(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("admin/member");
		
		MemberDAO dao = new MemberDAO();
		List<MemberDTO> list = dao.userList();
		
		mav.addObject("list", list);
		
		return mav;
	}
}
