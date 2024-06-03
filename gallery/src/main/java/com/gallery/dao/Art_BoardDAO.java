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
			sb.append(" SELECT num, ab.member_id, subject, hitCount, ");
			sb.append(" TO_CHAR(ab.reg_date, 'YYYY-MM-DD') reg_date, saveFilename " );
			sb.append(" FROM art_board ab ");
			sb.append(" JOIN Art a ON ab.member_id = a.member_id ");
			sb.append(" ORDER BY num DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROW ONLY ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				Art_BoardDTO dto = new Art_BoardDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setMember_id(rs.getString("member_id"));
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
	
	// 검색하기 인것 같은데 오류가 있음.
	public List<Art_BoardDTO> listArt_Board(int offset, int size, String schType, String kwd) {
		List<Art_BoardDTO> list = new ArrayList<Art_BoardDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" SELECT ab.num, member_id, subject, hitCount, ");
			sb.append(" TO_CHAR(reg_date, 'YYYY-MM-DD') reg_date, ");
			sb.append(" NVL(replyCount, 0) replyCount ");
			sb.append(" FROM art_board ab");
			sb.append(" JOIN Art a ON ab.member_id = a.member_id ");
			sb.append(" LEFT OUTER JOIN ( ");
			sb.append(" SELECT num, COUNT(*) replyCount ");
			sb.append(" FROM Art_Board_Reply ");
			//sb.append(" WHERE answer = 0");
			sb.append(" GROUP BY num");
			sb.append(" ) c ON ab.num = c.num"); 
			if( schType.equals("all")) {
				sb.append(" WHERE INSTR(subject, ?) >= 1 OR INSTR(content, ?) >= 1");
			} else if ( schType.equals("reg_date")) {
				kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
				sb.append(" WHERE TO_CHAR(reg_date, 'YYYYMMDD') = ?");
			} else {
				sb.append( " WHERE INSTR(" + schType + ", ?) >= 1 ");
			}
			sb.append(" ORDER BY num DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROW ONLY ");
			
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
			sql = "SELECT ab.num, ab.member_id, subject, content, saveFilename, uploadfilename, filesize, reg_date, hitcount, "
					+ " NVL(likeCount, 0) likeCount "
					+ " FROM art_board ab "
					+ " JOIN art a ON ab.member_id = a.member_id "
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
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
}
