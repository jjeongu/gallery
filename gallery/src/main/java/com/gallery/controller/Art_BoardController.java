package com.gallery.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gallery.annotation.Controller;
import com.gallery.annotation.RequestMapping;
import com.gallery.annotation.RequestMethod;
import com.gallery.annotation.ResponseBody;
import com.gallery.dao.Art_BoardDAO;
import com.gallery.domain.Art_BoardDTO;
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
public class Art_BoardController {
	
	@RequestMapping(value = "/art_board/list")
	public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		// 작가 게시글 리스트
		// 파라미터 : 페이지 번호, 검색 컬럼, 검색 키워드
		
		ModelAndView mav = new ModelAndView("art_board/list");

		Art_BoardDAO dao = new Art_BoardDAO();
		MyUtil util =new MyUtilBootstrap();
		
		
		try {
			String page = req.getParameter("page");
			int current_page = 1;
			if( page != null) {
				current_page = Integer.parseInt(page);
			}
			
			// 검색
			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");
			if(schType == null) {
				schType = "all";
				kwd = "";
			}
			
			if(req.getMethod().equalsIgnoreCase("GET")) {
				kwd = URLDecoder.decode(kwd, "utf-8");
			}
			
			int dataCount;
			if(kwd.length() == 0) {
				dataCount = dao.dataCount();
			} else {
				dataCount = dao.dataCount(schType, kwd);
			}
			
			int size = 10;
			int total_page = util.pageCount(dataCount, size);
			if(current_page > total_page) {
				current_page = total_page;
			}
			
			// 게시믈 가져오기
			int offset = (current_page -1 ) * size;
			if(offset < 0 ) offset = 0;
			
			List<Art_BoardDTO> list = null;
			if(kwd.length() ==0) {
				list = dao.listArt_Board(offset, size);
			}else {
				list = dao.listArt_Board(offset, size, schType, kwd);
			}

			String query = "";
			if(kwd.length() != 0) {
				query = "schType=" + schType + "&kwd=" + URLEncoder.encode(kwd,"utf-8");
			}
			
			// 페이징 처리
			String cp = req.getContextPath();
			String listUrl = cp + "/art_board/list";
			String articleUrl = cp + "/art_board/article?page="+ current_page;
			if(query.length() != 0) {
				listUrl += "&" + query; 
				articleUrl += "&" + query;
			}
			String paging = util.paging(current_page, total_page, listUrl);
			
			mav.addObject("list", list);
			mav.addObject("page", current_page);
			mav.addObject("total_page", total_page);
			mav.addObject("dataCount", dataCount);
			mav.addObject("size", size);
			mav.addObject("articleUrl", articleUrl);
			mav.addObject("paging", paging);
			mav.addObject("schType", schType);
			mav.addObject("kwd", kwd);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mav;
	}
	
	
	@RequestMapping(value = "/art_board/write", method = RequestMethod.GET)
	public ModelAndView writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		ModelAndView mav = new ModelAndView("art_board/write");
		mav.addObject("mode", "write");
		return mav;
	}
	
	
	@RequestMapping(value = "/art_board/write", method = RequestMethod.POST)
	public ModelAndView writSubmit(HttpServletRequest req, HttpServletResponse resq) throws ServletException, IOException {
		// 글 저장 
		Art_BoardDAO dao = new Art_BoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		FileManager fileManager = new FileManager();
		
		// 파일 저장 경로
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "art_board";
		
		try {
			Art_BoardDTO dto = new Art_BoardDTO();
			
			// userId는 세션에 저장된 정보
			dto.setMember_id(info.getUserId());
			
			// 파라미터 : 제목, 내용
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			Part p = req.getPart("selectFile");
			MyMultipartFile multFile = fileManager.doFileUpload(p, pathname);
			if(multFile != null) {
				String saveFilename = multFile.getSaveFilename();
				String uploadfilename = multFile.getOriginalFilename();
				long size = multFile.getSize();
				dto.setSaveFilename(saveFilename);
				dto.setUploadfilename(uploadfilename);
				dto.setFileSize(size);
				
			}
			dao.insertArt_Board(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/art_board/list");
	}
	
	@RequestMapping(value = "/art_board/article", method = RequestMethod.GET)
	public ModelAndView article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Art_BoardDAO dao = new Art_BoardDAO();
		
		String page = req.getParameter("page");
		String query = "page=" + page;
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");
			if(schType == null) {
				schType = "all";
				kwd = "";
			}
			kwd = URLDecoder.decode(kwd, "utf-8");
			
			// 게시물 가져오기
			if (kwd.length() != 0 ) {
				query += "&schType=" + schType + "&kwd=" + URLEncoder.encode(kwd, "UTF-8");
			}
		
			// 조회수 증가
			dao.updatHitCount(num);
			
			// 게시물 가져오기 
			Art_BoardDTO dto = dao.findById(num);
			if( dto == null ) {
				return new ModelAndView("redirect:/art_board/list?" + query);
			}
			
			// 이전글 다음글
			Art_BoardDTO prevDto = dao.findByPrev(dto.getNum(), schType, kwd);
			Art_BoardDTO nextDto = dao.findByNext(dto.getNum(), schType, kwd);
			
			
			ModelAndView mav = new ModelAndView("art_board/article");
			
			mav.addObject("dto", dto);
			mav.addObject("page", page);
			mav.addObject("query", query);
			mav.addObject("prevDto", prevDto);
			mav.addObject("nextDto", nextDto);
			
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/art_board/list?" + query);
	}
	
	@RequestMapping(value = "/art_board/update", method = RequestMethod.GET)
	public ModelAndView updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		// 수정 폼
		// 파라미터 : 글 번호 [, 페이지 번호, 세션: 사용자정보]
		
		Art_BoardDAO dao = new Art_BoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String page = req.getParameter("page");
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			Art_BoardDTO dto = dao.findById(num);
			
			if(dto == null) {
				return new ModelAndView("redirect:/art_board/list?page=" + page);
			}
			
			if(! dto.getMember_id().equals(info.getUserId())) {
				return new ModelAndView("redirect:/art_board/list?page=" + page);
			}
			
			ModelAndView mav = new ModelAndView("art_board/write");
			
			mav.addObject("dto", dto);
			mav.addObject("page", page);
			mav.addObject("mode", "update");
			
			return mav;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/art_board/list?page=" + page);
	}
	
	@RequestMapping(value = "/art_board/update", method = RequestMethod.POST)
	public ModelAndView updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정 완료
		
		Art_BoardDAO dao = new Art_BoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		FileManager fileManager = new FileManager();
		
		// 파일 저장 경로
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "art_board";
		
		String page = req.getParameter("page");
		
		try {
			Art_BoardDTO dto = new Art_BoardDTO();
			
			dto.setNum(Long.parseLong(req.getParameter("num")));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			dto.setSaveFilename(req.getParameter("saveFilename"));
			dto.setUploadfilename(req.getParameter("uploadfilename"));
			
			dto.setMember_id(info.getUserId());
		
			Part p = req.getPart("selectFile");
			MyMultipartFile multiFile = fileManager.doFileUpload(p, pathname);
			if(multiFile != null) {
				if(req.getParameter("saveFilename").length() !=0 ) {
					fileManager.doFiledelete(pathname, req.getParameter("saveFilename"));
				}
				
				String saveFilename = multiFile.getSaveFilename();
				String uploadfilename = multiFile.getOriginalFilename();
				long size = multiFile.getSize();
				dto.setSaveFilename(saveFilename);
				dto.setUploadfilename(uploadfilename);
				dto.setFileSize(size);
			}
			dao.updateArt_Board(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return new ModelAndView("redirect:/art_board/list?page=" + page);
	}
	
	@RequestMapping(value = "/art_board/deleteFile", method = RequestMethod.GET)
	public ModelAndView deleteFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정에서 파일만 삭제
		Art_BoardDAO dao = new Art_BoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		FileManager fileManager = new FileManager();
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "art_board";
		
		String page = req.getParameter("page");
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			Art_BoardDTO dto = dao.findById(num);
			if(dto == null) {
				return new ModelAndView("redirect:/art_board/list?page=" + page);
			}
			
			if (!info.getUserId().equals(dto.getMember_id())) {
				return new ModelAndView("redirect:/art_board/list?page=" + page);
			}
			
			fileManager.doFiledelete(pathname, dto.getSaveFilename());
			
			dto.setUploadfilename("");
			dto.setSaveFilename("");
			dto.setFileSize(0);
			dao.updateArt_Board(dto);
			
			ModelAndView mav = new ModelAndView("art_board/write");
			
			mav.addObject("dto", dto);
			mav.addObject("page", page);

			mav.addObject("mode", "update");

			return mav;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/art_board/list?page=" + page);
	}
	
	// 삭제
	@RequestMapping(value = "/art_board/delete", method = RequestMethod.GET)
	public ModelAndView delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Art_BoardDAO dao = new Art_BoardDAO();
		
		HttpSession sessoion = req.getSession();
		SessionInfo info = (SessionInfo) sessoion.getAttribute("member");
		
		FileManager fileManager = new FileManager();
		
		String root = sessoion.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "art_board";
		
		String page = req.getParameter("page");
		String query = "page=" + page;
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");
			if ( schType == null ) {
				schType = "all";
				kwd = "";
			}
			kwd = URLDecoder.decode(kwd, "utf-8");
			
			if(kwd.length() != 0) {
				query += "&schType=" + schType + "&kwd=" + URLEncoder.encode(kwd,"UTF-8");
			}
			
			Art_BoardDTO dto = dao.findById(num);
			if(dto==null) {
				return new ModelAndView("redirect:/art_board/list? " + query);
			}
			
			if(!info.getUserId().equals(dto.getMember_id()) && !info.getUserId().equals("admin")) {
				return new ModelAndView("redirect:/art_board/list?" + query);
			}
			if (dto.getSaveFilename() != null && dto.getSaveFilename().length()!=0) {
				fileManager.doFiledelete(pathname, dto.getSaveFilename());
			}
			
			dao.deleteArt_Board(num, info.getUserId());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/art_board/list?" + query);
	}

	// 파일 다운로드
	@RequestMapping(value = "/art_board/download")
	public void download(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Art_BoardDAO dao = new Art_BoardDAO();
		HttpSession session = req.getSession();
		
		FileManager fileManager = new FileManager();
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "art_board";
		
		boolean b = false;
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			
			Art_BoardDTO dto = dao.findById(num);
			if( dto != null ) {
				b = fileManager.doFiledownload(dto.getSaveFilename(), dto.getUploadfilename(),pathname, resp);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if( !b ) {
			resp.setContentType("text/html;charset=utf-8");
			PrintWriter out = resp.getWriter();
			out.print("<script>alert('파일다운로드가 실패 했습니다.');history.back();</script>");
		}
	}
	
	// 강좌 공감 저장 - AJAX : JSON
	@ResponseBody
	@RequestMapping(value = "/art_board/insertArt_BoardLike", method = RequestMethod.POST)
	public Map<String, Object> insertArt_BoardLike(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		
		Art_BoardDAO dao = new Art_BoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String state = "false";
		int likeCount = 0;
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			String isNoLike = req.getParameter("isNoLike");
			
			if(isNoLike.equals("true")) {
				dao.insertArt_BoardLike(num, info.getUserId());
			}else {
				dao.deleteArt_BoardLike(num, info.getUserId());
			}
			likeCount = dao.countArt_BoardLike(num);
			
			state = "true";
		} catch (SQLException e) {
			state = "liked";
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.put("state", state);
		model.put("likeCount", likeCount);
		
		return model;
	}
	
	/*
	// 리플 리스트 
	@RequestMapping(value = "art_board/listReply", method = RequestMethod.GET)
	public ModelAndView listReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Art_BoardDAO dao = new Art_BoardDAO();
		MyUtil util = new MyUtilBootstrap();
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			String pageNo = req.getParameter("pageNo");
			int current_page = 1;
			if(pageNo != null) {
				current_page = Integer.parseInt(pageNo);
			}
			
			int size = 5;
			int total_page = 0;
			int replyCount = 0;
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
*/
	
}



	