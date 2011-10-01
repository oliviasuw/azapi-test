package agt0.dev.util.ui;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.zest.layouts.InvalidLayoutConfiguration;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.LayoutRelationship;
import org.eclipse.zest.layouts.algorithms.DirectedGraphLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import static agt0.dev.util.JavaUtils.*;

public class PseudoTreeLayoutAlgorithm extends TreeLayoutAlgorithm {


	public PseudoTreeLayoutAlgorithm(int styles) {
		super(styles);
	}

	@Override
	public synchronized void applyLayout(LayoutEntity[] entitiesToLayout,
			LayoutRelationship[] relationshipsToConsider, double x, double y,
			double width, double height, boolean asynchronous,
			boolean continuous) throws InvalidLayoutConfiguration {

		List<LayoutRelationship> realRelationships = new LinkedList<LayoutRelationship>();
		for (LayoutRelationship r : relationshipsToConsider){
			if ((Boolean) r.getLayoutInformation()){
				realRelationships.add(r);
			}
		}
		super.applyLayout(entitiesToLayout, realRelationships.toArray(new LayoutRelationship[0]), x, y,
				width, height, asynchronous, continuous);
	}	
}
