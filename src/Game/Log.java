package Game;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Log {
	private ImageView img;
	private int i, j;
	private int size;
	private Boolean inActive;
	private double w,h;

	public Log(Image img, int size,int i,int j, double w,double h) {
		this.img = new ImageView();
		this.img.setImage(img);
		this.img.setFitWidth(w*(size+1));
		this.img.setFitHeight(h);
		this.w=w;
		this.h=h;
		setI(i);
		setJ(j);
		setSize(size);
		setInActive(false);
	}

	public int getI() {
		return i;
	}

	public void setI(int i) {
		this.i = i;
		img.setY(i*h);
	}

	public int getJ() {
		return j;
	}

	public void setJ(int j) {
		this.j = j;
		img.setX(j*w);
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Boolean getInActive() {
		return inActive;
	}

	public void setInActive(Boolean inActive) {
		this.inActive = inActive;
	}


	
	public ImageView getImg() {
		return this.img;
	}
}
