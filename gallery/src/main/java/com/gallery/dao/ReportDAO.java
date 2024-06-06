package com.gallery.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gallery.domain.ReportDTO;
import com.gallery.util.DBConn;
import com.gallery.util.DBUtil;
import com.gallery.util.MyMultipartFile;

public class ReportDAO {
	Connection conn=DBConn.getConnection();
	public void insertReport(ReportDTO dto) throws SQLException {
		PreparedStatement pstmt=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			sb.append(" INSERT INTO REPORT(NUM, MEMBER_ID, ");
			sb.append(" REG_DATE, SUBJECT, CONTENT) ");
			sb.append(" VALUES(REPORT_SEQ.NEXTVAL, ?, SYSDATE, ?, ?) ");
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setString(1, dto.getMember_id());
			pstmt.setString(2, dto.getSubject());
			pstmt.setString(3, dto.getContent());
			pstmt.executeQuery();
			DBUtil.close(pstmt);
			pstmt=null;
			
			sb=new StringBuilder();
			sb.append(" INSERT INTO REPORT_FILE(FILENUM, NUM, SAVEFILENAME, UPLOADFILENAME) ");
			sb.append(" VALUES(REPORT_FILE_SEQ.NEXTVAL, REPORT_SEQ.CURRVAL, ?, ?) ");
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
			sb.append(" SELECT COUNT(*) FROM REPORT ");
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
			sb.append(" SELECT COUNT(*) FROM REPORT R");
			if(schType.equals("all")) {
				sb.append(" WHERE INSTR(SUBJECT, ?)>=1 OR INSTR(CONTENT, ?)>=1 ");
			} else if(schType.equals("reg_date")) {
				kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
				sb.append(" WHERE TO_CHAR(R.REG_DATE, 'YYYYMMDD')=? ");
			} else {
				sb.append(" JOIN MEMBER1 M ON R.MEMBER_ID=M.MEMBER_ID ");
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
	public List<ReportDTO> listReport(int offset, int size) {
		List<ReportDTO> list=new ArrayList<ReportDTO>();
		PreparedStatement pstmt=null;
		StringBuilder sb=new StringBuilder();
		ResultSet rs=null;
		
		try {
			sb.append(" SELECT R.NUM, NAME, SUBJECT, ");
			sb.append(" CONTENT, TO_CHAR(R.REG_DATE, 'YYYYMMDD') AS REG_DATE ");
			sb.append(" FROM REPORT R JOIN MEMBER1 M");
			sb.append(" ON R.MEMBER_ID=M.MEMBER_ID ");
			sb.append(" ORDER BY NUM DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setInt(1, offset);
			pstmt.setInt(2, size);
			rs=pstmt.executeQuery();
			
			while(rs.next()) {
				ReportDTO dto=new ReportDTO();
				
				dto.setNum(rs.getLong(1));
				dto.setUserName(rs.getString(2));
				dto.setSubject(rs.getString(3));
				dto.setContent(rs.getString(4));
				dto.setReg_date(rs.getString(5));
				
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
	public List<ReportDTO> listReport(int offset, int size, String schType, String kwd) {
		List<ReportDTO> list=new ArrayList<ReportDTO>();
		PreparedStatement pstmt=null;
		StringBuilder sb=new StringBuilder();
		ResultSet rs=null;
		
		try {
			sb.append(" SELECT R.NUM, NAME, SUBJECT, ");
			sb.append(" CONTENT, TO_CHAR(R.REG_DATE, 'YYYYMMDD') AS REG_DATE ");
			sb.append(" FROM REPORT R JOIN MEMBER1 M");
			sb.append(" ON R.MEMBER_ID=M.MEMBER_ID ");
			if(schType.equals("all")) {
				sb.append(" WHERE INSTR(SUBJECT, ?)>=1 OR INSTR(CONTENT, ?)>=1 ");
			} else if(schType.equals("reg_date")) {
				kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
				sb.append(" WHERE TO_CHAR(R.REG_DATE, 'YYYYMMDD')=? ");
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
				ReportDTO dto=new ReportDTO();
				
				dto.setNum(rs.getLong(1));
				dto.setUserName(rs.getString(2));
				dto.setSubject(rs.getString(3));
				dto.setContent(rs.getString(4));
				dto.setReg_date(rs.getString(5));
				
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
	
	public ReportDTO findById(long num) {
		ReportDTO dto=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			sb.append(" SELECT R.NUM, R.MEMBER_ID, NAME, SUBJECT, ");
			sb.append(" CONTENT, TO_CHAR(R.REG_DATE, 'YYYYMMDD') AS REG_DATE ");
			sb.append(" FROM REPORT R JOIN MEMBER1 M");
			sb.append(" ON R.MEMBER_ID=M.MEMBER_ID ");
			sb.append(" WHERE R.NUM=? ");
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setLong(1, num);
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				dto=new ReportDTO();
				
				dto.setNum(rs.getInt(1));
				dto.setMember_id(rs.getString(2));
				dto.setUserName(rs.getString(3));
				dto.setSubject(rs.getString(4));
				dto.setContent(rs.getString(5));
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
	public ReportDTO findByPrev(long num, String schType, String kwd) {
		ReportDTO dto=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			if (kwd != null && kwd.length() != 0) {
				sb.append(" SELECT R.NUM, NAME, SUBJECT, ");
				sb.append(" CONTENT, TO_CHAR(R.REG_DATE, 'YYYYMMDD') AS REG_DATE ");
				sb.append(" FROM REPORT R JOIN MEMBER1 M");
				sb.append(" ON R.MEMBER_ID=M.MEMBER_ID ");
				sb.append(" WHERE R.NUM<? ");
				if(schType.equals("all")) {
					sb.append(" AND INSTR(SUBJECT, ?)>=1 OR INSTR(CONTENT, ?)>=1 ");
				} else if(schType.equals("reg_date")) {
					kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
					sb.append(" AND TO_CHAR(R.REG_DATE, 'YYYYMMDD')=? ");
				} else {
					sb.append(" AND INSTR("+schType+", ?)>=1 ");
				}
				sb.append(" ORDER BY NUM DESC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");
				pstmt=conn.prepareStatement(sb.toString());
				
				if(schType.equals("all")) {
					pstmt.setLong(1, num);
					pstmt.setString(2, kwd);
					pstmt.setString(3, kwd);
					
				} else {
					pstmt.setLong(1, num);
					pstmt.setString(2, kwd);
				}
			} else {
				sb.append(" SELECT R.NUM, NAME, SUBJECT, ");
				sb.append(" CONTENT, TO_CHAR(R.REG_DATE, 'YYYYMMDD') AS REG_DATE ");
				sb.append(" FROM REPORT R JOIN MEMBER1 M");
				sb.append(" ON R.MEMBER_ID=M.MEMBER_ID ");
				sb.append(" WHERE R.NUM<? ");
				sb.append(" ORDER BY NUM DESC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");
				pstmt=conn.prepareStatement(sb.toString());
				pstmt.setLong(1, num);
			}
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				dto=new ReportDTO();
				
				dto.setNum(rs.getInt(1));
				dto.setUserName(rs.getString(2));
				dto.setSubject(rs.getString(3));
				dto.setContent(rs.getString(4));
				dto.setReg_date(rs.getString(5));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		return dto;
	}
	public ReportDTO findByNext(long num, String schType, String kwd) {
		ReportDTO dto=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			if (kwd != null && kwd.length() != 0) {
				sb.append(" SELECT R.NUM, NAME, SUBJECT, ");
				sb.append(" CONTENT, TO_CHAR(R.REG_DATE, 'YYYYMMDD') AS REG_DATE ");
				sb.append(" FROM REPORT R JOIN MEMBER1 M");
				sb.append(" ON R.MEMBER_ID=M.MEMBER_ID ");
				sb.append(" WHERE R.NUM>? ");
				if(schType.equals("all")) {
					sb.append(" AND INSTR(SUBJECT, ?)>=1 OR INSTR(CONTENT, ?)>=1 ");
				} else if(schType.equals("reg_date")) {
					kwd = kwd.replaceAll("(\\-|\\/|\\.)", "");
					sb.append(" AND TO_CHAR(R.REG_DATE, 'YYYYMMDD')=? ");
				} else {
					sb.append(" AND INSTR("+schType+", ?)>=1 ");
				}
				sb.append(" ORDER BY NUM ASC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");
				pstmt=conn.prepareStatement(sb.toString());
				
				if(schType.equals("all")) {
					pstmt.setLong(1, num);
					pstmt.setString(2, kwd);
					pstmt.setString(3, kwd);
				} else {
					pstmt.setLong(1, num);
					pstmt.setString(2, kwd);
				}
			} else {
				sb.append(" SELECT R.NUM, NAME, SUBJECT, ");
				sb.append(" CONTENT, TO_CHAR(R.REG_DATE, 'YYYYMMDD') AS REG_DATE ");
				sb.append(" FROM REPORT R JOIN MEMBER1 M");
				sb.append(" ON R.MEMBER_ID=M.MEMBER_ID ");
				sb.append(" WHERE R.NUM>? ");
				sb.append(" ORDER BY NUM ASC ");
				sb.append(" FETCH FIRST 1 ROWS ONLY ");
				pstmt=conn.prepareStatement(sb.toString());
				pstmt.setLong(1, num);
			}
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				dto=new ReportDTO();
				
				dto.setNum(rs.getInt(1));
				dto.setUserName(rs.getString(2));
				dto.setSubject(rs.getString(3));
				dto.setContent(rs.getString(4));
				dto.setReg_date(rs.getString(5));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		return dto;
	}
	public List<ReportDTO> listReportFile(long num) {
		List<ReportDTO> list=new ArrayList<ReportDTO>();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			sb.append(" SELECT NUM, FILENUM, SAVEFILENAME, UPLOADFILENAME ");
			sb.append(" FROM REPORT_FILE WHERE NUM=? ");
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setLong(1, num);
			rs=pstmt.executeQuery();
			
			while(rs.next()) {
				ReportDTO dto=new ReportDTO();
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
	public int dataCount(String userId) {
		PreparedStatement pstmt=null;
		StringBuilder sb=new StringBuilder();
		ResultSet rs=null;
		int result=0;
		
		try {
			sb.append(" SELECT COUNT(*) FROM REPORT WHERE MEMBER_ID=?");
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setString(1, userId);
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
	public List<ReportDTO> listReport(int offset, int size, String userId) {
		List<ReportDTO> list=new ArrayList<ReportDTO>();
		PreparedStatement pstmt=null;
		StringBuilder sb=new StringBuilder();
		ResultSet rs=null;
		
		try {
			sb.append(" SELECT NUM, NAME, SUBJECT, CONTENT, REG_DATE FROM ( ");
			sb.append(" SELECT R.NUM AS NUM, NAME, SUBJECT, ");
			sb.append(" CONTENT, TO_CHAR(R.REG_DATE, 'YYYYMMDD') AS REG_DATE ");
			sb.append(" FROM REPORT R JOIN MEMBER1 M");
			sb.append(" ON R.MEMBER_ID=M.MEMBER_ID ");
			sb.append(" WHERE R.MEMBER_ID=? ");
			sb.append(" ) ");
			sb.append(" ORDER BY NUM DESC ");
			sb.append(" OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ");
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setString(1, userId);
			pstmt.setInt(2, offset);
			pstmt.setInt(3, size);
			rs=pstmt.executeQuery();
			while(rs.next()) {
				ReportDTO dto=new ReportDTO();
				
				dto.setNum(rs.getLong(1));
				dto.setUserName(rs.getString(2));
				dto.setSubject(rs.getString(3));
				dto.setContent(rs.getString(4));
				dto.setReg_date(rs.getString(5));
				
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
	public void updateReport(ReportDTO dto) {
		PreparedStatement pstmt=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			sb.append(" UPDATE REPORT SET SUBJECT=?, CONTENT=? ");
			sb.append(" WHERE NUM=? ");
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setLong(3, dto.getNum());
			pstmt.executeQuery();
			DBUtil.close(pstmt);
			pstmt=null;
			
			sb=new StringBuilder();
			sb.append(" INSERT INTO REPORT_FILE(FILENUM, NUM, SAVEFILENAME, UPLOADFILENAME) ");
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
	public ReportDTO findByPrev(long num, String userId) {
		ReportDTO dto=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuilder sb=new StringBuilder();
		
		try {	
			sb.append(" SELECT R.NUM, NAME, SUBJECT, ");
			sb.append(" CONTENT, TO_CHAR(R.REG_DATE, 'YYYYMMDD') AS REG_DATE ");
			sb.append(" FROM REPORT R JOIN MEMBER1 M");
			sb.append(" ON R.MEMBER_ID=M.MEMBER_ID ");
			sb.append(" WHERE R.NUM<? AND R.MEMBER_ID='"+userId+"'");
			sb.append(" ORDER BY NUM DESC ");
			sb.append(" FETCH FIRST 1 ROWS ONLY ");
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setLong(1, num);
			rs=pstmt.executeQuery();
	
			if(rs.next()) {
				dto=new ReportDTO();
		
				dto.setNum(rs.getInt(1));
				dto.setUserName(rs.getString(2));
				dto.setSubject(rs.getString(3));
				dto.setContent(rs.getString(4));
				dto.setReg_date(rs.getString(5));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		return dto;
	}
	public ReportDTO findByNext(long num, String userId) {
		ReportDTO dto=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuilder sb=new StringBuilder();
		
		try {	
			sb.append(" SELECT R.NUM, NAME, SUBJECT, ");
			sb.append(" CONTENT, TO_CHAR(R.REG_DATE, 'YYYYMMDD') AS REG_DATE ");
			sb.append(" FROM REPORT R JOIN MEMBER1 M");
			sb.append(" ON R.MEMBER_ID=M.MEMBER_ID ");
			sb.append(" WHERE R.NUM>? AND R.MEMBER_ID='"+userId+"'");
			sb.append(" ORDER BY NUM ASC ");
			sb.append(" FETCH FIRST 1 ROWS ONLY ");
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setLong(1, num);
			rs=pstmt.executeQuery();
	
			if(rs.next()) {
				dto=new ReportDTO();
		
				dto.setNum(rs.getInt(1));
				dto.setUserName(rs.getString(2));
				dto.setSubject(rs.getString(3));
				dto.setContent(rs.getString(4));
				dto.setReg_date(rs.getString(5));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(rs);
			DBUtil.close(pstmt);
		}
		return dto;
	}
	public void deleteReportFile(long num) {
		PreparedStatement pstmt=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			sb.append(" DELETE FROM REPORT_FILE WHERE NUM=? ");
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setLong(1, num);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}
	public void deleteNoticeFile(long num, long fileNum) {
		PreparedStatement pstmt=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			sb.append(" DELETE FROM REPORT_FILE WHERE NUM=? AND FILENUM=? ");
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setLong(1, num);
			pstmt.setLong(2, fileNum);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}
	public void deleteReport(long num) {
		PreparedStatement pstmt=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			sb.append(" DELETE FROM REPORT WHERE NUM=? ");
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setLong(1, num);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(pstmt);
		}
	}
	public ReportDTO findByFileId(long fileNum) {
		ReportDTO dto=null;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuilder sb=new StringBuilder();
		
		try {
			sb.append(" SELECT NUM, FILENUM, SAVEFILENAME, UPLOADFILENAME ");
			sb.append(" FROM REPORT_FILE WHERE FILENUM=? ");
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setLong(1, fileNum);
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				dto=new ReportDTO();
				
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
}
