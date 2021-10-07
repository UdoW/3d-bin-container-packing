package com.github.skjolber.packing.points2d;

public class DefaultPoint2D extends Point2D {

	public DefaultPoint2D(int minX, int minY, int maxY, int maxX) {
		super(minX, minY, maxX, maxY);
	}

	@Override
	public boolean isXSupport(int x) {
		return false;
	}

	@Override
	public boolean isYSupport(int y) {
		return false;
	}

}
