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
			sql = " select subject, content from faq ";
			
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while(rs.next()) {
				FAQDTO dto = new FAQDTO();
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
}
