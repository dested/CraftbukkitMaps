package org.bukkit.craftbukkit.maps;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.maps.Shape;

public class CraftShape implements Shape {
	public static CraftShape Square = new CraftShape(new Point(0, 0),
			new Point[] { new Point(1, -1), new Point(-1, -1),
					new Point(-1, 1), new Point(1, 1) });

	private ArrayList<Point> _points = new ArrayList<Point>();

	private CraftShape(Point cen, Point... pts) {
		_points = new ArrayList<Point>(Arrays.asList(pts));
		_center = cen;
	}

	public CraftShape() {
		_center = new Point(0, 0);
	}

	private Point _center;

	public Point[] getCoordinates() {
		return _points.toArray(new Point[_points.size()]);
	}

	public void pushCoordinate(int x, int y) {
		_points.add(new Point(x, y));
	}

	public void setCenter(int x, int y) {
		_center = new Point(x, y);
	}

	public Point getCenter() {
		return _center;
	}

	public Shape cloneMe() {
return new CraftShape(this._center,getCoordinates());
	}

}