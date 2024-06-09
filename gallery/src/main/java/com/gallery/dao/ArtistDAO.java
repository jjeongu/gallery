package com.gallery.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gallery.domain.ArtDTO;
import com.gallery.util.DBConn;
import com.gallery.util.DBUtil;

public class ArtistDAO {
	private Connection conn = DBConn.getConnection();
	
	public void insertAtrist(ArtDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO art (member_id, career, introduce, represent, img, upload_img) "
					+ " VALUES(?, ?, ?, ?, ?, ?)";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getMember_id());
			pstmt.setString(2, dto.getCareer());
			pstmt.setString(3, dto.getIntroduce());
			pstmt.setString(4, dto.getRepresent());
			pstmt.setString(5, dto.getImg());
			pstmt.setString(6, dto.getUpload_img());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public int dataCount() {
		String sql;
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		
		try {
			sql = "SELECT COUNT(*) FROM Art";
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
	
	
	public List<ArtDTO> listArtist(int offset, int size) {
		List<ArtDTO> list = new ArrayList<ArtDTO>();
		String sql;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			sql = "SELECT a.member_id, TO_CHAR(m.birth, 'YYYY-MM-DD') birth, "
					+ " career, introduce, represent, img, upload_img ,name "
					+ " FROM Art a "
					+ " JOIN member1 m ON a.member_id = m.member_id "
					+ " OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ";
			
			pstmt =conn.prepareStatement(sql);
			
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				ArtDTO dto = new ArtDTO();
				
				dto.setMember_id(rs.getString("member_id"));
				dto.setBirth(rs.getString("birth"));
				dto.setCareer(rs.getString("career"));
				dto.setIntroduce(rs.getString("introduce"));
				dto.setImg(rs.getString("img"));
				dto.setUpload_img(rs.getString("upload_img"));
				dto.setName(rs.getString("name"));
				
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
	
	public ArtDTO findById(String member_id) {
		ArtDTO dto =null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			   sql = "SELECT m.member_id, TO_CHAR(m.birth, 'YYYY-MM-DD') birth, "
			           + " career, introduce, represent, img,upload_img , name "
			           + " FROM Art a "
			           + " right outer JOIN member1 m ON a.member_id = m.member_id "
			           + " WHERE m.member_id = ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, member_id);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new ArtDTO();
				
				dto.setMember_id(rs.getString("member_id"));
				dto.setBirth(rs.getString("birth"));
				dto.setCareer(rs.getString("career"));
				dto.setIntroduce(rs.getString("introduce"));
				dto.setRepresent(rs.getString("represent"));
				dto.setImg(rs.getString("img"));
				dto.setUpload_img(rs.getString("upload_img"));
				dto.setName(rs.getString("name"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return dto;
	}
	
	public void updateArtist(ArtDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "UPDATE Art SET career=? , introduce =?, represent=?, img =?, upload_img=?"
					+ " WHERE member_id =? ";
			
			pstmt = conn.prepareStatement(sql);
			
	
			pstmt.setString(1, dto.getCareer());
			pstmt.setString(2, dto.getIntroduce());
			pstmt.setString(3, dto.getRepresent());
			pstmt.setString(4, dto.getImg());
			pstmt.setString(5, dto.getUpload_img());
			pstmt.setString(6, dto.getMember_id());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			DBUtil.close(pstmt);
		}
	}
	
	public void deleteArtist(String member_id) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = " DELETE FROM Art WHERE member_id = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, member_id);
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			DBUtil.close(pstmt);
		}
	}
	
	public String nameToId(String name) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		String member_id = null;
		
		try {
			sql = "select m.member_id from member1 m join art a on m.member_id=a.member_id where name = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, name);
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				member_id = rs.getString(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		return member_id;
	}
	
	public List<ArtDTO> notRegistArts() {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<ArtDTO> list = new ArrayList<ArtDTO>();
		String sql;
		
		try {
			sql = "SELECT m.member_id, m.name, TO_CHAR(m.birth, 'YYYY-MM-DD') birth "
					+ " FROM member1 m "
					+ " LEFT JOIN art a ON m.member_id = a.member_id"
					+ " WHERE m.role = 1 AND a.member_id IS NULL";
			pstmt = conn.prepareStatement(sql);
			rs= pstmt.executeQuery();
			
			while (rs.next()) {
				ArtDTO dto = new ArtDTO();
				dto.setMember_id(rs.getString("member_id"));
				dto.setName(rs.getString("name"));
				dto.setBirth(rs.getString("birth"));
				
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
