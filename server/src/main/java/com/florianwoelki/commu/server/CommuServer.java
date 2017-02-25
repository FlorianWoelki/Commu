package com.florianwoelki.commu.server;

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextArea;
import com.florianwoelki.commu.api.gfx.Window;
import com.florianwoelki.commu.server.network.NetworkServer;
import lombok.Getter;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Florian Woelki on 23.02.17.
 */
public class CommuServer extends Window {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");

    public static final String TITLE = "Commu Server";
    public static final int WIDTH = 500;
    public static final int HEIGHT = 400;
    @Getter
    private static CommuServer instance;

    private NetworkServer server;

    private WebTextArea console;
    private WebList listUsers;

    public CommuServer() {
        super(TITLE, WIDTH, HEIGHT);
        initFrame();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        server = new NetworkServer(8000);
    }

    @Override
    public void createView() {
        WebPanel panel = new WebPanel();
        setContentPane(panel);

        panel.setLayout(new BorderLayout());

        console = new WebTextArea();
        console.setEditable(false);
        ((DefaultCaret) console.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        WebScrollPane consoleSP = new WebScrollPane(console);
        consoleSP.setBorder(BorderFactory.createTitledBorder("Console Output:"));
        panel.add(consoleSP, BorderLayout.CENTER);

        listUsers = new WebList();
        WebScrollPane listUsersSP = new WebScrollPane(listUsers);
        listUsersSP.setBorder(BorderFactory.createTitledBorder("Connected Users:"));
        listUsersSP.setPreferredSize(new Dimension(200, 0));
        panel.add(listUsersSP, BorderLayout.EAST);
    }

    public void updateView() {
        DefaultListModel model = new DefaultListModel();
        server.getConnectedClientMap().keySet().forEach(username -> {
            model.addElement(username);
        });
        listUsers.setModel(model);
    }

    public void log(String message) {
        console.append(DATE_FORMAT.format(new Date()) + " " + message + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Install WebLaf
            WebLookAndFeel.install();

            instance = new CommuServer();
            instance.server.startServer();
            instance.setVisible(true);
        });
    }

}
