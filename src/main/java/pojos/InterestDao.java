package pojos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class InterestDao {
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
	
	public static Interest get(Long id) {
		Connection connection = getConnection();
		Interest interest =  null;
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM public.\"Interest\" WHERE public.\"Interest\".id = " + id);
			if(result.next()) 
				interest = new Interest((long)result.getInt(1), result.getString(2));
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
		return interest;
	}
	public static boolean save(Interest interest) {
		Connection connection = getConnection();
		
		try {
			Statement statement = connection.createStatement();
			if(interest.getId() == null) {
				connection.createStatement().executeUpdate("INSERT INTO public.\"Interest\"(title) VALUES('" + interest.getTitle() + "')");
				ResultSet justDid = statement.executeQuery("SELECT MAX(id) from public.\"Interest\";");
				Long theAdded = new Long(0);
				if (justDid.next()) {
					theAdded = justDid.getLong(1);
				} else {
					System.out.println("AHHHHHHHHH");
				}
				interest.setId(theAdded);
			}
			else {
				ResultSet result = statement.executeQuery("SELECT * FROM public.\"Interest\" WHERE public.\"Interest\".id = " + interest.getId());
				if(result.next()) {
					connection.createStatement().executeUpdate("UPDATE public.\"Interest\" SET title = '" + interest.getTitle() + "'  WHERE id = " + interest.getId()  + ";");
				}
				else {
					throw new RuntimeException("InterestDao.save(Interest): ID Field Not NULL but doesn't exist in the database! "); 
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
				e.printStackTrace();
			}
		}
		return true;
	}
}