import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;

/**
 * BPlusTree Class Assumptions:
 * 1. No duplicate keys inserted 
 * 2. Order D: D <= number of keys in a node <= 2*D 
 * 3. All keys are non-negative
 * 
 * TODO: Rename to BPlusTree
 */
public class BPlusTree<K extends Comparable<K>, T> {

	public Node<K,T> root;
	public static final int D = 2;

	/**
	 * TODO Search the value for a specific key
	 * 
	 * @param key
	 * @return value
	 */
	public T search(K key) {
		
		if (root == null) return null;
		
		int index = 0;
		Node<K,T> temp = root;
		
		// Search IndexNode until get the LeafNode
		while (!temp.isLeafNode){
			index = findKeyIndex(temp,key);
			temp = ((IndexNode<K,T>)temp).children.get(index + 1);
		}
		
		// Find the Index in the LeafNode
		index = findKeyIndex(temp, key);
		
		if(index == temp.keys.size() || temp.keys.get(index) != key) return null;
		return ((LeafNode<K,T>)temp).values.get(index);
	}

	/**
	 * TODO Insert a key/value pair into the BPlusTree
	 * 
	 * @param key
	 * @param value
	 */
	public void insert(K key, T value) {
		
		if (root == null){
			root = new LeafNode<K,T>(key, value);
			return;
		}
		
		Node<K,T> temp = root; 
		ArrayList<IndexNode<K,T>> parentNodes = new ArrayList<IndexNode<K,T>>();
		
		// Search until reach the LeafNode level
		// and store every IndexNode on the path
		while(!temp.isLeafNode){
			parentNodes.add((IndexNode<K,T>)temp);
			int index = findKeyIndex(temp, key);
			temp = ((IndexNode<K,T>)temp).children.get(index + 1);	
		}
		
		// Insert the key/value pair
		((LeafNode<K,T>)temp).insertSorted(key, value);
		
		// Check if the LeafNode is overflowed
		if (!temp.isOverflowed()) return;
		
		// LeafNode is overflowed
		Entry<K,Node<K,T>> newEntry = splitLeafNode((LeafNode<K,T>)temp);
		while(parentNodes.size() > 0){	
			
			int parentsNum = parentNodes.size() - 1;
			IndexNode<K,T> parentNode = parentNodes.get(parentsNum);
			int index = findKeyIndex(parentNode , newEntry.getKey());
			
			// Insert the new IndexNode
			parentNode.insertSorted(newEntry, index);
			
			// Check if the parent IndexNode is overflowed
			if (!parentNode.isOverflowed()) return;
			
			// Parent IndexNode is also overflowed
			newEntry = splitIndexNode(parentNode);
			parentNodes.remove(parentsNum);
		}
		
		// Tree needs to grow by one level 
		root = new IndexNode<K,T>(newEntry.getKey(), root, newEntry.getValue());
	}
	
	/**
	 * TODO Search the index of a current key in the node or
	 * 		the right position to insert an extra key to the node.
	 * 
	 * @param n, node to search
	 * @param key, key to search or insert
	 * @return index
	 */
	private int findKeyIndex(Node<K, T> n, K key){
		int i = 0;
		while(i < n.keys.size() && (n.keys.get(i).compareTo(key) < 0)){	// keys[i] < newkey
			 ++ i;	
		}
		return i;
	}

	/**
	 * TODO Split a leaf node into D & (size() - D)
	 * and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param leaf, any other relevant data
	 * @return the key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitLeafNode(LeafNode<K,T> leaf) {
		
		ArrayList<K> newKeys = new ArrayList<K>();
		ArrayList<T> newVals = new ArrayList<T>();
		
		// Move all elements beyond D to the new leaf
		while(leaf.keys.size() > D){
			newKeys.add(leaf.keys.remove(D));
			newVals.add(leaf.values.remove(D));
		}
		
		LeafNode<K,T> newLeaf = new LeafNode<K,T>(newKeys, newVals);
		newLeaf.nextLeaf = leaf.nextLeaf;
		newLeaf.previousLeaf = leaf;
		leaf.nextLeaf = newLeaf;
		if (newLeaf.nextLeaf != null) newLeaf.nextLeaf.previousLeaf = newLeaf;
		
		return new AbstractMap.SimpleEntry<K, Node<K,T>>(newLeaf.keys.get(0), newLeaf);
	}

	/**
	 * TODO split an indexNode into D & (size() - D)
	 * and return the new right node
	 * and the splitting key as an Entry<slitingKey, RightNode>
	 * 
	 * @param index, any other relevant data
	 * @return new key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitIndexNode(IndexNode<K,T> index) {
		
		ArrayList<K> newKeys = new ArrayList<K>();
		ArrayList<Node<K,T>> newChildren= new ArrayList<Node<K,T>>();
		
		int startIndex = 1;
		while(index.keys.size() > D + 1){
			newKeys.add(index.keys.remove(D + 1));
			newChildren.add(startIndex++, index.children.remove(D + 1));
		}
		newChildren.add(index.children.remove(D + 1));
		
		IndexNode<K,T> newIndexNode = new IndexNode<K,T>(newKeys, newChildren);
		return new AbstractMap.SimpleEntry<K, Node<K,T>>(index.keys.remove(D), newIndexNode);
	}

	/**
	 * TODO Delete a key/value pair from this B+Tree
	 * 
	 * @param key
	 */
	public void delete(K key) {

	}

	/**
	 * TODO Handle LeafNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleLeafNodeUnderflow(LeafNode<K,T> left, LeafNode<K,T> right,
			IndexNode<K,T> parent) {
		return -1;

	}

	/**
	 * TODO Handle IndexNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleIndexNodeUnderflow(IndexNode<K,T> leftIndex,
			IndexNode<K,T> rightIndex, IndexNode<K,T> parent) {
		return -1;
	}

}
