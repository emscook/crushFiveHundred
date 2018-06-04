package pojos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public class PersonDao {
	public static Person get(Long id) {
		Connection connection = getConnection();
		Person person = null;
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement
					.executeQuery("SELECT * FROM public.\"Person\" WHERE public.\"Person\".id = " + id);
			if (result.next())
				person = new Person(result.getLong(1), result.getString(2), result.getString(3), result.getInt(4),
						LocationDao.get(result.getLong(5)), PersonDao.getInterests(id));
			connection.close();
		} catch (Exception e) {
			System.out.println("Query failed!");
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return person;
	}

	private static Connection getConnection() {
		try {
			Class.forName("org.postgresql.Driver");
			Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ems_cook_interests",
					"postgres", "bondstone");
			return connection;
		} catch (Exception e) {
			System.out.println("Connection failed!");
			e.printStackTrace();
		}
		return null;
	}

	public static boolean save(Person myPerson) throws RuntimeException {
		Connection connection = getConnection();
		try {
			Statement statement = connection.createStatement();
			LocationDao.save(myPerson.getLocation());
			for (Interest hisInterest : myPerson.getInterests())
				InterestDao.save(hisInterest);
			if (myPerson.getId() == null) {
				connection.createStatement().executeUpdate(
						"INSERT INTO public.\"Person\"(\"firstName\",\"lastName\",age,\"locID\") VALUES('"
								+ myPerson.getFirstName() + "', '" + myPerson.getLastName() + "', " + myPerson.getAge()
								+ ", " + (myPerson.getLocation()).getId() + ")");
				ResultSet justDid = statement.executeQuery("SELECT MAX(id) from public.\"Person\";");
				Long theAdded = new Long(0);
				if (justDid.next()) {
					theAdded = justDid.getLong(1);
				} else {
					System.out.println("AHHHHHHHHH");
				}
				myPerson.setId(theAdded);
				for (Interest curInterest : myPerson.getInterests()) {
					connection.createStatement().executeUpdate(
							"INSERT INTO public.\"PersonInterest\"(\"person_id\",\"interest_id\") VALUES(" + theAdded
									+ ", " + curInterest.getId() + ");");
				}
			} else {
				ResultSet result = statement.executeQuery(
						"SELECT * FROM public.\"Person\" WHERE public.\"Person\".id = " + myPerson.getId());
				if (result.next()) {
					connection.createStatement()
							.executeUpdate("UPDATE public.\"Person\" SET \"firstName\" = '" + myPerson.getFirstName()
									+ "', \"lastName\" = '" + myPerson.getLastName() + "', age =" + myPerson.getAge()
									+ ", \"locID\" = " + myPerson.getLocation().getId() + " WHERE id = "
									+ myPerson.getId() + ";");
					removeOldInterestsAddNews(myPerson);
				} else {
					throw new RuntimeException(
							"PersonDao.save(Person): ID Field Not NULL but doesn't exist in the database! ");
				}
			}
		} catch (SQLException e) {
			System.out.println("Query failed!");
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	private static Set<Interest> getInterests(Long personId) throws SQLException {
		HashSet<Interest> currInterests = new HashSet<Interest>();
		Connection connection = getConnection();
		Statement resultStatement = connection.createStatement();
		ResultSet interestResult = resultStatement
				.executeQuery("SELECT *  from public.\"PersonInterest\" WHERE person_id = " + personId);
		while (interestResult.next())
			currInterests.add(InterestDao.get(interestResult.getLong(3)));
		return currInterests;
	}

	private static void removeOldInterestsAddNews(Person changePerson) throws SQLException {
		Connection connection = getConnection();
		Set<Interest> newInterests = changePerson.getInterests();
		Set<Interest> oldInterests = PersonDao.getInterests(changePerson.getId());
		Set<Long> newIds = new HashSet<Long>();
		Set<Long> oldIds = new HashSet<Long>();
		for (Interest oldy : oldInterests)
			oldIds.add(oldy.getId());
		
		for (Interest newInterest : newInterests)
			newIds.add(newInterest.getId());
		for (Long oldId : oldIds)
			if (!newIds.contains(oldId))
				connection.createStatement().executeUpdate("DELETE FROM public.\"PersonInterest\" WHERE person_id = "
						+ changePerson.getId() + " AND interest_id = " + oldId.longValue());
		for (Long newId : newIds)
			if (!oldIds.contains(newId))
				connection.createStatement()
						.executeUpdate("INSERT INTO public.\"PersonInterest\"(person_id, interest_id) VALUES("
								+ changePerson.getId() + ", " + newId.longValue() + ")");
	}

	public static Set<Person> findInterestGroup(Interest interest, Location location) {
		Set<Person> runningSet = new HashSet<Person>();
		Connection connection = getConnection();
		try {
			Statement statement = connection.createStatement();
			ResultSet result = statement.executeQuery(
					"SELECT public.\"Person\".id, public.\"Person\".\"firstName\", public.\"Person\".\"lastName\", public.\"Person\".age, public.\"Person\".\"locID\" "
							+ "FROM public.\"Person\" " + "join public.\"Location\" "
							+ "on public.\"Location\".id = public.\"Person\".\"locID\" "
							+ "and public.\"Location\".city = '" + location.getCity() + "' "
							+ "and public.\"Location\".state = '" + location.getState() + "' "
							+ "and public.\"Location\".country = '" + location.getCountry() + "' "
							+ "join public.\"PersonInterest\" "
							+ "on public.\"PersonInterest\".\"person_id\" = public.\"Person\".id "
							+ "join public.\"Interest\" "
							+ "on public.\"Interest\".id = public.\"PersonInterest\".\"interest_id\" "
							+ "and public.\"Interest\".title = '" + interest.getTitle() + "' "
							+ "order by public.\"Person\".id");
			while (result.next()) 
				runningSet.add(new Person(new Long(result.getLong(1)), result.getString(2), result.getString(3),
						result.getInt(4), LocationDao.get(new Long(result.getLong(5))), PersonDao.getInterests(result.getLong(1))));
			
		} catch (SQLException e) {
			System.out.println("Query failed!");
			e.printStackTrace();
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return runningSet;
	}

//	private static void printPeople() {
//		System.out.println("[People]");
//		Connection connection = getConnection();
//		try {
//			Statement statement = connection.createStatement();
//			ResultSet result = statement.executeQuery("SELECT * FROM public.\"Person\"");
//			while (result.next())
//				System.out.println(new Person((long) result.getInt(1), result.getString(2), result.getString(3), result.getInt(4),LocationDao.get((long) result.getInt(5)), PersonDao.getInterests(result.getLong(1))));
//			connection.close();
//		} catch (Exception e) {
//			System.out.println("Query failed!");
//			e.printStackTrace();
//		} finally {
//			try {
//				connection.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//		System.out.println("");
//	}
//
//	private static void printLocations() {
//		Location newPlace;
//		int currentPlace = 1;
//		System.out.println("[Places]");
//		while ((newPlace = LocationDao.get(new Long(currentPlace++))) != null)
//			System.out.println(newPlace);
//		System.out.println("");
//	}
//	
//	private static void printInterests() {
//		Interest newAction;
//		int currentAction = 1;
//		System.out.println("[Interests]");
//		while ((newAction = InterestDao.get(new Long(currentAction++))) != null)
//			System.out.println(newAction);
//		System.out.println("");
//	}
	
//	private static void printInterestGroup(Interest thisInterest, Location thisLocation) {
//		System.out.println("Interest Group for [" + thisInterest +"] and [" + thisLocation +"]");
//		Set<Person> interestSet = findInterestGroup(thisInterest,thisLocation);
//		for(Person interestingDude : interestSet) 
//			System.out.println(interestingDude);
//		System.out.println("");
//	}
	
//	private static Long getHighestId() throws SQLException {
//		Connection connection = getConnection();
//		Statement statement = connection.createStatement();
//		ResultSet justDid = statement.executeQuery("SELECT MAX(id) from public.\"Person\";");
//		Long theAdded = new Long(0);
//		if (justDid.next())
//			theAdded = justDid.getLong(1);
//		return theAdded;
//	}
	
//	public static void main(String[] args) throws SQLException {
//		printInterestGroup(InterestDao.get(new Long(1)),LocationDao.get(new Long(2)));
//		Set<Interest> pewSet = new HashSet<Interest>();
//		pewSet.add(InterestDao.get(new Long(3)));
//		pewSet.add(InterestDao.get(new Long(8)));
//		pewSet.add(InterestDao.get(new Long(7)));
//		PersonDao.save(new Person(null, "nully", "nullerson", 55, LocationDao.get(new Long(1)), pewSet));
//		PersonDao.printPeople();
//		PersonDao.printLocations();
//		PersonDao.printInterests();
//		Long highestId = getHighestId();
//		Person finalDude = PersonDao.get(highestId);
//		System.out.println(finalDude);
//		System.out.println("");
//		HashSet<Interest> fixEm = new HashSet<Interest>();
//		fixEm.add(InterestDao.get(new Long(7)));
//		fixEm.add(InterestDao.get(new Long(8)));
//		fixEm.add(InterestDao.get(new Long(5)));
//		finalDude.setInterests(fixEm);
//
//		PersonDao.save(finalDude);
//		Person interestTest = PersonDao.get(highestId);
//		System.out.println(interestTest);
//		PersonDao.printLocations();
//		PersonDao.printInterests();
//		
//		
//		
//		//Here we're making a new person, with a new interest and a new location
//		Set<Interest> finalInterests = new HashSet<Interest>();
//		finalInterests.add(new Interest(null, "Crazy Dog"));
//		finalInterests.add(new Interest(new Long(6), "Flip Endo!"));
//		Person realFinalDude = new Person(null, "Oh no", "What do?", 55, new Location(null, "Nowhere that","You want","To be"),finalInterests);
//		System.out.println(realFinalDude);
//		PersonDao.save(realFinalDude);
//		highestId = getHighestId();
//		realFinalDude = PersonDao.get(highestId);
//		System.out.println(realFinalDude);
//		PersonDao.printLocations();
//		PersonDao.printInterests();
//	}
}
