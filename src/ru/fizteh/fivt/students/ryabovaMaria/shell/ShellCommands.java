package ru.fizteh.fivt.students.ryabovaMaria.shell;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ShellCommands extends AbstractCommands {
    private void parse() throws Exception {
        String[] tempLexems = new String[0];
        if (lexems.length > 1) {
            tempLexems = lexems[1].split("[ \t\n\r]+");
        }
        lexems = tempLexems;
    }
    
    public void cd() throws Exception {
        parse();
        if (lexems.length == 0) {
            throw new Exception("cd: there is no arguments.");
        }
        if (lexems.length > 1) {
            throw new Exception("cd: there is so many arguments.");
        }
        File newDir = new File(lexems[0]);
        Path temp = currentDir.toPath().resolve(newDir.toPath()).normalize();
        if (temp.toFile().isDirectory()) {
            currentDir = temp.toFile();
        } else {
            throw new Exception("cd: the argument isn't a directory.");
        }
    }
        
    public void mkdir() throws Exception {
        parse();
        if (lexems.length == 0) {
            throw new Exception("mkdir: there id no arguments.");
        }
        if (lexems.length > 1) {
            throw new Exception("mkdir: there is so many arguments.");
        }
        File dirName = new File(lexems[0]);
        Path temp = currentDir.toPath().resolve(dirName.toPath()).normalize();
        if (temp.toFile().exists()) {
            throw new Exception("mkdir: this directory is already exists.");
        } else {
            if (!temp.toFile().mkdir()) {
                throw new Exception("mkdir: I can't make this directory.");
            }
        }
    }
        
    public void pwd() throws Exception {
        parse();
        if (lexems.length > 0) {
            throw new Exception("pwd: there is no arguments.");
        }
        System.out.println(currentDir);
    }
        
    public void rm() throws Exception {
        parse();
        if (lexems.length == 0) {
            throw new Exception("rm: there is no arguments.");
        }
        if (lexems.length > 1) {
            throw new Exception("rm: there is so many arguments.");
        }
        File dirName = new File(lexems[0]);
        Path temp = currentDir.toPath().resolve(dirName.toPath()).normalize();
        if (currentDir.toPath().startsWith(temp)) {
            throw new Exception("rm: I can't delete this.");
        } else {
            try {
                DeleteDir.delete(temp);
            } catch (Exception e) {
                throw new Exception("rm: I can't delete this.");
            }
        }
    }
    
    private void myCopy(boolean needDelete, 
                              Path sourcePath, 
                              Path destPath) throws Exception {
        if (sourcePath.toFile().isDirectory() 
            && destPath.toFile().isFile()) {
            if (needDelete) {
                throw new Exception("rm: I can't remove dir into file.");
            } else {
                throw new Exception("cp: I can't copy dir into file.");
            }
        }
        if (destPath.toFile().isDirectory()) {
            destPath = destPath.resolve(sourcePath.getFileName());
        }
        if (needDelete) {
            Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
        } else {
            Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
        }
        if (sourcePath.toFile().isDirectory()) {
            String[] dirList = sourcePath.toFile().list();
            for (int i = 0; i < dirList.length; ++i) {
                Path newSourcePath = sourcePath.resolve(dirList[i]).normalize();
                myCopy(needDelete, newSourcePath, destPath);
            }
        }
    }
        
    public void cp() throws Exception {
        parse();
        if (lexems.length < 2) {
            throw new Exception("cp: there is no enough arguments.");
        }
        if (lexems.length > 2) {
            throw new Exception("cp: there is so many arguments.");
        }
        Path currentPath = currentDir.toPath();
        File sourceName = new File(lexems[0]);
        Path sourcePath = sourceName.toPath();
        sourcePath = currentPath.resolve(sourcePath).normalize();
        File destName = new File(lexems[1]);
        Path destPath = currentPath.resolve(destName.toPath()).normalize();
        if (destPath.startsWith(sourcePath)) {
            throw new Exception("cp: there is a cyclic copying.");
        } else {
            try {
                myCopy(false, sourcePath, destPath);
            } catch (Exception e) {
                throw new Exception("cp: I can't copy this.");
            }
        }
    }
        
    public void mv() throws Exception {
        parse();
        if (lexems.length < 2) {
            throw new Exception("mv: there is no enough arguments.");
        }
        if (lexems.length > 2) {
            throw new Exception("mv: there is so many arguments.");
        }
        Path currentPath = currentDir.toPath();
        File sourceName = new File(lexems[0]);
        Path sourcePath = sourceName.toPath();
        sourcePath = currentPath.resolve(sourcePath).normalize();
        File destName = new File(lexems[1]);
        Path destPath = currentPath.resolve(destName.toPath()).normalize();
        if (currentDir.toPath().startsWith(sourcePath)) {
            throw new Exception("mv: I can't remove this.");
        } else {
            if (destPath.startsWith(sourcePath)) {
                throw new Exception("mv: there is a cyclic removeing.");
            } else {
                myCopy(true, sourcePath, destPath);
            }
        }
    }
        
    public void dir() throws Exception {
        parse();
        if (lexems.length > 0) {
            throw new Exception("dir: there is some arguments.");
        }
        String[] dirList = currentDir.list();
        for (int i = 0; i < dirList.length; ++i) {
            System.out.println(dirList[i]);
        }
    }
        
    public void exit() {
        System.exit(0);
    }
}
