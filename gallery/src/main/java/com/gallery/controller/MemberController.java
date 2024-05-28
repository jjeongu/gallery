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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class MemberController {
	@RequestMapping(value = "/member/login", method = RequestMethod.GET)
	public ModelAndView loginForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 로그인 폼
		return new ModelAndView("member/login");
	}

	@RequestMapping(value = "/member/login", method = RequestMethod.POST)
	public ModelAndView loginSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 로그인 처리
		// 세션객체. 세션 정보는 서버에 저장(로그인 정보, 권한등을 저장)
		HttpSession session = req.getSession();

		MemberDAO dao = new MemberDAO();

		String userId = req.getParameter("userId");
		String userPwd = req.getParameter("userPwd");

		MemberDTO dto = dao.loginMember(userId, userPwd);
		if (dto != null) {
			// 로그인 성공 : 로그인정보를 서버에 저장
			// 세션의 유지시간을 20분설정(기본 30분)
			session.setMaxInactiveInterval(20 * 60);

			// 세션에 저장할 내용
			SessionInfo info = new SessionInfo();
			info.setUserId(dto.getUserId());
			info.setUserName(dto.getUserName());

			// 세션에 member이라는 이름으로 저장
			session.setAttribute("member", info);

			// 메인화면으로 리다이렉트
			return new ModelAndView("redirect:/");
		}

		// 로그인 실패인 경우(다시 로그인 폼으로)
		ModelAndView mav = new ModelAndView("member/login");
		
		String msg = "아이디 또는 패스워드가 일치하지 않습니다.";
		mav.addObject("message", msg);

		return mav;
	}
	
	@RequestMapping(value = "/member/logout", method = RequestMethod.GET)
	public ModelAndView logout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 로그아웃
		HttpSession session = req.getSession();

		// 세션에 저장된 정보를 지운다.
		session.removeAttribute("member");

		// 세션에 저장된 모든 정보를 지우고 세션을 초기화 한다.
		session.invalidate();
		
		return new ModelAndView("redirect:/");
	}
	
}