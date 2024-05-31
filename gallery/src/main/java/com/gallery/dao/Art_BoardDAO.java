package com.gallery.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gallery.domain.Art_BoardDTO;
import com.gallery.util.DBConn;
import com.gallery.util.DBUtil;

public class Art_BoardDAO {

	private Connection conn = DBConn.getConnection();
	
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
	public List<Art_BoardDTO> listart_board(int offset, int size) {
		List<Art_BoardDTO> list = new ArrayList<Art_BoardDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT ab.num, member_id, subject, hitCount, ");
			sb.append(" TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date, " );
			sb.append("  NVL(replyCount,0) replyCount ");
			sb.append("FROM art_board ab ");
			sb.append("JOIN Art a ON  ab.member_id = a.member_id ");
			sb.append(" LEFT OUTER JOIN ( ");
			sb.append(" SELECT num, COUNT(*) replyCount ");
			sb.append(" FROM Art_Board_Reply ");
			sb.append(" WHERE answer = 0");
			sb.append(" GROUP BY num");
			sb.append(" ) c ON ab.num = c.num");
			sb.append(" ORDER BY num DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROW ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);
			
			rs = pstmt.executeQuery();
			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return list;
	}
	
	
	
}
