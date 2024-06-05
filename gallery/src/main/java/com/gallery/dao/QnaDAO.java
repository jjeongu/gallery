package com.gallery.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.gallery.util.DBConn;
import com.gallery.util.DBUtil;

public class QnaDAO {
	private Connection conn = DBConn.getConnection();
	
	public void insertQna() throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			/*
			 * NUM      NOT NULL NUMBER         
ID       NOT NULL VARCHAR2(30)   
SUBJECT  NOT NULL VARCHAR2(100)  
CONTENT  NOT NULL VARCHAR2(4000) 
REG_DATE NOT NULL DATE           
HITCOUNT NOT NULL NUMBER         
ANSWER 
			 */
			sql = "insert into qna(num, id, subject, content, reg_date, hitCount, answer) ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
}
