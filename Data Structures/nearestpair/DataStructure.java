import java.util.Arrays;

public class DataStructure implements DT {

    private Container firstX;
    private Container lastX;
    private Container firstY;
    private Container lastY;
    private int size;
   
 
    //////////////// DON'T DELETE THIS CONSTRUCTOR ////////////////
    public DataStructure()  {
        firstX = null;
        lastX = null;
        firstY = null;
        lastY = null;
        size = 0;
    }

    
    //Methods
	@Override
	public void addPoint(Point point) {
        Container x = new Container (point, null);
        Container y = new Container (point, x);
        x.setMatchingPair (y);
        if (size==0){
            firstX = x;
            firstY= y;
            lastX = x;
            lastY = y;
        }
        else{ //Lists are not empty
            //add to X list
            if (point.getX()<firstX.getData().getX()){
                x.next = firstX;
                firstX.prev = x;
                firstX = x;
            }
            else{
                Container currX = firstX;
                while (currX != null && point.getX()>currX.getData().getX()) {
                    currX = currX.next;
                }
                if (currX==null){
                    lastX.next = x;
                    x.prev = lastX;
                    lastX = x;
                }
                else{
                    x.next = currX;
                    x.prev = currX.prev;
                    currX.prev.next = x;
                    currX.prev = x;
                }
            }
            //add to Y list
            if (point.getY()<firstY.getData().getY()){
                y.next = firstY;
                firstY.prev = y;
                firstY = y;
            }
            else{
                Container currY = firstY;
                while (currY != null && point.getY()>currY.getData().getY()) {
                    currY = currY.next;
                }
                if (currY==null){
                    lastY.next = y;
                    y.prev = lastY;
                    lastY = y;
                }
                else{
                    y.next = currY;
                    y.prev = currY.prev;
                    currY.prev.next = y;
                    currY.prev = y;

                }
            }
        }
        size++;
    }

	@Override
	public Point[] getPointsInRangeRegAxis(int min, int max, Boolean axis) {
		//Initialize return array with helper method 
		Point[] pointsArr = new Point[counter(min, max, axis)];
		//Fill in the array with fitting points
		int ind = 0;
		if(axis){
			Container currX = firstX;
			while (currX!=null){
				if(((Point)currX.getData()).getX() >= min &&((Point)currX.getData()).getX() <= max){
					pointsArr[ind] = currX.getData();
					ind++;
				}
				currX = currX.next;
			}
		}
		else{
			Container currY = firstY;
			while (currY!=null){
				if(((Point)currY.getData()).getY() >= min &&((Point)currY.getData()).getY() <= max){
					pointsArr[ind] = currY.getData();
					ind++;
				}
				currY = currY.next;
			}
		}
		
		return pointsArr;
	}
	//Helper method - O(n)
	private int counter(int min, int max, Boolean axis){
		int count = 0;
		if(axis){
			for(Container currX = firstX; currX!=null ; currX=currX.next)
				if(((Point)currX.getData()).getX() >= min &&((Point)currX.getData()).getX() <= max)
					count++;
		}
		else{
			for(Container currY = firstY; currY!=null ; currY=currY.next)
				if(((Point)currY.getData()).getY() >= min &&((Point)currY.getData()).getY() <= max)
					count++;
		}
		
		return count;
	}

	@Override
	public Point[] getPointsInRangeOppAxis(int min, int max, Boolean axis) {
		//initialize size with helper method
		Point[] pointsArr = new Point[counter(min, max, axis)];
		//fill in the array
		if(axis){
			Container currY = firstY;
			int ind = 0;
			while(currY!=null){
				if(currY.getData().getX()>=min & currY.getData().getX()<=max){
					pointsArr[ind] = currY.getData();
					ind++;
				}
				currY=currY.next;
			}
		}
		else{
			Container currX = firstX;
			int ind = 0;
			while(currX!=null){
				if(currX.getData().getY()>=min && currX.getData().getY()<=max){
					pointsArr[ind] = currX.getData();
					ind++;
				}
				currX=currX.next;
			}
			
		}
		return pointsArr;
	}

	@Override
	public double getDensity() {
		if(size ==0 |size ==1)
			return 0.0;
		return ((double)size/((lastX.getData().getX()-firstX.getData().getX())*(lastY.getData().getY()-firstY.getData().getY())));
	}

