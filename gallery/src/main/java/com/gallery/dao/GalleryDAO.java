package com.gallery.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.gallery.domain.GalleryDTO;
import com.gallery.util.DBConn;
import com.gallery.util.DBUtil;



public class GalleryDAO {
	private Connection conn = DBConn.getConnection();
	
	public void insertPhoto(GalleryDTO dto) throws SQLException {
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "INSERT INTO gallery(num, member_id, reg_date, introduce, img) "
					+ " VALUES(gallery_seq.NEXTVAL, ?, SYSDATE, ?, ? )";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getMember_id());
			pstmt.setString(2, dto.getIntroduce());
			pstmt.setString(3, dto.getImg());
			
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			DBUtil.close(pstmt);
		}
	}
	
	
	
	
	public int dataCount() {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT COUNT(*) FROM gallery";
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
	
	
	
	
	
	public List<GalleryDTO> listPhoto(int offset, int size, String artist) {
        List<GalleryDTO> list = new ArrayList<GalleryDTO>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql;

        try {
            sql = "SELECT num, p.member_id, Img, introduce, name,  "
                    + " TO_CHAR(p.reg_date, 'YYYY-MM-DD') reg_date "
                    + " FROM gallery p  "
                    + " JOIN member1 m ON p.member_id = m.member_id "
                    + " WHERE p.member_id = ? "
                    + " ORDER BY num DESC " 
                    + " OFFSET ? ROWS FETCH FIRST ? ROWS ONLY ";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, artist);
            pstmt.setInt(2, offset);
            pstmt.setInt(3, size);
            

            rs = pstmt.executeQuery();

            while(rs.next()) {
                GalleryDTO dto = new GalleryDTO();

                dto.setNum(rs.getLong("num"));
                dto.setMember_id(rs.getString("member_id"));
                dto.setImg(rs.getString("img"));
                dto.setIntroduce(rs.getString("introduce"));
                dto.setReg_date(rs.getString("reg_date"));
                dto.setArtistName(rs.getString("name"));

                list.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(rs);
            DBUtil.close(pstmt);
        }

        return list;
	}
	
	
	
	
	//클릭 > 사진페이지
		public GalleryDTO findById(long num) {
			GalleryDTO dto = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql;
			
			try {
				sql = "SELECT num, name, "
						+ " Img, introduce, p.reg_date  "
						+ " FROM gallery p "
						+ " JOIN member1 m ON p.member_id = m.member_id "
						+ " WHERE num  = ? ";

				pstmt = conn.prepareStatement(sql);
				
				pstmt.setLong(1, num);
				
				rs = pstmt.executeQuery();
				
				if(rs.next()) {
					dto = new GalleryDTO();
					
					dto.setNum(rs.getLong("num"));
					dto.setArtistName(rs.getString("name"));
					dto.setIntroduce(rs.getString("introduce"));
					dto.setImg(rs.getString("Img"));
					dto.setReg_date(rs.getString("reg_date"));
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBUtil.close(rs);
				DBUtil.close(pstmt);
			}
			
			return dto;
		}
	
	
	
	
	//사진 삭제
		public void deletePhoto(long num) throws SQLException {
			PreparedStatement pstmt = null;
			
			String sql;
			
			try {
				sql = "DELETE FROM gallery WHERE num = ? ";
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setLong(1, num);
				
				pstmt.executeUpdate();
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DBUtil.close(pstmt);
			}
		}
	
		//사진 수정
				public void updateGallery(GalleryDTO dto) throws SQLException {
					PreparedStatement pstmt = null;
					String sql;
					
					try {
						sql = "UPDATE gallery SET introduce=?, img=?, member_id=? "
								+ " where num = ? ";
						
						pstmt = conn.prepareStatement(sql);
						
						pstmt.setString(1, dto.getIntroduce());
						pstmt.setString(2, dto.getImg());
						pstmt.setString(3,  dto.getMember_id());
						pstmt.setLong(4,  dto.getNum());
						
						pstmt.executeUpdate();
						
						
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						DBUtil.close(pstmt);
					}
					
				}
	
}
