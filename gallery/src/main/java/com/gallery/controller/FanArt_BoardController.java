package com.gallery.controller;

import java.io.File;
import java.io.IOException;
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
import com.gallery.dao.FanArtDAO;
import com.gallery.dao.MemberDAO;
import com.gallery.domain.FanArtDTO;
import com.gallery.domain.FanArt_ReplyDTO;
import com.gallery.domain.MemberDTO;
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
public class FanArt_BoardController {
	
	@RequestMapping("/fanArt_board/list")
	public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("fanArt_board/list");
		// [페이지, -작가- 검색 타입, 검색 값,]
		
		String page = req.getParameter("page");
		
		FanArtDAO dao = new FanArtDAO();
		MyUtil util = new MyUtilBootstrap();
		
		try {
			int current_page = 1;
			if(page != null) {
				current_page = Integer.parseInt(page); 
			}
			
			int size = 9;
			
			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");
			if (schType == null) {
				schType = "artist";
				kwd = "";
			}

			if (req.getMethod().equalsIgnoreCase("GET")) {
				kwd = URLDecoder.decode(kwd, "utf-8");
			}

			int dataCount;
			if (kwd.length() == 0) {
				dataCount = dao.dataCount();
			} else {
				dataCount = dao.dataCount(schType, kwd);
			}
			
			int total_page = util.pageCount(dataCount, size);
			if(current_page > total_page) {
				current_page = total_page;
			}
			
			int offset = (current_page - 1) * size;
			if(offset < 0) offset = 0;

			List<FanArtDTO> list = null;
			if (kwd.length() == 0) {
				list = dao.list(offset, size);
			} else {
				list = dao.list(offset, size, schType, kwd);
			}
			
			String query = "";
			if (kwd.length() != 0) {
				query = "schType=" + schType + "&kwd=" + URLEncoder.encode(kwd, "utf-8");
			}
			
			
			String cp = req.getContextPath();
			String listUrl = cp + "/fanArt_board/list";
			String articleUrl = cp + "/fanArt_board/article?page="+current_page;
			
			if (query.length() != 0) {
				listUrl += "?" + query;
				articleUrl += "&" + query;
			}
			
			String paging = util.paging(current_page, total_page, listUrl);
			
			List<FanArtDTO> noticeList = dao.noticeList();

			mav.addObject("noticeList", noticeList);
			mav.addObject("list", list);
			mav.addObject("dataCount", dataCount);
			mav.addObject("articleUrl", articleUrl);
			mav.addObject("page", current_page);
			mav.addObject("total_page", total_page);
			mav.addObject("paging", paging);
			mav.addObject("schType", schType);
			mav.addObject("kwd", kwd);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mav;
	}
	
	@RequestMapping(value = "/fanArt_board/write", method = RequestMethod.GET)
	public ModelAndView writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView model = new ModelAndView("fanArt_board/write");
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		if(info.getUserRole() == 1) {
			return new ModelAndView("redirect:/fanArt_board/list");
		}
		
		MemberDAO dao = new MemberDAO();
		List<MemberDTO> list = dao.artistList();
		
		model.addObject("mode", "write");
		model.addObject("list", list);
		
