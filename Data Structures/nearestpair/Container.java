
//Don't change the class name
public class Container{
	private Point data;//Don't delete or change this field;
	//Added fields

	protected Container next;
	protected Container prev;
	public Container matchingPair;
	//protected Container similarOppAxis;
	
	//Constructor
	public Container(Point data, Container matchingPair){//, Container similarOppAxis){
		this.data = data;
		this.next = null;
		this.prev = null;
		this.matchingPair = matchingPair;
	}
	//Don't delete or change this function
	public Point getData()
	{
		return data;
	}
	
	public void setMatchingPair(Container match){
		this.matchingPair = match;
	}
	
}
