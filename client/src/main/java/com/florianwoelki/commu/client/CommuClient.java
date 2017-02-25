package com.florianwoelki.commu.client;

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextArea;
import com.alee.laf.text.WebTextField;
import com.florianwoelki.commu.api.gfx.Window;
import com.florianwoelki.commu.client.network.NetworkClient;
import lombok.Getter;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by Florian Woelki on 23.02.17.
 */
public class CommuClient extends Window {

    public static final String TITLE = "Commu Client";
    public static final int WIDTH = 600;
    public static final int HEIGHT = 400;
    @Getter
    private static CommuClient instance;

    private NetworkClient client;

    private WebList listUsers;
    private WebTextArea textChat;
    private WebTextField fieldInput;
    private WebButton buttonSend;

    public CommuClient() {
        super(TITLE, WIDTH, HEIGHT);
        initFrame();

        client = new NetworkClient("127.0.0.1", 8000);
        client.connectToServer();
    }

    @Override
    public void createView() {
        WebPanel panel = new WebPanel();
        setContentPane(panel);

        panel.setLayout(new BorderLayout());

        listUsers = new WebList();
        WebScrollPane listUsersSP = new WebScrollPane(listUsers);
        listUsersSP.setBorder(BorderFactory.createTitledBorder("Connected Users:"));
        listUsersSP.setPreferredSize(new Dimension(200, 0));
        panel.add(listUsersSP, BorderLayout.EAST);

        WebPanel panelChat = new WebPanel(new BorderLayout());
        panel.add(panelChat, BorderLayout.CENTER);

        textChat = new WebTextArea();
        textChat.setEditable(false);
        ((DefaultCaret) textChat.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        WebScrollPane textChatSP = new WebScrollPane(textChat);
        textChatSP.setBorder(BorderFactory.createTitledBorder("Chat:"));
        panelChat.add(textChatSP, BorderLayout.CENTER);

        WebPanel panelInput = new WebPanel(new BorderLayout());
        panel.add(panelInput, BorderLayout.SOUTH);

        fieldInput = new WebTextField();
        fieldInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String message = fieldInput.getText();

                    if(message.length() == 0) {
                        return;
                    }

                    textChat.append(client.getUsername() + ": " + message + "\n");
                    fieldInput.setText("");
                    // TODO: send chat message to server
                }
            }
        });
        panelInput.add(fieldInput, BorderLayout.CENTER);
        buttonSend = new WebButton("Send");
        buttonSend.addActionListener(e -> {
            String message = fieldInput.getText();

            if(message.length() == 0) {
                return;
            }

            textChat.append(client.getUsername() + ": " + message + "\n");
            fieldInput.setText("");
            // TODO: send chat message to server
        });
        panelInput.add(buttonSend, BorderLayout.EAST);
    }

    public void updateView() {
        DefaultListModel model = new DefaultListModel();
        client.getConnectedClients().keySet().forEach(username -> {
            model.addElement(username);
        });
        listUsers.setModel(model);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Install WebLaf
            WebLookAndFeel.install();

            instance = new CommuClient();
            instance.setVisible(true);
        });
    }

}
