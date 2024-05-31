package com.gallery.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.gallery.domain.Art_BoardDTO;
import com.gallery.util.DBConn;
import com.gallery.util.DBUtil;

public class Art_BoardDAO {

	private Connection conn = DBConn.getConnection();
	
	public void insertAar_Board(Art_BoardDTO dto, String mode) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO Art_Board(num , member_id, notice , subject, content, saveFilename, uploadfilename, filesize, "
					+ " hitcount, reg_date) "
					+ " VALUES(Art_Board_seq.NEXTAVAL, ?, ?, ?, ?, ?, ?, 0, SYSDATE)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getMember_id());
			pstmt.setInt(2, dto.getNotice());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());
			pstmt.setString(5, dto.getUploadfilename());
			pstmt.setLong(6, dto.getFileSize());
			
			pstmt.executeUpdate();
					
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
}
