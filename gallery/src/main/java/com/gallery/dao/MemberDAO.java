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
			sql = " SELECT member_id, member_pwd, name, role " + " FROM member1"
					+ " WHERE member_id = ? AND member_pwd = ?";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, userId);
			pstmt.setString(2, userPwd);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new MemberDTO();

				dto.setUserId(rs.getString("member_id"));
				dto.setUserPwd(rs.getString("member_pwd"));
				dto.setName(rs.getString("name"));
				dto.setRole(rs.getInt("role"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}

		return dto;
	}
	
	public void insertMember(MemberDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			
			sql = "INSERT INTO member1(member_id, member_pwd, name, birth, tel, email, role, reg_date) VALUES (?, ?, ?, ?, ?, ?, ?, SYSDATE)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getUserId());
			pstmt.setString(2, dto.getUserPwd());
			pstmt.setString(3, dto.getName());
			pstmt.setString(4, dto.getBirth());
			pstmt.setString(5, dto.getTel());
			pstmt.setString(6, dto.getEmail());
			pstmt.setInt(7, dto.getRole());
			
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}

	}
}
