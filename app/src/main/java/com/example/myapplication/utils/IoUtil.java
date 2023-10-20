package com.example.myapplication.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class IoUtil {

    public static long copy(InputStream in, OutputStream out, MessageDigest md) throws IOException {
        if (!(out instanceof BufferedOutputStream)) {
            out = new BufferedOutputStream(out, 1024 * 16);
        }
        long total = 0;
        byte[] buffer = new byte[4096];

        int count;
        while ((count = in.read(buffer)) != -1) {
            if (md != null) {
                md.update(buffer,0,count);
            }
            out.write(buffer, 0, count);
            total += count;
        }

        out.flush();

        return total;
    }
    public  static  MessageDigest newMd5Digest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw  new RuntimeException(" should never  happens", e);
        }
    }

    public static String md5(File  file) {
        return  null;
    }

    private IoUtil() {
        throw new UnsupportedOperationException();
    }
}
