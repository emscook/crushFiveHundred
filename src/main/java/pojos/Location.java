package pojos;

public class Location {
	private Long id;
	private String city;
	private String state;
	private String country;
	public Location() {
		
	}
	public Location(Long id, String city, String state, String country) {
		this.id = id;
		this.city = city;
		this.state = state;
		this.country = country;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
	
	public String toString() {
		return "" +this.getId() + " " +  this.getCity() + " " + this.getState() + " " + this.getCountry();
	}

}
