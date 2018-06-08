import java.util.ArrayList;


//SUBMIT
public class BNode implements BNodeInterface {

	// ///////////////////BEGIN DO NOT CHANGE ///////////////////
	// ///////////////////BEGIN DO NOT CHANGE ///////////////////
	// ///////////////////BEGIN DO NOT CHANGE ///////////////////
	private final int t;
	private int numOfBlocks;
	private boolean isLeaf;
	private ArrayList<Block> blocksList;
	private ArrayList<BNode> childrenList;

	/**
	 * Constructor for creating a node with a single child.<br>
	 * Useful for creating a new root.
	 */
	public BNode(int t, BNode firstChild) {
		this(t, false, 0);
		this.childrenList.add(firstChild);
	}

	/**
	 * Constructor for creating a <b>leaf</b> node with a single block.
	 */
	public BNode(int t, Block firstBlock) {
		this(t, true, 1);
		this.blocksList.add(firstBlock);
	}

	public BNode(int t, boolean isLeaf, int numOfBlocks) {
		this.t = t;
		this.isLeaf = isLeaf;
		this.numOfBlocks = numOfBlocks;
		this.blocksList = new ArrayList<>();
		this.childrenList = new ArrayList<>();
	}

	// For testing purposes.
	public BNode(int t, int numOfBlocks, boolean isLeaf,
			ArrayList<Block> blocksList, ArrayList<BNode> childrenList) {
		this.t = t;
		this.numOfBlocks = numOfBlocks;
		this.isLeaf = isLeaf;
		this.blocksList = blocksList;
		this.childrenList = childrenList;
	}

	@Override
	public int getT() {
		return t;
	}

	@Override
	public int getNumOfBlocks() {
		return numOfBlocks;
	}

	@Override
	public boolean isLeaf() {
		return isLeaf;
	}

	@Override
	public ArrayList<Block> getBlocksList() {
		return blocksList;
	}

	@Override
	public ArrayList<BNode> getChildrenList() {
		return childrenList;
	}

	@Override
	public boolean isFull() {
		return numOfBlocks == 2 * t - 1;
	}

	@Override
	public boolean isMinSize() {
		return numOfBlocks == t - 1;
	}
	
	@Override
	public boolean isEmpty() {
		return numOfBlocks == 0;
	}
	
	@Override
	public int getBlockKeyAt(int indx) {
		return blocksList.get(indx).getKey();
	}
	
	@Override
	public Block getBlockAt(int indx) {
		return blocksList.get(indx);
	}

	@Override
	public BNode getChildAt(int indx) {
		return childrenList.get(indx);
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((blocksList == null) ? 0 : blocksList.hashCode());
		result = prime * result
				+ ((childrenList == null) ? 0 : childrenList.hashCode());
		result = prime * result + (isLeaf ? 1231 : 1237);
		result = prime * result + numOfBlocks;
		result = prime * result + t;
		return result;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BNode other = (BNode) obj;
		if (blocksList == null) {
			if (other.blocksList != null)
				return false;
		} else if (!blocksList.equals(other.blocksList))
			return false;
		if (childrenList == null) {
			if (other.childrenList != null)
				return false;
		} else if (!childrenList.equals(other.childrenList))
			return false;
		if (isLeaf != other.isLeaf)
			return false;
		if (numOfBlocks != other.numOfBlocks)
			return false;
		return t == other.t;
	}
	
	@Override
	public String toString() {
		return "BNode [t=" + t + ", numOfBlocks=" + numOfBlocks + ", isLeaf="
				+ isLeaf + ", blocksList=" + blocksList + ", childrenList="
				+ childrenList + "]";
	}

	// ///////////////////DO NOT CHANGE END///////////////////
	// ///////////////////DO NOT CHANGE END///////////////////
	// ///////////////////DO NOT CHANGE END///////////////////


	/**
	 * Search given key.
	 * @param key
	 * @return block with key
	 */
	@Override
	public Block search(int key) {
		int ind=0;
		while (ind<numOfBlocks && getBlockKeyAt(ind)<key)
			ind++;	
		if (ind<numOfBlocks && getBlockKeyAt(ind) == key)
			return getBlockAt(ind);
		else {
			if(isLeaf)
				return null;
			return getChildAt(ind).search(key);
		}
	}

	/**
	 * inserts block d to it's right place.
	 * @param d
	 */
	@Override
	public void insertNonFull(Block d) {
		int i=0;
		if(isLeaf) {
			while(i<numOfBlocks && getBlockKeyAt(i)<d.getKey())
				i++;
			if (i==numOfBlocks)
				blocksList.add(d);
			else
				blocksList.add(i, d);
			numOfBlocks++;
		}else { //is not leaf
			while(i<numOfBlocks && getBlockKeyAt(i)<d.getKey())
				i++;

			if(getChildAt(i).isFull()){
				this.splitChild(i);

				if(d.getKey() > getBlockKeyAt(i))
					getChildAt(i + 1).insertNonFull(d);
				else
					getChildAt(i).insertNonFull(d);
			}
			else //!child.isFull
				getChildAt(i).insertNonFull(d);
		}
	}

