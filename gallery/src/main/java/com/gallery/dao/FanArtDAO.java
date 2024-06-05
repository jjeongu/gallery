package com.gallery.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gallery.domain.FanArtDTO;
import com.gallery.domain.FanArt_ReplyDTO;
import com.gallery.domain.MemberDTO;
import com.gallery.util.DBConn;
import com.gallery.util.DBUtil;

public class FanArtDAO {
	private Connection conn = DBConn.getConnection();
	
	public void insertFanArt(FanArtDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			
			sql = "insert into FanArt(num, member_id, notice, subject, content, hitcount, reg_date, img, artist) values(FanArt_seq.nextval, ?, ?, ?, ?, 0, sysdate, ?, ?)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getMember_id());
			pstmt.setInt(2, dto.getNotice());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());
			pstmt.setString(5, dto.getImg());
			pstmt.setString(6, dto.getArtist());
			
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
			sql = "select num, f.member_id, m.name, notice, subject, content, hitcount, to_char(f.reg_date, 'YYYY-MM-DD HH24:mm:dd') reg_date, img, artist, m2.name artname from fanArt f join member1 m on f.member_id=m.member_id "
					+ " left outer join member1 m2 on f.artist=m2.member_id where notice = 0 order by num desc offset ? rows fetch first ? rows only ";
			
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
				dto.setArtist(rs.getString("artist"));
				dto.setArtName(rs.getString("artname"));
				
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
	
	public List<FanArtDTO> noticeList() {
		List<FanArtDTO> list = new ArrayList<FanArtDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		FanArtDTO dto = null;
		
		try {
			sql = "select num, f.member_id, name, notice, subject, content, hitcount, to_char(f.reg_date, 'YYYY-MM-DD HH24:mm:dd') reg_date, img from fanArt f join member1 m on f.member_id=m.member_id where notice = 1 order by num desc";
			
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
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
		}
		
		return list;
	}
	
