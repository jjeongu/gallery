package com.gallery.filter;

import java.io.IOException;

import com.gallery.domain.SessionInfo;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter("/*")
public class LoginFilter implements Filter {
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		String cp = req.getContextPath();
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		if(info == null && isExcludeUri(req) == false) {
			if(isAjaxRequest(req)) {
				resp.sendError(403);
			} else {
				String uri = req.getRequestURI();
				
				if(uri.indexOf(req.getContextPath()) == 0) {
					uri = uri.substring(req.getContextPath().length());
				}
				uri = "redirect:" + uri;
				
				String queryString = req.getQueryString();
				if(queryString != null) {
					uri += "?" + queryString;
				}
				session.setAttribute("preLoginURI", uri);
				
				resp.sendRedirect(cp + "/member/login");
			}

			return;
		}
		
		if(info != null && info.getUserRole()!=0) {
			String uri = req.getRequestURI();
			
			if(uri.indexOf(req.getContextPath()) == 0) {
				uri = uri.substring(req.getContextPath().length());
			}
			if(uri.indexOf("/admin") != -1) {
				resp.sendError(403);
				return;
			}
		}
		
		chain.doFilter(request, response);
		
	}

	@Override
	public void destroy() {
	}
	
	private boolean isAjaxRequest(HttpServletRequest req) {
		String h = req.getHeader("AJAX");
		
		return h != null && h.equals("true");
	}
	
	private boolean isExcludeUri(HttpServletRequest req) {
		String uri = req.getRequestURI();
		String cp = req.getContextPath();
		uri = uri.substring(cp.length());
		
		String uris[] = {
				"/index.jsp", "/main", 
				"/member/login", "/member/logout",
				"/member/member", "/member/userIdCheck",
				"/notice/list",
				"/artist/list", "/artist/**", 
				"/gallery/list",
				"/uploads/gallery/**",
				"/resources/**",
				"/faq/*",
				"/qna/*",
				"/report/*"
		};
		
		if(uri.length() <= 1) {
			return true;
		}
		
		for(String s : uris) {
			if(s.lastIndexOf("**") != -1) {
                s = s.substring(0, s.lastIndexOf("**"));
                if(uri.indexOf(s) == 0) {
					return true;
				}
			} else if(uri.equals(s)) {
				return true;
			}
		}
		
		return false;
	}

}
