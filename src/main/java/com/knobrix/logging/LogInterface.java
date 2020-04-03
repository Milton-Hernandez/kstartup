/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.knobrix.logging;

/**
 *
 * @author milto
 */
public interface LogInterface {
    public void trace(String msg, Object ... args);
    public void debug(String msg, Object ... args);
    public void info(String msg, Object ... args);
    public void warn(String msg, Object ... args);
    public void error(String msg, Object ... args);
    public void error(Throwable ex);
    public void warn(Throwable ex);
}
