package jfk2;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClassThings {
    private JarThings jarThings;

    public String write = "";
    public ClassThings(JarThings jarThings){
        this.jarThings = jarThings;
    }

    public void addClassToJar(String className, String classPath, String afterClassName, File file) throws CannotCompileException, IOException, ClassNotFoundException, NotFoundException {
        ClassPool classPool = jarThings.getClassPool();
        //CtClass ctClass = classPool.makeClass(className);

        CtClass superClass = getSuperClass(className + " " +afterClassName,file,  classPool);
        List<CtClass> interfaces = getInterfaces(className + " " +afterClassName, classPool);

        //String newClassName = className.split("implements")[0].split(" extends ")[0];
        CtClass ctClass = classPool.makeClass(className);

        if (superClass != null) {
            ctClass.setSuperclass(superClass);
        }
        if(!interfaces.isEmpty()) {
            ctClass.setInterfaces(interfaces.toArray(new CtClass[interfaces.size()]));
        }

        ctClass.writeFile("./newFolder/" + classPath);
        //allClasses.add(Class.forName(className));
    }

    private CtClass getSuperClass(String className, File file, ClassPool classPool) throws NotFoundException {
        CtClass extendClass = null;
        System.out.println(className);
        if(className.contains("extends")){
            String superClass = className.substring(className.indexOf("extends ") + 8);
            System.out.println(superClass);
            if(superClass.contains("implements"))
                superClass = superClass.substring(0, superClass.indexOf("implements"));
            extendClass = classPool.get(superClass);
        }
        return extendClass;
    }

    private List<CtClass> getInterfaces(String className, ClassPool classPool) throws NotFoundException {
        List<CtClass> interfaces = new ArrayList<>();
        if(className.contains("implements")){
            String aInterfaces = className.substring(className.indexOf("implements") + 11);
            if(aInterfaces.contains("extends"))
                aInterfaces = aInterfaces.substring(0, aInterfaces.indexOf("extends"));
            aInterfaces = aInterfaces.replace(" ", "");
            while(aInterfaces.contains(",")){
                String aInterfaceName = aInterfaces.substring(0, aInterfaces.indexOf(","));
                aInterfaces = aInterfaces.replace(aInterfaceName + ",", "");
                interfaces.add(classPool.get(aInterfaceName));
            }
            interfaces.add(classPool.get(aInterfaces));
        }
        return interfaces;
    }

    public void removeClass(File file, String className) throws NotFoundException {
        ClassPool classPool = jarThings.getClassPool();
        String classPath = jarThings.getClassPathFromDirectory(file, className);
        CtClass ctClass = classPool.get(classPath);
        ctClass.defrost();
        ctClass.detach();
        deleteFile(file,className);
    }

    private void deleteFile(File file, String className) {
        for (final File fileEntry : file.listFiles()) {
            if(fileEntry.isDirectory()){
                deleteFile(fileEntry,className);
            }else{
                if (fileEntry.getName().endsWith("class")) {
                    String name = jarThings.fromFileToClassName(fileEntry.getName());
                    if (name.equals(className)) {
                        className += ".class";
                        String path = file.getAbsolutePath() +File.separator + className;
                        File removeFile = new File(path);
                        removeFile.delete();
                    }
                }
            }
        }
    }


    public String getClassesFromDirectory(final File file) {
        ClassPool classPool = jarThings.getClassPool();
        try {
            for (final File fileEntry : file.listFiles()) {
                if(fileEntry.isDirectory()){
                    getClassesFromDirectory(fileEntry);
                }else{
                    //System.out.println(fileEntry.getName());
                    if (fileEntry.getName().endsWith("class")) {
                        String name = fileEntry.getAbsolutePath();
                        int index = name.lastIndexOf(".\\newFolder");
                        //System.out.println(fileEntry.getAbsolutePath());
                        String packageName = jarThings.fromFileToClassName(fileEntry.getAbsolutePath().substring(index + 12, fileEntry.getAbsolutePath().length()));
                        write = write.concat(packageName +"\n");
                        String className = jarThings.fromFileToClassName(fileEntry.getName());
                        String classPath = jarThings.getClassPathFromDirectory(file, className);
                        CtClass ctClass = classPool.get(classPath);
                        if(ctClass.isInterface())
                            write = write.concat("Interface"+"\n");
                        write = write.concat("Class name: " +className+"\n");
                        write = write.concat("Super class: " + ctClass.getSuperclass().getName()+"\n");

                        CtClass[] ctClasses = ctClass.getInterfaces();

                        write = write.concat("Interfaces:"+"\n");
                        for(CtClass ctClass1 : ctClasses){
                            write = write.concat(ctClass1.getName()+"\n");
                        }
                        write = write.concat("\n");
                        //("Class name: " + className +"\n");
                        //System.out.println(write);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read classes from jar file: " + file, e);
        }
        return write;
    }
    public void addInterface(String className) throws CannotCompileException, IOException {
        ClassPool pool = jarThings.getClassPool();
        CtClass ctClass = pool.makeInterface(className);
        ctClass.writeFile("./newFolder/");
    }
}
