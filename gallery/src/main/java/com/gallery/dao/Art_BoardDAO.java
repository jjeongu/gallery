package com.gallery.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gallery.domain.Art_BoardDTO;
import com.gallery.domain.Art_Board_ReplyDTO;
import com.gallery.util.DBConn;
import com.gallery.util.DBUtil;

public class Art_BoardDAO {
	private Connection conn = DBConn.getConnection();
	
	// 작가 게시판 등록 
	public void insertArt_Board(Art_BoardDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO Art_Board(num , member_id, notice , subject, content, saveFilename, uploadfilename, filesize, "
					+ " hitcount, reg_date) "
					+ " VALUES(Art_Board_seq.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, 0, SYSDATE)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getMember_id());
			pstmt.setInt(2, dto.getNotice());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());
			pstmt.setString(5, dto.getSaveFilename());
			pstmt.setString(6, dto.getUploadfilename());
			pstmt.setLong(7, dto.getFileSize());
			
			pstmt.executeUpdate();
					
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	// 데이터 개수
	public int dataCount() {
		
		int result =0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*),0) FROM art_board";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = rs.getInt(1);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		return result;
	}
	
	// 검색에서의 데이터 개수
	public int dataCount(String schType, String kwd) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*),0) "
					+ " FROM art_board ab "
					+ " JOIN Art a ON ab.member_id = a.member_id ";
			if(schType.equals("all")) {
				sql += " WHERE INSTR(subject, ? ) >= 1 OR INSTR(content, ?) >= 1 ";
			} else if ( schType.equals("reg_date")) {
				kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
				sql += " WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ? ";
			} else {
				sql += " WHERE INSTR(" + schType + ", ? ) >= 1 ";
			}
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, kwd);
			if(schType.equals("all")) {
				pstmt.setString(2, kwd);
			}
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = rs.getInt(1);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return result;
	}
	
	// 게시물 리스트
	public List<Art_BoardDTO> listArt_Board(int offset, int size) {
		List<Art_BoardDTO> list = new ArrayList<Art_BoardDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT num, ab.member_id,name ,subject, hitCount, ");
			sb.append(" TO_CHAR(ab.reg_date, 'YYYY-MM-DD') reg_date, saveFilename " );
			sb.append(" FROM art_board ab ");
			sb.append(" JOIN Art a ON ab.member_id = a.member_id ");
			sb.append(" JOIN member1 m ON m.member_id = ab.member_id ");
			sb.append(" ORDER BY num DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROW ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				Art_BoardDTO dto = new Art_BoardDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setMember_id(rs.getString("member_id"));;
				dto.setName(rs.getString("name"));
				dto.setSubject(rs.getString("subject"));
				dto.setSaveFilename(rs.getString("saveFilename"));
				dto.setHitcount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				
				list.add(dto);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return list;
	}
	
	
	public List<Art_BoardDTO> listArt_Board(int offset, int size, String schType, String kwd) {
		List<Art_BoardDTO> list = new ArrayList<Art_BoardDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT num, ab.member_id, name, subject, hitCount, ");
			sb.append(" TO_CHAR(ab.reg_date, 'YYYY-MM-DD') reg_date ");
			sb.append(" FROM art_board ab");
			sb.append(" JOIN Art a ON ab.member_id = a.member_id ");
			sb.append(" JOIN member1 m ON m.member_id = ab.member_id ");
			if( schType.equals("all")) {
				sb.append(" WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1");
			} else if ( schType.equals("reg_date")) {
				kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
				sb.append(" WHERE TO_CHAR(ab.reg_date, 'YYYYMMDD') = ?");
			} else {
				sb.append( " WHERE INSTR(" + schType + ", ?) >= 1 ");
			}
			sb.append(" ORDER BY num DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			if(schType.equals("all")) {
				pstmt.setString(1, kwd);
				pstmt.setString(2, kwd);
				pstmt.setInt(3, offset);
				pstmt.setInt(4, size);
			} else {
				pstmt.setString(1, kwd);
				pstmt.setInt(2, offset);
				pstmt.setInt(3, size);
			}
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				Art_BoardDTO dto = new Art_BoardDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setMember_id(rs.getString("member_id"));
				dto.setName(rs.getString("name"));
				dto.setSubject(rs.getString("subject"));
				dto.setHitcount(rs.getInt("hitcount"));
				dto.setReg_date(rs.getString("reg_date"));
				
				list.add(dto);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return list;
	}
	
	// 조회수 증가하기 
	public void updatHitCount(long num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			
			sql = "UPDATE art_board SET hitcount = hitcount+1 WHERE num = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	// 해당 게시판 보기
	public Art_BoardDTO findById(long num) {
		Art_BoardDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT ab.num, ab.member_id, name, subject, content, saveFilename, uploadfilename, filesize, ab.reg_date, hitcount, "
					+ " NVL(likeCount, 0) likeCount "
					+ " FROM art_board ab "
					+ " JOIN art a ON ab.member_id = a.member_id "
					+ " JOIN member1 m ON m.member_id = ab.member_id "
					+ " LEFT OUTER JOIN ( "
					+ " SELECT num, COUNT(*) likeCount FROM Art_Board_Like "
					+ "	GROUP BY num "
					+ " ) ac ON ab.num = ac.num" 
					+ " WHERE ab.num = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				dto = new Art_BoardDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setMember_id(rs.getString("member_id"));
				dto.setName(rs.getString("name"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setSaveFilename(rs.getString("saveFilename"));
				dto.setUploadfilename(rs.getString("uploadfilename"));
				dto.setFileSize(rs.getLong("fileSize"));
				dto.setHitcount(rs.getInt("hitcount"));
				dto.setReg_date(rs.getString("reg_date"));
				
				dto.setLikeCount(rs.getInt("likeCount"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		return dto;
	}
	
	// 이전글
	public Art_BoardDTO findByPrev(long num, String schType, String kwd) {
		Art_BoardDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			if (kwd != null && kwd.length() != 0 ) {
				sb.append(" SELECT num, subject ");
				sb.append(" FROM art_board ab ");
				sb.append(" JOIN art a ON ab.member_id = a.member_id ");
				sb.append(" WHERE num > ?" );
				if (schType.equals("all")) {
					sb.append(" AND (INSTR(subject, ? ) >= 1 OR INSTR(content, ? ) >= 1 ");
				} else if ( schType.equals("reg_date")) {
					kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
					sb.append(" AND TO_CHAR(reg_date, 'YYYYMMDD') = ? " );
				} else {
					sb.append(" AND (INSTR(" + schType + ", ?) >= 1) ");
				} 
				sb.append(" ORDER BY num ASC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");
					
				pstmt = conn.prepareStatement(sb.toString());
					
				pstmt.setLong(1, num);
				pstmt.setString(2, kwd);
				if(schType.equals("all")) {
					pstmt.setString(3, kwd);
				}
			} else {
				sb.append(" SELECT num, subject ");
				sb.append(" FROM art_board");
				sb.append(" WHERE num > ? ");
				sb.append(" ORDER BY num ASC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, num);
			}
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				dto = new Art_BoardDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setSubject(rs.getString("subject"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return dto;
	}
	
	// 다음글
	public Art_BoardDTO findByNext(long num , String schType, String kwd ) {
		Art_BoardDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			
			if(kwd != null && kwd.length() != 0) {
				sb.append(" SELECT num, subject ");
				sb.append(" FROM art_board ab ");
				sb.append(" JOIN art a ON ab.member_id = a.member_id ");
				sb.append(" WHERE num < ? ");
				if( schType.equals("all")) {
					sb.append(" AND( INSTR(subject, ?) >= 1 OR INSTR(content, ? ) >= 1 ) ");
				} else if (schType.equals("reg_date")) {
					kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
					sb.append("   AND TO_CHAR(reg_date, 'YYYYMMDD') = ? ");
				} else {
					sb.append("   AND INSTR(" + schType + ", ?) >= 1 ");
				}
				sb.append(" ORDER BY num DESC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");
				
				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, num);
				pstmt.setString(2, kwd);
				if(schType.equals("all")) {
					pstmt.setString(3, kwd);
				}
			} else {
				sb.append(" SELECT num, subject FROM art_board ");
				sb.append(" WHERE num < ? ");
				sb.append(" ORDER BY num DESC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");

				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, num);
			}

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new Art_BoardDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setSubject(rs.getString("subject"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		return dto;
	}
	
	// 수정
	public void updateArt_Board(Art_BoardDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "UPDATE art_board SET subject=?, content=?, "
					+ " saveFilename=?, uploadfilename=?, filesize=? WHERE num =? AND member_id=? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setString(3, dto.getSaveFilename());
			pstmt.setString(4, dto.getUploadfilename());
			pstmt.setLong(5, dto.getFileSize());
			pstmt.setLong(6, dto.getNum());
			pstmt.setString(7, dto.getMember_id());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	// 게시판 삭제
	public void deleteArt_Board(long num, String member_id) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			if (member_id.equals("admin")) {
				sql = "DELETE FROM art_board WHERE num =? ";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setLong(1, num);
				
				pstmt.executeUpdate();
			}else {
				sql = "DELETE FROM art_board WHERE num =? AND member_id = ?" ;
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setLong(1, num);
				pstmt.setString(2, member_id);
				
				pstmt.executeUpdate();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	// 로그인 유저의 게시판 공감 유무 
	public boolean isUserArt_BoardLike(long num, String member_id) {
		
		boolean result = false;
		PreparedStatement pstmt = null;
		ResultSet rs = null ;
		String sql;
		
		try {
			sql = "SELECT num, member_id "
					+ " FROM Art_Board_Like "
					+ " WHERE num = ? AND member_id = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			pstmt.setString(2, member_id);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		return result;
	}
	
	// 게시판 공감 추가 
	public void insertArt_BoardLike(long num, String member_id) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO Art_Board_Like(num, member_id) VALUES (?,?)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			pstmt.setString(2, member_id);
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}finally {
			DBUtil.close(pstmt);
		}
	}
	
	// 게시판 공감 삭제
	public void deleteArt_BoardLike(long num, String member_id) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "DELETE FROM Art_Board_Like WHERE num = ? AND member_id = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			pstmt.setString(2, member_id);
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	// 게시판 공감 개수
	public int countArt_BoardLike(long num) {
		
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql ="SELECT NVL(COUNT(*),0) FROM Art_Board_Like WHERE num =? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return result;
	}
	
	// 게시판 댓글 및 답글 추가
	public void insertReply(Art_Board_ReplyDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO Art_Board_Reply(r_num, num, member_id, content, answer, reg_Date)"
					+ " VALUES (Art_Board_Reply_seq.NEXTVAL,?,?,?,?, SYSDATE)";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, dto.getNum());
			pstmt.setString(2, dto.getMember_id());
			pstmt.setString(3,dto.getContent());
			pstmt.setLong(4, dto.getAnswer());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}finally {
			 DBUtil.close(pstmt);
		}
	}
	// 게시판 댓글 개수
	public int dataCountReply(long num) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*),0)  "
					+ " FROM Art_Board_Reply "
					+ " WHERE num = ? AND answer = 0 "; 
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
			rs=pstmt.executeQuery();
			if(rs.next()) {
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return result;
	}
	
	// 게시판 댓글 리스트
	public List<Art_Board_ReplyDTO> listReply(long num, int offset, int size) {
		List<Art_Board_ReplyDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT r.r_num, r.member_id, r.num,name, r.content, r.reg_date, ");
			sb.append(" NVL(a.answerCount,0) answerCount, a.answer, NVL(likeCount, 0) likeCount ");
			sb.append(" FROM Art_Board_Reply r ");
			sb.append(" JOIN Member1 m ON r.member_id = m.member_id");
			sb.append(" LEFT OUTER JOIN ( ");
			sb.append(" 	SELECT answer, COUNT(*) answerCount ");
			sb.append(" 	FROM Art_Board_Reply ");
			sb.append(" 	WHERE answer != 0 ");
			sb.append(" GROUP BY answer ");
			sb.append(" ) a ON r.r_num = a.answer ");
			
			sb.append(" LEFT OUTER  JOIN ( ");
			sb.append(" SELECT r_num, COUNT(*) likeCount ");
			sb.append(" from Art_Board_Reply_Like ");
			sb.append(" GROUP BY r_num ");
			sb.append(" ) b ON r.r_num = b.r_num ");
			
			sb.append(" WHERE r.num =? AND r.answer = 0");
			sb.append(" ORDER BY r.r_num DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");
			
			/*
			 * 			sb.append(" LEFT OUTER  JOIN ( ");
			sb.append("	    SELECT replyNum, ");
			sb.append("         COUNT(DECODE(replyLike, 1, 1)) likeCount, ");
			sb.append("         COUNT(DECODE(replyLike, 0, 1)) disLikeCount ");
			sb.append("     FROM bbsReplyLike ");
			sb.append("     GROUP BY replyNum ");
			sb.append(" ) b ON r.replyNum = b.replyNum  ");
			 */
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, num);
			pstmt.setInt(2, offset);
			pstmt.setInt(3, size);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				Art_Board_ReplyDTO dto = new Art_Board_ReplyDTO();
				
				dto.setR_num(rs.getLong("r_num"));
				dto.setNum(rs.getLong("num"));
				dto.setName(rs.getString("name"));
				dto.setMember_id(rs.getString("member_id"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setAnswer(rs.getLong("answer"));
				dto.setAnswerCount(rs.getInt("answerCount"));
				dto.setLikeCount(rs.getInt("likeCount"));
				
				list.add(dto);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		return list;
	}
	
	public Art_Board_ReplyDTO readReply(long r_num) {
		Art_Board_ReplyDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " SELECT r_num, num, r.member_id, name, content, r.reg_Date "
					+ " FROM Art_Board_Reply r"
					+ " JOIN member1 m ON r.member_id = m.member_id "
					+ " WHERE r_num = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, r_num);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new Art_Board_ReplyDTO();
				
				dto.setR_num(rs.getLong("r_num"));
				dto.setNum(rs.getLong("num"));
				dto.setMember_id(rs.getString("member_id"));
				dto.setName(rs.getString("name"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		return dto;
	}
	
	// 게시판 댓글 삭제
	public void deleteReply(long r_num, String member_id) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		if(! member_id.equals("admin")) {
			Art_Board_ReplyDTO dto = readReply(r_num);
			if(dto==null || (! member_id.equals(dto.getMember_id()))) {
				return;
			}
		}
		try {
			sql ="DELETE FROM Art_Board_Reply_like "
					+ " WHERE r_Num IN "
					+ " (SELECT r_Num FROM Art_Board_Reply START WITH r_Num = ?  "
					+ " CONNECT BY PRIOR r_Num = answer) ";

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, r_num);
			
			pstmt.executeUpdate();
			DBUtil.close(pstmt);
			
			sql ="DELETE FROM Art_Board_Reply where r_num = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, r_num);
			
			pstmt.executeUpdate();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	// 댓글의 답글 리스트
	public List<Art_Board_ReplyDTO> listReplyAnswer(long answer) {
		List<Art_Board_ReplyDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " SELECT r_num, num, r.member_id, name,content, r.reg_date, answer "
					+ " FROM Art_Board_Reply r "
					+ " JOIN member1 m ON r.member_id = m.member_id "
					+ " WHERE answer = ? "
					+ " ORDER BY r_num DESC ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, answer);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				Art_Board_ReplyDTO dto = new Art_Board_ReplyDTO();
				
				dto.setR_num(rs.getLong("r_num"));
				dto.setNum(rs.getLong("num"));
				dto.setMember_id(rs.getString("member_id"));
				dto.setName(rs.getString("name"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setAnswer(rs.getLong("answer"));
				
				list.add(dto);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return list;
	}
	
	// 댓글의 답글 개수
		public int dataCountReplyAnswer(long answer) {
			int result = 0;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql;
			
			try {
				sql = "SELECT NVL(COUNT(*), 0) FROM Art_Board_Reply WHERE answer = ?";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setLong(1, answer);
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					result = rs.getInt(1);
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DBUtil.close(rs);
				DBUtil.close(pstmt);
			}
			
			return result;
		}
		
		
		public void insertReplyLike(Art_Board_ReplyDTO dto) throws SQLException {
			PreparedStatement pstmt = null;
			String sql;
			
			try {
				sql = "INSERT INTO Art_Board_Reply_Like(r_num, member_id) VALUES (?, ?)";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setLong(1, dto.getR_num());
				pstmt.setString(2, dto.getMember_id());
				
				pstmt.executeUpdate();
				
			} catch (SQLException e) {
				if(e.getErrorCode() == 1) {
					throw e;
				}
				e.printStackTrace();
			} finally {
				DBUtil.close(pstmt);
			}		
		}
		
		public int countReplyLike(long r_num) {
			int count = 0;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql;
			
			try {
				sql = " SELECT NVL(COUNT(*), 0) likeCount FROM Art_Board_Reply_Like WHERE r_num = ? ";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setLong(1, r_num);				
				rs = pstmt.executeQuery();
				
				if(rs.next()) {
					count = rs.getInt("likeCount");
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DBUtil.close(rs);
				DBUtil.close(pstmt);
			}
			
			return count;
		}
		
	}