	/**
	 * deletes block with given key.
	 * @param key
	 */
	@Override
	public void delete(int key) {
		int ind=0;
		while (ind<numOfBlocks && getBlockKeyAt(ind)<key)
			ind++;
		if (ind<numOfBlocks && getBlockKeyAt(ind) == key) {
			if(isLeaf()){
				blocksList.remove(ind);
				numOfBlocks--;
			}else{ //!isLeaf, specific cases
				if(childHasNonMinimalLeftSibling(ind+1)) {
					Block pred=getChildAt(ind).getMaxKeyBlock();
					blocksList.set(ind, pred);
					getChildAt(ind).delete(pred.getKey());
				}else if(childHasNonMinimalRightSibling(ind)){
					Block succ=getChildAt(ind+1).getMinKeyBlock();
					blocksList.set(ind, succ);
					getChildAt(ind+1).delete(succ.getKey());
				}else { //both children has t-1 keys -> merge them
					mergeWithRightSibling(ind);
					this.delete(key);
				}
			}
		}else
			if(ind<=numOfBlocks && !isLeaf()) {
				shiftOrMergeChildIfNeeded(ind);
				int i=0;
				while(i<numOfBlocks&&getBlockKeyAt(i)<key)
					i++;
				if(i<numOfBlocks && getBlockKeyAt(i)==key)
					this.delete(key);
				else if(!isLeaf) {
					shiftOrMergeChildIfNeeded(i);
					i=0;
					while(i<numOfBlocks&&getBlockKeyAt(i)<key)
						i++;
					if(i<numOfBlocks && getBlockKeyAt(i)==key)
						this.delete(key);
					getChildAt(i).delete(key);
				} else
					this.delete(key);
			}

	}

	/**
	 * creates node's hash signature
	 * @return MerkleBNode
	 */
	public MerkleBNode createHashNode() {
		if(isLeaf)
			return new MerkleBNode(hashGenLeaf());
		else {//!isLeaf
			ArrayList<MerkleBNode> merkleCL = new ArrayList<>();

			for(BNode child:childrenList)
				merkleCL.add(child.createHashNode());
				byte[] hCode = hashGenInternal(merkleCL);
			return new MerkleBNode(hCode, false, merkleCL);
		}
	}

	/**
	 * generates hashValue for non-leaf nodes.
	 * @param merkleCL
	 * @return byte[] value
	 */
	private byte[] hashGenInternal(ArrayList merkleCL){
		ArrayList<byte[]> dataList = new ArrayList<>();
		int i;
		for(i=0 ; i<numOfBlocks; i++){
			dataList.add(((MerkleBNode)merkleCL.get(i)).getHashValue());
			dataList.add(getBlockAt(i).getData());
		}
		dataList.add(((MerkleBNode)merkleCL.get(i)).getHashValue());
		return HashUtils.sha1Hash(dataList);
	}

	/**
	 * generates hashValue for leaf nodes.
	 * @return byte[] value
	 */
	private byte[] hashGenLeaf (){
		ArrayList<byte[]> toHash = new ArrayList<>() ;
		for (Block b :blocksList){
			toHash.add(b.getData());
		}
		return HashUtils.sha1Hash (toHash);
	}

	/**
	* Splits the child node at childIndex into 2 nodes.
	* @param childIndex
	 */
	public void splitChild(int childIndex){
		BNode y=this.getChildAt(childIndex);
		BNode z=new BNode(t, getChildAt(childIndex).isLeaf, t-1);

		//moves the block to parent & remove from child's blocklist
		blocksList.add(childIndex, y.blocksList.remove(t-1));
		numOfBlocks++;
		y.numOfBlocks=t-1;

		z.blocksList= new ArrayList<>(y.getBlocksList().subList(t - 1, 2 * t - 2));
		y.blocksList= new ArrayList<>(y.blocksList.subList(0, t - 1));

		if(!z.isLeaf()) {
			z.childrenList= new ArrayList<>(y.getChildrenList().subList(t, 2 * t));
			y.childrenList= new ArrayList<>(y.childrenList.subList(0, t));
		}
		childrenList.add(childIndex+1, z);
	}

	/**
	 * Finds and returns the block with the min key in the subtree (used to find successor).
	 * @return min key block
	 */
	private Block getMinKeyBlock() {
		if(isLeaf)
			return getBlockAt(0);
		return getChildAt(0).getMinKeyBlock();
	}
	/**
	 * Finds and returns the block with the max key in the subtree (used to find predecessor).
	 * @return max key block (used to find predecessor)
	 */
	private Block getMaxKeyBlock() {
		if(isLeaf)
			return getBlockAt(numOfBlocks-1);
		return getChildAt(numOfBlocks).getMaxKeyBlock();
	}

