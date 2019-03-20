package jfk2;

import javassist.ClassPool;
import javassist.NotFoundException;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.*;

public class JarThings {

    private ClassPool classPool;
    private JarInputStream jarInputStream;
    private Manifest manifest;
    private String jarPath;
    private JarOutputStream jarOutputStream;
    public String classPath;

    private void unZipJar(String jarPath, String newJarPath) throws IOException {
        File fileFirst = new File(jarPath);
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(fileFirst);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Enumeration<JarEntry> enumeration = jarFile.entries();

        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = enumeration.nextElement();

            String name = newJarPath + File.separator + jarEntry.getName();
            File file = new File(name);

            if (name.endsWith("/"))
                file.mkdirs();
        }
        enumeration = jarFile.entries();
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = enumeration.nextElement();

            String name = newJarPath + File.separator + jarEntry.getName();
            File file = new File(name);

            if (!name.endsWith("/")) {
                InputStream inputStream = null;
                try {
                    inputStream = jarFile.getInputStream(jarEntry);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                while (inputStream.available() > 0) {
                    fileOutputStream.write(inputStream.read());
                }
                fileOutputStream.close();
                inputStream.close();
            }
        }
        jarFile.close();
    }

    public String hierarchyList = "";
    private int howMany = -1;
    private String tab = "  ";
    private String multiplyTab(int index){
        tab = " ";
        for(int i=0;i<index;i++){
            tab = tab.concat(tab);
        }
        return tab;
    }
    public String hierarchy(File file, int howMany){
        try {
            for (final File fileEntry : file.listFiles()) {
                if (fileEntry.isDirectory()) {
//                    howMany++;
                    hierarchyList = hierarchyList.concat(multiplyTab(++howMany) + fileEntry.getName()+"\n");
//                    System.out.println("    " + fileEntry.getName()+"\n");
                    hierarchy(fileEntry, howMany--);
                } else {
                    hierarchyList = hierarchyList.concat(multiplyTab(howMany+1) + fileEntry.getName() +"\n");
//                    System.out.println(fileEntry.getName()+"\n");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read classes from jar file: " + file, e);
        }
        return hierarchyList;
    }
    public void getJarFile(String jarPath) throws IOException {
        ArrayList<JarEntry> jarEntries = new ArrayList();
        if (jarPath.endsWith(".jar")) {
            this.jarPath = jarPath;
            try {
                unZipJar(this.jarPath, "./newFolder/");
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (jarInputStream != null) {
                    jarInputStream.close();
                }
            } catch (IOException e) {
                System.err.println(e.toString());
            }

            File jarFile = new File(jarPath);
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(jarFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                jarInputStream = new JarInputStream(fileInputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.manifest = jarInputStream.getManifest();
            JarEntry jarEntry;
            while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                jarEntries.add(jarEntry);
            }
            try {
                classPool = new ClassPool();
                classPool.appendClassPath("./newFolder/");
                classPool.appendSystemPath();
            } catch (NotFoundException e) {
                e.printStackTrace();
            } finally {
                jarInputStream.close();
            }
        }
    }


        public ClassPool getClassPool () {
            return classPool;
        }

        public void exportJar () throws IOException {
            if (this.jarPath != null && this.jarPath.endsWith(".jar")) {

                if (this.manifest == null) {
                    this.manifest = new Manifest();
                    this.manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
                }

                String output = this.jarPath.replace(".jar", "01.jar");
                this.jarOutputStream = new JarOutputStream(new FileOutputStream(output), this.manifest);
                File myApp = new File("newFolder");
                addToJar(myApp, this.jarOutputStream);
                this.jarOutputStream.close();
                myApp.deleteOnExit();
            }
            File file = new File("newFolder");
            deleteDirectory(file);
        }

        private void addToJar (File source, JarOutputStream target) throws IOException {
            BufferedInputStream in = null;

            try {
                if (source.isDirectory()) {
                    String name = source.getPath().replace("\\", "/");
                    if (!name.isEmpty()) {
                        if (!name.endsWith("/")) name += "/";
                        if (!name.equals("newFolder/")) {
                            name = name.replace("newFolder/", "");
                            JarEntry entry = new JarEntry(name);
                            entry.setTime(source.lastModified());
                            target.putNextEntry(entry);
                            target.closeEntry();
                        }
                    }
                    for (File nestedFile : source.listFiles()) {
                        if (!nestedFile.getName().equals("META-INF"))
                            addToJar(nestedFile, target);
                    }
                    return;
                }

                JarEntry entry = new JarEntry(source.getPath().replace("\\", "/").replace("newFolder/", ""));
                entry.setTime(source.lastModified());
                target.putNextEntry(entry);


                in = new BufferedInputStream(new FileInputStream(source));

                byte[] buffer = new byte[1024];
                while (true) {
                    int count = in.read(buffer);
                    if (count == -1)
                        break;
                    target.write(buffer, 0, count);
                }
                target.closeEntry();
            } finally {
                if (in != null)
                    in.close();
            }
        }


        String fromFileToClassName ( final String fileName){
            return fileName.substring(0, fileName.length() - 6).replaceAll("/|\\\\", "\\.");
        }

        public String getClassPathFromDirectory ( final File file, String className){
            try {
                for (final File fileEntry : file.listFiles()) {
                    if (fileEntry.isDirectory()) {
                        getClassPathFromDirectory(fileEntry, className);
                    } else {
                        if (fileEntry.getName().endsWith("class")) {
                            //System.out.println(fileEntry.getName());
                            String classNameCheckWith = fileEntry.getName().substring(0, fileEntry.getName().length() - 6);
                            //System.out.println(classNameCheckWith);
                            if (classNameCheckWith.equals(className)) {
                                classPath = fileEntry.getPath();
                                classPath = classPath.replaceFirst(".\\\\newFolder", "");
                                classPath = classPath.replaceAll("\\\\", ".").replace(".class", "");
                                classPath = classPath.substring(1, classPath.length());
                                //System.out.println(classPath);
                                return classPath;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to read classes from jar file: " + file, e);
            }
            return classPath;
        }

        private void deleteDirectory (File file){
            for (final File fileEntry : file.listFiles()) {
                if (fileEntry.isDirectory()) {
                    deleteDirectory(fileEntry);
                } else {
                    fileEntry.getAbsoluteFile().delete();
                }
            }
            file.getAbsoluteFile().delete();
        }
    }
