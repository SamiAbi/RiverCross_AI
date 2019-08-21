package Game;

import java.util.ArrayList;
import java.util.Collections;

import javafx.scene.image.ImageView;

public class Rick {
	private ImageView rick;
	private volatile GameController game;
	private int[] destMap;
	private int indexX, indexY;
	private int n, m;
	private double w, h, t;

	public Rick(ImageView rick, GameController g, double w, double h, double t, int... indexs) {
		super();
		this.rick = rick;
		this.game = g;
		this.indexX = indexs[0];
		this.indexY = indexs[1];
		this.n = indexs[2];
		this.m = indexs[3];
		this.w = w;
		this.h = h;
		this.t = t;
		destMap = new int[m];
	}

	public int getIndexX() {
		return indexX;
	}

	public int getIndexY() {
		return indexY;
	}

	public void setIndexX(int indexX) {
		this.indexX = indexX;
	}

	public void setIndexY(int indexY) {
		this.indexY = indexY;
	}

	// this method searching for path to other side and return if there is path or
	// null if not
	public synchronized ArrayList<Point> setMap() {
		// start with first row find all A-star value for all logs in first row
		destMap = new int[m];
		for (int j = m - 1; j >= 0; j--)
			if (game.map[n - 1][j] == 1)
				destMap[j] = F(n - 1, j);
		ArrayList<Point> path = new ArrayList<Point>();
		int j = GetMaxIndex();
		// if j!=-1 then there is log in this row
		if (j != -1) {
			// add first point to path (if Rick hasn't move yet add the first point else
			// then
			// Rick want to find new path and add first point to move
			if (indexY == n)
				path.add(new Point(n - 1, j, destMap[j]));
			else
				path.add(new Point(indexY, indexX));
			// start to finding path to other side
			while (true) {
				Point p = path.get(path.size() - 1);
				ArrayList<Point> possPoints = new ArrayList<Point>();
				if (!path.contains(new Point(p.getI() - 1, p.getJ(), F(p.getI() - 1, p.getJ())))
						&& game.map[p.getI() - 1][p.getJ()] == 1)
					possPoints.add(new Point(p.getI() - 1, p.getJ(), F(p.getI() - 1, p.getJ())));
				if (((p.getJ() + 1) > 0 && (p.getJ() + 1) < m - 1)
						&& !path.contains(new Point(p.getI(), p.getJ() + 1, F(p.getI(), p.getJ() + 1)))
						&& game.map[p.getI()][p.getJ() + 1] == 1)
					possPoints.add(new Point(p.getI(), p.getJ() + 1, F(p.getI(), p.getJ() + 1)));
				if (((p.getJ() - 1) > 0 && (p.getJ() - 1) < m - 1)
						&& !path.contains(new Point(p.getI(), p.getJ() - 1, F(p.getI(), p.getJ() - 1)))
						&& game.map[p.getI()][p.getJ() - 1] == 1)
					possPoints.add(new Point(p.getI(), p.getJ() - 1, F(p.getI(), p.getJ() - 1)));
				Collections.sort(possPoints);
				if (possPoints.size() == 0) {
					path.clear();
					break;
				} else {
					path.add(possPoints.get(0));
					if (path.get(path.size() - 1).getI() == 0) {
						break;
					}
				}
			}
		}
		if (path.size() != 0 && path.get(path.size() - 1).getI() != 0)
			path.clear();
		return path;
	}

	// this method to calculate the cost that have to move from i,j to other side
	public synchronized int Cost(int i, int j, ArrayList<Point> points) {
		if (i == 0)
			return 1;
		if (j == m || j == -1 || i == n)
			return 0;
		if (game.map[i - 1][j] == 1) {
			points.add(new Point(i, j));
			return Cost(i - 1, j, points) + 1;
		} else if ((j > 0 && j < m - 1) && !points.contains(new Point(i, j + 1)) && game.map[i][j + 1] == 1) {
			points.add(new Point(i, j));
			return Cost(i, j + 1, points) - 1;

		} else if ((j > 0 && j < m - 1) && !points.contains(new Point(i, j - 1)) && game.map[i][j - 1] == 1) {
			points.add(new Point(i, j));
			return Cost(i, j - 1, points) - 1;
		}
		return 0;
	}

	// this method return the A-star value => g(n) + h(n)=>( number of rows away
	// from the other side) + (how much really cost to move to other side)
	public int F(int i, int j) {
		return (n - i) + Cost(i, j, new ArrayList<Point>());
	}
	//move Rick 
	public void MoveRick() {
		rick.setLayoutY((indexY) * h + t);
		rick.setLayoutX(indexX * w);

	}
	//return max index in array
	public int GetMaxIndex() {
		int max = destMap[0];
		int index = 0;
		for (int i = 0; i < m; i++)
			if (destMap[i] > max) {
				max = destMap[i];
				index = i;
			}
		if (max == 0)
			return -1;
		else
			return index;
	}

}