	@Override
	public void narrowRange(int min, int max, Boolean axis) {
		if(size > 0){ //lists are not empty
			if(axis){ // X axis
				//if min > biggest x or max < smallest x, clears the DataStructure
				if(min > lastX.getData().getX() || max < firstX.getData().getX()){
					firstX = null;
					lastX = null;
					firstY = null;
					lastY = null;
					size = 0;
				}else{
					//fix from the start forwards
					Container currX = firstX;
					while (currX!=null && currX.getData().getX()<min){
						removeMatching(currX.matchingPair);
						currX = currX.next;
						size--;
					}
					firstX = currX;
					firstX.prev = null;
					//fix from the end backwards
					currX = lastX;
					while (currX!=null && currX.getData().getX()>max){
						removeMatching(currX.matchingPair);
						currX = currX.prev;
						size--;
					}
					lastX = currX;
					if(null == lastX)
						firstX = currX;
					if(lastX != null)
						lastX.next = null;
				}
			}
			else{ // Y axis
				//if min > biggest y or max < smallest y, clears the DataStructure
				if(min > lastY.getData().getY() || max < firstY.getData().getY()){
					firstX = null;
					lastX = null;
					firstY = null;
					lastY = null;
					size = 0;
				}else{
					//fix from the start forwards
					Container currY = firstY;
					while (currY!=null && currY.getData().getY()<min){
						removeMatching(currY.matchingPair);
						currY = currY.next;
						size--;
					}
					firstY = currY;
					firstY.prev = null;
					//fix from the end backwards
					currY = lastY;
					while (currY!=null && currY.getData().getY()>max){
						removeMatching(currY.matchingPair);
						currY = currY.prev;
						size--;
					}
					lastY = currY;
					if(null == lastY)
						firstY = currY;
					if(lastY != null)
						lastY.next = null;
				}
			}
		}
	}
	//remove matchingPair@oppAxis helper method: O(1)
	private void removeMatching(Container toRemove){
		if (toRemove==firstX | toRemove == firstY){
			if(toRemove == firstX && toRemove == lastX){
				firstX = null;
				lastX = null;
			}
			else if(toRemove == firstX){
				firstX = toRemove.next;
				firstX.prev = null;
			}
			else{//toRemove == firstY
				if(toRemove == firstY && toRemove == lastY){
					firstY = null;
					lastY = null;
				}
				else{
				firstY = toRemove.next;
				firstY.prev = null;
				}
			}
		}
		else if(toRemove == lastX | toRemove == lastY){
			if (toRemove == lastX){
				lastX = toRemove.prev;
				lastX.next = null;
			}
			else{//toRemove == lastY
				lastY = toRemove.prev;
				lastY.next = null;
			}
		}
		else{//toRemove is a middle link in the list
			toRemove.prev.next = toRemove.next;
			toRemove.next.prev = toRemove.prev;
		}
	}
	
	public Boolean getLargestAxis() {
		if (size==0)
			return null;
		else if (size==1)
			return (firstX.getData().getX() > firstY.getData().getY());
		else return (lastX.getData().getX()-firstX.getData().getX() > lastY.getData().getY()-firstY.getData().getY());		
	}
	
	@Override
	public Container getMedian(Boolean axis) {
        if (size==0)
            return null;
        int counter=0;
        if (axis){
            Container currX = firstX;
            while (counter!=size/2){
                currX = currX.next;
                counter++;
            }
            return currX;
        }
        else{
            Container currY = firstY;
            while (counter!=size/2){
                currY = currY.next;
                counter++;
            }
            return currY;
        }
    }

	@Override
	public Point[] nearestPairInStrip(Container container, double width, Boolean axis) {
		Point[] nP = new Point[2];
		int b = 1; //points in strip counter, 1 due to given container
		Container curr;
		if(axis){
			curr = container.prev;
			while(curr!=null && curr.getData().getX() > container.getData().getX()-((int)width)){
				b++;
				curr=curr.prev;
			}
			curr = container.next;
			while(curr!=null && curr.getData().getX() < container.getData().getX()+((int)width)){
				b++;
				curr=curr.next;
			}
		}
		else{ //!axis
			curr = container.prev;
			while(curr!=null && curr.getData().getY() > container.getData().getY()-((int)width)){
				b++;
				curr=curr.prev;
			}
			curr = container.next;
			while(curr!=null && curr.getData().getY() < container.getData().getY()+((int)width)){
				b++;
				curr=curr.next;
			}
		}
		Point[] arr;
		if (b<2)
			return null;

		if(size < b*((int)(Math.log((double)b)/Math.log(2.0)))) //Identity noted in pdf
			if(axis)//X axis
				arr = getPointsInRangeOppAxis(container.getData().getX()-((int)width), container.getData().getX()+((int)width), true);
			else //Y axis
				arr = getPointsInRangeOppAxis(container.getData().getY() - ((int)width), container.getData().getY()+((int)width), false);
		else{	//size>blogb
			arr = new Point[b];
			arr[0] = container.getData();

			int index = 1;
			if(axis){
				curr = container.prev;
				while (curr!= null && curr.getData().getX() > container.getData().getX()-((int)width)){
					arr[index] = curr.getData();
					index++;
					curr = curr.prev;
				}
				curr = container.next;
				while (curr!= null && curr.getData().getX() < container.getData().getX()+((int)width)){
					arr[index] = curr.getData();
					index++;
					curr = curr.next;
				}
				Arrays.sort(arr, new YComparator()); //Sort by y value
			}
			else{ //!axis
				curr = container.prev;
				while (curr!=null && curr.getData().getY() > container.getData().getY()-((int)width)){
					arr[index] = curr.getData();
					index++;
					curr = curr.prev;
				}
				curr = container.next;
				while (curr!=null && curr.getData().getY() < container.getData().getY()+((int)width)){
					arr[index] = curr.getData();
					index++;
					curr = curr.next;
				}	
				Arrays.sort(arr, new XComparator()); //Sort by x value
			}
		}
		double minDist = Double.MAX_VALUE;
		for(int i=0; i<arr.length ; i++)
			for(int j=i+1; j<arr.length & j < i+8 ; j++){
				double dist = d(arr[i], arr[j]);
				if ( dist < minDist){
					minDist = dist;
					nP[0] = arr[i];
					nP[1] = arr[j];
				}
			}
		if(nP[0] == null && nP[1] == null)
			return null;
		return nP;
	}

