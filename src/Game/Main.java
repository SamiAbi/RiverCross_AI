package Game;
	
import javafx.application.Application;
import javafx.stage.Stage;


public class Main  extends Application{
	static GameController gameController;
	@Override
	public void start(Stage primaryStage) {
		gameController=new GameController();
		try {
			gameController.start(primaryStage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
