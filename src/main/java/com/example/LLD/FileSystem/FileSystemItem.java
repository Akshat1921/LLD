package com.example.LLD.FileSystem;

public interface FileSystemItem {
    public void ls(int indent);
    public void  openAll(int indent);
    public int getSize();
    public FileSystemItem cd(String name);
    public String getName();
    public boolean isFolder();
}
