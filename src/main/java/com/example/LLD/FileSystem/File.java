package com.example.LLD.FileSystem;

public class File implements FileSystemItem{
    private String name;
    private int size;

    public File(String name, int size){
        this.name = name;
        this.size = size;
    }

    @Override
    public void ls(int indent) {
        String indentSpaces = new String(new char[indent]).replace('\0', ' ');
        System.out.println(indentSpaces + this.name);
    }

    @Override
    public void openAll(int indent) {
        String indentSpaces = new String(new char[indent]).replace('\0', ' ');
        System.out.println(indentSpaces + this.name);
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public FileSystemItem cd(String name) {
        System.out.println("No folder is present at this level");
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isFolder() {
        return false;
    }
    
}
