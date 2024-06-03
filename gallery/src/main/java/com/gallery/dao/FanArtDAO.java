package com.gallery.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gallery.domain.FanArtDTO;
import com.gallery.util.DBConn;
import com.gallery.util.DBUtil;

public class FanArtDAO {
	private Connection conn = DBConn.getConnection();
	
	public void insertFanArt(FanArtDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			
			sql = "insert into FanArt(num, member_id, notice, subject, content, hitcount, reg_date, img) values(FanArt_seq.nextval, ?, ?, ?, ?, 0, sysdate, ?)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getMember_id());
			pstmt.setInt(2, dto.getNotice());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());
			pstmt.setString(5, dto.getImg());
			
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
		
	}
	
	public List<FanArtDTO> list(int offset, int size) {
		List<FanArtDTO> list= new ArrayList<FanArtDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		FanArtDTO dto = null;
		String sql;
		
		try {
			sql = "select num, f.member_id, name, notice, subject, content, hitcount, to_char(f.reg_date, 'YYYY-MM-DD HH24:mm:dd') reg_date, img from fanArt f join member1 m on f.member_id=m.member_id "
					+ " order by num desc offset ? rows fetch first ? rows only ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				dto = new FanArtDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setMember_id(rs.getString("member_id"));
				dto.setName(rs.getString("name"));
				dto.setNotice(rs.getInt("notice"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setHitcount(rs.getInt("hitcount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setImg(rs.getString("img"));
				
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
	
	public int dataCount() {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT COUNT(*) FROM fanArt";
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
	
	public FanArtDTO findById(long num) {
		FanArtDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "select num, f.member_id, name, notice, subject, content, hitcount, to_char(f.reg_date, 'YYYY-MM-DD HH24:mm:dd') reg_date, img from fanArt f join member1 m on f.member_id=m.member_id where num = ?";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new FanArtDTO();
				
				dto.setNum(rs.getLong("num"));
				dto.setMember_id(rs.getString("member_id"));
				dto.setName(rs.getString("name"));
				dto.setNotice(rs.getInt("notice"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setHitcount(rs.getInt("hitcount"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setImg(rs.getString("img"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return dto;
	}
	
	public void updateFanArt(FanArtDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "update fanArt set subject = ?, content = ?, img = ?, notice = ? where num = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setString(3, dto.getImg());
			pstmt.setInt(4, dto.getNotice());
			pstmt.setLong(5, dto.getNum());
			
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public void deleteFanArt(long num) {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "delete fanArt where num = ?";
			
			pstmt= conn.prepareStatement(sql);
			pstmt.setLong(1, num);
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public void updateHitCount(long num) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "update fanArt set hitCount=hitCount+1 where num=?";
			
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
	
}
