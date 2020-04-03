/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.knobrix.config;

/**
 *
 * @author milto
 */
public class Configurable {
    static {
        if(Settings.isFirstUse()) {
            Settings.init();
        }
    }
}
