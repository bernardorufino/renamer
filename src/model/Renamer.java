package model;

import utils.Filter;
import utils.Utils;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.PatternSyntaxException;

public class Renamer {
    private String path;
    private boolean directories;
    private File root;
    private List<File> files;
    private HashMap<File, Replacement> replacements;
    
    public static final class PathNotDirectoryException extends RuntimeException { /* Nothing */ }
     
    public static final class Replacement {
        public String name;
        public String originalName;
        public boolean renamed;
        public boolean matched;
        public boolean triedToRename;
    }
    
    public final Filter<File> fileFilter = new Filter<File>() {
        public boolean filter(File member) {
            return directories ? member.isDirectory() : member.isFile();
        }
    };
    
    public Renamer(String path) {
        setPath(path);
    }
    
    public void setPath(String path) {
        this.path = path;
        setDir();
    }

    public void renameDirectories(boolean directories) {
        this.directories = directories;
    }

    public void reload() {
        setDir();
    }

    private void setDir() {
        File dir = new File(path);
        if (!dir.isDirectory()) throw new PathNotDirectoryException();
        root = dir;
        files = Arrays.asList(root.listFiles());
        files = Utils.filter(files, fileFilter);
    }

    private boolean safeMatch(String target, String pattern) {
        try {
            return target.matches(pattern);
        } catch (PatternSyntaxException e) {
            return false;
        }
    }

    private String safeReplace(String target, String searchPattern, String replacement) {
        try {
            return target.replaceFirst(searchPattern, replacement);
        } catch (PatternSyntaxException | IndexOutOfBoundsException e) {
            return target;
        }
    }

    public void set(String pattern, String replacement) {
        replacements = new HashMap<>();
        for (File file : files) {
            String name = file.getName();
            Replacement result = new Replacement();
            result.originalName = name;
            result.matched = safeMatch(name, pattern);
            result.renamed = false;
            result.name = (result.matched) ? safeReplace(name, pattern, replacement) : name;
            result.triedToRename = false;
            replacements.put(file, result);
        }
    }
    
    public void apply() {
        for (File file : replacements.keySet()) {
            Replacement result = replacements.get(file);
            result.triedToRename = true;
            if (result.matched) {
                File destination = new File(file.getParent() + File.separator + result.name);
                result.renamed = file.renameTo(destination);
                result.name = file.getName();
            } else {
                result.renamed = false;
            }
        }
    }
    
    public HashMap<File, Replacement> getReplacements() {
        return replacements;
    }
    
}
