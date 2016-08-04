import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends Application{
    public enum Shapes{
        CIRCLE, SQUARE
    }

    public enum Coloring{
        WHITE, RAINBOW, RANDOM
    }

    private static final int CANVAS_WIDTH = 1200;
    private static final int CANVAS_HEIGHT = 600;

    private Canvas canvas;
    private GraphicsContext graphicsContext;

    private double c = 7;
    private int n = 10000;
    private double angle = Math.toRadians(137.5);
    private Shapes shape;
    private Coloring coloring;
    private int size = 8;

    private Random random;

    private double lastDrawTime = System.currentTimeMillis();

    private Spinner<Integer> numberOfDotsSpinner;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.random = new Random();

        this.canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        BorderPane borderPane = new BorderPane();
        FlowPane flowPane = new FlowPane(Orientation.VERTICAL);
        flowPane.setVgap(10);

        FlowPane numberOfObjectsPane = new FlowPane();
        this.numberOfDotsSpinner = new Spinner<>();
        this.numberOfDotsSpinner.setEditable(true);
        this.numberOfDotsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, this.n));
        this.numberOfDotsSpinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                n = newValue;
                render();
            }
        });

        Label numberOfDotsLabel = new Label("Number of Points");
        numberOfDotsLabel.setPadding(new Insets(0, 10, 0, 0));

        numberOfObjectsPane.getChildren().add(numberOfDotsLabel);
        numberOfObjectsPane.getChildren().add(this.numberOfDotsSpinner);

        FlowPane anglePane = new FlowPane();
        Label angleLabel = new Label("Angle: 137.5");
        angleLabel.setPadding(new Insets(0, 10, 0, 0));

        Spinner angleSpinner = new Spinner();
        angleSpinner.setEditable(true);

        SpinnerValueFactory.DoubleSpinnerValueFactory valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(1, 360, 137.5);
        valueFactory.setAmountToStepBy(0.1);

        angleSpinner.setValueFactory(valueFactory);
        angleSpinner.valueProperty().addListener(new ChangeListener<Double>() {
            @Override
            public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
                angle = newValue;
                angleLabel.setText("Angle: " + angle);
                render();
            }
        });

        anglePane.getChildren().add(angleLabel);
        anglePane.getChildren().add(angleSpinner);

        FlowPane shapePane = new FlowPane();
        Label shapeLabel = new Label("Shape");
        shapeLabel.setPadding(new Insets(0, 10, 0, 0));

        ChoiceBox<String> shapeChoiceBox = new ChoiceBox<>();
        List<String> shapesList = new ArrayList<>();
        shapesList.add("Circle");
        shapesList.add("Square");
        this.shape = Shapes.CIRCLE;

        ObservableList<String> shapesObserveList = FXCollections.observableArrayList(shapesList);
        shapeChoiceBox.setItems(shapesObserveList);
        shapeChoiceBox.getSelectionModel().select(0);

        shapeChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.equals("Circle")){
                    shape = Shapes.CIRCLE;
                }else if(newValue.equals("Square")){
                    shape = Shapes.SQUARE;
                }
                render();
            }
        });


        shapePane.getChildren().add(shapeLabel);
        shapePane.getChildren().add(shapeChoiceBox);

        FlowPane sizePane = new FlowPane();
        Spinner sizeSpinner = new Spinner();
        sizeSpinner.setEditable(true);
        sizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, this.size));
        sizeSpinner.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                size = newValue;
                render();
            }
        });

        Label sizeLabel = new Label("Size");
        sizeLabel.setPadding(new Insets(0, 10, 0, 0));

        sizePane.getChildren().add(sizeLabel);
        sizePane.getChildren().add(sizeSpinner);


        FlowPane spreadPane = new FlowPane();
        Label spreadLabel = new Label("Spread");
        spreadLabel.setPadding(new Insets(0, 10, 0, 0));

        Spinner spreadSpinner = new Spinner();
        spreadSpinner.setEditable(true);
        spreadSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1, Integer.MAX_VALUE, this.c));
        spreadSpinner.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                c = (double)newValue;
                render();
            }
        });

        spreadPane.getChildren().add(spreadLabel);
        spreadPane.getChildren().add(spreadSpinner);

        FlowPane coloringPane = new FlowPane();
        Label coloringLabel = new Label("Coloring");
        coloringLabel.setPadding(new Insets(0, 10, 0, 0));

        ChoiceBox<String> coloringChoiceBox = new ChoiceBox<>();
        List<String> coloringList = new ArrayList<>();
        coloringList.add("Rainbow");
        coloringList.add("Random");
        coloringList.add("White");

        this.coloring = Coloring.RAINBOW;

        ObservableList<String> coloringObserveList = FXCollections.observableArrayList(coloringList);
        coloringChoiceBox.setItems(coloringObserveList);
        coloringChoiceBox.getSelectionModel().select(0);

        coloringChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.equals("Rainbow")){
                    coloring = Coloring.RAINBOW;
                }else if(newValue.equals("Random")){
                    coloring = Coloring.RANDOM;
                }else if(newValue.equals("White")){
                    coloring = Coloring.WHITE;
                }
                render();
            }
        });


        coloringPane.getChildren().add(coloringLabel);
        coloringPane.getChildren().add(coloringChoiceBox);

        Button saveImageButton = new Button("Save Image");
        saveImageButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                WritableImage wImage = new WritableImage(CANVAS_WIDTH, CANVAS_HEIGHT);
                canvas.snapshot(null, wImage);

                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialFileName(System.nanoTime()+".png");
                File imageFile = fileChooser.showSaveDialog(primaryStage);

                if(imageFile != null){
                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(wImage, null), "png", imageFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        flowPane.getChildren().add(numberOfObjectsPane);
        flowPane.getChildren().add(anglePane);
        flowPane.getChildren().add(shapePane);
        flowPane.getChildren().add(sizePane);
        flowPane.getChildren().add(spreadPane);
        flowPane.getChildren().add(coloringPane);
        flowPane.getChildren().add(saveImageButton);


        this.graphicsContext = this.canvas.getGraphicsContext2D();

        this.graphicsContext.setFill(Color.BLACK);
        this.graphicsContext.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        new Thread(){
            public void run() {

            }
        }.start();

        borderPane.setCenter(this.canvas);
        borderPane.setRight(flowPane);

        Group rootGroup = new Group();
        rootGroup.getChildren().add(borderPane);
        Scene rootScene = new Scene(rootGroup);
        primaryStage.setScene(rootScene);
        primaryStage.show();

        render();
    }

    public void render(){
        this.graphicsContext.setFill(Color.BLACK);
        this.graphicsContext.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        for(int i = 0; i < this.n; ++i) {
            double phi = i * this.angle;
            double radius = this.c * Math.sqrt(i);

            double x = radius * Math.cos(phi) + CANVAS_WIDTH / 2;
            double y = radius * Math.sin(phi) + CANVAS_HEIGHT / 2;

            switch (this.coloring){
                case RAINBOW:
                    this.graphicsContext.setFill(Color.hsb(phi * 180 / Math.PI - radius % 256, 1, 1));
                    break;
                case RANDOM:
                    Color randomColor = Color.rgb(this.random.nextInt(256), this.random.nextInt(256), this.random.nextInt(256));
                    this.graphicsContext.setFill(randomColor);
                    break;
                case WHITE:
                    this.graphicsContext.setFill(Color.WHITE);
                    break;
            }

            switch(this.shape){
                case CIRCLE:
                    this.graphicsContext.fillOval(x, y, this.size, this.size);
                    break;
                case SQUARE:
                    this.graphicsContext.fillRect(x, y, this.size, this.size);
                    break;
            }
        }
    }
}
