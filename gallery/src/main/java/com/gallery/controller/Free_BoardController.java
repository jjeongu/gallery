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
import com.gallery.dao.Free_BoardDAO;
import com.gallery.domain.Free_BoardDTO;
import com.gallery.domain.Free_Board_ReplyDTO;
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
public class Free_BoardController {
	// 리스트
	
	@RequestMapping(value = "/free_board/list")
	public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		ModelAndView mav = new ModelAndView("free_board/list");
		
		Free_BoardDAO dao = new Free_BoardDAO();
		MyUtil util = new MyUtilBootstrap();
		
		try {
			String page = req.getParameter("page");
			int current_page = 1;
			if(page != null) {
				current_page = Integer.parseInt(page);
			}
			
			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");

			
			if(schType == null) {
				schType = "all";
				kwd = "";
			}
			
			if(req.getMethod().equals("GET")) { // get 방식일 때 디코딩 
				kwd = URLDecoder.decode(kwd,"utf-8");
			}
			
			int dataCount;
			if (kwd.length() == 0) {
				dataCount = dao.dataCount();
			} else {
				dataCount = dao.dataCount(schType, kwd);
			}
			
			int size = 10;
			int total_page = util.pageCount(dataCount, size);
			if (current_page > total_page) {
				current_page = total_page;
			}

			
			int offset = (current_page - 1) * size;
			if(offset < 0) offset = 0;
			
			List<Free_BoardDTO> list = null;
			if(kwd.length() == 0 ) {
				list = dao.listFree_Board(offset, size);
			} else {
				list = dao.listFree_Board(offset, size, schType, kwd);
			}
			
			
			// 공지사항 리스트 가져오기
			List<Free_BoardDTO> listFree_Board = null;
			if (current_page == 1 ) {
			    listFree_Board = dao.listFree_Board();
			}

			
			String query = "";
			if (kwd.length() != 0) {
				query = "schType=" + schType + "&kwd=" + URLEncoder.encode(kwd, "utf-8");
			}
			
			String cp = req.getContextPath();
			String listUrl = cp + "/free_board/list";
			String articleUrl = cp + "/free_board/article?page=" + current_page;
			if (query.length() != 0) {
				listUrl += "?" + query;
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
			
			
			mav.addObject("listFree_Board", listFree_Board);
			
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mav;
		
		  
	}

	
	// 글 쓰기
	@RequestMapping(value = "/free_board/write", method = RequestMethod.GET)
	public ModelAndView writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		ModelAndView mav = new ModelAndView("free_board/write");
		mav.addObject("mode", "write");
		
		return mav;
		
	}
	
	// 글 저장
	@RequestMapping(value = "/free_board/write", method = RequestMethod.POST)
	public ModelAndView writeSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Free_BoardDAO dao  = new Free_BoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		FileManager fileManager = new FileManager();
		
		// 파일 저장 경로
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "free_board";
		
		
		try {
			Free_BoardDTO dto = new Free_BoardDTO();
			
			dto.setMember_id(info.getUserId());
			
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			if(req.getParameter("notice")!=null) {
				dto.setNotice(Integer.parseInt(req.getParameter("notice")));
			} else {
				dto.setNotice(0);
			}
			
			Part p = req.getPart("selectFile");
			MyMultipartFile multiFile = fileManager.doFileUpload(p, pathname);
			if (multiFile != null) {
				String saveFileName = multiFile.getSaveFilename();
				String uploadFileName = multiFile.getOriginalFilename();
				long size = multiFile.getSize();
				dto.setSaveFileName(saveFileName);
				dto.setUploadFileName(uploadFileName);
				dto.setFileSize(size);
			}
			
			dao.insertFree_Board(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return new ModelAndView("redirect:/free_board/list");
	}
	
	@RequestMapping(value = "/free_board/article", method = RequestMethod.GET)
	// 글 보기
	public ModelAndView article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Free_BoardDAO dao = new Free_BoardDAO();
		
		String page = req.getParameter("page");
		String query = "page=" + page;
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			
			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");
			if (schType == null) {
				schType = "all";
				kwd = "";
			}
			kwd = URLDecoder.decode(kwd, "utf-8");

			if (kwd.length() != 0) {
				query += "&schType=" + schType + "&kwd=" + URLEncoder.encode(kwd, "UTF-8");
			}
			
			dao.updateHitCount(num);

			Free_BoardDTO dto = dao.findById(num);
			if(dto == null) {
				return new ModelAndView("redirect:/free_board/list?" + query);
			}
			
			HttpSession session = req.getSession();
			SessionInfo info = (SessionInfo)session.getAttribute("member");
			boolean isUserFree_boardLike = dao.isUserFree_boardLike(num, info.getUserId());
			
			Free_BoardDTO prevDto = dao.findByPrev(dto.getNum(), schType, kwd);
			Free_BoardDTO nextDto = dao.findByNext(dto.getNum(), schType, kwd);
			
			ModelAndView mav = new ModelAndView("free_board/article");
			
			mav.addObject("dto", dto);
			mav.addObject("page", page);
			mav.addObject("query", query);
			mav.addObject("prevDto", prevDto);
			mav.addObject("nextDto", nextDto);
			mav.addObject("isUserFree_boardLike", isUserFree_boardLike);
			
			return mav;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
		return new ModelAndView("redirect:/free_board/list?"+query);
	}
	
