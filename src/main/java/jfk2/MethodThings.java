package jfk2;

import javassist.*;

import java.io.File;
import java.io.IOException;

public class MethodThings {
    private JarThings jarThings;
    public String write = "";
    public String classPath = "";

    public MethodThings(JarThings jarThings) {
        this.jarThings = jarThings;
    }

    public void addMethod(File file, String className, String methodBody) throws NotFoundException, CannotCompileException, IOException {
        ClassPool classPool = jarThings.getClassPool();
        String classPath = jarThings.getClassPathFromDirectory(file, className);
        CtClass ctClass = classPool.get(classPath);
        ctClass.defrost();
        CtMethod ctMethod = CtMethod.make(methodBody, ctClass);

        ctClass.addMethod(ctMethod);
        ctClass.writeFile("./newFolder/");
    }

    public String getMethods(File file, String className) throws NotFoundException {
        //System.out.println(className);
        ClassPool classPool = jarThings.getClassPool();
        jarThings.classPath = null;
        String classPath = jarThings.getClassPathFromDirectory(file, className);
//        System.out.println(classPath);
        CtClass ctClass = classPool.get(classPath);
        ctClass.defrost();

        CtMethod[] methods = ctClass.getDeclaredMethods();

        for(CtMethod method : methods){
            write = write.concat(Modifier.toString(method.getModifiers()) + " ");
            String type = method.getReturnType().getName();
            int index1 = type.lastIndexOf("[");
            int index2 = type.lastIndexOf("]");
            if(index1 != -1 && index2 != -1)
            type = type.substring(index1, index2);
            write = write.concat(type + " ");
            write = write.concat(method.getName() +"\n");
        }
        return write;
    }



    public void writeAfter(File file, String className, String methodName, String body) throws NotFoundException, CannotCompileException, IOException {
        ClassPool classPool = jarThings.getClassPool();
        String classPath = jarThings.getClassPathFromDirectory(file, className);
        CtClass ctClass = classPool.get(classPath);
        ctClass.defrost();

        CtMethod ctMethod = findMethod(methodName,classPath, ctClass);
        ctMethod.insertAfter(body);
        ctClass.writeFile("./newFolder/");
    }

    public void writeBefore(File file, String className, String methodName, String body) throws NotFoundException, CannotCompileException, IOException {
        ClassPool classPool = jarThings.getClassPool();
        String classPath = jarThings.getClassPathFromDirectory(file, className);
        CtClass ctClass = classPool.get(classPath);
        ctClass.defrost();

        CtMethod ctMethod = findMethod(methodName,classPath, ctClass);
        ctMethod.insertBefore(body);
        ctClass.writeFile("./newFolder/");
    }

    public void updateMethod(File file, String className, String methodName, String body) throws NotFoundException, CannotCompileException, IOException {
        ClassPool classPool = jarThings.getClassPool();
        String classPath = jarThings.getClassPathFromDirectory(file, className);
        CtClass ctClass = classPool.get(classPath);
        ctClass.defrost();

        CtMethod ctMethod = findMethod(methodName,classPath, ctClass);
        ctMethod.setBody(body);
        ctClass.writeFile("./newFolder/");
    }
    public void removeMethod(File file, String className, String methodName) throws NotFoundException, CannotCompileException, IOException {
        ClassPool classPool = jarThings.getClassPool();
        String classPath = jarThings.getClassPathFromDirectory(file, className);
        CtClass ctClass = classPool.get(classPath);
        ctClass.defrost();

        CtMethod ctMethod = findMethod(methodName,classPath, ctClass);
        ctClass.removeMethod(ctMethod);
        ctClass.writeFile("./newFolder/");
    }

    private CtMethod findMethod(String methodName, String classPath, CtClass ctClass){
        CtMethod[] ctMethods = ctClass.getDeclaredMethods();
        CtMethod method = null;
        for(CtMethod ctMethod : ctMethods){
            String methodLongName = ctMethod.getLongName().replace(classPath + ".", "");
            methodLongName = methodLongName.replaceFirst("\\(.+", "");
            if(methodName.equals(methodLongName)){
                method = ctMethod;
                break;
            }
        }
        return method;
    }
}
