package ext.sim.tools;

import java.util.ArrayList;


/**
 * @author alongrub
 * A multi-dimensional regular array (regular == every dimension is of the same size) 
 */
public class MultiDimRegularArray<T>{
	private final int DIMENSIONS;
	private final int DIM_SIZE;
	
	private ArrayList<T> data;
	
	private final int SIZE;
	


	public MultiDimRegularArray(int n, int d) {
		DIMENSIONS = n;
		DIM_SIZE = d;
		SIZE = (int) Math.pow(n, d);
		data = new ArrayList<T>(SIZE);
	}

	/**
	 * Get the value of the hyper cube at {index[0],index[1],...index[dimension-1]}
	 * Note that I assume that the requester is well aware of the indices ordering
	 * @param index
	 * @return
	 */
	public T getValueOf(int[] index) {
		if (index.length != DIMENSIONS)
			throw new IllegalArgumentException("MultiDimRegularArray getValueOf index is illegal "+index.length);
		int key=0;
		for (int i=0; i<index.length; i++){
			if (index[i]<0)
				throw new IllegalArgumentException("MultiDimRegularArray negative getValueOf index["+i+"], "+index[i]);
			key += index[i]*Math.pow(DIM_SIZE, i);
		}
		return data.get(key);
	}

	/**
	 * Set the value of the hyper cube at {index[0],index[1],...index[dimension-1]}
	 * Note that I assume that the requester is well aware of the indices ordering
	 * @param index
	 * @param value
	 * @return
	 */
	public void setValueAt(int[] index, T value) {
		if (index.length != DIMENSIONS)
			throw new IllegalArgumentException("MultiDimRegularArray getValueOf index is illegal "+index.length);
		int key=0;
		for (int i=0; i<index.length; i++){
			if (index[i]<0)
				throw new IllegalArgumentException("MultiDimRegularArray negative getValueOf index["+i+"], "+index[i]);
			key += index[i]*Math.pow(DIM_SIZE, i);
		}
		data.add(key, value);
	}

	public int getDimensions() {
		return DIMENSIONS;
	}

	public int getDimSize() {
		return DIM_SIZE;
	}

	public int getSize() {
		return SIZE;
	}
}
