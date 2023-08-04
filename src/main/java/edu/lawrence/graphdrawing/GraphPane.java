package edu.lawrence.graphdrawing;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.TreeMap;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

//extends pane class and add additional capabilities
public class GraphPane extends Pane {
    private ArrayList<Vertex> vertices;
    private ArrayList<Edge> edges;
    //made for algorithm
    private Vertex draggingV;
    //during the dragging process use this two member variables
    private double lastX;
    private double lastY;
    private Vertex dragSource;
    private Line dragLine;
    private boolean drawingEdge;
    
    
    public GraphPane() {
        vertices = new ArrayList<Vertex>();
        edges = new ArrayList<Edge>();
        //made for algorithm
        draggingV = null;
        dragSource = null;
        dragLine = null;
        drawingEdge = false;
        Scanner input = null;
        
        //in the constructer, has to read the past graph info from graph.txt
        try {
            input = new Scanner(new File("graph.txt"));
            TreeMap<String,Vertex> map = new TreeMap<String,Vertex>();
            //vertices
            int vCount = input.nextInt();
            for(int n = 0;n < vCount;n++) {
                String label = input.next();
                double x = input.nextDouble();
                double y = input.nextDouble();
                Vertex v = new Vertex(label,x,y);
                map.put(label, v);
                vertices.add(v);
                this.getChildren().addAll(v.getShapes());
                v.adjustLocations();
            }
            //edges
            int eCount = input.nextInt();
            for(int n = 0;n < eCount;n++) {
                String source = input.next();
                String dest = input.next();
                String weight = input.next();
                Vertex src = map.get(source);
                Vertex dst = map.get(dest);
                //made change
                String wgt = weight;
                if(src != null && dst != null) {
                    Edge e = new Edge(src,dst,wgt);
                    edges.add(e);
                    this.getChildren().addAll(0,e.getShapes());
                }
                
                
            }
        } catch(Exception ex) {
        }
        
        //mouse interactions
        //need code to drag events
        //lambda expression
        this.setOnMousePressed(e -> startDrag(e));
        this.setOnMouseDragged(e -> continueDrag(e));
        this.setOnMouseReleased(e -> endDrag(e));
    }
    
    public void clear() {
        vertices.clear();
        edges.clear();
        this.getChildren().clear();
    }
    
    public void save() {
       PrintWriter output = null;
        try { 
            output = new PrintWriter(new File("graph.txt"));
            output.println(vertices.size());
            for(Vertex v : vertices) {
                v.save(output);
            }
            output.println(edges.size());
            for(Edge e : edges) {
                e.save(output);
            }
            output.close();
        } catch(Exception ex) {
            System.out.println("Error writing data to text file");
        } 
    }
    
    public void newVertex(String label) {
        Vertex v = new Vertex(label,this.getWidth()/2,this.getHeight()/2);
        vertices.add(v);
        this.getChildren().addAll(v.getShapes());
        v.adjustLocations();
        drawingEdge = false;
    }
    
    public void newEdge() {
        drawingEdge = true;
    }
    
    public void init(){
        int temp = (int)(Math.random()*vertices.size()-1);
        vertices.get(temp).markRed();
        
    }
   
    public void mstRound(){
        
        int min = 1000;
        Edge minEdge = null;

        for(Edge e : edges){
            Vertex src = e.getSource();
            Vertex dest = e.getDest();
            //if it is red&red, black&black you don't care
            if(src.isMarked() != dest.isMarked()){
                //get the minimum weight amongst possible routes
                if(min > e.getWeight()){
                    min = e.getWeight();
                    minEdge = e;
                }
            }
        }

        minEdge.markRed();
        Vertex src = minEdge.getSource();
        Vertex dest = minEdge.getDest();

        if(!src.isMarked()){
            src.markRed();
        }
        else{
            dest.markRed();
        }
    }
    
    public int getVerticeSize(){
        return vertices.size();
    }
    
    
    //ask which of these two modes are we in? edge drawing mode or vertice drawing mode?
    public void startDrag(MouseEvent e) {
        
        //the user clicks on the screen, create a line segment
        //the start and end point is on the same spot
        if(drawingEdge) {
            dragSource = null;
            dragLine = null;
            for(Vertex v : vertices) {
                //go to each vertices and ask, do you contain this point?
                if(v.containsPoint(e.getX(), e.getY()))
                    dragSource = v;
            }
            
            //store the line in a temporary member variable
            //just to see if the line was a valid one or not before we actually save
            if(dragSource != null) {
                dragLine = new Line(dragSource.getCenterX(),dragSource.getCenterY(),e.getX(),e.getY());
                this.getChildren().add(dragLine);
            }
        } else {
            draggingV = null;
            for(Vertex v : vertices) {
                if(v.containsPoint(e.getX(), e.getY()))
                    draggingV = v;
            }
            lastX = e.getX();
            lastY = e.getY();
        }
        

    }

    public void continueDrag(MouseEvent e) {
        if(drawingEdge) {
            if(dragLine != null) {
                dragLine.setEndX(e.getX());
                dragLine.setEndY(e.getY());
            }
        } else {
            if(draggingV != null) {
                double deltaX = e.getX() - lastX;
                double deltaY = e.getY() - lastY;
                draggingV.moveBy(deltaX, deltaY);
                
                //when the vertice was moved, relocate edges too
                adjustEdges();
                lastX = e.getX();
                lastY = e.getY();
            }
        }
    }
    
    public void endDrag(MouseEvent e) {
        if(drawingEdge) {
            Vertex dragDest = null;
            for(Vertex v : vertices) {
                if(v.containsPoint(e.getX(), e.getY()))
                    dragDest = v;
                    
            }
            //do we have a valid dragsource?
            if(dragLine != null && dragDest != null && dragDest != dragSource) {

                
                TextInputDialog dialog = new TextInputDialog("");
                dialog.setTitle("New Edge");
                dialog.setHeaderText(null);
                dialog.setContentText("Weight:");
                
                Optional<String> result = dialog.showAndWait();
                
                if(result.isPresent()) {
                    String label = result.get();
                    Edge edge = new Edge(dragSource,dragDest,label);
                    edges.add(edge);
                    this.getChildren().addAll(0, edge.getShapes());
                    edge.adjustLocations();
                }
                

                
                
            }
            this.getChildren().remove(dragLine);
        }
    }
    
    
    
    public void adjustEdges() {
        for(Edge e : edges) {
            e.adjustLocations();
        }
    }
    
    @Override
    protected double computePrefHeight(double width) {
        return 600;
    }

    @Override
    protected double computePrefWidth(double height) {
        return 400;
    }
}
