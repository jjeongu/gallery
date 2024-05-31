package com.gallery.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import com.gallery.annotation.Controller;
import com.gallery.servlet.ModelAndView;
import com.gallery.util.MyUtil;
import com.gallery.util.MyUtilBootstrap;
import com.gallery.util.MyMultipartFile;
import com.gallery.util.FileManager;
import com.gallery.dao.NoticeDAO;
import com.gallery.domain.NoticeDTO;
import com.gallery.domain.SessionInfo;
import com.gallery.annotation.RequestMapping;
import com.gallery.annotation.RequestMethod;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class NoticeController {
	
	@RequestMapping(value="/notice/list")
	public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// parameters : page [, schType, kwd]
		ModelAndView mav=new ModelAndView("notice/list");
		NoticeDAO dao=new NoticeDAO();
		MyUtil util=new MyUtilBootstrap();
		List<NoticeDTO> list;
		int size=10;
		int dataCount, total_page;
		
		try {
			String page=req.getParameter("page");
			int current_page=1;
			if(page!=null) {
				current_page=Integer.parseInt(page);
			}
			
			String schType=req.getParameter("schType");
			String kwd=req.getParameter("kwd");
			if(schType==null) {
				schType="all";
				kwd="";
			}
			
			if(req.getMethod().equals("GET")) {
				kwd=URLDecoder.decode(kwd, "UTF-8");
			}
			
			if(kwd.length()!=0) {
				dataCount=dao.dataCount(schType, kwd);
			} else {
				dataCount=dao.dataCount();
			}
			
			total_page=util.pageCount(dataCount, size);
			int offset=(current_page-1)*size;
			if(offset<0) {
				offset=0;
			}
			
			if(kwd.length()==0) {
				list=dao.listNotice(offset, size);
			} else {
				list=dao.listNotice(offset, size, schType, kwd);
			}
						
			String cp=req.getContextPath();
			String listUrl;
			String articleUrl;
			
			if(kwd.length()!=0) {
				listUrl=cp+"/notice/list?schType="+schType+"&kwd="+URLEncoder.encode(kwd, "UTF-8");
				articleUrl=cp+"/notice/article?page="+current_page+"&schType="+schType+"&kwd="+URLEncoder.encode(kwd, "UTF-8");;
			} else {
				listUrl=cp+"/notice/list";
				articleUrl=cp+"/notice/article?page="+current_page;
			}
			
			String paging=util.paging(current_page, total_page, listUrl);
			mav.addObject("list", list);
			mav.addObject("page", current_page);
			mav.addObject("dataCount", dataCount);
			mav.addObject("total_page", total_page);
			mav.addObject("size", size);
			mav.addObject("articleUrl", articleUrl);
			mav.addObject("schType", schType);
			mav.addObject("kwd", kwd);
			mav.addObject("paging", paging);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mav;
	}
	
	@RequestMapping(value="/notice/write", method=RequestMethod.GET)
	public ModelAndView writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo) session.getAttribute("member");
		
		if(! info.getUserId().equals("admin")) {
			return new ModelAndView("redirect:/notice/list");
		}
		ModelAndView mav=new ModelAndView("notice/write");
		mav.addObject("mode", "write");
		return mav;
	}
	
	@RequestMapping(value="/notice/write", method=RequestMethod.POST)
	public ModelAndView writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글 등록
		// parameter : subject, content [, file]
		NoticeDAO dao=new NoticeDAO();
		FileManager fileManager=new FileManager();
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo) session.getAttribute("member");		
				
		if(! info.getUserId().equals("admin")) {
			return new ModelAndView("redirect:/notice/list");
		}	
		
		String path=session.getServletContext().getRealPath("/");
		String pathname=path+"uploads"+File.separator+"notice";	
		
		try {
			NoticeDTO dto=new NoticeDTO();
					
			dto.setMember_id(info.getUserId());
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			//List<MyMultipartFile> listFile=fileManager.doFileUpload(req.getParts(), pathname);
			//dto.setListFile(listFile);
			dao.insertNotice(dto);		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView("redirect:/notice/list");
	}
	
	@RequestMapping(value="/notice/article", method=RequestMethod.GET)
	public ModelAndView article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// parameters : page, num [, schType, kwd]
		NoticeDAO dao=new NoticeDAO();
		String page=req.getParameter("page");
		String schType=req.getParameter("schType");
		String kwd=req.getParameter("kwd");
		if(schType==null) {
			schType="all";
			kwd="";
		}
		
		try {
			long num=Long.parseLong(req.getParameter("num"));
			dao.updateHitCount(num);
			NoticeDTO dto=dao.findById(num);
			
			if(dto==null) {
				return new ModelAndView("redirect:/notice/list?page="+page+"num="+num);
			}
			
			NoticeDTO prevDto=dao.findByPrev(num, schType, kwd);
			NoticeDTO nextDto=dao.findByNext(num, schType, kwd);
			
			ModelAndView mav=new ModelAndView("notice/article");
			mav.addObject("dto", dto);
			mav.addObject("prevDto", prevDto);
			mav.addObject("nextDto", nextDto);
			mav.addObject("page", page);
			mav.addObject("num", num);
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/notice/list");

	}
}
