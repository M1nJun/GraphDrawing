package edu.lawrence.graphdrawing;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;


//menu bar - menu - menu item

public class PrimaryController implements Initializable {
    @FXML private VBox vBox;
    @FXML private Timeline animation;
    //for custom components - > pane
    //border pane gives u a large area in the center and small areas on each 4 corners
    private GraphPane graphPane;
    
    @FXML
    private void newGraph(ActionEvent event) {
        graphPane.clear();
    }
    
    @FXML
    private void save(ActionEvent event) {
        graphPane.save();
    }
    
    @FXML
    private void exit(ActionEvent event) {
        Platform.exit();
    }
    
    @FXML
    private void newVertex(ActionEvent event)  {
        //dialog that has the text field in it, and they can click yes/no smthing like that
        TextInputDialog dialog = new TextInputDialog("");

        dialog.setTitle("New Vertex");
        //in this case you don't need a header text to explain why would you need this dialog
        dialog.setHeaderText(null);
        dialog.setContentText("Label:");

        //waits until the user is done interacting with the dialog
        //user can cancel, so u neeed a optional
        Optional<String> result = dialog.showAndWait();
        
        //lambda expression
        //if the user didn't input anything don't give out label to the method
        result.ifPresent(label -> {
            graphPane.newVertex(label);
        });
    }
    
    @FXML
    private void newEdge(ActionEvent event)  {
        graphPane.newEdge();
    }
    
    @FXML
    private void runAlgorithm(ActionEvent event) {
        graphPane.init();
        animation = new Timeline(
                new KeyFrame(new Duration(1000), t -> {
                    graphPane.mstRound();
                })
        );
        animation.setCycleCount(graphPane.getVerticeSize()-1);
        animation.playFromStart();
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        graphPane = new GraphPane();
        vBox.getChildren().add(graphPane);
    } 
}
