package com.gallery.controller;

import java.io.IOException;

import com.gallery.annotation.Controller;
import com.gallery.annotation.RequestMapping;
import com.gallery.annotation.RequestMethod;
import com.gallery.dao.MemberDAO;
import com.gallery.domain.MemberDTO;
import com.gallery.domain.SessionInfo;
import com.gallery.servlet.ModelAndView;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class MemberController {
	@RequestMapping(value = "/member/login", method = RequestMethod.GET)
	public ModelAndView loginForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		ModelAndView model = new ModelAndView("member/login");
		String check = null;
		String userid = "";
		Cookie[] cookie = req.getCookies();
		if (cookie != null) {
			for (int i = 0; i < cookie.length; i++) {
				if (cookie[i].getName().equals("remember")) {
					check = "checked";
					userid = cookie[i].getValue();
					break;
				}
			}
		}
		
		model.addObject("userid", userid);
		model.addObject("check", check);
		
		return model;
	}

	@RequestMapping(value = "/member/login", method = RequestMethod.POST)
	public ModelAndView loginSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();

		MemberDAO dao = new MemberDAO();

		String userId = req.getParameter("userId");
		String userPwd = req.getParameter("userPwd");
		String rememberMe = req.getParameter("rememberMe");

		MemberDTO dto = dao.loginMember(userId, userPwd);
		if (dto != null) {
			// 세션의 유지시간을 20분설정(기본 30분)
			session.setMaxInactiveInterval(20 * 60);

			// 세션에 저장할 내용
			SessionInfo info = new SessionInfo();
			info.setUserId(dto.getUserId());
			info.setUserName(dto.getName());
			info.setUserRole(dto.getRole());
			if(rememberMe != null) {
				Cookie rcookie = new Cookie("remember", userId);
				rcookie.setMaxAge(60 * 60);
				rcookie.setPath("/");
				resp.addCookie(rcookie);
			} else {
				Cookie noCookie = new Cookie("remember", "");
				noCookie.setMaxAge(0);
				noCookie.setPath("/");
				resp.addCookie(noCookie);
			}

			session.setAttribute("member", info);
			
			String preLoginURI = (String)session.getAttribute("preLoginURI");
			if(preLoginURI != null) {
				// 로그인 전페이지로 리다이렉트
				return new ModelAndView(preLoginURI);
			} 

			return new ModelAndView("redirect:/");
		}

		ModelAndView mav = new ModelAndView("member/login");
		
		String msg = "아이디 또는 패스워드가 일치하지 않습니다.";
		mav.addObject("message", msg);

		return mav;
	}
	
	@RequestMapping(value = "/member/logout", method = RequestMethod.GET)
	public ModelAndView logout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();

		session.removeAttribute("member");

		session.invalidate();
		
		return new ModelAndView("redirect:/");
	}
	
	@RequestMapping(value = "/member/member", method = RequestMethod.GET)
	public ModelAndView memberForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView model = new ModelAndView("/member/member");
		
		model.addObject("mode", "member");
		
		return model;
	}
	
	// 회원가입시 관리자는 role을 선택하는 박스를 만들어주고 role이 넘어오지 않으면 2로 설정
	@RequestMapping(value = "/member/member", method = RequestMethod.POST)
	public ModelAndView memberSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		MemberDAO dao = new MemberDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		try {
			MemberDTO dto = new MemberDTO();
			
			dto.setUserId(req.getParameter("userId"));
			dto.setUserPwd(req.getParameter("userPwd"));
			dto.setName(req.getParameter("userName"));
			
			String tel1 = req.getParameter("tel1");
			String tel2 = req.getParameter("tel2");
			String tel3 = req.getParameter("tel3");
			dto.setTel(tel1 + "-" + tel2 + "-" + tel3);
			
			String email1 = req.getParameter("email1");
			String email2 = req.getParameter("email2");
			dto.setEmail(email1 + "@" + email2);
			
			dto.setBirth(req.getParameter("birth"));
			
			int role = 2;
			String userRole = req.getParameter("role");
			if(userRole != null && info.getUserRole() == 0) {
				role = Integer.parseInt(userRole);
			}
			dto.setRole(role);
			
			dao.insertMember(dto);
			
			return new ModelAndView("redirect:/");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ModelAndView model = new ModelAndView("/member/member");
		model.addObject("mode", "member");
		
		return model;
	}
	
	@RequestMapping(value = "/member/update", method = RequestMethod.GET)
	public ModelAndView upadateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView model = new ModelAndView("member/member");
		
		MemberDAO dao = new MemberDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		MemberDTO dto = dao.findById(info.getUserId());
		
		model.addObject("mode", "update");
		model.addObject("dto", dto);
		
		return model;
	}
	
	@RequestMapping(value = "/member/update", method = RequestMethod.POST)
	public ModelAndView upadateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		MemberDAO dao = new MemberDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		try {
			if (info == null) {
				return new ModelAndView("redirect:/member/login");
			}
			MemberDTO dto = new MemberDTO();
			
			dto.setUserId(req.getParameter("userId"));
			dto.setUserPwd(req.getParameter("userPwd"));
			dto.setName(req.getParameter("userName"));
			
			String tel1 = req.getParameter("tel1");
			String tel2 = req.getParameter("tel2");
			String tel3 = req.getParameter("tel3");
			dto.setTel(tel1 + "-" + tel2 + "-" + tel3);
			
			String email1 = req.getParameter("email1");
			String email2 = req.getParameter("email2");
			dto.setEmail(email1 + "@" + email2);
			
			dto.setBirth(req.getParameter("birth"));
			
			int role = 2;
			String userRole = req.getParameter("role");
			if(userRole != null && info.getUserRole() == 0) {
				role = Integer.parseInt(userRole);
			}
			dto.setRole(role);
			
			dao.updateMember(dto);
			
			return new ModelAndView("redirect:/");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/");
	}
	
	@RequestMapping(value = "/member/delete")
	public ModelAndView delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		MemberDAO dao = new MemberDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		try {
			if(info.getUserRole() == 0 || info.getUserId().equals(req.getParameter("userId"))) {
				dao.deleteMeber(req.getParameter("userId"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/");
	}
	
	
	
	
}
