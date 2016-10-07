TEAM MEMBERS:
Rouchen Song
Hongshu Ye
Jiayu Dong


Coding logic for B+Tree: 

1. search():
Search the IndexNode contains the key until reach the LeafNode.
Return the value if the key exists in the LeafNode or return null.


/* Helper Function */ int findKeyIndex(Node<K,T> n, K key):
Return the Index of key in node n’s keys if the key exist or
return the Index to insert the key in node’s keys if the key
does not exist.
 

2. insert(): 
First search the LeafNode to insert the key/value pair and store all
the IndexNodes on the search path. Then check if the LeafNode is overflowed.
If it is, split the LeafNode and add an extra key to its parent which
should be the last IndexNode stored. After that, use a while() loop to
check every parent IndexNode if is overflowed from bottom to top until a
parent IndexNode is not overflowed or reach the root. If the root is still
overflowed then finally grow the tree by one level by creating a new IndexNode
to become the new root.

/* Helper Function */ Entry<K, Node<K,T>> splitLeafNode(LeafNode<K,T> leaf):
Move all keys in the LeafNode beyond D to a new LeafNode and return the first 
entry of the new LeafNode for its parent IndexNode to insert it.

/* Helper Function */ Entry<K, Node<K,T>> splitIndexNode(IndexNode<K,T> index):
Move all keys and children in the IndexNode beyond D to a new IndexNode and
return the first entry of the new IndexNode for its parent IndexNode to insert it.


3. delete(): 
First search the LeafNode to delete the key/value pair and store all
the IndexNodes on the search path. Then check if the key to delete exists.
If not, function returns. If the key exist delete the key/value pair and check if 
the LeafNode is underflowed. If it is, handle the underflow by redistribution or
merging. If the underflow is handled by merging, delete the previous left LeafNode
in their parent IndexNode. After that, use a while() loop to check every parent 
IndexNode if is underflowed from bottom to top until a parent IndexNode is not underflowed or reach the root. The root can be underflowed but if the root is 
empty, finally set its only child as the new root.

/* Helper Function */ ArrayList<Node<K,T>> getSiblings(IndexNode<K,T> parent,
							 Node<K,T> node):
Get the two siblings of the node. If any siblings does not exist, put an null in
the corresponding position of the ArrayList returned.

/* Helper Function */ int handleLeafNodeUnderflow(LeafNode<K,T> left,
					LeafNode<K,T> right, IndexNode<K,T> parent):
Handle the LeafNode underflowed happens on either the left LeafNode or the right
LeafNode by first trying to redistribute two LeafNodes. If two LeafNodes have keys
no fewer than 2*D in total, then redistribute them evenly in the two LeafNodes. Or
the two LeafNodes need to merge and return the splitkey index so that their parent
IndexNode can delete the key and the node later on.

/* Helper Function */ int handleIndexNodeUnderflow(IndexNode<K,T> left,
					IndexNode<K,T> right, IndexNode<K,T> parent):
Handle the IndexNode underflowed happens on either the left IndexNode or the right
IndexNode by first trying to redistribute two LeafNodes. If two LeafNodes have keys
no fewer than 2*D in total, then redistribute them evenly in the two IndexNodes. Or
the two IndexNodes need to merge and return the splitkey index so that their parent
IndexNode can delete the key and the node later on.


