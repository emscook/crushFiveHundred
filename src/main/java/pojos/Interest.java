package pojos;

public class Interest {
	private Long id;
	private String title;
	public Interest(){
		
	}
	
	public Interest(Long id, String title){
		this.id = id;
		this.title = title;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String toString() {
		return "" +this.getId() + " " +  this.getTitle();
	}
}