	//글 수정
	@RequestMapping(value = "/free_board/update", method = RequestMethod.GET)
	public ModelAndView updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Free_BoardDAO dao = new Free_BoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		String page = req.getParameter("page");

		try {
			long num = Long.parseLong(req.getParameter("num"));
			Free_BoardDTO dto = dao.findById(num);
			
			if(dto==null) {
				return new ModelAndView("redirect:/free_board/list?page=" + page);
			}
			
			if(! dto.getMember_id().equals(info.getUserId())) {
				return new ModelAndView("redirect:/free_board/list?page=" + page);
			}
			
			ModelAndView mav = new ModelAndView("free_board/write");
			
			mav.addObject("dto", dto);
			mav.addObject("page", page);
			mav.addObject("mode", "update");
			
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/free_board/list?page=" + page);

	}
	
	@RequestMapping(value = "/free_board/update", method = RequestMethod.POST)
	public ModelAndView updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Free_BoardDAO dao = new Free_BoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		FileManager fileManager = new FileManager();
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "free_board";
		
		String page = req.getParameter("page");
		String num = req.getParameter("num");

		try {
			Free_BoardDTO dto = new Free_BoardDTO();

			dto.setNum(Integer.parseInt(num));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			dto.setMember_id(info.getUserId());
			
			Part p = req.getPart("selectFile");
			MyMultipartFile multiFile = fileManager.doFileUpload(p, pathname);
			if(multiFile != null) {
				if(req.getParameter("saveFileName").length() !=0 ) {
					fileManager.doFiledelete(pathname, req.getParameter("saveFileName"));
				}
				
				String saveFilename = multiFile.getSaveFilename();
				String uploadfilename = multiFile.getOriginalFilename();
				long size = multiFile.getSize();
				dto.setSaveFileName(saveFilename);
				dto.setUploadFileName(uploadfilename);
				dto.setFileSize(size);
			}
			
			dao.updateFree_board(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/free_board/article?page="+page+"&num="+num);
	}
	
	@RequestMapping(value = "/free_board/deleteFile")
	public ModelAndView deleteFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Free_BoardDAO dao = new Free_BoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		FileManager fileManager = new FileManager();
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "free_board";
		
		String page = req.getParameter("page");
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			Free_BoardDTO dto = dao.findById(num);
			if(dto == null) {
				return new ModelAndView("redirect:/free_board/list");
			}
			
			if (!info.getUserId().equals(dto.getMember_id())) {
				return new ModelAndView("redirect:/free_board/list");
			}
			
			fileManager.doFiledelete(pathname, dto.getSaveFileName());
			
			dto.setUploadFileName("");
			dto.setSaveFileName("");
			dto.setFileSize(0);
			dao.updateFree_board(dto);
			
			ModelAndView mav = new ModelAndView("free_board/write");
			
			mav.addObject("dto", dto);
			mav.addObject("page", page);
			
			mav.addObject("mode", "update");
			
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/free_board/list?page=" + page);
	}
	
	// 삭제
	@RequestMapping(value = "/free_board/delete", method = RequestMethod.GET)
	public ModelAndView delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Free_BoardDAO dao = new Free_BoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String page = req.getParameter("page");
		String query = "page=" + page;
		
