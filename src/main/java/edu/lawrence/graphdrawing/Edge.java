package edu.lawrence.graphdrawing;

import java.io.PrintWriter;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

public class Edge {
    //two vertices that get's connected
    private Vertex source, destination;
    private Line line;
    private Text weight;
    private int w;
    
    public Edge(Vertex src,Vertex dest,String label) {
        source = src;
        destination = dest;
        //line object that connects vertices
        line = new Line(src.getCenterX(),src.getCenterY(),dest.getCenterX(),dest.getCenterY());
        
        w = Integer.parseInt(label);
        weight = new Text((dest.getCenterX()+src.getCenterX())/2,
                         (dest.getCenterY()+src.getCenterY())/2, label);
    }
    
    //save edge
    public void save(PrintWriter w) {
        //printwriter method saves the prints to a text file.
        w.println(source.getLabel()+ " " + destination.getLabel());
    }
    
    public void adjustLocations() {
        line.setStartX(source.getCenterX());
        line.setStartY(source.getCenterY());
        line.setEndX(destination.getCenterX());
        line.setEndY(destination.getCenterY());
        
        weight.setX((line.getStartX()+line.getEndX())/2);
        weight.setY((line.getStartY()+line.getEndY())/2);
    }
    
    //changed into an arraylist of shapes
    public ArrayList<Shape> getShapes() {
        ArrayList<Shape> result = new ArrayList<Shape>();
        result.add(line);
        result.add(weight);
        return result;
    }
    
    public Vertex getSource(){
        return source;
    }
    
    public Vertex getDest(){
        return destination;
    }
    
    public void markRed(){
        line.setStroke(Color.RED);
    }
    
    public int getWeight(){
        return w;
    }
}
