import java.util.Comparator;

public class YComparator implements Comparator<Point>{
	public YComparator(){
		super();
	}
	
	public int compare(Point c1, Point c2){
		if(c1 == null | c2 == null)
			throw new ClassCastException();
		return Integer.compare(c1.getY(), c2.getY());
	}
}

