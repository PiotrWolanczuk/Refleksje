package jfk2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PackageThings {

    JarThings jarThings;
    public String write = "";

    public PackageThings(JarThings jarThings) {
        this.jarThings = jarThings;
    }

    public void addPackage(String packageName){
        File file;
        String path="";
        while(packageName.contains(".")){
            path = path + File.separator + packageName.substring(0, packageName.indexOf("."));
            file = new File("./newFolder" +path);//
            file.mkdir();

            packageName = packageName.substring(packageName.indexOf(".")+1, packageName.length());
            System.out.println(path);
        }
        if(!packageName.contains(".")){
            file = new File("./newFolder"+path + File.separator + packageName);
            file.mkdir();
        }
    }

    public String getPackages(){
        File file =  new File("./newFolder");
        return findClass(file);
    }

    private String findClass(File file){
        List<String> packages = new ArrayList<String>();
        for (final File fileEntry : file.listFiles()) {
            if(fileEntry.isDirectory()){
                findClass(fileEntry);
            }else{
                if (fileEntry.getName().endsWith("class")) {
                    if(changeName(fileEntry).length() > 0 && check(changeName(fileEntry),packages))
                        packages.add(changeName(fileEntry));
                }
            }
        }
        return writePackages(packages);
    }

    private String changeName(File fileEntry) {
        String packageName = fileEntry.getPath();
        packageName = packageName.replace(".\\newFolder\\", "");
        packageName = packageName.substring(0, packageName.length() - fileEntry.getName().length()).replaceAll("/|\\\\", "\\.");
        if(packageName.length() > 0)
            packageName = packageName.substring(0, packageName.length()- 1);
        return packageName;
    }

    private String writePackages(List<String> packages) {
        for(String onePackage : packages)
            write = write.concat(onePackage +"\n");
        return write;
    }

    private boolean check(String packageName, List<String> packages) {
        for(String onePackage : packages){
            if(onePackage.equals(packageName))
                return false;
        }
        return true;
    }

    public void removePackage(String packageName){
        File file = new File("./newFolder/"+packageName);
        String[] entries = file.list();
        if(entries.length > 0){
            for(String s: entries){
                File currentFile = new File(file.getPath(),s);
                currentFile.delete();
            }
        }
        file.delete();
    }


}
