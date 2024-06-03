package com.gallery.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.gallery.annotation.Controller;
import com.gallery.annotation.RequestMapping;
import com.gallery.annotation.RequestMethod;
import com.gallery.dao.GalleryDAO;
import com.gallery.domain.GalleryDTO;
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
import jakarta.servlet.http.Part;

@Controller
public class GalleryController {
	
	
	
	
		//사진 리스트
		@RequestMapping(value="/gallery/list", method=RequestMethod.GET)
		public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			ModelAndView mav = new ModelAndView("gallery/list");
			
			GalleryDAO dao = new GalleryDAO();
			MyUtil util = new MyUtilBootstrap();
			
			
			try {
				String page = req.getParameter("page");
				int current_page = 1;
				if(page != null) {
					current_page = Integer.parseInt(page);
				}
				
				// 전체 데이터 개수
				int dataCount = dao.dataCount();
				
				// 전체 페이지수
				int size = 12;
				int total_page = util.pageCount(dataCount, size);
				if(current_page > total_page) {
					current_page = total_page;
				}
				
				// 게시글 가져오기
				int offset = (current_page - 1) * size;
				if(offset < 0) offset = 0;
				
				List<GalleryDTO> list = dao.listPhoto(offset, size);
				
				// 페이징
				String cp = req.getContextPath();
				String listUrl = cp + "/gallery/list";
				String articleUrl = cp + "/gallery/article?page=" + current_page;
				String paging = util.paging(current_page, total_page, listUrl);
				
				// 포워딩할 list에 전달할 속성
				mav.addObject("list", list);
				mav.addObject("dataCount", dataCount);
				mav.addObject("articleUrl", articleUrl);
				mav.addObject("page", current_page);
				mav.addObject("total_page", total_page);
				mav.addObject("paging", paging);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return mav;
		}
		
		
		
		
		
		
		@RequestMapping(value="/gallery/write", method=RequestMethod.GET)
		public ModelAndView writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			ModelAndView mav = new ModelAndView("gallery/write");
			
			return mav;
		}
		
		
		
		
		
		//사진 등록
		@RequestMapping(value="/gallery/write", method=RequestMethod.POST)
		public ModelAndView writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			GalleryDAO dao = new GalleryDAO();
			
			// parameter : introduce, selectFile, artistName
			HttpSession session = req.getSession();
			SessionInfo info = (SessionInfo)session.getAttribute("member");
			
			if(! info.getUserId().equals("admin")) {
				return new ModelAndView("redirect:/gallery/list");
			}
			
			
			FileManager fileManager = new FileManager();
			
			// 파일 저장 경로
			String root = session.getServletContext().getRealPath("/");
			String pathname = root + "uploads" + File.separator + "gallery";
			String artistName=req.getParameter("artistName");
			
			try {
				GalleryDTO dto=new GalleryDTO();
				
				dto.setMember_id(artistName); // 작가 아이디
				dto.setIntroduce(req.getParameter("introduce"));
			
				
				String filename = null;
				Part p = req.getPart("selectFile");
				MyMultipartFile multipart = fileManager.doFileUpload(p, pathname);
				if(multipart != null) {
					filename = multipart.getSaveFilename();
					dto.setImg(filename);
				}
				
				if(filename != null) {
					dto.setImg(filename);
					// dto에 데이터 추가
					dao.insertPhoto(dto);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			return new ModelAndView("redirect:/gallery/list");
		}
		
		
		//아티클로 던져줌
		@RequestMapping(value = "/gallery/article", method = RequestMethod.GET)
		public ModelAndView article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			GalleryDAO dao = new GalleryDAO();
			String page = req.getParameter("page");
			
			try {
				long num = Long.parseLong(req.getParameter("num"));
				
				GalleryDTO dto = dao.findById(num);
				
				if(dto == null) {
					return new ModelAndView("redirect:/gallery/list?page=" + page);
				}
				
				dto.setIntroduce(dto.getIntroduce().replaceAll("\n", "<br>"));
				
				ModelAndView mav = new ModelAndView("gallery/article");
				
				mav.addObject("dto", dto);
				mav.addObject("page", page);
				
				return mav;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return new ModelAndView("redirect:/gallery/list?page=" + page);
		}
		

		//사진 삭제
		@RequestMapping(value = "/gallery/delete", method = RequestMethod.GET)
		public ModelAndView delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			GalleryDAO dao=new GalleryDAO();
			HttpSession session = req.getSession();
			SessionInfo info = (SessionInfo) session.getAttribute("member");
			
			FileManager fileManager = new FileManager();
			
			//파일 저장 경로
			String root = session.getServletContext().getRealPath("/");
			String pathname = root + "uploads" + File.separator + "gallery";
			
			String page = req.getParameter("page");
			
					
			try {
				long num=Long.parseLong(req.getParameter("num"));
				GalleryDTO dto = dao.findById(num);
				
				if(dto==null) {
					return new ModelAndView("redirect:/gallery/list?page=" + page);
				}
				
				//게시물을 올린 사용자나 admin이 아니면 
				if(! info.getUserId().equals("admin")) {
					
					return new ModelAndView("redirect:/gallery/list?page=" + page);
				}
				
				//이미지 파일 지우기
				fileManager.doFiledelete(pathname, dto.getImg());
				
				//테이블 데이터 삭제
				dao.deletePhoto(num);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return new ModelAndView("redirect:/gallery/list?page=" + page);
					
		}
		
		
		
	}
