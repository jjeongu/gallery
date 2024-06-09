package com.gallery.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gallery.domain.FAQDTO;
import com.gallery.util.DBConn;
import com.gallery.util.DBUtil;

public class FaqDAO {
	private Connection conn = DBConn.getConnection();
	
	public void insertFaq(FAQDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = " insert into faq(num, member_id, subject, content, reg_date)"
					+ " values(faq_seq.NEXTVAL, ?, ?, ?, sysdate) ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getMember_id());
			pstmt.setString(2, dto.getSubject());
			pstmt.setString(3, dto.getContent());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public List<FAQDTO> listFaq(){
		List<FAQDTO> list = new ArrayList<FAQDTO>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " select num, subject, content from faq order by num ";
			
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while(rs.next()) {
				FAQDTO dto = new FAQDTO();
				dto.setNum(rs.getLong("num"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				
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
	
	public void updateFaq(FAQDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = " update faq set subject=?, content=? where num=? and member_id=? ";
		
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
	
	public FAQDTO findById(long num) {
		FAQDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " select num, f.member_id, subject, content, f.reg_date"
					+ " from faq f "
					+ " join member1 m on f.member_id = m.member_id "
					+ " where f.num = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new FAQDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setMember_id(rs.getString("member_id"));
				dto.setSubject(rs.getString("subject"));
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
	
	public void deleteFaq(long num, String member_id) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			if(member_id.equals("admin")) {
			sql = " delete from faq where num =? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
			pstmt.executeUpdate();
		} else {
			return;
		}
		
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
				
	}
	
}
