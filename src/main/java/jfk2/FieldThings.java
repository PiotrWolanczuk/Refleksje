package jfk2;

import javassist.*;

import java.io.File;
import java.io.IOException;

/*
    package = 0
    public = 1
    private nie ma
    protected = 4
    static = 8
    final = 16
    synchronized = 32
    volatile = 64
    = 128
    = 256
    = 512
    abstract = 1024
    strictfp = 2048
     */
public class FieldThings {
    private JarThings jarThings;
    public String write = "";

    public FieldThings(JarThings jarThings) {
        this.jarThings = jarThings;
    }

    public void addField(File file, String className, String field) throws NotFoundException, CannotCompileException, IOException {
        ClassPool classPool = jarThings.getClassPool();
        String classPath = jarThings.getClassPathFromDirectory(file, className);
        CtClass ctClass = classPool.get(classPath);
        ctClass.defrost();
        CtField ctField = CtField.make(field, ctClass);

        ctClass.addField(ctField);
        ctClass.writeFile("./newFolder");
    }

    public String getFields(File file, String className) throws NotFoundException {
        ClassPool classPool = jarThings.getClassPool();
        jarThings.classPath = null;
        String classPath = jarThings.getClassPathFromDirectory(file, className);
        CtClass ctClass =  classPool.get(classPath);
        ctClass.defrost();

        CtField[] fields = ctClass.getDeclaredFields();
        for(CtField field : fields){
            write = write.concat(Modifier.toString(field.getModifiers())+" ");
            String type = field.getType().getName();
            int index =  type.lastIndexOf(".");
            if(index != -1)
                type = type.substring(index + 1, type.length());
            write = write.concat(type+" ");
            write = write.concat(field.getName()+"\n");
        }
        return write;
    }

    public void removeField(File file, String className, String fieldName) throws NotFoundException, CannotCompileException, IOException {
        ClassPool classPool = jarThings.getClassPool();
        String classPath = jarThings.getClassPathFromDirectory(file, className);
        CtClass ctClass = classPool.get(classPath);
        ctClass.defrost();
        CtField field = ctClass.getField(fieldName);
        ctClass.removeField(field);
        ctClass.writeFile("./newFolder");
    }

}
