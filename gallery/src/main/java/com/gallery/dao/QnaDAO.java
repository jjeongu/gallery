package com.gallery.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gallery.domain.QnADTO;
import com.gallery.util.DBConn;
import com.gallery.util.DBUtil;

public class QnaDAO {
	private Connection conn = DBConn.getConnection();
	
	public void insertQuestion(QnADTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "insert into qna(num, member_id, subject, content, reg_date, hitCount) values(qna_seq.nextval, ?, ?, ?, sysdate, 0)";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getMember_id());
			pstmt.setString(2, dto.getSubject());
			pstmt.setString(3, dto.getContent());
			
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public void updateAnswer(QnADTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "update qna set answer = ?, answer_id = ?, answer_date = sysdate where num = ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getAnswer());
			pstmt.setString(2, dto.getAnswer_id());
			pstmt.setLong(3, dto.getNum());
			
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public int dataCount() {
		int count=0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "select nvl(count(*), 0) from qna";
			
			pstmt = conn.prepareStatement(sql);
			
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				count = rs.getInt(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return count;
	}
	
	public List<QnADTO> listQuestion(int offset, int size) {
		List<QnADTO> list = new ArrayList<QnADTO>();
		QnADTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" select num, q.member_id, subject, to_char(q.reg_date, 'YYYY-MM-DD') reg_date, ");
			sb.append(" name, answer_id from qna q join member1 m on q.member_id=m.member_id ");
			sb.append(" order by num desc offset ? rows fetch first ? rows only ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				dto = new QnADTO();
				dto.setNum(rs.getLong("num"));
				dto.setMember_id(rs.getString("member_id"));
				dto.setSubject(rs.getString("subject"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setName(rs.getString("name"));
				dto.setAnswer_id(rs.getString("answer_id"));
				
				list.add(dto);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return list;
	}

	public int dataCount(String kwd) {
		int count = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "select nvl(count(*), 0) from qna where instr(subject, ?) >=1 or instr(content, ?) >=1 or instr(answer, ?) >=1";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, kwd);
			pstmt.setString(2, kwd);
			pstmt.setString(3, kwd);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				count = rs.getInt(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return count;
	}

	public List<QnADTO> listQuestion(int offset, int size, String kwd) throws SQLException  {
		List<QnADTO> list = new ArrayList<QnADTO>();
		QnADTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append(" select num, q.member_id, subject, to_char(q.reg_date, 'YYYY-MM-DD') reg_date, ");
			sb.append(" name, answer_id from qna q join member1 m on q.member_id=m.member_id ");
			sb.append(" where instr(subject, ?) >=1 or instr(content, ?) >=1 or instr(answer, ?) >=1 ");
			sb.append(" order by num desc offset ? rows fetch first ? rows only ");
			
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setString(1, kwd);
			pstmt.setString(2, kwd);
			pstmt.setString(3, kwd);
			pstmt.setInt(4, offset);
			pstmt.setInt(5, size);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				dto = new QnADTO();
				dto.setNum(rs.getLong("num"));
				dto.setMember_id(rs.getString("member_id"));
				dto.setSubject(rs.getString("subject"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setName(rs.getString("name"));
				dto.setAnswer_id(rs.getString("answer_id"));
				
				list.add(dto);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return list;
	}

	public QnADTO findById(long num) throws SQLException {
		QnADTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		
		try {
			sb.append("select num, m.member_id, subject, content, m.name, hitCount, to_char(m.reg_date, 'YYYY-MM-DD') reg_date, ");
			sb.append(" answer_id, to_char(answer_date, 'YYYY-MM-DD') answer_date, m2.name answer_name, answer from qna q join member1 m on q.member_id=m.member_id ");
			sb.append(" left outer join member1 m2 on q.answer_id=m2.member_id where num = ?");
			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, num);
			
			rs=pstmt.executeQuery();
			
			while (rs.next()) {
				dto = new QnADTO();
				dto.setNum(rs.getLong("num"));
				dto.setMember_id(rs.getString("member_id"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setName(rs.getString("name"));
				dto.setHitcount(rs.getInt("hitCount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setAnswer_id(rs.getString("answer_id"));
				dto.setAnswer_date(rs.getString("answer_date"));
				dto.setAnswer_name(rs.getString("answer_name"));
				dto.setAnswer(rs.getString("answer"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return dto;
	}

	public void updateQuestion(QnADTO dto) {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "update qna set subject = ?,content = ? where member_id=? and num =?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setString(3, dto.getMember_id());
			pstmt.setLong(4, dto.getNum());
			
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public void deleteQuestion(long num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "DELETE FROM qna WHERE num=?";
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
	
	public QnADTO findByPrev(long num, String kwd) {
		QnADTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" select num, subject from qna ");
			sb.append(" where num > ? ");
			
			if(kwd != null && kwd.length() != 0) {
				sb.append(" and (instr(subject, ?) >=1 or instr(content, ?) >=1 or instr(answer, ?) >=1) ");
			}
			sb.append(" order by num asc fetch first 1 rows only ");

			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, num);
			if(kwd != null && kwd.length() != 0) {
				pstmt.setString(2, kwd);
				pstmt.setString(3, kwd);
				pstmt.setString(4, kwd);
			}
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new QnADTO();
				
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

	public QnADTO findByNext(long num, String kwd) {
		QnADTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();

		try {
			sb.append(" select num, subject from qna ");
			sb.append(" where num < ? ");
			
			if(kwd != null && kwd.length() != 0) {
				sb.append(" and (instr(subject, ?) >=1 or instr(content, ?) >=1 or instr(answer, ?) >=1) ");
			}
			sb.append(" order by num desc fetch first 1 rows only ");

			pstmt = conn.prepareStatement(sb.toString());
			
			pstmt.setLong(1, num);
			if(kwd != null && kwd.length() != 0) {
				pstmt.setString(2, kwd);
				pstmt.setString(3, kwd);
				pstmt.setString(4, kwd);
			}
			
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new QnADTO();
				
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
	
}
