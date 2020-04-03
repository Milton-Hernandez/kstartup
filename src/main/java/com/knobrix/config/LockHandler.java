package com.knobrix.config;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

public class LockHandler {

    private FileLock lock;
    private String FolderName;
    private int Instance = 1;

    private final int MAX_INSTANCES = 1024;

    public int getInstance() {
        return Instance;
    }

    public void release() {
        try {
            lock.release();
        }
        catch(IOException ex) { }
    }
    private void initLockFolder(String fld) {
       FolderName = fld + File.separator + "locks";
       var hndl = new File(FolderName);
       if(!hndl.exists() ) {
           hndl.mkdir();
       }
       else if(!hndl.isDirectory())
           throw new Error("Can not create locks folder: " + FolderName);
    }

    public LockHandler(String folder) {

        boolean lockAcquired = false;
        try {
            initLockFolder(folder);
          for(;Instance <= MAX_INSTANCES; Instance++) {
              var fileName = FolderName + File.separator + Instance + ".lock";
              File file = new File(fileName);
              FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
              try {
                  lock = channel.tryLock();
                  if(lock != null) {
                      lockAcquired = true;
                      break;
                  }
              } catch (OverlappingFileLockException e) {  }
          }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new Error("INSTANCE_LOCK:  I/O Error creating locks: " + e.getMessage());
        }
        if(!lockAcquired)
            throw new Error("INSTANCE_LOCK: Could not acquire lock");
    }
}
