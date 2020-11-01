package dsa_assignment3;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

/**
 * A node in a binary tree representing a Propositional Logic expression.
 * <p>
 * Each node has a type (AND node, OR node, NOT node etc.) and can have zero one
 * or two children as required by the node type (AND has two children, NOT has
 * one, TRUE, FALSE and variables have none
 * </p>
 * <p>
 * This class is mutable, and some of the operations are intended to modify the
 * tree internally. There are a few cases when copies need to be made of whole
 * sub-tree nodes in such a way that these copied trees do not share any nodes
 * with their originals. To do this the class implements a deep copying copy
 * constructor <code>PLTreeNode(PLTreeNode node)</code>
 * </p>
 * 
 */
public final class PLTreeNode implements PLTreeNodeInterface {
	private static final Logger logger = Logger.getLogger(PLTreeNode.class);

	NodeType type;
	PLTreeNode child1;
	PLTreeNode child2;

	/**
	 * For marking purposes
	 * 
	 * @return Your student id
	 */
	public static String getStudentID() {
		// change this return value to return your student id number
		return "1993805";
	}

	/**
	 * For marking purposes
	 * 
	 * @return Your name
	 */
	public static String getStudentName() {
		// change this return value to return your name
		return "Reuben Sidhu";
	}

	/**
	 * The default constructor should never be called and has been made private
	 */
	private PLTreeNode() {
		throw new RuntimeException("Error: default PLTreeNode constuctor called");
	}

	/**
	 * The usual constructor used internally in this class. No checks made on
	 * validity of parameters as this has been made private, so can only be called
	 * within this class.
	 * 
	 * @param type   The <code>NodeType</code> represented by this node
	 * @param child1 The first child, if there is one, or null if there isn't
	 * @param child2 The second child, if there is one, or null if there isn't
	 */
	private PLTreeNode(NodeType type, PLTreeNode child1, PLTreeNode child2) {
		// Don't need to do lots of tests because only this class can create nodes
		// directly,
		// any construction required by another class has to go through
		// reversePolishBuilder,
		// which does all the checks
		this.type = type;
		this.child1 = child1;
		this.child2 = child2;
	}

	/**
	 * A copy constructor to take a (recursive) deep copy of the sub-tree based on
	 * this node. Guarantees that no sub-node is shared with the original other
	 * 
	 * @param node The node that should be deep copied
	 */
	private PLTreeNode(PLTreeNode node) {
		if (node == null)
			throw new RuntimeException("Error: tried to call the deep copy constructor on a null PLTreeNode");
		type = node.type;
		if (node.child1 == null)
			child1 = null;
		else
			child1 = new PLTreeNode(node.child1);
		if (node.child2 == null)
			child2 = null;
		else
			child2 = new PLTreeNode(node.child2);
	}

