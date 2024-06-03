package com.gallery.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gallery.domain.Free_BoardDTO;
import com.gallery.domain.Free_Board_ReplyDTO;
import com.gallery.util.DBConn;
import com.gallery.util.DBUtil;

import jakarta.servlet.ServletException;
import oracle.jdbc.proxy.annotation.Pre;

public class Free_BoardDAO {
	private Connection conn = DBConn.getConnection();
	
	// 입력
	public void insertFree_Board(Free_BoardDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = " insert into Free_Board(num, member_id, hitcount, reg_date, notice, subject, content, saveFileName, uploadFileName, fileSize) "
					+ " values(Free_Board_seq.NEXTVAL, ?, 0, sysdate, ?, ?, ?, ?, ?, ?) ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getMember_id());
			pstmt.setInt(2, dto.getNotice());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());
			pstmt.setString(5, dto.getSaveFileName());
			pstmt.setString(6, dto.getUploadFileName());
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
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " select nvl(count(*),0) from free_board ";
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
	// 검색
	public int dataCount(String schType, String kwd) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " select nvl(count(*),0)"
					+ " from free_board f "
					+ " join member1 m on f.member_id = m.member_id ";
			if(schType.equals("all")) {
				sql += "  WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ";
			} else if (schType.equals("reg_date")) {
				kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
				sql += "  WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ? ";
			} else {
				sql += "  WHERE INSTR(" + schType + ", ?) >= 1 ";
			}

			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, kwd);
			if (schType.equals("all")) {
				pstmt.setString(2, kwd);
			}

			rs = pstmt.executeQuery();
			
			if (rs.next()) {
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
	
	// 리스트
	public List<Free_BoardDTO> listFree_Board(int offset, int size) {
		List<Free_BoardDTO> list = new ArrayList<Free_BoardDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" select num, f.member_id, subject, hitCount, ");
			sb.append(" to_char(f.reg_date, 'YYYY-MM-DD') reg_date ");
			sb.append(" from free_board f");
			sb.append(" join member1 m on f.member_id=m.member_id ");
			sb.append(" order by num desc ");
			sb.append(" offset ? rows fetch first ? rows only ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				Free_BoardDTO dto = new Free_BoardDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setMember_id(rs.getString("member_id"));
				dto.setSubject(rs.getString("subject"));
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
	
	public List<Free_BoardDTO> listFree_Board(int offset, int size, String schType, String kwd) {
		List<Free_BoardDTO> list = new ArrayList<Free_BoardDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" select num, f.member_id, subject, hitCount, ");
			sb.append(" to_char(f.reg_date, 'YYYY-MM-DD') reg_date ");
			sb.append(" from free_board f");
			sb.append(" join member1 m on f.member_id=m.member_id ");
			if (schType.equals("all")) {
				sb.append(" WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ");
			} else if (schType.equals("reg_date")) {
				kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
				sb.append(" WHERE TO_CHAR(f.reg_date, 'YYYYMMDD') = ?");
			} else {
				sb.append(" WHERE INSTR(" + schType + ", ?) >= 1 ");
			}
			sb.append(" order by num desc ");
			sb.append(" offset ? rows fetch first ? rows only ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			if (schType.equals("all")) {
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
				Free_BoardDTO dto = new Free_BoardDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setMember_id(rs.getString("member_id"));
				dto.setSubject(rs.getString("subject"));
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
	
	// 조회수
	public void updateHitCount(long num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE free_board SET hitCount=hitCount+1 WHERE num=?";
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
	
	// 게시물 보기
	public Free_BoardDTO findById(long num) {
		Free_BoardDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " select f.num, f.member_id, hitCount, f.reg_date, subject, content,"
					+ " saveFileName, uploadFileName, fileSize "
					+ " from free_board f  "
					+ " join member1 m on f.member_id = m.member_id"
					+ " where f.num = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new Free_BoardDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setMember_id(rs.getString("member_id"));
				dto.setHitcount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setSaveFileName(rs.getString("saveFileName"));
				dto.setUploadFileName(rs.getString("uploadFileName"));
				dto.setFileSize(rs.getLong("fileSize"));
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return dto;
	}
	
	// 이전글
	public Free_BoardDTO findByPrev(long num, String schType, String kwd ) {
		Free_BoardDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			if(kwd != null && kwd.length() != 0) {
				sb.append(" select f.num, sunject ");
				sb.append(" from free_board f ");
				sb.append(" join member1 m on f.member_id = m.member_id ");
				sb.append(" where (num > ? )");
				if (schType.equals("all")) {
					sb.append("   AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
				} else if (schType.equals("reg_date")) {
					kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
					sb.append("   AND TO_CHAR(reg_date, 'YYYYMMDD') = ? ");
				} else {
					sb.append("   AND INSTR(" + schType + ", ?) >= 1 ");
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
				sb.append(" FROM free_board ");
				sb.append(" WHERE num > ? ");
				sb.append(" ORDER BY num ASC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");
				
				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, num);
			}
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new Free_BoardDTO();
				
				dto.setNum(rs.getInt("num"));
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
	
	// 다음글
	public Free_BoardDTO findByNext(long num, String schType, String kwd ) {
		Free_BoardDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			if(kwd != null && kwd.length() != 0) {
				sb.append(" select f.num, sunject ");
				sb.append(" from free_board f ");
				sb.append(" join member1 m on f.member_id = m.member_id ");
				sb.append(" where (num < ?) ");
				if (schType.equals("all")) {
					sb.append("   AND ( INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ) ");
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
				sb.append(" SELECT num, subject FROM free_board ");
				sb.append(" WHERE num < ? ");
				sb.append(" ORDER BY num DESC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");
				
				pstmt = conn.prepareStatement(sb.toString());
				
				pstmt.setLong(1, num);
			}
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new Free_BoardDTO();
				
				dto.setNum(rs.getInt("num"));
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
	public void updateFree_board(Free_BoardDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE free_board SET subject=?, content=? WHERE num=? AND member_id=?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setLong(3, dto.getNum());
			pstmt.setString(4, dto.getMember_id());
			
			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}

	}
	
	// 삭제
	public void deleteFree_board(long num, String member_id) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			if(member_id.equals("admin")) {
				sql = " delete from free_board where num =? ";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setLong(1, num);
				
				pstmt.executeUpdate();
			} else {
				sql = " delete from free_board where num = ? and member_id = ? ";
				
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
	
	public boolean isUserFree_boardLike(long num, String member_id) {
		boolean result = false;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " select num, member_id from free_board_like where num = ? and member_id = ? ";
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
	
	public void insertFree_board_like(long num, String member_id) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = " insert into free_board_like(num, member_id) values(?,?) ";
			
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
	
	public void deleteFree_board_like(long num, String member_id) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = " delete from free_board_like where num = ? and member_id = ? ";
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
	
	public int  countFree_board_Like(int num) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " select nvl(count(*),0) from free_board_like where num = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
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
	
	public void insertReply(Free_Board_ReplyDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = " insert into free_board_reply(r_num, num, content, reg_date) "
					+ " values(free_board_reply_seq.NEXTVAL, ?, ?, sysdate) ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, dto.getNum());
			pstmt.setString(2, dto.getContent());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public int dataCountReply(long num) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*), 0) "
					+ " FROM free_board_reply "
					+ " WHERE num = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
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
	
	public List<Free_Board_ReplyDTO> listReply(long num, int offset, int size) {
		List<Free_Board_ReplyDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" select r_num, num, content, reg_date, f.member_id ");
			sb.append(" from free_board_reply f ");
			sb.append(" join member1 m on f.member_id = m.member_id ");
			sb.append(" where num = ? ");
			sb.append(" ORDER BY r_num DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, num);
			pstmt.setInt(2, offset);
			pstmt.setInt(3, size);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				Free_Board_ReplyDTO dto = new Free_Board_ReplyDTO();
				
				dto.setR_num(rs.getInt("r_num"));
				dto.setNum(rs.getInt("num"));
				dto.setContent(rs.getString("content"));
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
}
