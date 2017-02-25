package com.florianwoelki.commu.api.gfx;

import com.alee.laf.rootpane.WebFrame;
import lombok.AllArgsConstructor;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Florian Woelki on 23.02.17.
 */
@AllArgsConstructor
public abstract class Window extends WebFrame {

    private String title;
    private int width;
    private int height;

    public abstract void createView();

    public void initFrame() {
        Dimension size = new Dimension(width, height);
        setSize(size);
        setMinimumSize(size);
        setTitle(title);
        setResizable(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        createView();
    }

}
