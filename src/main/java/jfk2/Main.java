package jfk2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class Main  extends Application {

    public static void main(String[] args){
        /*
        //zmienianie nazw
        javafx.scene.control.TextField  download = (javafx.scene.control.TextField) getSearchField.getScene().lookup("#getLyricsButton");
        search.setText("Search");

        javafx.scene.control.TextField  search = (javafx.scene.control.TextField) getSearchField.getScene().lookup("#getSearchButton");
        search.setText("QuickDownload");

/*      //add field
        private boolean once = false;

        if(!once && totalScore >= 50){
        once = true;
        javax.swing.JFrame frame = new javax.swing.JFrame();
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        javax.swing.JLabel label = new javax.swing.JLabel("Wygrales");
        label.setHorizontalTextPosition(javax.swing.JLabel.CENTER);
        frame.setSize(300,300);
        frame.getContentPane().add(label);
        frame.setVisible(true);}*/
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("scene.fxml"));
        primaryStage.setTitle("Test");
        int height = 500;
        int width = 844;
        primaryStage.setScene(new Scene(root, width, height));

        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
    }
}