	@Override
	public Point[] nearestPair() {
		if(size < 2)
			return null;
		else if(size == 2){
			Point[] nP = new Point[2];
			nP[0] = firstX.getData();
			nP[1] = lastX.getData();
			return nP;
		} 
		else{
			boolean axis = getLargestAxis();
			Container median = getMedian(axis);
		
			DataStructure DSLeft;
			DataStructure DSRight;
			if(axis){ // X axis
				DSLeft = copyAndCut(firstX.getData().getX(), median.getData().getX()-1, axis);
				DSRight = copyAndCut(median.getData().getX(), lastX.getData().getX(), axis);
			}
			else{ // Y axis
				DSLeft = copyAndCut(firstY.getData().getY(), median.getData().getY()-1, axis);
				DSRight = copyAndCut(median.getData().getY(), lastY.getData().getY(), axis);
			}
		
			Point[] nPLeft = DSLeft.nearestPair();
			Point[] nPRight = DSRight.nearestPair();
		
			Point[] nP = d(nPLeft, nPRight) ;
			double width = d(nP[0], nP[1]);
			Point[] toCheck = nearestPairInStrip(median, width, axis);
			return d(nP, toCheck);
		}
	}

	private double d(Point a, Point b){ //calculates the Euclidian distance between point.a to point.b, O(1)
		return Math.sqrt(Math.pow(b.getX()-a.getX(),2) + Math.pow(a.getY() - b.getY(), 2));
	}
	
	private Point[] d(Point[] left, Point[] right){ //calculates the minimal Euclidian distance between left and right input arrays
		if(left == null | right == null){
			if(left == null)
				return right;
			else
				return left;
		}

		double dLeft = Math.sqrt(Math.pow(left[0].getX()-left[1].getX(), 2) + Math.pow(left[0].getY()-left[1].getY(), 2));
		double dRight = Math.sqrt(Math.pow(right[0].getX()-right[1].getX(), 2) + Math.pow(right[0].getY()-right[1].getY(), 2));
		
		double minD = Math.min(dLeft, dRight);
		if(dLeft == minD)
			return left;
		else 
			return right;
	}
		
	//if adds to X list DOES NOT increases size
	//for internal use only!
	private void naiveAddLast(Point data, boolean axis){
		Container toAdd = new Container(data, null);
		if(axis){
			if(firstX == null){
				firstX = toAdd;
				lastX = toAdd;
			}
			else{
				lastX.next = toAdd;
				toAdd.prev = lastX;
				lastX = toAdd;
			}
		}
		else{ //!axis
			if(firstY == null){
				firstY = toAdd;
				lastY = toAdd;
			}
			else{
				lastY.next = toAdd; 
				toAdd.prev = lastY;
				lastY = toAdd;
			}
			size++;
		}
	}
	
	//Copies and narrows the DS into a new DS, returns it, loses narrowRange functionality
	//O(n) efficiency
	private DataStructure copyAndCut (int min, int max, boolean axis){
		DataStructure newDS = new DataStructure();
		Point[] regAxis = getPointsInRangeRegAxis(min, max, axis); //O(n)
		Point[] oppAxis = getPointsInRangeOppAxis(min, max, axis); //O(n)
		
		for(int i=0; i<regAxis.length ; i++)
			newDS.naiveAddLast(regAxis[i], axis); //O(1) * n times
		
		for(int i=0; i<oppAxis.length ; i++)
			newDS.naiveAddLast(oppAxis[i], !axis); //O(1) * n times
		
		return newDS;
	}
}