	public int dataCount() {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT COUNT(*) FROM fanArt where notice = 0";
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
			sql = "select f.num, f.member_id, m.name, notice, subject, content, hitcount, to_char(f.reg_date, 'YYYY-MM-DD HH24:mm:dd') reg_date, img, NVL(boardLikeCount, 0) boardLikeCount, artist, m2.name artname from fanArt f join member1 m on f.member_id=m.member_id "
					+ "  left outer join member1 m2 on f.artist=m2.member_id LEFT OUTER JOIN ( SELECT num, NVL(COUNT(*), 0) boardLikeCount FROM FanArt_Like GROUP BY num ) fl ON f.num = fl.num where f.num = ?";
			
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
				dto.setBoardLikeCount(rs.getInt("boardLikeCount"));
				dto.setArtist(rs.getString("artist"));
				dto.setArtName(rs.getString("artname"));
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
			sql = "update fanArt set subject = ?, content = ?, img = ?, notice = ?, artist=? where num = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setString(3, dto.getImg());
			pstmt.setInt(4, dto.getNotice());
			pstmt.setString(5, dto.getArtist());
			pstmt.setLong(6, dto.getNum());
			
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
			
			// 해당 글에 달린 댓글의 좋아요 삭제 
			sql = " delete from FanArt_Reply_Like l WHERE r_num IN (select r_num FROM FanArt_Reply where num = ?)";
			
			pstmt= conn.prepareStatement(sql);
			pstmt.setLong(1, num);
			pstmt.executeUpdate();
			
			DBUtil.close(pstmt);
			
			
			// 글에 달린 댓글 삭제
			sql = "delete FROM FanArt_Reply r WHERE r.num IN  (SELECT num FROM fanArt f START WITH r.num = ? CONNECT BY PRIOR r.num = f.num)";
			
			pstmt= conn.prepareStatement(sql);
			pstmt.setLong(1, num);
			pstmt.executeUpdate();
			
			DBUtil.close(pstmt);
			
			
			//글 삭제
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
	
	public boolean isUserBoardLike(long num, String member_id) {
		boolean result = false;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT num, member_id FROM FanArt_Like WHERE num = ? AND member_id = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			pstmt.setString(2, member_id);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return result;
	}
	
	public void insertBoardLike(long num, String member_id) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO FanArt_Like(num, member_id) VALUES (?, ?)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			pstmt.setString(2, member_id);
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public void deleteBoardLike(long num, String member_id) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "DELETE FROM FanArt_Like WHERE num = ? AND member_id = ?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			pstmt.setString(2, member_id);
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	public int countBoardLike(long num) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM FanArt_Like WHERE num=?";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
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
	
	// 게시물의 댓글 및 답글 추가
	public void insertReply(FanArt_ReplyDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO FanArt_Reply(r_num, num, member_id, content, reg_date) "
					+ " VALUES (FanArt_Reply_seq.NEXTVAL, ?, ?, ?, SYSDATE)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, dto.getNum());
			pstmt.setString(2, dto.getMember_id());
			pstmt.setString(3, dto.getContent());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public void deleteReply(long r_num) {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "delete from FanArt_Reply_Like where r_num = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, r_num);
			pstmt.executeUpdate();
			
			DBUtil.close(pstmt);
			
			sql = "delete from FanArt_Reply where r_num = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, r_num);
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public FanArt_ReplyDTO findByReply(long r_num) {
		FanArt_ReplyDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "select r_num, num, r.member_id, name, content, r.reg_date from FanArt_Reply r join member1 m on r.member_id = m.member_id where r_num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, r_num);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new FanArt_ReplyDTO();
				
				dto.setR_num(rs.getLong("r_num"));
				dto.setNum(rs.getLong("num"));
				dto.setMember_id(rs.getString("member_id"));
				dto.setName(rs.getString("name"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dto;
	}
	public int dataCountReply(long num) {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM FanArt_Reply WHERE num = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			
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
	
	public List<FanArt_ReplyDTO> listReply(long num, int offset, int size) {
		List<FanArt_ReplyDTO> list = new ArrayList<FanArt_ReplyDTO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		FanArt_ReplyDTO dto = null;
		String sql;
		
		try {
			sql = "select r.r_num, num, r.member_id, content, r.reg_date, name, likeCount  from FanArt_Reply r join member1 m on r.member_id=m.member_id "
					+ " LEFT OUTER  JOIN ( SELECT r_num, NVL(COUNT(*), 0) likeCount FROM FanArt_Reply_Like GROUP BY r_num ) b ON r.r_num = b.r_num"
					+ " where num=? order by r_num desc offset ? rows fetch first ? rows only ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, num);
			pstmt.setInt(2, offset);
			pstmt.setInt(3, size);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				dto = new FanArt_ReplyDTO();
				
				dto.setR_num(rs.getLong("r_num"));
				dto.setNum(rs.getLong("num"));
				dto.setMember_id(rs.getString("member_id"));
				dto.setName(rs.getString("name"));
				dto.setContent(rs.getString("content"));
				dto.setReg_date(rs.getString("reg_date"));
				dto.setLikeCount(rs.getInt("likeCount"));
				
				list.add(dto);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
			DBUtil.close(rs);
		}
		
		return list;
	}
	
	public void insertReplyLike(FanArt_ReplyDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO FanArt_Reply_Like(r_num, member_id) VALUES (?, ?)";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, dto.getR_num());
			pstmt.setString(2, dto.getMember_id());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			if(e.getErrorCode() == 1) {
				throw e;
			}
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}		
	}
	
	public int countReplyLike(long r_num) {
		int count = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = " SELECT NVL(COUNT(*), 0) likeCount FROM FanArt_Reply_Like WHERE r_num = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setLong(1, r_num);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				count = rs.getInt("likeCount");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return count;
	}
	
	public List<MemberDTO> artistList() {
		List<MemberDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "select member_id, name from member1 where member_id in(select member_id from art)";
			
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				MemberDTO dto = new MemberDTO();
				
				dto.setUserId(rs.getString("member_id"));
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
}
