package com.gallery.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gallery.domain.NoticeDTO;
import com.gallery.util.DBConn;
import com.gallery.util.DBUtil;
import com.gallery.util.MyMultipartFile;

public class NoticeDAO {
	Connection conn=DBConn.getConnection();
	public void insertNotice(NoticeDTO dto) throws SQLException {
		PreparedStatement pstmt=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			sb.append(" INSERT INTO NOTICE(NUM, REG_DATE, ");
			sb.append(" HITCOUNT, SUBJECT, CONTENT, MEMBER_ID) ");
			sb.append(" VALUES(NOTICE_SEQ.NEXTVAL, SYSDATE, 0, ?, ?, ?) ");
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setString(3, dto.getMember_id());
			pstmt.executeQuery();
			DBUtil.close(pstmt);
			pstmt=null;
			
			sb=new StringBuilder();
			sb.append(" INSERT INTO NOTICEFILE(FILENUM, NUM, SAVEFILENAME, ORIGINALFILENAME) ");
			sb.append(" VALUES(NOTICEFILE_SEQ.NEXTVAL, NOTICE_SEQ.CURRVAL, ?, ?) ");
			pstmt=conn.prepareStatement(sb.toString());
			for(MyMultipartFile mf:dto.getListFile()) {
				pstmt.setString(1, mf.getSaveFilename());
				pstmt.setString(2, mf.getOriginalFilename());
				pstmt.executeUpdate();			
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	public int dataCount() {
		PreparedStatement pstmt=null;
		StringBuilder sb=new StringBuilder();
		ResultSet rs=null;
		int result=0;
		
		try {
			sb.append(" SELECT COUNT(*) FROM NOTICE ");
			pstmt=conn.prepareStatement(sb.toString());
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				result=rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
				
		return result;
	}
	
	public int dataCount(String schType, String kwd) {
		PreparedStatement pstmt=null;
		StringBuilder sb=new StringBuilder();
		ResultSet rs=null;
		int result=0;
		
		try {
			sb.append(" SELECT COUNT(*) FROM NOTICE ");
			if(schType.equals("all")) {
				sb.append(" WHERE INSTR(SUBJECT, ?)>=1 OR INSTR(CONTENT, ?)>=1 ");
			} else if(schType.equals("reg_date")) {
				kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
				sb.append(" WHERE TO_CHAR(REG_DATE, 'YYYYMMDD')=? ");
			} else {
				sb.append(" WHERE INSTR("+schType+", ?)>=1 ");
			}
			
			pstmt=conn.prepareStatement(sb.toString());
			if(schType.equals("all")) {
				pstmt.setString(1, kwd);
				pstmt.setString(2, kwd);
			} else {
				pstmt.setString(1, kwd);
			}
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				result=rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
				
		return result;
	}
	
	public List<NoticeDTO> listNotice(int offset, int size) {
		List<NoticeDTO> list=new ArrayList<NoticeDTO>();
		PreparedStatement pstmt=null;
		StringBuilder sb=new StringBuilder();
		ResultSet rs=null;
		
		try {
			sb.append(" SELECT NUM, MEMBER_ID, SUBJECT, ");
			sb.append(" CONTENT, HITCOUNT, TO_CHAR(REG_DATE, 'YYYYMMDD') AS REG_DATE ");
			sb.append(" FROM NOTICE");
			sb.append(" ORDER BY NUM DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);
			rs=pstmt.executeQuery();
			
			while(rs.next()) {
				NoticeDTO dto=new NoticeDTO();
				
				dto.setNum(rs.getInt(1));
				dto.setMember_id(rs.getString(2));
				dto.setSubject(rs.getString(3));
				dto.setContent(rs.getString(4));
				dto.setHitcount(rs.getInt(5));
				dto.setReg_date(rs.getString(6));
				
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
	public List<NoticeDTO> listNotice(int offset, int size, String schType, String kwd) {
		List<NoticeDTO> list=new ArrayList<NoticeDTO>();
		PreparedStatement pstmt=null;
		StringBuilder sb=new StringBuilder();
		ResultSet rs=null;
		
		try {
			sb.append(" SELECT NUM, MEMBER_ID, SUBJECT, ");
			sb.append(" CONTENT, HITCOUNT, TO_CHAR(REG_DATE, 'YYYYMMDD') AS REG_DATE ");
			sb.append(" FROM NOTICE");
			if(schType.equals("all")) {
				sb.append(" WHERE INSTR(SUBJECT, ?)>=1 OR INSTR(CONTENT, ?)>=1 ");
			} else if(schType.equals("reg_date")) {
				kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
				sb.append(" WHERE TO_CHAR(REG_DATE, 'YYYYMMDD')=? ");
			} else {
				sb.append(" WHERE INSTR("+schType+", ?)>=1 ");
			}	
			sb.append(" ORDER BY NUM DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");
			pstmt=conn.prepareStatement(sb.toString());
			if(schType.equals("all")) {
				pstmt.setString(1, kwd);
				pstmt.setString(2, kwd);
				pstmt.setInt(3, offset);
				pstmt.setInt(4, size);
			} else {
				pstmt.setString(1, kwd);
				pstmt.setInt(2, offset);
				pstmt.setInt(3, size);
			}	
			rs=pstmt.executeQuery();
			
			while(rs.next()) {
				NoticeDTO dto=new NoticeDTO();
				
				dto.setNum(rs.getInt(1));
				dto.setMember_id(rs.getString(2));
				dto.setSubject(rs.getString(3));
				dto.setContent(rs.getString(4));
				dto.setHitcount(rs.getInt(5));
				dto.setReg_date(rs.getString(6));
				
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

	public void updateHitCount(long num) {
		PreparedStatement pstmt=null;
		String sb;
		
		try {
			sb=" UPDATE NOTICE SET HITCOUNT=HITCOUNT+1 WHERE NUM=? ";
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setLong(1, num);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}

	public NoticeDTO findById(long num) {
		NoticeDTO dto=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			sb.append(" SELECT NUM, MEMBER_ID, SUBJECT, ");
			sb.append(" CONTENT, HITCOUNT, TO_CHAR(REG_DATE, 'YYYYMMDD') AS REG_DATE ");
			sb.append(" FROM NOTICE WHERE NUM=? ");
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setLong(1, num);
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				dto=new NoticeDTO();
				
				dto.setNum(rs.getInt(1));
				dto.setMember_id(rs.getString(2));
				dto.setSubject(rs.getString(3));
				dto.setContent(rs.getString(4));
				dto.setHitcount(rs.getInt(5));
				dto.setReg_date(rs.getString(6));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		return dto;
	}

	public NoticeDTO findByPrev(long num, String schType, String kwd) {
		NoticeDTO dto=null;
		PreparedStatement pstmt=null;
		StringBuilder sb=new StringBuilder();
		ResultSet rs=null;
		
		try {
			if (kwd != null && kwd.length() != 0) {
				sb.append(" SELECT NUM, MEMBER_ID, SUBJECT, ");
				sb.append(" CONTENT, HITCOUNT, TO_CHAR(REG_DATE, 'YYYYMMDD') AS REG_DATE ");
				sb.append(" FROM NOTICE WHERE NUM<? ");
				if(schType.equals("all")) {
					sb.append(" AND INSTR(SUBJECT, ?)>=1 OR INSTR(CONTENT, ?)>=1 ");
				} else if(schType.equals("reg_date")) {
					kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
					sb.append(" AND TO_CHAR(REG_DATE, 'YYYYMMDD')=? ");
				} else {
					sb.append(" AND INSTR("+schType+", ?)>=1 ");
				}
				sb.append(" ORDER BY NUM DESC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");
				pstmt=conn.prepareStatement(sb.toString());
			
				if(schType.equals("all")) {
					pstmt.setString(1, kwd);
					pstmt.setString(2, kwd);
					pstmt.setLong(3, num);
				} else {
					pstmt.setString(1, kwd);
					pstmt.setLong(2, num);
				}
			} else {
				sb.append(" SELECT NUM, MEMBER_ID, SUBJECT, ");
				sb.append(" CONTENT, HITCOUNT, TO_CHAR(REG_DATE, 'YYYYMMDD') AS REG_DATE ");
				sb.append(" FROM NOTICE WHERE NUM<? ");
				sb.append(" ORDER BY NUM DESC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");
				pstmt=conn.prepareStatement(sb.toString());
				pstmt.setLong(1, num);
			}
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				dto=new NoticeDTO();
				
				dto.setNum(rs.getInt(1));
				dto.setMember_id(rs.getString(2));
				dto.setSubject(rs.getString(3));
				dto.setContent(rs.getString(4));
				dto.setHitcount(rs.getInt(5));
				dto.setReg_date(rs.getString(6));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		return dto;
	}

	public NoticeDTO findByNext(long num, String schType, String kwd) {
		NoticeDTO dto=null;
		PreparedStatement pstmt=null;
		StringBuilder sb=new StringBuilder();
		ResultSet rs=null;
		
		try {
			if (kwd != null && kwd.length() != 0) {
				sb.append(" SELECT NUM, MEMBER_ID, SUBJECT, ");
				sb.append(" CONTENT, HITCOUNT, TO_CHAR(REG_DATE, 'YYYYMMDD') AS REG_DATE ");
				sb.append(" FROM NOTICE WHERE NUM>? ");
				if(schType.equals("all")) {
					sb.append(" AND INSTR(SUBJECT, ?)>=1 OR INSTR(CONTENT, ?)>=1 ");
				} else if(schType.equals("reg_date")) {
					kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
					sb.append(" AND TO_CHAR(REG_DATE, 'YYYYMMDD')=? ");
				} else {
					sb.append(" AND INSTR("+schType+", ?)>=1 ");
				}
				sb.append(" ORDER BY NUM ASC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");
				pstmt=conn.prepareStatement(sb.toString());
			
				if(schType.equals("all")) {
					pstmt.setString(1, kwd);
					pstmt.setString(2, kwd);
					pstmt.setLong(3, num);
				} else {
					pstmt.setString(1, kwd);
					pstmt.setLong(2, num);
				}
			} else {
				sb.append(" SELECT NUM, MEMBER_ID, SUBJECT, ");
				sb.append(" CONTENT, HITCOUNT, TO_CHAR(REG_DATE, 'YYYYMMDD') AS REG_DATE ");
				sb.append(" FROM NOTICE WHERE NUM>? ");
				sb.append(" ORDER BY NUM ASC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");
				pstmt=conn.prepareStatement(sb.toString());
				pstmt.setLong(1, num);
			}
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				dto=new NoticeDTO();
				
				dto.setNum(rs.getInt(1));
				dto.setMember_id(rs.getString(2));
				dto.setSubject(rs.getString(3));
				dto.setContent(rs.getString(4));
				dto.setHitcount(rs.getInt(5));
				dto.setReg_date(rs.getString(6));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		return dto;
	}

	public List<NoticeDTO> listNoticeFile(long num) {
		List<NoticeDTO> list=new ArrayList<NoticeDTO>();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			sb.append(" SELECT NUM, FILENUM, SAVEFILENAME, UPLOADFILENAME ");
			sb.append(" FROM NOTICEFILE WHERE NUM=? ");
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setLong(1, num);
			rs=pstmt.executeQuery();
			
			while(rs.next()) {
				NoticeDTO dto=new NoticeDTO();
				dto.setNum(rs.getLong(1));
				dto.setFileNum(rs.getLong(2));
				dto.setSaveFilename(rs.getString(3));
				dto.setUploadFilename(rs.getString(4));
				
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

	public NoticeDTO findByFileId(long fileNum) {
		NoticeDTO dto=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			sb.append(" SELECT NUM, FILENUM, SAVEFILENAME, UPLOADFILENAME ");
			sb.append(" FROM NOTICEFILE WHERE FILENUM=? ");
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setLong(1, fileNum);
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				dto=new NoticeDTO();
				
				dto.setNum(rs.getLong(1));
				dto.setFileNum(rs.getLong(2));
				dto.setSaveFilename(rs.getString(3));
				dto.setUploadFilename(rs.getString(4));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		
		return dto;
	}

	public void updateNotice(NoticeDTO dto) {
		PreparedStatement pstmt=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			sb.append(" UPDATE NOTICE SET SUBJECT=?, CONTENT=? ");
			sb.append(" WHERE NUM=? ");
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setLong(3, dto.getNum());
			pstmt.executeQuery();
			DBUtil.close(pstmt);
			pstmt=null;
			
			sb=new StringBuilder();
			sb.append(" INSERT INTO NOTICEFILE(FILENUM, NUM, SAVEFILENAME, UPLOADFILENAME) ");
			sb.append(" VALUES(NOTICEFILE_SEQ.NEXTVAL, ?, ?, ?) ");
			pstmt=conn.prepareStatement(sb.toString());
			for(MyMultipartFile mf:dto.getListFile()) {
				pstmt.setLong(1, dto.getNum());
				pstmt.setString(2, mf.getSaveFilename());
				pstmt.setString(3, mf.getOriginalFilename());
				pstmt.executeUpdate();			
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}

	public void deleteNotice(long num) {
		PreparedStatement pstmt=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			sb.append(" DELETE FROM NOTICE WHERE NUM=? ");
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setLong(1, num);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}

	public void deleteNoticeFile(long num, long filenum) {
		PreparedStatement pstmt=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			sb.append(" DELETE FROM NOTICEFILE WHERE NUM=? AND FILENUM=?");
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setLong(1, num);
			pstmt.setLong(2, filenum);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}

	public void deleteNoticeFile(long num) {
		PreparedStatement pstmt=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			sb.append(" DELETE FROM NOTICEFILE WHERE NUM=? ");
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setLong(1, num);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}	
}