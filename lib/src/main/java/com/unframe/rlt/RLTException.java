package com.unframe.rlt;

public class RLTException extends RuntimeException {
    public RLTException(String message) {
        super(message);
    }
    public RLTException(String message, Exception exc) {
        super(message, exc);
    }
    public static class FileException extends RLTException {
        public FileException(String message) {
          super(message);
        }
        public FileException(String message, Exception exc) {
            super(message, exc);
        }
    }
}
