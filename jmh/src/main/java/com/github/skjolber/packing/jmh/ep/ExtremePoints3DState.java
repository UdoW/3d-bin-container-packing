package com.github.skjolber.packing.jmh.ep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import com.github.skjolber.packing.api.ep.Point3D;
import com.github.skjolber.packing.ep.points3d.DefaultPlacement3D;
import com.github.skjolber.packing.ep.points3d.ExtremePoints3D;
import com.github.skjolber.packing.test.BouwkampCode;
import com.github.skjolber.packing.test.BouwkampCodeDirectory;
import com.github.skjolber.packing.test.BouwkampCodeLine;
import com.github.skjolber.packing.test.BouwkampCodes;

@State(Scope.Benchmark)
public class ExtremePoints3DState {

	private List<ExtremePoints3DEntries> entries = new ArrayList<>(); 
	
	@Setup(Level.Trial)
	public void init() {
		// these does not really result in successful stacking, but still should run as expected
		BouwkampCodeDirectory directory = BouwkampCodeDirectory.getInstance();
		
		List<BouwkampCodes> codesForCount = directory.getAll();
		for(BouwkampCodes c : codesForCount) {
			for(BouwkampCode bkpLine : c.getCodes()) {
				add(bkpLine);
			}
		}
	}

	private void add(BouwkampCode bkpLine) {
		ExtremePoints3D<DefaultPlacement3D> points = new ExtremePoints3D<>(bkpLine.getWidth(), bkpLine.getDepth(), 1);
		
		ExtremePoints3DEntries extremePointsEntries = new ExtremePoints3DEntries(points);

		List<BouwkampCodeLine> lines = bkpLine.getLines();

		int count = 0;
		
		lines:
		for(BouwkampCodeLine line : lines) {
			List<Integer> squares = line.getSquares();
			int minY = points.getMinY();
			
			Point3D value = points.getValue(minY);
			
			int offset = value.getMinX();
			
			int nextY = minY;
			
			for (int i = 0; i < squares.size(); i++) {
				Integer square = squares.get(i);
				int factoredSquare = square;
				
				DefaultPlacement3D defaultPlacement3D = new DefaultPlacement3D(offset, value.getMinY(), 0, offset + factoredSquare - 1, value.getMinY() + factoredSquare - 1, 0, Collections.emptyList());
				extremePointsEntries.add(new ExtremePoint3DEntry(nextY, defaultPlacement3D));
				points.add(nextY, defaultPlacement3D);
	
				offset += factoredSquare;
	
				nextY = points.get(offset, value.getMinY(), 0);
				
				count++;
				
				if(nextY == -1 && i + 1 < squares.size()) {
					throw new IllegalStateException("No next y at " + offset + "x" + value.getMinY() +  "x0 with " + (squares.size() - 1 - i) + " remaining");
				}

			}
		}
		
		if(points.getValueCount() > 0) {
			throw new IllegalStateException("Still have " + points.getValueCount() + ": " + points.getValues());
		}

		
		points.redo();
		
		entries.add(extremePointsEntries);
	}
	
	public List<ExtremePoints3DEntries> getEntries() {
		return entries;
	}

	@TearDown(Level.Trial)
	public void shutdown() throws InterruptedException {
	}
}
