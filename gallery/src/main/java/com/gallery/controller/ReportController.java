package com.gallery.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import com.gallery.annotation.Controller;
import com.gallery.annotation.RequestMapping;
import com.gallery.annotation.RequestMethod;
import com.gallery.dao.ReportDAO;
import com.gallery.domain.ReportDTO;
import com.gallery.domain.SessionInfo;
import com.gallery.servlet.ModelAndView;
import com.gallery.util.FileManager;
import com.gallery.util.MyMultipartFile;
import com.gallery.util.MyUtil;
import com.gallery.util.MyUtilBootstrap;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class ReportController {
	@RequestMapping(value="/report/list")
	public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// parameters : page [, schType, kwd]
		ModelAndView mav=new ModelAndView("report/list");
		ReportDAO dao=new ReportDAO();
		MyUtil util=new MyUtilBootstrap();
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo) session.getAttribute("member");
		List<ReportDTO> list;
		int size=10;
		int dataCount, total_page;
		String schType=req.getParameter("schType");
		String kwd=req.getParameter("kwd");
		String page=req.getParameter("page");
		int current_page=1;
		
		if(schType==null) {
			schType="all";
			kwd="";
		}
		
		try {
			if(info.getUserRole()==0) {
				if(page!=null) {
					current_page=Integer.parseInt(page);
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
					list=dao.listReport(offset, size);
				} else {
					list=dao.listReport(offset, size, schType, kwd);
				}
				
				String cp=req.getContextPath();
				String listUrl;
				String articleUrl;
				
				if(kwd.length()!=0) {
					listUrl=cp+"/report/list?schType="+schType+"&kwd="+URLEncoder.encode(kwd, "UTF-8");
					articleUrl=cp+"/report/article?page="+current_page+"&schType="+schType+"&kwd="+URLEncoder.encode(kwd, "UTF-8");
				} else {
					listUrl=cp+"/report/list";
					articleUrl=cp+"/report/article?page="+current_page;
				}
				String paging=util.paging(current_page, total_page, listUrl);
				mav.addObject("articleUrl", articleUrl);
				mav.addObject("paging", paging);
			} else {
				if(page!=null) {
					current_page=Integer.parseInt(page);
				}
				
				dataCount=dao.dataCount(info.getUserId());
				
				total_page=util.pageCount(dataCount, size);
				int offset=(current_page-1)*size;
				if(offset<0) {
					offset=0;
				}
				
				list=dao.listReport(offset, size, info.getUserId());
				
				String cp=req.getContextPath();
				String listUrl;
				String articleUrl;
				
				listUrl=cp+"/report/list";
				articleUrl=cp+"/report/article?page="+current_page;
				String paging=util.paging(current_page, total_page, listUrl);
				mav.addObject("articleUrl", articleUrl);
				mav.addObject("paging", paging);
			}
			mav.addObject("list", list);
			mav.addObject("page", current_page);
			mav.addObject("dataCount", dataCount);
			mav.addObject("total_page", total_page);
			mav.addObject("size", size);	
			mav.addObject("schType", schType);
			mav.addObject("kwd", kwd);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mav;
	}
	
	@RequestMapping(value="/report/write", method=RequestMethod.GET)
	public ModelAndView reportForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav=new ModelAndView("report/write");
		mav.addObject("mode", "write");
		return mav;
	}
	
	@RequestMapping(value="/report/write", method=RequestMethod.POST)
	public ModelAndView reportSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글 등록
		// parameter : subject, content [, file]
		ReportDAO dao=new ReportDAO();
		FileManager fileManager=new FileManager();
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo) session.getAttribute("member");
		String path=session.getServletContext().getRealPath("/");
		String pathname=path+"uploads"+File.separator+"report";	
		try {
			ReportDTO dto=new ReportDTO();
					
			dto.setMember_id(info.getUserId());
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			List<MyMultipartFile> listFile=fileManager.doFileUpload(req.getParts(), pathname);
			dto.setListFile(listFile);
			dao.insertReport(dto);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView("redirect:/report/list");
	}
	
	@RequestMapping(value="/report/article", method=RequestMethod.GET)
	public ModelAndView article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// parameters : page, num [, schType, kwd]
		ReportDAO dao=new ReportDAO();
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo) session.getAttribute("member");
		String page=req.getParameter("page");
		String schType=req.getParameter("schType");
		String kwd=req.getParameter("kwd");
		
		if(schType==null) {
			schType="all";
			kwd="";
		}
		kwd=URLDecoder.decode(kwd, "UTF-8");
		
		try {
			long num=Long.parseLong(req.getParameter("num"));
			ReportDTO dto=dao.findById(num);
			
			if(dto==null) {
				return new ModelAndView("redirect:/report/list?page="+page+"num="+num);
			}
			ReportDTO prevDto=null;
			ReportDTO nextDto=null;
			
			if(info.getUserRole()==0) {
				prevDto=dao.findByPrev(num, schType, kwd);
				nextDto=dao.findByNext(num, schType, kwd);
			} else {
				prevDto=dao.findByPrev(num, dto.getMember_id());
				nextDto=dao.findByNext(num, dto.getMember_id());
			}
			List<ReportDTO> listFile=dao.listReportFile(num);
			
			ModelAndView mav=new ModelAndView("/report/article");
			mav.addObject("dto", dto);
			mav.addObject("prevDto", prevDto);
			mav.addObject("nextDto", nextDto);
			mav.addObject("page", page);
			mav.addObject("num", num);
			mav.addObject("listFile", listFile);
			
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/report/list");

	}
	
	@RequestMapping(value="/report/update", method=RequestMethod.GET)
	public ModelAndView updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ReportDAO dao=new ReportDAO();
		String page=req.getParameter("page");
		String num=req.getParameter("num");
		
		try {
			ReportDTO dto=dao.findById(Long.parseLong(num));
			if(dto==null) {
				return new ModelAndView("redirect:/report/list?page="+page+"&num="+num);
			}
			
			List<ReportDTO> listFile=dao.listReportFile(Long.parseLong(num));
			ModelAndView mav=new ModelAndView("report/write");
			mav.addObject("dto", dto);
			mav.addObject("listFile", listFile);
			mav.addObject("page", page);
			mav.addObject("mode", "update");
			
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView("redirect:/report/list?page="+page+"&num="+num);
	}
	
	@RequestMapping(value="/report/update", method=RequestMethod.POST)
	public ModelAndView updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ReportDAO dao=new ReportDAO();
		HttpSession session=req.getSession();
		FileManager fileManager=new FileManager();
		String page=req.getParameter("page");
		String num=req.getParameter("num");
		String schType=req.getParameter("schType");
		String kwd=req.getParameter("kwd");		
		if(schType==null) {
			schType="all";
			kwd="";
		}
		
		String root=session.getServletContext().getRealPath("/");
		String pathname=root+"uploads"+File.separator+"notice";
		
		try {
			ReportDTO dto=new ReportDTO();
			dto.setNum(Long.parseLong(num));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			List<MyMultipartFile> listFile=fileManager.doFileUpload(req.getParts(), pathname);
			dto.setListFile(listFile);
			dao.updateReport(dto);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView("redirect:/report/article?page="+page+"&num="+num+"&schType="+schType+"&kwd="+kwd);
	}
	
	@RequestMapping(value="/report/delete", method=RequestMethod.GET)
	public ModelAndView deleteReport(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ReportDAO dao=new ReportDAO();
		
		HttpSession session=req.getSession();
		FileManager fileManager=new FileManager();

		String page=req.getParameter("page");
		String num=req.getParameter("num");
		String schType=req.getParameter("schType");
		String kwd=req.getParameter("kwd");		
		if(schType==null) {
			schType="all";
			kwd="";
		}
		
		if(kwd.length()!=0) {
			kwd=URLDecoder.decode(kwd, "UTF-8");
		}
		
		String root=session.getServletContext().getRealPath("/");
		String pathname=root+"uploads"+File.separator+"notice";
		
		try {
			List<ReportDTO> listFile=dao.listReportFile(Long.parseLong(num));
			for(ReportDTO dto:listFile) {
				fileManager.doFiledelete(pathname, dto.getSaveFilename());
			}
			dao.deleteReportFile(Long.parseLong(num));
			dao.deleteReport(Long.parseLong(num));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView("redirect:/report/list?page="+page+"&num="+num+"&schType="+schType+"&kwd="+kwd);
	}
	
	@RequestMapping(value="/report/download", method=RequestMethod.GET)
	public void download(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// parameters : filenum
		ReportDAO dao=new ReportDAO();
		
		HttpSession session=req.getSession();
		FileManager fileManager=new FileManager();
		
		String root=session.getServletContext().getRealPath("/");
		String pathname=root+"uploads"+File.separator+"notice";
		
		boolean b=false;
		try {
			long fileNum=Long.parseLong(req.getParameter("fileNum"));
			ReportDTO dto=dao.findByFileId(fileNum);
			if(dto!=null) {
				b=fileManager.doFiledownload(dto.getSaveFilename(), dto.getUploadFilename(), pathname, resp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(!b) {
			
			resp.setContentType("text/html; charset=utf-8");
			PrintWriter out=resp.getWriter();
			out.print("<script>alert('파일 다운로드가 실패했습니다.');history.back();</script>");
		}
	}
}
