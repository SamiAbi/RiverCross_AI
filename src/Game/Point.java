package Game;

public class Point implements Comparable<Object> {
	private int i, j;
	private int cost;

	public Point(int i, int j, int cost) {
		super();
		setCost(cost);
		setI(i);
		setJ(j);
	}
	

	public Point(int i, int j) {
		super();
		this.i = i;
		this.j = j;
		setCost(0);
	}


	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public int getJ() {
		return j;
	}

	public void setJ(int j) {
		this.j = j;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	@Override
	public int compareTo(Object o) {
		int comp = ((Point) o).getCost();
		return comp - this.cost;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		Point p = (Point) obj;
		return (p.getI() == this.i && p.getJ() == this.j) ? true : false;
	}

}
