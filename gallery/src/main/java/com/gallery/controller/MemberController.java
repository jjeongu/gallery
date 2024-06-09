package com.gallery.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.gallery.annotation.Controller;
import com.gallery.annotation.RequestMapping;
import com.gallery.annotation.RequestMethod;
import com.gallery.annotation.ResponseBody;
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

			return new ModelAndView("redirect:/main");
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
		
		return new ModelAndView("redirect:/main");
	}
	
	@RequestMapping(value = "/member/member", method = RequestMethod.GET)
	public ModelAndView memberForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView model = new ModelAndView("/member/member");
		
		model.addObject("mode", "member");
		model.addObject("title", "회원가입");
		
		return model;
	}
	
	@RequestMapping(value = "/member/member", method = RequestMethod.POST)
	public ModelAndView memberSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		MemberDAO dao = new MemberDAO();
		
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
			
			dto.setRole(2);
			
			dao.insertMember(dto);
			
			return new ModelAndView("redirect:/main");
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
		model.addObject("title", "나의 정보수정");
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
			
			dto.setRole(info.getUserRole());
			
			dao.updateMember(dto);
			
			return new ModelAndView("redirect:/main");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/main");
	}
	
	/*
	@RequestMapping(value = "/member/delete", method = RequestMethod.POST)
	public ModelAndView delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		MemberDAO dao = new MemberDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		try {
			if (info == null) {
				return new ModelAndView("redirect:/member/login");
			}
			
			dao.deleteMeber(info.getUserId());
			session.removeAttribute("member");
			
		} catch (Exception e) {
			e.printStackTrace();
//			msg="회원 탈퇴가 실패했습니다";
//			req.setAttribute("msg", msg);
//			return upadateForm(req, resp);
		}
		
		return new ModelAndView("redirect:/main");
	}
	*/
	
	@ResponseBody
	@RequestMapping(value = "/member/delete", method = RequestMethod.POST)
	public Map<String, Object> delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> map = new HashMap<>();
		MemberDAO dao = new MemberDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String result = "false";
		try {
			
			dao.deleteMeber(info.getUserId());
			session.removeAttribute("member");
			result = "true";
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		map.put("result", result);
		
		return map;
	}
	
	@ResponseBody
	@RequestMapping(value = "/member/userIdCheck", method = RequestMethod.POST)
	public Map<String, Object> userIdCheck(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		
		MemberDAO dao = new MemberDAO();
		
		String userId = req.getParameter("userId");
		MemberDTO dto = dao.findById(userId);
		
		String passed = "false";
		if(dto == null) {
			passed = "true";
		}
		
		map.put("passed", passed);
		
		return map;
	}
	
	@RequestMapping(value = "/admin/member/member", method = RequestMethod.GET)
	public ModelAndView adminMemberForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView model = new ModelAndView("/member/member");
		
		
		model.addObject("mode", "member");
		model.addObject("title", "회원등록");
		
		return model;
	}
	
	@RequestMapping(value = "/admin/member/member", method = RequestMethod.POST)
	public ModelAndView adminMemberSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		MemberDAO dao = new MemberDAO();
		
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
			
			int role = Integer.parseInt(req.getParameter("role"));
			dto.setRole(role);
			
			dao.insertMember(dto);
			
			return new ModelAndView("redirect:/admin/member");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ModelAndView model = new ModelAndView("/member/member");
		model.addObject("mode", "member");
		
		return model;
	}
	
	@RequestMapping(value = "/admin/member/update", method = RequestMethod.GET)
	public ModelAndView adminUpadateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView model = new ModelAndView("member/member");
		
		MemberDAO dao = new MemberDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String member_id = req.getParameter("member_id");
		
		MemberDTO dto = dao.findById(member_id);
		
		if(dto.getRole() == 0 && ! info.getUserId().equals(dto.getUserId())) {
			return new ModelAndView("admin/member");
		}
		
		model.addObject("mode", "update");
		model.addObject("title", "회원정보수정");
		model.addObject("dto", dto);
		
		return model;
	}
	@RequestMapping(value = "/admin/member/update", method = RequestMethod.POST)
	public ModelAndView adminUpadateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
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
			
			dto.setRole(Integer.parseInt(req.getParameter("role")));
			
			dao.updateMember(dto);
			
			return new ModelAndView("redirect:/admin/member");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/main");
	}
	
	
	@RequestMapping(value = "/admin/member/delete", method = RequestMethod.GET)
	public ModelAndView adminDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		ModelAndView mav = new ModelAndView("redirect:/admin/member");
		MemberDAO dao = new MemberDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		try {
			String member_id = req.getParameter("member_id");
			
			MemberDTO dto = dao.findById(member_id);
			
			if(dto.getRole() == 0 && ! info.getUserId().equals(dto.getUserId())) {
				return mav;
			}
			
			dao.deleteMeber(dto.getUserId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mav;
	}
	
	@ResponseBody
	@RequestMapping(value = "/admin/member/delete", method = RequestMethod.POST)
	public Map<String, Object> adminDelete2(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> map = new HashMap<>();
		MemberDAO dao = new MemberDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String result = "false";
		try {
			
			String member_id = req.getParameter("member_id");
			
			MemberDTO dto = dao.findById(member_id);
			
			if(dto.getRole() != 0 || info.getUserId().equals(dto.getUserId())) {
				dao.deleteMeber(dto.getUserId());
				result = "true";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		map.put("result", result);
		
		return map;
	}
}
