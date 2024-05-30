package com.gallery.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.gallery.domain.MemberDTO;
import com.gallery.util.DBConn;
import com.gallery.util.DBUtil;

public class MemberDAO {
	private Connection conn = DBConn.getConnection();
	
	public MemberDTO loginMember(String userId, String userPwd) {
		MemberDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " SELECT member_id, member_pwd "
					+ " FROM member1"
					+ " WHERE member_id = ? AND member_pwd = ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, userId);
			pstmt.setString(2, userPwd);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new MemberDTO();
				
				dto.setUserId(rs.getString("member_id"));
				dto.setUserPwd(rs.getString("member_pwd"));
				/*
				dto.setUserName(rs.getString("userName"));
				dto.setRegister_date(rs.getString("register_date"));
				dto.setModify_date(rs.getString("modify_date"));
				*/
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
