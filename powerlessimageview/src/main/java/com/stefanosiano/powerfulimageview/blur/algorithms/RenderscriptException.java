package com.stefanosiano.powerfullibraries.imageview.blur.algorithms;

/**
 * Custom exception when something goes wrong with renderscript
 */

final class RenderscriptException extends Exception {
    RenderscriptException() {
        super();
    }

    RenderscriptException(String message) {
        super(message);
    }

    RenderscriptException(String message, Throwable cause) {
        super(message, cause);
    }

    RenderscriptException(Throwable cause) {
        super(cause);
    }
}