	/**
	 * Takes a list of <code>NodeType</code> values describing a valid propositional
	 * logic expression in reverse polish notation and constructs the corresponding
	 * expression tree. c.f. <a href=
	 * "https://en.wikipedia.org/wiki/Reverse_Polish_notation">https://en.wikipedia.org/wiki/Reverse_Polish_notation</a>
	 * <p>
	 * Thus an input containing
	 * </p>
	 * 
	 * <pre>
	 * {NodeType.P, NodeType.Q, NodeType.NOT, NodeType.AND.
	 * </pre>
	 * 
	 * corresponds to
	 * 
	 * <pre>
	 * P∧¬Q
	 * </pre>
	 * 
	 * Leaving out the <code>NodeType</code> enum class specifier, we get that
	 * 
	 * <pre>
	 * { R, P, OR, TRUE, Q, NOT, AND, IMPLIES }
	 * </pre>
	 * 
	 * represents
	 * 
	 * <pre>
	 * ((R∨P)→(⊤∧¬Q))
	 * </pre>
	 * 
	 * @param typeList An <code>NodeType</code> array in reverse polish order
	 * @return the <code>PLTreeNode</code> of the root of the tree representing the
	 *         expression constructed for the reverse polish order
	 *         <code>NodeType</code> array
	 */
	public static PLTreeNodeInterface reversePolishBuilder(NodeType[] typeList) {
		if (typeList == null || typeList.length == 0) {
			logger.error("Trying to create an empty PLTree");
			return null;
		}

		Deque<PLTreeNode> nodeStack = new LinkedList<>();

		for (NodeType type : typeList) {
			int arity = type.getArity();

			if (nodeStack.size() < arity) {
				logger.error(String.format(
						"Error: Malformed reverse polish type list: \"%s\" has arity %d, but is being applied to only %d arguments", //
						type, arity, nodeStack.size()));
				return null;
			}
			if (arity == 0)
				nodeStack.addFirst(new PLTreeNode(type, null, null));
			else if (arity == 1) {
				PLTreeNode node1 = nodeStack.removeFirst();
				nodeStack.addFirst(new PLTreeNode(type, node1, null));
			} else {
				PLTreeNode node2 = nodeStack.removeFirst();
				PLTreeNode node1 = nodeStack.removeFirst();
				nodeStack.addFirst(new PLTreeNode(type, node1, node2));
			}
		}
		if (nodeStack.size() > 1) {
			logger.error("Error: Incomplete term: multiple subterms not combined by top level symbol");
			return null;
		}

		return nodeStack.removeFirst();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dsa_assignment3.PLTreeNodeInterface#getReversePolish()
	 */
	@Override
	public NodeType[] getReversePolish() {
		Deque<NodeType> nodeQueue = new LinkedList<>();

		getReversePolish(nodeQueue);

		return nodeQueue.toArray(new NodeType[0]);

	}

	/**
	 * A helper method for <code>getReversePolish()</code> used to accumulate the
	 * elements of the reverse polish notation description of the current tree
	 * 
	 * @param nodeQueue A queue of <code>NodeType</code> objects used to accumulate
	 *                  the values of the reverse polish notation description of the
	 *                  current tree
	 */
	private void getReversePolish(Deque<NodeType> nodeQueue) {
		if (child1 != null)
			child1.getReversePolish(nodeQueue);
		if (child2 != null)
			child2.getReversePolish(nodeQueue);
		nodeQueue.addLast(type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dsa_assignment3.PLTreeNodeInterface#toString()
	 */
	@Override
	public String toString() {
		return toStringPrefix();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dsa_assignment3.PLTreeNodeInterface#toStringPrefix()
	 */
	@Override
	public String toStringPrefix() {
		// WRITE YOUR CODE HERE, CHANGING THE RETURN STATEMENT IF NECESSARY
		String prefix = type.getPrefixName();

		if (type.getArity() == 0) {
			return prefix;
		} else if (type.getArity() == 1) {
			prefix += "(" + child1.toStringPrefix() + ")";
		} else {
			prefix += "(" + child1.toStringPrefix() + "," + child2.toStringPrefix() + ")";
		}
		return prefix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dsa_assignment3.PLTreeNodeInterface#toStringInfix()
	 */
	@Override
	public String toStringInfix() {
		// WRITE YOUR CODE HERE, CHANGING THE RETURN STATEMENT IF NECESSARY
		String infix = type.getInfixName();

		if (type.getArity() == 0) {
			return infix;
		} else if (type.getArity() == 1) {
			infix += child1.toStringInfix();
		} else {
			infix = "(" + child1.toStringInfix() + infix + child2.toStringInfix() + ")";
		}
		return infix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dsa_assignment3.PLTreeNodeInterface#applyVarBindings(java.util.Map)
	 */
	@Override
	public void applyVarBindings(Map<NodeType, Boolean> bindings) {
		// WRITE YOUR CODE HERE
		if (type.getArity() == 0) {
			if (bindings.containsKey(type)) {
				Boolean bVal = bindings.get(type);
				if (bVal) {
					type = NodeType.TRUE;
				}
				else {
					type = NodeType.FALSE;
				}
			}
		} 
		else if (type.getArity() == 1) {
			child1.applyVarBindings(bindings);
		} 
		else {
			child1.applyVarBindings(bindings);
			child2.applyVarBindings(bindings);
		}
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dsa_assignment3.PLTreeNodeInterface#evaluateConstantSubtrees()
	 */
	@Override
	public Boolean evaluateConstantSubtrees() {
		// WRITE YOUR CODE HERE, CHANGING THE RETURN STATEMENT IF NECESSARY
		if (type.getArity() == 2) {
			child1.evaluateConstantSubtrees();
			child2.evaluateConstantSubtrees();
			if (type == NodeType.AND) {
				if (child1.type == NodeType.FALSE || child2.type == NodeType.FALSE) {
					type = NodeType.FALSE; 
					return false;
				}
				else if (child1.type == NodeType.TRUE && child2.type == NodeType.TRUE) {
					type = NodeType.TRUE;
					return true;
				}
				else if (child1.type == NodeType.TRUE) {
					type = child2.type;
					child1 = child2.child1;
					child2 = child2.child2;
					return null;
				}
				else if (child2.type == NodeType.TRUE) {
					type = child1.type;
					child2 = child1.child2;
					child1 = child1.child1;
					return null;
				}
				else {
					return null;
			}
			}
				
			else if (type == NodeType.OR) {
				if (child1.type == NodeType.TRUE || child2.type == NodeType.TRUE) {
					type = NodeType.TRUE;
					return true;
				}
				else if (child1.type == NodeType.FALSE && child2.type == NodeType.FALSE) {
					type = NodeType.FALSE;
					return false;
				}
				else if (child1.type == NodeType.FALSE) {
					type = child2.type;
					child1 = child2.child1;
					child2 = child2.child2;
					return null;
				}
				else if (child2.type == NodeType.FALSE) {
					type = child1.type;
					child2 = child1.child2;
					child1 = child1.child1;
					return null;
				}
				else {
					return null;
				}
			}
			else if (type == NodeType.IMPLIES) {
				if (child1.type == NodeType.FALSE) {
					type = NodeType.TRUE;
					return true;
				}
				else if (child1.type == NodeType.TRUE && child2.type == NodeType.TRUE) {
					type = NodeType.TRUE;
					return true;
				}
				else if (child1.type == NodeType.TRUE && child2.type == NodeType.FALSE) {
					type = NodeType.FALSE;
					return false;
				}
				else if (child1.type == NodeType.TRUE) {
					type = child2.type;
					child1 = child2.child1;
					child2 = child2.child2;
					return null;
				}
				else if (child2.type == NodeType.FALSE) {
					type = NodeType.NOT;
					child2 = null;
					return null;
				}
				else {
					return null;
				}
			}
			child1.evaluateConstantSubtrees();
			child2.evaluateConstantSubtrees();
			
		}
		else if (type.getArity() == 1) {
			child1.evaluateConstantSubtrees();
			if (child1.type == NodeType.FALSE) {
				type = NodeType.TRUE;
				return true;
			}
			else if (child1.type == NodeType.TRUE) {
				type = NodeType.FALSE;
				return false;
			}
			else {
				child1.evaluateConstantSubtrees();
				return null;
			}
			//child1.evaluateConstantSubtrees();
		}
		
		else if (type == NodeType.TRUE) {
			return true;
		}
		else if (type == NodeType.FALSE) {
			return false;
		}
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dsa_assignment3.PLTreeNodeInterface#reduceToCNF()
	 */
	@Override
	public void reduceToCNF() {
		replaceImplies();
		pushNotDown();
		pushOrBelowAnd();
		makeAndOrRightDeep();
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dsa_assignment3.PLTreeNodeInterface#replaceImplies()
	 */
	@Override
	public void replaceImplies() {
		// WRITE YOUR CODE HERE
		if (type.getArity() == 2) {
			if (type == NodeType.IMPLIES) {
				PLTreeNode ltree = child1 ;
				this.child1 = new PLTreeNode (NodeType.NOT, ltree, null) ;
				type = NodeType.OR;
			}
			child1.replaceImplies();
			child2.replaceImplies();
		}
		else if (type.getArity() == 1) {
			child1.replaceImplies();
		}
		
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dsa_assignment3.PLTreeNodeInterface#pushNotDown()
	 */
	@Override
	public void pushNotDown() {
		// WRITE YOUR CODE HERE
		if (type == NodeType.NOT) {
			if (child1.type == NodeType.NOT) {
				PLTreeNode subtree = child1.child1;
				PLTreeNode ltree = child1.child1.child1;
				PLTreeNode rtree = child1.child1.child2;
				type = subtree.type;
				child1 = ltree;
				child2 = rtree;
				this.pushNotDown();
			}
			else if (child1.type == NodeType.AND) {
				PLTreeNode ltree = child1.child1;
				PLTreeNode rtree = child1.child2 ;
				type = NodeType.OR;
				this.child1 = new PLTreeNode (NodeType.NOT, ltree, null) ;
				this.child2 = new PLTreeNode (NodeType.NOT, rtree, null) ;
				child1.pushNotDown();
				child2.pushNotDown();
			}
			else if (child1.type == NodeType.OR) {
				PLTreeNode ltree = child1.child1;
				PLTreeNode rtree = child1.child2 ;
				type = NodeType.AND;
				this.child1 = new PLTreeNode (NodeType.NOT, ltree, null) ;
				this.child2 = new PLTreeNode (NodeType.NOT, rtree, null) ;
				child1.pushNotDown();
				child2.pushNotDown();
			}
			else if (child1.type.getArity() != 0) {
				child1.pushNotDown();
			}
		}
		else if (type.getArity()==2) {
			child1.pushNotDown();
			child2.pushNotDown();
		}
		//what about variables and true/false?
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dsa_assignment3.PLTreeNodeInterface#pushOrBelowAnd()
	 */
	@Override
	public void pushOrBelowAnd() {
		// WRITE YOUR CODE HERE
		if (type.getArity() == 2) {
			child1.pushOrBelowAnd();
			child2.pushOrBelowAnd();
			if (type == NodeType.OR) {
				if (child1.type == NodeType.AND) {
					type = NodeType.AND;
					child1.type = NodeType.OR;
					//PLTreeNode R = child2;
					PLTreeNode Q = child1.child2;
					child1.child2 = child2;
					child2 = new PLTreeNode (child1);
					child2.child1 = Q;
					//this.child2 = new PLTreeNode(NodeType.OR, Q, R);
					//child1.pushOrBelowAnd();
					//child2.pushOrBelowAnd();
					}
				
				else if (child2.type == NodeType.AND) {
					type = NodeType.AND;
					child2.type = NodeType.OR;
					PLTreeNode P = child1;
					PLTreeNode Q = child2.child1;
					child2.child1 = child1;
					child1 = new PLTreeNode (child2);
					child1.child2 = Q;
					//this.child1 = new PLTreeNode(NodeType.OR, P, Q );
					//child1.pushOrBelowAnd();
					//child2.pushOrBelowAnd();
					}
				
				
				/*
				 * else if ((child1.type == NodeType.OR) && ((child1.child1.type ==
				 * NodeType.AND) || (child1.child2.type == NodeType.AND))) {
				 * child1.pushOrBelowAnd(); this.pushOrBelowAnd(); } else if ((child2.type ==
				 * NodeType.OR) && ((child2.child1.type == NodeType.AND) || (child2.child2.type
				 * == NodeType.AND))) { child2.pushOrBelowAnd(); this.pushOrBelowAnd(); }
				 */
			}
		child1.pushOrBelowAnd();
		child2.pushOrBelowAnd();
		}
		
		
		else if (type.getArity() == 1) {
			child1.pushOrBelowAnd();
		}
		/*else if (type.getArity() == 2) {
			child1.pushOrBelowAnd();
			child2.pushOrBelowAnd();
		}*/
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dsa_assignment3.PLTreeNodeInterface#makeAndOrRightDeep()
	 */
	@Override
	public void makeAndOrRightDeep() {
		// WRITE YOUR CODE HERE
		if (type.getArity() == 2) {
			if (type == NodeType.AND && child1.type == NodeType.AND) {
				PLTreeNode P = child1.child1;
				PLTreeNode Q = child1.child2;
				PLTreeNode R = child2;
				child1 = P;
				this.child2 = new PLTreeNode (NodeType.AND, Q, R) ;
			}
			else if (type == NodeType.OR && child1.type == NodeType.OR) {
				PLTreeNode P = child1.child1;
				PLTreeNode Q = child1.child2;
				PLTreeNode R = child2;
				child1 = P;
				this.child2 = new PLTreeNode (NodeType.OR, Q, R) ;
			}
			
			child1.makeAndOrRightDeep();
			child2.makeAndOrRightDeep();
		}
	
		else if (type.getArity() == 1) {
			child1.makeAndOrRightDeep();
		}
		return;
	}

}