	/**
	 * Merges the child node at childIndx with its right sibling.
	 * The right sibling node is removed.
	 *@param childIndx
	 */
	private void mergeWithRightSibling(int childIndx){
		BNode nextChild = getChildAt(childIndx+1);
		BNode currChild = getChildAt(childIndx);
		//moves the parent block to the child
		currChild.blocksList.add(this.blocksList.remove(childIndx));
		this.numOfBlocks--;
		currChild.numOfBlocks++;

		for(Block blockItr: nextChild.getBlocksList()) {
			currChild.blocksList.add(blockItr);
			currChild.numOfBlocks++;
		}
		currChild.childrenList.addAll(nextChild.getChildrenList());

		this.childrenList.remove(childIndx+1);
		//checks if current node is empty, if yes-replaces it with it's child
		if(this.numOfBlocks==0){
			this.numOfBlocks=currChild.getNumOfBlocks();
			this.blocksList=currChild.getBlocksList();
			this.childrenList=currChild.getChildrenList();
			this.isLeaf=currChild.isLeaf();
		}
	}
	/**
	 * Merges the child node at childIndx with its left sibling.
	 * The left sibling node is removed.
	 *@param childIndx
	 */
	private void mergeWithLeftSibling(int childIndx){
		BNode prevChild = getChildAt(childIndx-1);
		BNode currChild = getChildAt(childIndx);
		//moves the parent block to the child
		prevChild.blocksList.add(this.blocksList.remove(childIndx-1));
		this.numOfBlocks--;
		prevChild.numOfBlocks++;

		for(Block blockItr: currChild.getBlocksList()) {
			prevChild.blocksList.add(blockItr);
			prevChild.numOfBlocks++;
		}
		if(!currChild.isLeaf())
			prevChild.childrenList.addAll(currChild.getChildrenList());

		this.childrenList.remove(childIndx);
		//checks if current node is empty, if yes-replaces it with it's child
		if(this.numOfBlocks==0){
			this.numOfBlocks=prevChild.getNumOfBlocks();
			this.blocksList=prevChild.getBlocksList();
			this.childrenList=prevChild.getChildrenList();
			this.isLeaf=prevChild.isLeaf();
		}
	}

	/**
	 * checks if a child has left sibling with>t-1 blocks.
	 * @param childIndx
	 * @return boolean
	 */
	private boolean childHasNonMinimalLeftSibling(int childIndx){
		return (childIndx !=0 && getChildAt (childIndx-1).getNumOfBlocks()>t-1);
	}
	/**
	 * checks if a child has right sibling with>t-1 blocks.
	 * @param childIndx
	 * @return boolean
	 */
	private boolean childHasNonMinimalRightSibling(int childIndx){
		return (childIndx<numOfBlocks && getChildAt (childIndx+1).getNumOfBlocks()>t-1);
	}

	/**
	 * checks if a given child is viable for deletion process.
	 * @param childIndx
	 */
	private void shiftOrMergeChildIfNeeded(int childIndx){
		if (getChildAt (childIndx).getNumOfBlocks()<=t-1){
			if (childHasNonMinimalLeftSibling(childIndx))
				shiftFromLeftSibling(childIndx);
			else if (childHasNonMinimalRightSibling(childIndx))
				shiftFromRightSibling(childIndx);
			else{
				if (childIndx !=0)
					mergeWithLeftSibling(childIndx);
				else mergeWithRightSibling(childIndx);
			}
		}
	}

	/**
	 * shifts a block from left sibling.
	 * @param childIndx
	 */
	private void shiftFromLeftSibling(int childIndx){
		BNode left=getChildAt(childIndx-1);
		BNode right=getChildAt(childIndx);

		Block tmp=left.getBlockAt(left.numOfBlocks-1);
		tmp=blocksList.set(childIndx-1, tmp);
		right.blocksList.add(0, tmp);
		right.numOfBlocks++;
		left.blocksList.remove(left.numOfBlocks-1);

		if(!right.isLeaf())
			right.childrenList.add(0, left.childrenList.remove(left.numOfBlocks));
		left.numOfBlocks--;

	}

	/**
	 * shifts a block from right sibling.
	 * @param childIndx
	 */
	private void shiftFromRightSibling(int childIndx){
		BNode left=getChildAt(childIndx);
		BNode right=getChildAt(childIndx+1);

		Block tmp=right.getBlockAt(0);
		tmp=blocksList.set(childIndx, tmp);
		left.blocksList.add(tmp);
		left.numOfBlocks++;
		right.blocksList.remove(0);
		right.numOfBlocks--;

		if(!right.isLeaf())
			left.childrenList.add(right.childrenList.remove(0));
	}

}