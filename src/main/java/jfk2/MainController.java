package jfk2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javassist.NotFoundException;

import java.io.File;
import java.io.IOException;

public class MainController {

    private static JarThings jarThings = new JarThings();
    private static ClassThings classThings = new ClassThings(jarThings);
    private static MethodThings methodThings = new MethodThings(jarThings);
    private static FieldThings fieldThings = new FieldThings(jarThings);
    private static ConstructorThings constructorThings = new ConstructorThings(jarThings);
    private static PackageThings packageThings = new PackageThings(jarThings);

    ObservableList<String> createChoiceList = FXCollections
            .observableArrayList("Package", "Class", "Field", "Method", "Constructor", "Interface");
    ObservableList<String> readChoiceList = FXCollections
            .observableArrayList("Package", "Class", "Field", "Method", "Constructor", "Interface");
    ObservableList<String> updateChoiceList = FXCollections
            .observableArrayList("Before method","After method", "Constructor", "Method body");
    ObservableList<String> deleteChoiceList = FXCollections
            .observableArrayList("Package", "Class", "Field", "Method", "Contructor", "Interface");

    File file = new File("./newFolder");

    @FXML
    private TextArea writeText;
    @FXML
    private ChoiceBox createChoice;
    @FXML
    private ChoiceBox readChoice;
    @FXML
    private ChoiceBox updateChoice;
    @FXML
    private ChoiceBox deleteChoice;
    @FXML
    private TextField pacClasName;
    @FXML
    private TextField methodName;
    @FXML
    private TextField jarPath;
    @FXML
    private Label text;
    @FXML
    private TextField classPath;
    @FXML
    private TextArea errorText;
    @FXML
    private TextArea hierarchy;

    @FXML
    public void initialize() {
        createChoice.setItems(createChoiceList);
        readChoice.setItems(readChoiceList);
        updateChoice.setItems(updateChoiceList);
        deleteChoice.setItems(deleteChoiceList);
        //jarPath.setText("C:\\Users\\Piotr\\Documents\\Programy\\JFK\\Invaders.jar");
    }