		try {
			long num = Integer.parseInt(req.getParameter("num"));
			String schType = req.getParameter("schType");
			String kwd = req.getParameter("kwd");
			if (schType == null) {
				schType = "all";
				kwd = "";
			}
			kwd = URLDecoder.decode(kwd, "utf-8");

			if (kwd.length() != 0) {
				query += "&schType=" + schType + "&kwd=" + URLEncoder.encode(kwd, "UTF-8");
			}
			
			dao.deleteFree_board(num, info.getUserId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/free_board/list?" + query);
	}
	
	@RequestMapping(value = "/free_board/download")
	public void download(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Free_BoardDAO dao = new Free_BoardDAO();
		HttpSession session = req.getSession();
		
		FileManager fileManager = new FileManager();
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "free_board";
		
		boolean b = false;
		
		try {
			long num = Long.parseLong(req.getParameter("num"));

			Free_BoardDTO dto = dao.findById(num);
			if(dto != null) {
				b = fileManager.doFiledownload(dto.getSaveFileName(), dto.getUploadFileName(), pathname, resp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if ( ! b ) {
			resp.setContentType("text/html;charset=utf-8");
			PrintWriter out = resp.getWriter();
			out.print("<script>alert('파일다운로드가 실패 했습니다.');history.back();</script>");
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/free_board/insertFree_board_Like", method = RequestMethod.POST)
	// 게시글 공감 저장
	public Map<String, Object> insertFree_board_Like(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		
		Map<String, Object> model = new HashMap<String, Object>();
		
		Free_BoardDAO dao = new Free_BoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String state = "false";
		long free_boardLikeCount = 0;
		
		try {
			long num = Long.parseLong(req.getParameter("num"));
			String isNoLike = req.getParameter("isNoLike");
			
			if(isNoLike.equals("true")) {
				dao.insertFree_board_like(num, info.getUserId());
			} else {
				dao.deleteFree_board_like(num, info.getUserId());
			}
			
			free_boardLikeCount = dao.countFree_board_Like(num);
			
			state = "true";
			
			
		} catch (Exception e) {
		}
		
		model.put("state", state);
		model.put("free_boardLikeCount", free_boardLikeCount);
		
		return model;
	}
	
	// 댓글 저장
	@ResponseBody
	@RequestMapping(value = "/free_board/insertReply", method = RequestMethod.POST)
	public Map<String, Object> insertReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		
		Free_BoardDAO dao = new Free_BoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String state = "false";
		
		try {
			Free_Board_ReplyDTO dto = new Free_Board_ReplyDTO();
			
			int num = Integer.parseInt(req.getParameter("num"));
			dto.setNum(num);
			dto.setMember_id(info.getUserId());
			dto.setContent(req.getParameter("content"));
			String answer = req.getParameter("answer");
			if(answer != null) {
				dto.setAnswer(Long.parseLong(answer));
			}
			
			dao.insertReply(dto);
			
			state = "true";
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		model.put("state", state);
		
		return model;
	}
	
	@RequestMapping(value = "/free_board/listReply", method = RequestMethod.GET)
	public ModelAndView listReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Free_BoardDAO dao = new Free_BoardDAO();
		MyUtil util = new  MyUtilBootstrap();
		
		try {
			int num = Integer.parseInt(req.getParameter("num"));
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
			if(offset < 0) offset = 0;
			
			List<Free_Board_ReplyDTO> listReply = dao.listReply(num, offset, size);
			
			for(Free_Board_ReplyDTO dto : listReply) {
				dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
				dto.setContent(dto.getContent().replaceAll(" ", "&nbsp;"));
			}
			
			String paging = util.pagingMethod(current_page, total_page, "listPage");

			ModelAndView mav = new ModelAndView("free_board/listReply");

			
			int likeCount = dao.countReplyLike(num);
			mav.addObject("likeCount", likeCount);
			mav.addObject("listReply", listReply);
			mav.addObject("pageNo", current_page);
			mav.addObject("replyCount", replyCount);
			mav.addObject("total_page", total_page);
			mav.addObject("paging", paging);
			
			return mav;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/free_board/deleteReply", method = RequestMethod.POST)
	public Map<String, Object> deleteReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		
		Free_BoardDAO dao = new Free_BoardDAO();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		String state = "false";

		try {
			int r_num = Integer.parseInt(req.getParameter("r_num"));

			dao.deleteReply(r_num, info.getUserId());
			
			state = "true";
		} catch (Exception e) {
			e.printStackTrace();
		}

		model.put("state", state);

		return model;
	}
	
	@RequestMapping(value = "/free_board/listReplyAnswer", method = RequestMethod.GET)
	public ModelAndView listReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Free_BoardDAO dao = new Free_BoardDAO();
		
		try {
			long answer = Long.parseLong(req.getParameter("answer"));

			List<Free_Board_ReplyDTO> listReplyAnswer = dao.listReplyAnswer(answer);
			
			for(Free_Board_ReplyDTO dto : listReplyAnswer) {
				dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
				dto.setContent(dto.getContent().replaceAll(" ", "&nbsp;"));
			}
			
			ModelAndView mav = new ModelAndView("free_board/listReplyAnswer");
			mav.addObject("listReplyAnswer", listReplyAnswer);
			
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
			resp.sendError(400);
			throw e;
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/free_board/countReplyAnswer", method = RequestMethod.POST)
	public Map<String, Object> countReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Free_BoardDAO dao = new Free_BoardDAO();
		int count = 0;
		
		try {
			long answer = Long.parseLong(req.getParameter("answer"));
			count = dao.dataCountReplyAnswer(answer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("count", count);
		
		return model;
	}
	
	@ResponseBody
	@RequestMapping(value = "/free_board/insertReplyLike", method = RequestMethod.POST)
	public Map<String, Object> insertReplyLike(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		
		Free_BoardDAO dao = new Free_BoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String state = "false";
		int likeCount = 0;
		try {
			int r_num = Integer.parseInt(req.getParameter("r_num"));

			Free_Board_ReplyDTO dto = new Free_Board_ReplyDTO();
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
	
}
