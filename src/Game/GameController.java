package Game;

import java.util.ArrayList;
import java.util.Random;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class GameController implements Runnable {
	@FXML
	private Label woodlbl;
	@FXML
	private Button startbtn;
	@FXML
	private Button stopbtn;
	@FXML
	private ImageView woodimg;
	@FXML
	private ImageView rick;
	@FXML
	private ImageView thinkingimg;
	@FXML
	private ImageView solimg;
	@FXML
	private Pane riverpnl;
	public volatile int[][] map;
	private int n, m;
	private ArrayList<Log> logs;
	private volatile boolean flag = true;
	private volatile boolean rickIsMove = false;
	private volatile Rick rickMove;
	private volatile ArrayList<Point> path = new ArrayList<Point>();
	private volatile RickMoving rt;

	/**
	 * start method to init the main window
	 */
	public void start(Stage primaryStage) throws Exception {
		AnchorPane root = (AnchorPane) FXMLLoader.load(getClass().getResource("main_page.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	/**
	 * to check if map in i , j position is equal to zero
	 */
	public synchronized boolean checkIndexInMap(int i, int j) {
		if (map[i][j] == 0)
			return true;
		return false;

	}

	/**
	 * main thread for logs moving and update Rick moving with logs
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Random rand = new Random();
		ArrayList<Log> deleLog = new ArrayList<Log>();
		while (flag) {
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					// move all logs and if there is log reach the end remove it from screen (by add
					// the logs to temp array)
					for (Log l : logs) {
						if (l.getInActive()) {
							if (l.getJ() + l.getSize() == 0) {
								riverpnl.getChildren().remove(l.getImg());
								deleLog.add(l);
								map[l.getI()][0] = 0;
							} else {
								try {
									for (int i = 0; i < l.getSize(); i++) {
										if (l.getJ() - i > 0 && l.getJ() - i < m)
											map[l.getI()][l.getJ() - 1] = 1;
										if (l.getJ() + l.getSize() - i > 0 && l.getJ() + l.getSize() - i < m)
											map[l.getI()][l.getJ() + l.getSize() - i] = 0;
									}
									l.setJ(l.getJ() - 1);
								} catch (Exception e) {
								}
							}
						}
					}
					// check if Rick is moving and update his movement with logs
					if (rickMove.getIndexY() > 0 && rickMove.getIndexY() < n) {
						if (rickMove.getIndexX() > 0)
							rickMove.setIndexX(rickMove.getIndexX() - 1);
						else {
							rickMove.setIndexX(m / 2);
							rickMove.setIndexY(n);
							rickIsMove = false;
						}
						// to make sure Rick doesn't step in water and if he step move him back to start
						if (rickMove.getIndexX() >= 0 && rickMove.getIndexX() < m && rickMove.getIndexY() > 0
								&& rickMove.getIndexY() < n
								&& checkIndexInMap(rickMove.getIndexY(), rickMove.getIndexX())) {
							rickMove.setIndexX(m / 2);
							rickMove.setIndexY(n);
							rickMove.MoveRick();
							rickIsMove = false;
						}
						rickMove.MoveRick();

					}
					// remove all logs that have to remove from screen
					logs.removeAll(deleLog);
					deleLog.clear();
					// start new logs to move randomly
					for (int i = 0; i < n; i++) {
						if (rand.nextBoolean()) {
							int size = rand.nextInt(1) + 1;
							if (checkIndexInMap(i, m - 1)) {
								logs.add(new Log(woodimg.getImage(), size, i, m - 1, woodimg.getFitWidth(),
										woodimg.getFitHeight()));
								map[i][m - 1] = 1;
								riverpnl.getChildren().add(logs.get(logs.size() - 1).getImg());
								logs.get(logs.size() - 1).setInActive(true);
							}
						}
					}
					// this boolean value to update the path with logs (in another thread) that rick
					// have to go with
					rt.update = true;

				}
			});

			try {
				Thread.sleep(950);
			} catch (InterruptedException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	// start button method
	public void StartHandle(ActionEvent event) throws Exception {
		double h = riverpnl.getHeight(), w = riverpnl.getWidth();
		n = (int) (h / woodimg.getFitHeight());
		m = (int) (w / woodimg.getFitWidth());
		map = new int[n][m];
		logs = new ArrayList<Log>();
		rick.setLayoutX((m / 2) * 100);
		rick.setLayoutY(riverpnl.getLayoutY() + riverpnl.getHeight());
		rick.setVisible(true);
		flag = true;
		rt = new RickMoving();
		new Thread(this).start();
		new Thread(rt).start();
		rickMove = new Rick(rick, this, 100, woodimg.getFitHeight(), riverpnl.getLayoutY(), m / 2, n, n, m);
		startbtn.setDisable(true);
		stopbtn.setDisable(false);
		rickIsMove = false;
		thinkingimg.setVisible(true);
		solimg.setVisible(false);
	}

	// stop button method
	public void StopHandle(ActionEvent event) throws Exception {
		startbtn.setDisable(false);
		stopbtn.setDisable(true);
		flag = false;
		rick.setVisible(false);
		for (Log l : logs) {
			riverpnl.getChildren().remove(l.getImg());
		}
		logs.clear();
		thinkingimg.setVisible(false);
		solimg.setVisible(false);
	}

	// thread for Rick movement and finding path
	public class RickMoving implements Runnable {
		volatile boolean update = false;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				// the logs has move and need to update the path or finding new path
				if (update) {
					// if Rick doesn't moving yet finding new path
					if (!rickIsMove) {
						thinkingimg.setVisible(true);
						solimg.setVisible(false);
						path = rickMove.setMap();
						if (path.size() > 0) {
							rickIsMove = true;
							for (int i = 0; i < path.size(); i++)
								System.out.println(path.get(i).getI() + "," + path.get(i).getJ());
							// flag = false;

							for (int i = 0; i < n; i++) {
								for (int j = 0; j < m; j++)
									System.out.print(map[i][j] + " ");
								System.out.println();
							}
							System.out.println("______________________________");
							thinkingimg.setVisible(false);
							solimg.setVisible(true);
						}
					}
					// else then update the path with new logs movement
					else {
						for (Point p : path)
							if (p.getJ() == 0) {
								path.clear();
								rickIsMove = false;
								break;
							} else
								p.setJ(p.getJ() - 1);
					}
					update = false;
				}
				// if Rick find path then update his position by the path array
				if (rickIsMove && path.size() > 0) {
					if (rickMove.getIndexX() < path.get(0).getJ() && rickMove.getIndexY() == n) {
						rickMove.setIndexX(rickMove.getIndexX() + 1);
						rickMove.MoveRick();
					} else if (rickMove.getIndexX() > path.get(0).getJ() && rickMove.getIndexY() == n) {
						rickMove.setIndexX(rickMove.getIndexX() - 1);
						rickMove.MoveRick();
					} else {
						if (path.get(0).getJ() > 0 && map[path.get(0).getI()][path.get(0).getJ()] == 1) {
							rickMove.setIndexX(path.get(0).getJ());
							rickMove.setIndexY(path.get(0).getI());
							rickMove.MoveRick();
							path.remove(0);

						} else {
							path.clear();
							rickIsMove = false;
						}
					}
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// if Rick reach the last row in map then update his postion to other side and
				// stop all thread
				else if (rickIsMove && rickMove.getIndexY() == 0) {
					rickMove.setIndexY(-1);
					rickMove.MoveRick();
					flag = false;
					break;
				}
			}
		}

	}

}