		return model;
	}
	
	@RequestMapping(value = "/fanArt_board/write", method = RequestMethod.POST)
	public ModelAndView writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		FileManager fileManager = new FileManager();
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads"+File.separator+"fanArt";
		
		FanArtDAO dao = new FanArtDAO();
		
		
		try {
			String subject = req.getParameter("subject");
			String content = req.getParameter("content");
			String artist = req.getParameter("artist");
			FanArtDTO dto = new FanArtDTO();
			
			int notice = 0;
			if(req.getParameter("notice") != null && info.getUserRole() == 0) {
				notice = Integer.parseInt(req.getParameter("notice"));
			}
			
			dto.setMember_id(info.getUserId());
			dto.setSubject(subject);
			dto.setContent(content);
			dto.setNotice(notice);
			dto.setArtist(artist);
			
			String filename = null;
			Part p = req.getPart("selectFile");
			MyMultipartFile multipart = fileManager.doFileUpload(p, pathname);
			
			if(multipart != null) {
				filename = multipart.getSaveFilename();
			}
			
			if(filename != null) {
				dto.setImg(filename);
				
				dao.insertFanArt(dto);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/fanArt_board/list");
	}
	
	@RequestMapping(value = "/fanArt_board/article", method = RequestMethod.GET)
	public ModelAndView article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		FanArtDAO dao = new FanArtDAO();
		String page = req.getParameter("page");
		String query = "page=" + page;
		
		try {
			
			long num = Long.parseLong(req.getParameter("num"));
			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");
			if (schType == null) {
				schType = "artist";
				kwd = "";
			}

			kwd = URLDecoder.decode(kwd, "utf-8");
			
			if (kwd.length() != 0) {
				query += "&schType=" + schType + "&kwd=" + URLEncoder.encode(kwd, "UTF-8");
			}
			
			dao.updateHitCount(num);
			
			FanArtDTO dto = dao.findById(num);
			if(dto == null) {
				return new ModelAndView("redirect:/fanArt_board/list?" + query);
			}
			HttpSession session = req.getSession();
			SessionInfo info = (SessionInfo)session.getAttribute("member");
			boolean isUserLike = dao.isUserBoardLike(num, info.getUserId());
			
			FanArtDTO prevDto = dao.findByPrev(dto.getNotice(), num, schType, kwd);
			FanArtDTO nextDto = dao.findByNext(dto.getNotice(), num, schType, kwd);
			
			ModelAndView mav = new ModelAndView("fanArt_board/article");
			
			mav.addObject("dto", dto);
			mav.addObject("query", "&"+query);
			mav.addObject("prevDto", prevDto);
			mav.addObject("nextDto", nextDto);
			mav.addObject("page", page);
			mav.addObject("isUserLike", isUserLike);
			
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/fanArt_board/list?" + query);
	}
	
	@RequestMapping(value = "/fanArt_board/update", method = RequestMethod.GET)
	public ModelAndView updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("fanArt_board/write");
		
		FanArtDAO dao = new FanArtDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String page = req.getParameter("page");
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			
			FanArtDTO dto = dao.findById(num);
			if(dto == null || ! dto.getMember_id().equals(info.getUserId())) {
				return new ModelAndView("redirect:/fanAat_borad/list?page="+page);
			}
			
			MemberDAO dao2 = new MemberDAO();
			List<MemberDTO> list = dao2.artistList();
			
			mav.addObject("dto", dto);
			mav.addObject("page", page);
			mav.addObject("mode", "update");
			mav.addObject("list", list);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mav;
	}
	
	@RequestMapping(value = "/fanArt_board/update", method = RequestMethod.POST)
	public ModelAndView updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		FanArtDAO dao = new FanArtDAO();
		
		String root = req.getServletContext().getRealPath("/");
		String path= root + "uploads" + File.separator +"fanArt";
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		FileManager fileManager = new FileManager();
		
		String page = req.getParameter("page");
		
		
		try {
			FanArtDTO dto = new FanArtDTO();
			
			dto.setNum(Long.parseLong(req.getParameter("num")));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			int notice = 0;
			if(req.getParameter("notice") != null && info.getUserRole() == 0) {
				notice = Integer.parseInt(req.getParameter("notice"));
			}
			dto.setNotice(notice);
			String img = req.getParameter("img");
			dto.setImg(img);
			dto.setArtist(req.getParameter("artist"));
			
			Part p = req.getPart("selectFile");
			MyMultipartFile multipart = fileManager.doFileUpload(p, path);
			
			if(multipart != null) {
				fileManager.doFiledelete(path, img);
				
				String filename = multipart.getSaveFilename();
				dto.setImg(filename);
			}
			
			dao.updateFanArt(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/fanArt_board/list?page="+page);
	}
	
	@RequestMapping(value = "/fanArt_board/delete", method = RequestMethod.GET)
	public ModelAndView delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		FanArtDAO dao = new FanArtDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		FileManager fileManager = new FileManager();
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads"+File.separator+"photo";
		String page = req.getParameter("page");
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			
			FanArtDTO dto = dao.findById(num);
			if(dto == null) {
				return new ModelAndView("redirect:/photo/list?page="+page);
			}
			
			if(!dto.getMember_id().equals(info.getUserId()) && !info.getUserId().equals("admin")) {
				return new ModelAndView("redirect:/photo/list?page="+page);
			}
			
			fileManager.doFiledelete(pathname, dto.getImg());
			dao.deleteFanArt(num);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/fanArt_board/list?page="+page);
	}
	
	@ResponseBody
	@RequestMapping(value = "/fanArt_board/boardLike", method = RequestMethod.POST)
	public Map<String, Object> insertLike(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		FanArtDAO dao = new FanArtDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String state = "false";
		int boardLikeCount = 0;
		
		try {
			
			long num = Long.parseLong(req.getParameter("num"));
			
			String isNoLike = req.getParameter("isNoLike");
			
			if(isNoLike.equals("true")) {
				dao.insertBoardLike(num, info.getUserId());
			}  else {
				dao.deleteBoardLike(num, info.getUserId());
			}
			
			boardLikeCount = dao.countBoardLike(num);
			
			state = "true";
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		map.put("state", state);
		map.put("boardLikeCount", boardLikeCount);
		
		return map;
	}
	
	@ResponseBody
	@RequestMapping(value = "/fanArt_board/insertReply", method = RequestMethod.POST)
	public Map<String, Object> insertReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		FanArtDAO dao = new FanArtDAO();
		
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String state = "false";
		
		try {
			FanArt_ReplyDTO dto = new FanArt_ReplyDTO();
			
			long num = Long.parseLong(req.getParameter("num"));
			dto.setNum(num);
			dto.setMember_id(info.getUserId());
			dto.setContent(req.getParameter("content"));
			
			dao.insertReply(dto);
			
			state = "true";
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		map.put("state", state);
		
		return map;
	}
	
	@RequestMapping(value = "/fanArt_board/listReply", method = RequestMethod.GET)
	public ModelAndView listReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//넘어오는 파라미터 : 글번호 [, 페이지 번호]
		FanArtDAO dao = new FanArtDAO();
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
			
			replyCount = dao.dataCountReply(num);
			total_page = util.pageCount(replyCount, size);
			if(current_page > total_page) {
				current_page = total_page;
			}
			
			int offset = (current_page -1) * size;
			if(offset <0) offset = 0;
			
			List<FanArt_ReplyDTO> listReply = dao.listReply(num, offset, size);
			
			for(FanArt_ReplyDTO dto : listReply) {
				dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			}
			// 페이징 : 자바스크립트 함수 listpage 호출
			String paging = util.pagingMethod(current_page, total_page, "listPage");
			
			ModelAndView mav = new ModelAndView("fanArt_board/listReply");
			
			mav.addObject("listReply", listReply);
			mav.addObject("pageNo", current_page);
			mav.addObject("replyCount", replyCount);
			mav.addObject("total_page", total_page);
			mav.addObject("paging", paging);
			
			return mav;
			
		} catch (Exception e) {
			e.printStackTrace();
			
			resp.sendError(400);
			
			throw e;
		}
		
	}
	@ResponseBody
	@RequestMapping(value = "/fanArt_board/insertReplyLike", method = RequestMethod.POST)
	public Map<String, Object> insertReplyLike(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		
		FanArtDAO dao = new FanArtDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String state = "false";
		int likeCount = 0;
	
		try {
			long r_num = Long.parseLong(req.getParameter("r_num"));
			
			FanArt_ReplyDTO dto = new FanArt_ReplyDTO();
			dto.setR_num(r_num);
			dto.setMember_id(info.getUserId());
			
			dao.insertReplyLike(dto);
			
			likeCount = dao.countReplyLike(r_num);
			
			state = "true";
		} catch (SQLException e) {
			if(e.getErrorCode() == 1) {
				state = "liked";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		model.put("state", state);
		model.put("likeCount", likeCount);
		
		return model;
	}
	
	@ResponseBody
	@RequestMapping(value = "/fanArt_board/deleteReply", method = RequestMethod.POST)
	public Map<String, Object> deleteReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		
		FanArtDAO dao = new FanArtDAO();
		
		HttpSession session = req.getSession();
		SessionInfo  info = (SessionInfo)session.getAttribute("member");
		
		String state = "false";
		
		try {
			long r_num = Long.parseLong(req.getParameter("r_num"));
			FanArt_ReplyDTO dto = dao.findByReply(r_num);
			
			if(info.getUserRole() == 0 || dto.getMember_id().equals(info.getUserId())) {
				dao.deleteReply(r_num);
				
				state = "true";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		model.put("state", state);
		
		return model;
	}
	
}
