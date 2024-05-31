package com.gallery.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gallery.domain.Free_BoardDTO;
import com.gallery.util.DBConn;
import com.gallery.util.DBUtil;

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
					+ " join member m on f.member_id = m.member_id ";
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
			sb.append(" select f.num, member_id, subject, hitCount, ");
			sb.append(" to_char(reg_date, 'YYYY-MM-DD') reg_date, ");
			sb.append(" nvl(replyCount, 0) replyCount ");
			sb.append(" from free_board f ");
			sb.append(" join member1 m on f.member_id = m.member_id ");
			sb.append(" left outer join( ");
			sb.append(" select num, count(*) replyCount ");
			sb.append(" from free_board_reply ");
			sb.append(" where answer = 0 ");
			sb.append(" group by num ");
			sb.append(" ) c on f.num = c.num ");
			if (schType.equals("all")) {
				sb.append(" WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1 ");
			} else if (schType.equals("reg_date")) {
				kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
				sb.append(" WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ?");
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
				dto.setReplyCount(rs.getInt("replyCount"));
				
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
	
	
}
