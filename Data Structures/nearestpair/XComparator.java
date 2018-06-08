import java.util.Comparator;

public class XComparator implements Comparator<Point>{
	public XComparator(){
		super();
	}
	
	public int compare(Point c1, Point c2){
		if(c1 == null | c2 == null)
			throw new ClassCastException();
		return Integer.compare(c1.getX(), c2.getX());
	}
}

