package pojos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LocationDao {
	private static Connection getConnection() {
		try {
			Class.forName("org.postgresql.Driver");
			Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ems_cook_interests", "postgres",
					"bondstone");
			return connection;
		} catch (Exception e) {
			System.out.println("Connection failed!");
			e.printStackTrace();
		}
		return null;
	}
	public static Location get(Long id) {
		Connection connection = getConnection();
		Location location =  null;
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM public.\"Location\" WHERE public.\"Location\".id = " + id);
			if(result.next()) 
				location = new Location((long)result.getInt(1), result.getString(2),result.getString(3),result.getString(4));
			
			connection.close();
		} catch (Exception e) {
			System.out.println("Query failed!");
			e.printStackTrace();
		}
		finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return location;
	}
	
	public static boolean save(Location location) throws RuntimeException{
		Connection connection = getConnection();
		try {
			Statement statement = connection.createStatement();
			if(location.getId() == null) {
				connection.createStatement().executeUpdate("INSERT INTO public.\"Location\"(city,state,country) VALUES('" + location.getCity()  + "', '" + location.getState() + "', '" + location.getCountry() + "')");
				ResultSet justDid = statement.executeQuery("SELECT MAX(id) from public.\"Location\";");
				Long theAdded = new Long(0);
				if (justDid.next()) {
					theAdded = justDid.getLong(1);
				} else {
					System.out.println("AHHHHHHHHH");
				}
				location.setId(theAdded);
			}
			else {
				ResultSet result = statement.executeQuery("SELECT * FROM public.\"Location\" WHERE public.\"Location\".id = " + location.getId());
				if(result.next()) {
					connection.createStatement().executeUpdate("UPDATE public.\"Location\" SET " 
				   +  "city = '" +  location.getCity() +    "', "
				   + "state = '" + location.getState() +    "', " 
				   + "country ='" + location.getCountry() + "'  WHERE id = " + location.getId()  + ";");
				}
				else {
					throw new RuntimeException("LocationDao.save(Location): ID Field Not NULL but doesn't exist in the database! "); 
				}
			}
		} catch (Exception e) {
			System.out.println("Query failed!");
			e.printStackTrace();
		}
		finally {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		return true;
	}
}
