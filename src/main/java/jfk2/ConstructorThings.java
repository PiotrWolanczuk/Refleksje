package jfk2;

import javassist.*;

import java.io.File;
import java.io.IOException;

public class ConstructorThings {
    private JarThings jarThings;
    public String write = "";
    public ConstructorThings(JarThings jarThings) {
        this.jarThings = jarThings;
    }

    public void addConstructor(File file, String className, String constructor) throws NotFoundException, CannotCompileException, IOException {
        ClassPool classPool = jarThings.getClassPool();
        String classPath = jarThings.getClassPathFromDirectory(file, className);
        CtClass ctClass = classPool.get(classPath);
        ctClass.defrost();

        CtConstructor ctConstructor = CtNewConstructor.make(constructor,ctClass);
        ctClass.addConstructor(ctConstructor);
        ctClass.writeFile("./newFolder/");
    }

    public String getConstructors(File file, String className) throws NotFoundException {
        ClassPool classPool = jarThings.getClassPool();
        String classPath = jarThings.getClassPathFromDirectory(file, className);
        CtClass ctClass = classPool.get(classPath);
        ctClass.defrost();

        CtConstructor[] ctConstructors = ctClass.getConstructors();

        write = write.concat("Class name: " + ctClass.getName() + "\n");
        for(CtConstructor ctConstructor : ctConstructors){
            write =  write.concat("Constructor: " + ctConstructor.getName() + "\n");
        }
        return  write;
    }

    public void updateConstructor(File file, String className, String constructorName,  String code) throws NotFoundException, CannotCompileException, IOException {
        ClassPool classPool = jarThings.getClassPool();
        String classPath = jarThings.getClassPathFromDirectory(file, className);
        CtClass ctClass = classPool.get(classPath);
        ctClass.defrost();

        CtConstructor ctConstructor = ctClass.getConstructor(constructorName);
        ctConstructor.setBody(code);
        ctClass.writeFile("./newFolder/");
    }

    public void removeConstructor(File file, String className, String constructor) throws NotFoundException, CannotCompileException, IOException {
        ClassPool classPool = jarThings.getClassPool();
        String classPath = jarThings.getClassPathFromDirectory(file, className);
        CtClass ctClass = classPool.get(classPath);
        ctClass.defrost();

        CtConstructor ctConstructor = ctClass.getConstructor(constructor);
        ctClass.removeConstructor(ctConstructor);
        ctClass.writeFile("./newFolder/");
    }
}
