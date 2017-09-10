package com.florianwoelki.commu.api.gfx;

import com.alee.laf.rootpane.WebFrame;
import lombok.AllArgsConstructor;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Florian Woelki on 23.02.17.
 *
 * This class represents a Window.
 * You can create easily a simple frame with custom properties.
 */
@AllArgsConstructor
public abstract class Window extends WebFrame {

    private String title;
    private int width;
    private int height;

    /**
     * In this method you need to create the view.
     * For example if you want to put a button in your window,
     * you implement it right here.
     */
    public abstract void createView();

    /**
     * This method initializes the frame with all given properties.
     * In addition, it calls the method createView(). See also {@link #createView()}.
     */
    protected void initFrame() {
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
