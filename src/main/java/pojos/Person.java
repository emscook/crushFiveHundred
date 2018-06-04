package pojos;

import java.util.Set;

public class Person {
	private Long id;
	private int age;
	private String firstName;
	private String lastName;
	private Location location;
	private Set<Interest> interests;
	
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}

	public Set<Interest> getInterests() {
		return interests;
	}

	public void setInterests(Set<Interest> interests) {
		this.interests = interests;
	}

	public Person() {

	}

	public Person(Long id, String firstName, String lastName, int age, Location location, Set<Interest> interests) {
		this.id = id;
		this.age = age;
		this.firstName = firstName;
		this.lastName = lastName;
		this.location = location;
		this.interests = interests;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String toString() {
		String retString = "<Person id= " + this.getId() + ", name= " +  this.getFirstName() + " " + this.getLastName() + ", age= " +  this.getAge() +">\n";
		 retString += "<Location> " + this.getLocation() + "</Location>\n";
		 retString += "<Interests>\n";
		 for(Interest myInterest: this.getInterests()) {
			 retString += "<Interest>" + myInterest + "</Interest>\n";
		 }
		 retString += "</Interests>\n";
		 retString += "</Person>";
		return retString;
	}

}
