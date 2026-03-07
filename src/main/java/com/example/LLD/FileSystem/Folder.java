package com.example.LLD.FileSystem;

import java.util.ArrayList;
import java.util.List;

public class Folder implements FileSystemItem{
    private String name;
    private List<FileSystemItem> childrens;

    public Folder(String name){
        this.name = name;
        childrens = new ArrayList<>();
    }

    public void add(FileSystemItem item){
        childrens.add(item);
    }

    @Override
    public void ls(int indent){
        String indentSpaces = new String(new char[indent]).replace('\0', ' ');
        for(FileSystemItem item: childrens){
            if(item.isFolder()){
                System.out.println(indentSpaces + "+ " + item.getName());
            }else{
                System.out.println(indentSpaces + item.getName());
            }
        }
    }

    @Override
    public void openAll(int indent){
        String indentSpaces = new String(new char[indent]).replace('\0', ' ');
        System.out.println(indentSpaces + "+ " + this.name);
        for(FileSystemItem item: childrens){
            item.openAll(indent + 4);
        }
    }

    @Override
    public int getSize(){
        return childrens.stream().mapToInt(item->item.getSize()).sum();
    }

    @Override
    public FileSystemItem cd(String target){
        for(FileSystemItem item: childrens){
            if(item.getName().equals(target)){
                return item;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isFolder() {
        return true;
    }

}
