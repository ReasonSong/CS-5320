import java.util.*;

public class FDChecker {

	/**
	 * Checks whether a decomposition of a table is dependency
	 * preserving under the set of functional dependencies fds
	 * 
	 * @param t1 one of the two tables of the decomposition
	 * @param t2 the second table of the decomposition
	 * @param fds a complete set of functional dependencies that apply to the data
	 * 
	 * @return true if the decomposition is dependency preserving, false otherwise
	 **/
	public static boolean checkDepPres(AttributeSet t1, AttributeSet t2, Set<FunctionalDependency> fds) {
		boolean dp = false;
		for (FunctionalDependency fd: fds) {
			AttributeSet result = fd.left;
			AttributeSet prev_result = new AttributeSet();
			while(!result.equals(prev_result)) {
				AttributeSet temp = new AttributeSet();
				for (int i = 0; i < 2; i++) {	
					AttributeSet t = new AttributeSet();
					t.addAll(i == 0 ? t1 : t2);
					temp.addAll(result);
					temp.retainAll(t);
					temp = closure(temp, fds);
					temp.retainAll(t);
					prev_result = result;
					result.addAll(temp);	
				}
			}		
			if (result.contains(fd.right)) dp = true;
			else                           return false; 
		}
		return dp;
	}

	/**
	 * Checks whether a decomposition of a table is lossless
	 * under the set of functional dependencies fds
	 * 
	 * @param t1 one of the two tables of the decomposition
	 * @param t2 the second table of the decomposition
	 * @param fds a complete set of functional dependencies that apply to the data
	 * 
	 * @return true if the decomposition is lossless, false otherwise
	 **/
	public static boolean checkLossless(AttributeSet t1, AttributeSet t2, Set<FunctionalDependency> fds) {
		AttributeSet intersection = new AttributeSet();
		intersection.addAll(t1);
		intersection.retainAll(t2);		
		if (closure(intersection, fds).containsAll(t1) || closure(intersection, fds).containsAll(t2)){ 
			return true;
		}
		return false;
	}

	// helper method
	// finds the total set of attributes implied by attrs
	private static AttributeSet closure(AttributeSet attrs, Set<FunctionalDependency> fds) {
		if (attrs == null) return null; // attrs cannot be null
		AttributeSet closure = new AttributeSet();
		closure.addAll(attrs);
		AttributeSet prev = new AttributeSet();
		while (closure.size() > prev.size()) {
			prev.clear();
			prev.addAll(closure);
			for (FunctionalDependency fd: fds) {
				if (closure.containsAll(fd.left)) closure.add(fd.right); 
			}
		}
		return closure;
	}
}