    @FXML
    public void openJar(){
        try {
            jarThings.getJarFile(jarPath.getText());
            hierarchy.setText(jarThings.hierarchy(file, -1));
            text.setText("Open Done");
        } catch (IOException e) {
            errorText.setText(errorText.getText() + "\n" + e.toString());
            e.printStackTrace();
        }
        //jarThings.getJarFile("C:\\Users\\Piotr\\Documents\\Programy\\JFK\\Invaders.jar");
    }
    @FXML
    public void close(){
        //a.setLabel("asd");
        //System.out.println("close");
        try {
            jarThings.exportJar();
        } catch (IOException e) {
            errorText.setText(errorText.getText() + "\n" + e.toString());
        }
    }
    @FXML
    public void create(){
        text.setText("");
        //writeText.clear();
        switch (createChoice.getSelectionModel().getSelectedIndex()){
            case 0:{
                packageThings.addPackage(pacClasName.getText());
                text.setText("");
                text.setText("Create Done");
            }break;
            case 1:{
                try {
                    if(classPath.getText().equals("")){
                        methodName.setText(pacClasName.getText() + " "+writeText.getText());
                        classThings.addClassToJar(pacClasName.getText(),  "", writeText.getText(), file );
                    }
                    else
                        classThings.addClassToJar(pacClasName.getText(), classPath.getText(), writeText.getText(),file );
                    text.setText("");
                    text.setText("Create Done");
                } catch (Exception e) {
                    errorText.setText(errorText.getText() + "\n" + e.toString());
                    e.printStackTrace();
                }

            }break;
            case 2:{
                try {
                    fieldThings.addField(file, pacClasName.getText(), writeText.getText());
                    text.setText("");
                    text.setText("Create Done");
                } catch (Exception e) {
                    e.printStackTrace();
                    errorText.setText(errorText.getText() + "\n" + e.toString());
                }
            }break;
            case 3:{
                try {
                    methodThings.addMethod(file, pacClasName.getText(), writeText.getText());
                    text.setText("");
                    text.setText("Create Done");
                } catch (Exception e) {
                    e.printStackTrace();
                    errorText.setText(errorText.getText() + "\n" + e.toString());
                }
            }break;
            case 4:{
                try {
                    constructorThings.addConstructor(file, pacClasName.getText(), writeText.getText());
                    text.setText("");
                    text.setText("Create Done");
                } catch (Exception e) {
                    e.printStackTrace();
                    errorText.setText(errorText.getText() + "\n" + e.toString());
                }
            }break;
            case 5:{
                try {
                    classThings.addInterface(pacClasName.getText());
                    text.setText("");
                    text.setText("Create Done");
                } catch (Exception e) {
                    e.printStackTrace();
                    errorText.setText(errorText.getText() + "\n" + e.toString());
                }
            }break;
        }
    }
    @FXML
    public void read(){
        text.setText("");
        switch (readChoice.getSelectionModel().getSelectedIndex()){
            case 0:{
                writeText.setText(packageThings.getPackages());
                packageThings.write = "";
            }break;
            case 1:
            case 5:{
                writeText.setText(classThings.getClassesFromDirectory(file));
                hierarchy.setText(jarThings.hierarchy(file, -1));
                //writeText.setText(jarThings.getClassesFromDirectory(file));
                classThings.write = "";
            }break;
            case 2:{
                try {
                    writeText.setText(fieldThings.getFields(file, pacClasName.getText()));
                } catch (NotFoundException e) {
                    //writeText.setText(e.getStackTrace().toString());
                    errorText.setText(errorText.getText() + "\n" + e.toString());
                    e.printStackTrace();
                }
                methodThings.classPath = "";
                fieldThings.write = "";
            }break;
            case 3:{
                try {
                    methodThings.classPath = "";
                    writeText.setText(methodThings.getMethods(file, pacClasName.getText()));

                } catch (NotFoundException e) {
                    errorText.setText(errorText.getText() + "\n" + e.toString());
                    e.printStackTrace();
                }
                methodThings.classPath = "";
                methodThings.write = "";
            }break;
            case 4:{
                try {
                    writeText.setText(constructorThings.getConstructors(file, pacClasName.getText()));
                } catch (NotFoundException e) {
                    errorText.setText(errorText.getText() + "\n" + e.toString());
                    e.printStackTrace();
                }
                constructorThings.write = "";
            }break;
        }
        text.setText("");
    }
    @FXML
    public void update(){
        text.setText("");
        switch (updateChoice.getSelectionModel().getSelectedIndex()){
            case 0:{
                try {
                    methodThings.writeBefore(file, pacClasName.getText(), methodName.getText(), writeText.getText());
                    text.setText("");
                    text.setText("Update Done");
                } catch (Exception e) {
                    errorText.setText(errorText.getText() + "\n" + e.toString());
                    e.printStackTrace();
                }
            }break;
            case 1:{
                try {
                    methodThings.writeAfter(file, pacClasName.getText(), methodName.getText(), writeText.getText());
                    text.setText("");
                    text.setText("Update Done");
                } catch (Exception e) {
                    errorText.setText(errorText.getText() + "\n" + e.toString());
                    e.printStackTrace();
                }
            }break;
            case 2:{
                try {
                    constructorThings.updateConstructor(file, pacClasName.getText(), pacClasName.getText(), writeText.getText());
                    text.setText("");
                    text.setText("Update Done");
                } catch (Exception e) {
                    errorText.setText(errorText.getText() + "\n" + e.toString());
                    e.printStackTrace();
                }
            }break;
            case 3:{
                try {
                    methodThings.updateMethod(file, pacClasName.getText(), methodName.getText(), writeText.getText());
                    text.setText("");
                    text.setText("Update Done");
                } catch (Exception e) {
                    errorText.setText(errorText.getText() + "\n" + e.toString());
                    e.printStackTrace();
                }
            }break;
        }
    }
    @FXML
    public void delete(){
        text.setText("");
        switch (deleteChoice.getSelectionModel().getSelectedIndex()){
            case 0:{
                packageThings.removePackage(pacClasName.getText());
                text.setText("");
                text.setText("Delete Done");
            }break;
            case 1:
            case 5:{
                try {
                    classThings.removeClass(file, pacClasName.getText());
                    text.setText("");
                    text.setText("Delete Done");
                } catch (NotFoundException e) {
                    errorText.setText(errorText.getText() + "\n" + e.toString());
                    e.printStackTrace();
                }
            }break;
            case 2:{
                try {
                    fieldThings.removeField(file, pacClasName.getText(), methodName.getText());
                    text.setText("");
                    text.setText("Delete Done");
                } catch (Exception e) {
                    errorText.setText(errorText.getText() + "\n" + e.toString());
                    e.printStackTrace();
                }
            }break;
            case 3:{
                try {
                    methodThings.removeMethod(file, pacClasName.getText(), writeText.getText());
                    text.setText("");
                    text.setText("Delete Done");
                } catch (Exception e) {
                    errorText.setText(errorText.getText() + "\n" + e.toString());
                    e.printStackTrace();
                }
            }break;
            case 4:{
                try {
                    constructorThings.removeConstructor(file,  pacClasName.getText(),  pacClasName.getText());
                    text.setText("");
                    text.setText("Delete Done");
                } catch (Exception e) {
                    errorText.setText(errorText.getText() + "\n" + e.toString());
                    e.printStackTrace();
                }
            }break;
        }
    }

    public void clear() {
        writeText.clear();
        text.setText("");
    }
}
