package com.example.myapplication.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DiskCache {

    private final File parent;

    public DiskCache(File parent) {
        this.parent = parent;
        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                throw new RuntimeException("makdir failed" + parent);
            }
        }

        File stubFile = new File(parent, "README");
        if (!stubFile.exists()) {
            try {
                stubFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("dir can't create" + stubFile);
            }
        }
    }


    public OutputStream createFileOutputStream(String fileName) throws IOException {
        return new BufferedOutputStream(new FileOutputStream(new File(parent, fileName)), 1024 * 16);
    }

    public File getFile(String fileName) {
        return new File(parent, fileName);
    }
}
