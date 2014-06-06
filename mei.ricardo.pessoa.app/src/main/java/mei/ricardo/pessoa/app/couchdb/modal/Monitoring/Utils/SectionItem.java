package mei.ricardo.pessoa.app.couchdb.modal.Monitoring.Utils;

public class SectionItem implements InterfaceItem {

	private final String title;
	
	public SectionItem(String title) {
		this.title = title;
	}
	
	public String getTitle(){
		return title;
	}
	
	@Override
	public boolean isSection() {
		return true;
	}

}
