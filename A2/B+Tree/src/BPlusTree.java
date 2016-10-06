import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
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
			temp = ((IndexNode<K,T>)temp).children.get(index);	
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
	 * @return index of the exist key OR index to insert the extra key
	 */
	/* packet */ int findKeyIndex(Node<K, T> n, K key){
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
		
		// Move all elements beyond D to the new LeafNode
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
		
		// Move keys and nodes beyond D to the new IndexNode
		while(index.keys.size() > D + 1){
			newKeys.add(index.keys.remove(D + 1));
			newChildren.add(index.children.remove(D + 1));
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
		
		if (root == null) return;
		
		Node<K,T> temp = root; 
		ArrayList<IndexNode<K,T>> parentNodes = new ArrayList<IndexNode<K,T>>();
		
		// Search the key until reach the LeafNode level
		// and store every IndexNode on the path
		while(!temp.isLeafNode){
			parentNodes.add((IndexNode<K,T>)temp);
			int index = findKeyIndex(temp, key);
			if (index <= temp.keys.size() - 1 && temp.keys.get(index) == key) ++ index;
			temp = ((IndexNode<K,T>)temp).children.get(index);	
		}
		
		// Delete the key/value pair
		int keyIndex = findKeyIndex(temp, key);
		if (temp.keys.get(keyIndex) != key) return;	// no such key
		((LeafNode<K,T>)temp).keys.remove(keyIndex);
		((LeafNode<K,T>)temp).values.remove(keyIndex);
		
		// Check if the LeafNode is overflowed or is the root
		if (!temp.isUnderflowed() || temp == root) return;
		
		// LeafNode is not the root and is underflowed
		IndexNode<K,T> parentNode = parentNodes.remove(parentNodes.size() - 1);
		ArrayList<Node<K,T>> siblingNodes = new ArrayList<Node<K,T>>(getSiblings(parentNode, temp));
		int index;
		if (siblingNodes.get(0) == null) {
			index =  handleLeafNodeUnderflow((LeafNode<K,T>)temp,
												(LeafNode<K,T>)(siblingNodes.get(1)),
												parentNode);
		} else index = handleLeafNodeUnderflow((LeafNode<K,T>)(siblingNodes.get(0)),
												(LeafNode<K,T>)temp, 
												parentNode);
		if (index == -1) return;	// LeafNode underflow fixed by redistribution 
		// LeafNodes merge, parent need to delete the split key
		parentNode.keys.remove(index);
		parentNode.children.remove(index);
		
		while(parentNodes.size() > 0){	
			
			// Check if the parent IndexNode is underflowed
			if (!parentNode.isUnderflowed()) return;
			
			// Parent IndexNode is also underflowed
			int parentsNum = parentNodes.size() - 1;
			temp = parentNode;
			parentNode = parentNodes.remove(parentsNum);
			siblingNodes = new ArrayList<Node<K,T>>(getSiblings(parentNode, temp));
			if (siblingNodes.get(0) == null) {	// no left sibling
				index =  handleIndexNodeUnderflow((IndexNode<K,T>)temp,
													(IndexNode<K,T>)(siblingNodes.get(1)), 
													parentNode);
			} else index = handleIndexNodeUnderflow((IndexNode<K,T>)(siblingNodes.get(0)), 
													(IndexNode<K,T>)temp, 
													parentNode);
			if (index == -1) return;	// IndexNode underflow fixed by redistribution 
			
			// IndexNodes merge, parent need to delete the split key
			parentNode.keys.remove(index);
			parentNode.children.remove(index);
		}
		
		// If root's key is empty, make its only child the new root  
		if (parentNode.keys.isEmpty()) root = parentNode.children.get(0);
	}

	/**
	 * TODO Get the two siblings of a node. 
	 * 
	 * @param parent, the parent node of the node
	 * @param node, the node to get its siblings
	 * @return the ArrayList of the node's siblings; null if the node is not a child of the parent.
	 * 		   Node to its left stored in (0) and node to its right stored in (1).
	 * 		   Assign null if any of its siblings does not exist.
	 */
	/* packet */ ArrayList<Node<K,T>> getSiblings(IndexNode<K,T> parent, Node<K,T> node){
		// Create a ArrayList of size 2, and initialize its two members to null
		ArrayList<Node<K,T>> siblingNodes = new ArrayList<Node<K,T>>(Arrays.asList(null, null));
		int index = parent.children.indexOf(node);
		if (index == -1) return null;	// no such child in the parent
		if (index != 0) siblingNodes.set(0, parent.children.get(index - 1));
		if (index != parent.children.size() - 1) siblingNodes.set(1, parent.children.get(index + 1));
		return siblingNodes;
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
			
		if (left.keys.size() + right.keys.size() >= 2*D){	// only redistribution needed		
			if (left.isUnderflowed()) {		// the smaller LeafNode is underflowed
				left.keys.add(right.keys.remove(0));
				left.values.add(right.values.remove(0));
			} else {	// the bigger LeafNode is underflowed
				right.keys.add(0, left.keys.remove(left.keys.size()-1));
				right.values.add(0, left.values.remove(left.values.size()-1));
			}
			parent.keys.set(parent.children.indexOf(left), right.keys.get(0));
			return -1;
		}
		
		// Merge left LeafNode to the right LeafNode
		right.keys.addAll(0, left.keys);
		right.values.addAll(0, left.values);
		right.previousLeaf = left.previousLeaf;
		return parent.children.indexOf(left);
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
		
		// Get the parent key between the two IndexNodes
		K parentKey = parent.keys.get(parent.children.indexOf(leftIndex));
		
		if (leftIndex.keys.size() + rightIndex.keys.size() >= 2*D){		// only redistribution needed
			if (leftIndex.isUnderflowed()){		// the smaller IndexNode is underflowed
				leftIndex.keys.add(parentKey);
				leftIndex.children.add(rightIndex.children.remove(0));
				parent.keys.set(parent.children.indexOf(leftIndex), rightIndex.keys.remove(0));
			} else {	// the bigger IndexNode is underflowed
				rightIndex.keys.add(0, parentKey);
				rightIndex.children.add(0, leftIndex.children.remove(leftIndex.children.size()-1));
				parent.keys.set(parent.children.indexOf(leftIndex),
								leftIndex.keys.remove(leftIndex.keys.size()-1));
			}
			return -1;
		}
		
		// Merge the parent key and the left IndexNode to the right IndexNode
		rightIndex.keys.add(0, parentKey);
		rightIndex.keys.addAll(0, leftIndex.keys);
		rightIndex.children.addAll(0, leftIndex.children);
		return parent.children.indexOf(leftIndex);
	}

}
