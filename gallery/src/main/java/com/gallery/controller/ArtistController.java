package com.gallery.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.gallery.annotation.Controller;
import com.gallery.annotation.RequestMapping;
import com.gallery.annotation.RequestMethod;
import com.gallery.dao.ArtistDAO;
import com.gallery.domain.ArtDTO;
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
public class ArtistController {
	@RequestMapping(value="/artist/list")
	public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("artist/list");
		
		ArtistDAO dao = new ArtistDAO();
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
			
			List<ArtDTO> list = dao.listArtist(offset, size);
			
			// 페이징
			String cp = req.getContextPath();
			String listUrl = cp + "/artist/list";
			String articleUrl = cp + "/artist/article?page=" + current_page;
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
	
	@RequestMapping(value="/artist/write", method=RequestMethod.GET)
	public ModelAndView writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("artist/write");
		
		try {
			ArtistDAO dao = new ArtistDAO();
			
			mav.addObject("mode", "write");
			
			List<ArtDTO> list = dao.notRegistArts();
			
			mav.addObject("list", list);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mav;
	}
	
	@RequestMapping(value = "/artist/write", method=RequestMethod.POST)
	public ModelAndView writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ArtistDAO dao = new ArtistDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		if(! info.getUserId().equals("admin")) {
			return new ModelAndView("redirect:/artist/list");
		}
		
		FileManager fileManager = new FileManager();
		
		// 파일 저장 경로
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "artist";
	
		
		try {
			String artist = req.getParameter("artist");
			ArtDTO dto=new ArtDTO();
		
			// 생일, 경력, 소개, 대표작 
			dto.setMember_id(artist);
			dto.setIntroduce(req.getParameter("introduce"));
			dto.setCareer(req.getParameter("career"));
			dto.setRepresent(req.getParameter("represent"));
			
			String filename = null;
			Part p = req.getPart("selectFile");
			MyMultipartFile multipart = fileManager.doFileUpload(p, pathname);
			if(multipart != null) {
				filename = multipart.getSaveFilename();
			}
			dto.setRepresent(filename);
			
			if(filename != null) {
				dto.setImg(filename);
				dto.setUpload_img(filename);
				// dto에 데이터 추가
				dao.insertAtrist(dto);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ModelAndView("redirect:/artist/list");
	}

	@RequestMapping(value = "/artist/article", method = RequestMethod.GET)
	public ModelAndView article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ArtistDAO dao = new ArtistDAO();
		
		try {
			String member_id = req.getParameter("member_id");
			
			ArtDTO dto = dao.findById(member_id);
			
			if(dto == null) {
				return new ModelAndView("redirect:/artist/list");
			}
			
			dto.setIntroduce(dto.getIntroduce().replaceAll("\n", "<br>"));
			
			ModelAndView mav = new ModelAndView("artist/article");
			
			mav.addObject("dto", dto);
			
			return mav;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/artist/list");
	}
	
	
	// 작가 수정 폼
	@RequestMapping(value = "/artist/update", method = RequestMethod.GET)
	public ModelAndView updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ArtistDAO dao = new ArtistDAO();
		String artist = req.getParameter("artist");
		
		 // 로그인 및 아이디 일치 여부 등을 자바스크르비트로 검증하고 막는건 우회 가능해서 무의미
		
		try {
			HttpSession session = req.getSession();
			SessionInfo info = (SessionInfo)session.getAttribute("member");
			
			String member_id = req.getParameter("member_id");
			
			ArtDTO dto = dao.findById(member_id);

			if (dto == null || !info.getUserId().equals("admin")) {
				return new ModelAndView("redirect:/artist/list");
			}
			
			ModelAndView mav = new ModelAndView("artist/write");
			
			mav.addObject("dto", dto);
			mav.addObject("artist", artist);
			mav.addObject("mode", "update");
			
			return mav;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/artist/list");
	}
	
	//작가 수정 완료
	@RequestMapping(value = "/artist/update", method = RequestMethod.POST)
	public ModelAndView updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		ArtistDAO dao = new ArtistDAO();
		
		HttpSession session = req.getSession();
		FileManager fileManager = new FileManager();
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "artist";
		
		String artist = req.getParameter("artist");
		
		try {
			ArtDTO dto = new ArtDTO();
			
			dto.setCareer(req.getParameter("career"));
			dto.setMember_id(req.getParameter("member_id"));
			dto.setIntroduce(req.getParameter("introduce"));
			dto.setRepresent(req.getParameter("represent"));
			
			String img = req.getParameter("img");
			dto.setImg(img);
			
			Part p = req.getPart("selectFile");
			MyMultipartFile multipart = fileManager.doFileUpload(p, pathname);
			
			if(multipart != null) { // null 이면 사진 안올린 것.
				String filename = multipart.getSaveFilename();
				dto.setImg(filename); // 이미지파일 갈아치우기.
				dto.setUpload_img(filename);
				
				// 기존의 파일 지우기
				fileManager.doFiledelete(pathname, img); // doFiledelete: 파일 지우기
			}
			
			dao.updateArtist(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/artist/list?artist=" + artist);
	}
	
	// 삭제
	@RequestMapping(value = "/artist/delete", method = RequestMethod.GET)
	public ModelAndView delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		ArtistDAO dao=new ArtistDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		FileManager fileManager = new FileManager();
		
		//파일 저장 경로
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "artist";
				
		try {
			String member_id = req.getParameter("member_id");
			
			ArtDTO dto = dao.findById(member_id);
			
			if(dto==null) {
				return new ModelAndView("redirect:/artist/list");
			}
			
			//게시물을 올린 사용자나 admin이 아니면 
			if(! info.getUserId().equals("admin")) {
				return new ModelAndView("redirect:/artist/list");
			}
			
			//이미지 파일 지우기
			fileManager.doFiledelete(pathname, dto.getImg());
			
			//테이블 데이터 삭제
			dao.deleteArtist(member_id);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/artist/list");
				
	}
}

