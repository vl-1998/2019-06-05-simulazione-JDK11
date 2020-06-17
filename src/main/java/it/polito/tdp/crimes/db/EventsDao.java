package it.polito.tdp.crimes.db;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


import it.polito.tdp.crimes.model.Event;



public class EventsDao {
	
	public List<Event> listAllEvents(){
		String sql = "SELECT * FROM events" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Event> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Event(res.getLong("incident_id"),
							res.getInt("offense_code"),
							res.getInt("offense_code_extension"), 
							res.getString("offense_type_id"), 
							res.getString("offense_category_id"),
							res.getTimestamp("reported_date").toLocalDateTime(),
							res.getString("incident_address"),
							res.getDouble("geo_lon"),
							res.getDouble("geo_lat"),
							res.getInt("district_id"),
							res.getInt("precinct_id"), 
							res.getString("neighborhood_id"),
							res.getInt("is_crime"),
							res.getInt("is_traffic")));
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List <LocalDate> getDate(){
		String sql = "select DATE(reported_date) as data from events";
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			List<LocalDate> result = new ArrayList<>();
			ResultSet res = st.executeQuery() ;
			
			while (res.next()) {
				result.add((res.getDate("data").toLocalDate()));
			}
			conn.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Integer> getVertex (Integer anno){
		String sql = "Select distinct district_id " + 
				"from events " + 
				"where YEAR(reported_date)=?";
		try {
			List <Integer> result = new ArrayList<>();
		
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, anno);
			ResultSet res = st.executeQuery() ;
			
			while (res.next()) {
				result.add(res.getInt("district_id"));
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}	
	}
	
	public Double getLon (Integer anno, Integer district) {
		String sql ="select AVG(geo_lon) as media " + 
				"from events " + 
				"where district_id=? " + 
				"and YEAR(reported_date)=?";
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, district);
			st.setInt(2, anno);
			ResultSet res = st.executeQuery() ;
			
			if (res.next()) {
				Double result = res.getDouble("media");
				conn.close();
				return result;
			} else {
				conn.close();
				return null;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}
	}
	
	public Double getLat (Integer anno, Integer district) {
		String sql ="select AVG(geo_lat) as media " + 
				"from events " + 
				"where district_id=? " + 
				"and YEAR(reported_date)=?";
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;
			st.setInt(1, district);
			st.setInt(2, anno);
			ResultSet res = st.executeQuery() ;
			
			if (res.next()) {
				Double result = res.getDouble("media");
				conn.close();
				return result;
			} else {
				conn.close();
				return null;
			}		
		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}
	}
	
	

}
