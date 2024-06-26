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
import com.gallery.dao.Fan_BoardDAO;
import com.gallery.domain.Fan_BoardDTO;
import com.gallery.domain.Fan_Board_ReplyDTO;
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
public class Fan_BoardController {
	
	//리스트
	@RequestMapping(value = "/fan_board/list")
	public ModelAndView list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		// 팬 게시글 리스트
		// 파라미터 : 페이지 번호, 검색 컬럼, 검색 키워드
		
		ModelAndView mav = new ModelAndView("fan_board/list");

		Fan_BoardDAO dao = new Fan_BoardDAO();
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
			
			List<Fan_BoardDTO> list = null;
			if(kwd.length() ==0) {
				list = dao.listFan_Board(offset, size);
			}else {
				list = dao.listFan_Board(offset, size, schType, kwd);
			}

			String query = "";
			if(kwd.length() != 0) {
				query = "schType=" + schType + "&kwd=" + URLEncoder.encode(kwd,"utf-8");
			}
			
			// 페이징 처리
			String cp = req.getContextPath();
			String listUrl = cp + "/fan_board/list";
			String articleUrl = cp + "/fan_board/article?page="+ current_page;
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
	
	
	
	//글쓰기
	@RequestMapping(value = "/fan_board/write", method = RequestMethod.GET)
	public ModelAndView writeForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ModelAndView mav = new ModelAndView("fan_board/write");
		mav.addObject("mode", "write");
		return mav;
	}
	
	
	//글쓰기 완료
	@RequestMapping(value = "/fan_board/write", method = RequestMethod.POST)
	public ModelAndView writSubmit(HttpServletRequest req, HttpServletResponse resq) throws ServletException, IOException {
		// 글 저장 
		Fan_BoardDAO dao = new Fan_BoardDAO();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");
		
		FileManager fileManager = new FileManager();
		
		// 파일 저장 경로
		String root = session.getServletContext().getRealPath("/");
		String pathname = root + "uploads" + File.separator + "fan_board";
		
		try {
			Fan_BoardDTO dto = new Fan_BoardDTO();
			// userId는 세션에 저장된 정보
			dto.setMember_id(info.getUserId());
			
			// 파라미터
			dto.setNickname(req.getParameter("artistName"));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			Part p = req.getPart("selectFile");
			MyMultipartFile multFile = fileManager.doFileUpload(p, pathname);
			if(multFile != null) {
				String saveFilename = multFile.getSaveFilename();
				String uploadfilename = multFile.getSaveFilename();
				long size = multFile.getSize();
				dto.setSaveFilename(saveFilename);
				dto.setUploadfilename(uploadfilename);
				dto.setFileSize(size);
			}
			dao.insertFan_Board(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/fan_board/list");
	}
	

	//게시물의 상세페이지
	@RequestMapping(value = "/fan_board/article", method = RequestMethod.GET)
	public ModelAndView article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Fan_BoardDAO dao = new Fan_BoardDAO();
		
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
			dao.updateHitCount(num);
			
			// 게시물 가져오기 
			Fan_BoardDTO dto = dao.findById(num);
			if( dto == null ) {
				return new ModelAndView("redirect:/fan_board/list?" + query);
			}
			
			// 이전글 다음글
			Fan_BoardDTO prevDto = dao.findByPrev(dto.getNum(), schType, kwd);
			Fan_BoardDTO nextDto = dao.findByNext(dto.getNum(), schType, kwd);
			
			// 삭제할수있음
			HttpSession session = req.getSession();
			SessionInfo info = (SessionInfo)session.getAttribute("member");
			boolean isUserLike = dao.isUserFan_BoardLike(num, info.getUserId());
			
			ModelAndView mav = new ModelAndView("fan_board/article");
			
			mav.addObject("dto", dto);
			mav.addObject("page", page);
			mav.addObject("query", query);
			mav.addObject("prevDto", prevDto);
			mav.addObject("nextDto", nextDto);
			
			mav.addObject("isUserLike", isUserLike);
			
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new ModelAndView("redirect:/fan_board/list?" + query);
	}
	
		
	//수정해야댐//////////////////////////////////////////////

		@RequestMapping(value = "/fan_board/update", method = RequestMethod.GET)
		public ModelAndView updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			
			// 수정 폼
			// 파라미터 : 글 번호 [, 페이지 번호, 세션: 사용자정보]
			
			Fan_BoardDAO dao = new Fan_BoardDAO();
			
			HttpSession session = req.getSession();
			SessionInfo info = (SessionInfo) session.getAttribute("member");
			
			String page = req.getParameter("page");
			
			try {
				long num = Long.parseLong(req.getParameter("num"));
				Fan_BoardDTO dto = dao.findById(num);
				
				if(dto == null) {
					return new ModelAndView("redirect:/fan_board/list?page=" + page);
				}
				
				if(! dto.getMember_id().equals(info.getUserId())) {
					return new ModelAndView("redirect:/fan_board/list?page=" + page);
				}
				
				ModelAndView mav = new ModelAndView("fan_board/write");
				
				mav.addObject("dto", dto);
				mav.addObject("page", page);
				mav.addObject("mode", "update");
				
				return mav;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return new ModelAndView("redirect:/fan_board/list?page=" + page);
		}
		
		
		
		
		@RequestMapping(value = "/fan_board/update", method = RequestMethod.POST)
		public ModelAndView updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			// 수정 완료
			
			Fan_BoardDAO dao = new Fan_BoardDAO();
			
			HttpSession session = req.getSession();
			SessionInfo info = (SessionInfo)session.getAttribute("member");
			
			FileManager fileManager = new FileManager();
			
			// 파일 저장 경로
			String root = session.getServletContext().getRealPath("/");
			String pathname = root + "uploads" + File.separator + "fan_board";
			
			String page = req.getParameter("page");
			
			try {
				Fan_BoardDTO dto = new Fan_BoardDTO();
				
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
				dao.updateFan_Board(dto);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return new ModelAndView("redirect:/fan_board/list?page=" + page);
		}
		
		
		
		
		
		@RequestMapping(value = "/fan_board/deleteFile", method = RequestMethod.GET)
		public ModelAndView deleteFile(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			// 수정에서 파일만 삭제
			Fan_BoardDAO dao = new Fan_BoardDAO();
			
			HttpSession session = req.getSession();
			SessionInfo info = (SessionInfo)session.getAttribute("member");
			
			FileManager fileManager = new FileManager();
			
			String root = session.getServletContext().getRealPath("/");
			String pathname = root + "uploads" + File.separator + "fan_board";
			
			String page = req.getParameter("page");
			
			try {
				long num = Long.parseLong(req.getParameter("num"));
				Fan_BoardDTO dto = dao.findById(num);
				if(dto == null) {
					return new ModelAndView("redirect:/fan_board/list?page=" + page);
				}
				
				if (!info.getUserId().equals(dto.getMember_id())) {
					return new ModelAndView("redirect:/fan_board/list?page=" + page);
				}
				
				fileManager.doFiledelete(pathname, dto.getSaveFilename());
				
				dto.setUploadfilename("");
				dto.setSaveFilename("");
				dto.setFileSize(0);
				dao.updateFan_Board(dto);
				
				ModelAndView mav = new ModelAndView("fan_board/write");
				
				mav.addObject("dto", dto);
				mav.addObject("page", page);
	
				mav.addObject("mode", "update");
	
				return mav;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return new ModelAndView("redirect:/fan_board/list?page=" + page);
		}
		
		// 삭제
		@RequestMapping(value = "/fan_board/delete", method = RequestMethod.GET)
		public ModelAndView delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			
			Fan_BoardDAO dao = new Fan_BoardDAO();
			
			HttpSession sessoion = req.getSession();
			SessionInfo info = (SessionInfo) sessoion.getAttribute("member");
			
			FileManager fileManager = new FileManager();
			
			String root = sessoion.getServletContext().getRealPath("/");
			String pathname = root + "uploads" + File.separator + "fan_board";
			
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
				
				Fan_BoardDTO dto = dao.findById(num);
				if(dto==null) {
					return new ModelAndView("redirect:/fan_board/list? " + query);
				}
				
				if(!info.getUserId().equals(dto.getMember_id()) && !info.getUserId().equals("admin")) {
					return new ModelAndView("redirect:/fan_board/list?" + query);
				}
				if (dto.getSaveFilename() != null && dto.getSaveFilename().length() != 0) {
					fileManager.doFiledelete(pathname, dto.getSaveFilename());
				}
				
				dao.deleteFan_Board(num, info.getUserId());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new ModelAndView("redirect:/fan_board/list?" + query);
		}
	
		// 파일 다운로드
		@RequestMapping(value = "/fan_board/download")
		public void download(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			
			Fan_BoardDAO dao = new Fan_BoardDAO();
			HttpSession session = req.getSession();
			
			FileManager fileManager = new FileManager();
			
			String root = session.getServletContext().getRealPath("/");
			String pathname = root + "uploads" + File.separator + "fan_board";
			
			boolean b = false;
			
			try {
				long num = Long.parseLong(req.getParameter("num"));
				
				Fan_BoardDTO dto = dao.findById(num);
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
		
		// 게시글 공감 저장 - AJAX : JSON
		@ResponseBody
		@RequestMapping(value = "/fan_board/insertFan_BoardLike", method = RequestMethod.POST)
		public Map<String, Object> insertfan_boardLike(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			Map<String, Object> model = new HashMap<String, Object>();
			
			Fan_BoardDAO dao = new Fan_BoardDAO();
			
			HttpSession session = req.getSession();
			SessionInfo info = (SessionInfo) session.getAttribute("member");
			
			String state = "false";
			int likeCount = 0;
			
			try {
				long num = Long.parseLong(req.getParameter("num"));
				String isNoLike = req.getParameter("isNoLike");
				
				if(isNoLike.equals("true")) {
					dao.insertFan_BoardLike(num, info.getUserId()); // 영진아... 누나 힘들어.. 이거 왜 잘못했냐구 ........... 임뫄...
				}else {
					dao.deleteFan_BoardLike(num, info.getUserId());
				}
				likeCount = dao.countFan_BoardLike(num);
				
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
	
		// 리플 리스트 
		@RequestMapping(value = "/fan_board/listReply", method = RequestMethod.GET)
		public ModelAndView listReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			Fan_BoardDAO dao = new Fan_BoardDAO();
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
				
				int offset = (current_page -1 ) * size; 
				if(offset < 0) offset = 0;
				
				List<Fan_Board_ReplyDTO> listReply = dao.listReply(num, offset, size);
				
				for(Fan_Board_ReplyDTO dto : listReply) {
					dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
				}
				
				String paging = util.pagingMethod(current_page, total_page, "listPage");
				
				ModelAndView mav = new ModelAndView("fan_board/listReply");
				
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
		@RequestMapping(value = "/fan_board/insertReply", method = RequestMethod.POST)
		public Map<String, Object> insertReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			
			Map<String, Object> model = new HashMap<String, Object>();
			
			Fan_BoardDAO dao = new Fan_BoardDAO();
			
			HttpSession session = req.getSession();
			SessionInfo info = (SessionInfo) session.getAttribute("member");
			
			String state = "false";
			
			try {
				Fan_Board_ReplyDTO dto = new Fan_Board_ReplyDTO();
				
				long num = Long.parseLong(req.getParameter("num"));
				dto.setNum(num);
				dto.setMember_id(info.getUserId());
				
				dto.setContent(req.getParameter("content"));
				String answer = req.getParameter("answer");
				if(answer!=null) {
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
		
		// 리플 또는 답글 삭제
		@ResponseBody
		@RequestMapping(value = "/fan_board/deleteReply", method = RequestMethod.POST)
		public Map<String, Object> deleteReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			Map<String, Object> model = new HashMap<String, Object>();
			
			Fan_BoardDAO dao = new Fan_BoardDAO();
	
			HttpSession session = req.getSession();
			SessionInfo info = (SessionInfo) session.getAttribute("member");
			String state = "false";
	
			try {
				long r_num = Long.parseLong(req.getParameter("r_num"));
	
				dao.deleteReply(r_num , info.getUserId());
				
				state = "true";
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			model.put("state", state);
			
			return model;
		}
	
		// 리플의 답글 리스트 - AJAX:TEXT
		@RequestMapping(value = "/fan_board/listReplyAnswer", method = RequestMethod.GET)
		public ModelAndView listReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			Fan_BoardDAO dao = new Fan_BoardDAO();
	
			try {
				long answer = Long.parseLong(req.getParameter("answer"));
				
	
				List<Fan_Board_ReplyDTO> listReplyAnswer = dao.listReplyAnswer(answer);
	
				for (Fan_Board_ReplyDTO dto : listReplyAnswer) {
					dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
				}
	
				ModelAndView mav = new ModelAndView("fan_board/listReplyAnswer");
				mav.addObject("listReplyAnswer", listReplyAnswer);
	
				return mav;
			} catch (Exception e) {
				e.printStackTrace();
				resp.sendError(400);
				throw e;
			}
		}
	
		// 리플의 답글 개수 
		@ResponseBody
		@RequestMapping(value = "/fan_board/countReplyAnswer", method = RequestMethod.POST)
		public Map<String, Object> countReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			Fan_BoardDAO dao = new Fan_BoardDAO();
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
		@RequestMapping(value = "/fan_board/insertReplyLike", method = RequestMethod.POST)
		public Map<String, Object> insertReplyLike(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			Map<String, Object> model = new HashMap<String, Object>();
			
			Fan_BoardDAO dao = new Fan_BoardDAO();
			
			HttpSession session = req.getSession();
			SessionInfo info = (SessionInfo)session.getAttribute("member");
			
			String state = "false";
			int likeCount = 0;
		
			try {
				long r_num = Long.parseLong(req.getParameter("r_num"));
				
				Fan_Board_ReplyDTO dto = new Fan_Board_ReplyDTO();
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
