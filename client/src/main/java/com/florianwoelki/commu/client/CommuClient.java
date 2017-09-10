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
import com.florianwoelki.commu.protocol.packet.ChatPacket;
import lombok.Getter;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * Created by Florian Woelki on 23.02.17.
 *
 * This class creates the GUI for the client.
 * It inherits from the {@link Window}
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

    /**
     * This constructor initializes the frame and it connects
     * to the server.
     */
    public CommuClient() {
        super(TITLE, WIDTH, HEIGHT);
        initFrame();

        client = new NetworkClient("127.0.0.1", 8000);
        client.connectToServer();
    }

    /**
     * See {@link Window#createView()}
     */
    @Override
    public void createView() {
        WebPanel panel = new WebPanel();
        setContentPane(panel);

        panel.setLayout(new BorderLayout());

        // Creating the connected users panel in the east
        listUsers = new WebList();
        WebScrollPane listUsersSP = new WebScrollPane(listUsers);
        listUsersSP.setBorder(BorderFactory.createTitledBorder("Connected Users:"));
        listUsersSP.setPreferredSize(new Dimension(200, 0));
        panel.add(listUsersSP, BorderLayout.EAST);

        // Creating the chat panel in the center
        WebPanel panelChat = new WebPanel(new BorderLayout());
        panel.add(panelChat, BorderLayout.CENTER);
        textChat = new WebTextArea();
        textChat.setEditable(false);
        ((DefaultCaret) textChat.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        WebScrollPane textChatSP = new WebScrollPane(textChat);
        textChatSP.setBorder(BorderFactory.createTitledBorder("Chat:"));
        panelChat.add(textChatSP, BorderLayout.CENTER);

        // Creating the input panel in the south
        WebPanel panelInput = new WebPanel(new BorderLayout());
        panel.add(panelInput, BorderLayout.SOUTH);
        fieldInput = new WebTextField();
        fieldInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendChatPacket();
                }
            }
        });
        panelInput.add(fieldInput, BorderLayout.CENTER);
        WebButton buttonSend = new WebButton("Send");
        buttonSend.addActionListener(e -> sendChatPacket());
        panelInput.add(buttonSend, BorderLayout.EAST);
    }

    /**
     * Send the chat packet to the server.
     * It also append the chat message to the client GUI.
     */
    private void sendChatPacket() {
        String message = fieldInput.getText();

        // Check if message is empty
        if(message.length() == 0) {
            return;
        }

        appendText(client.getUsername(), message);
        fieldInput.setText("");

        try {
            client.sendPacket(new ChatPacket(client.getUsername(), message));
        } catch(IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * This method appends a given text to the chat of
     * the client GUI.
     *
     * @param username String, who sends a message
     * @param message  String, what the message is
     */
    public void appendText(String username, String message) {
        textChat.append(username + ": " + message + "\n");
    }

    /**
     * This method updates the view in the client GUI.
     * It updates the connected users.
     */
    public void updateView() {
        DefaultListModel model = new DefaultListModel();
        client.getConnectedClients().keySet().forEach(model::addElement);
        listUsers.setModel(model);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Install WebLaf
            WebLookAndFeel.install();

            // Creating instance of client and starts it
            instance = new CommuClient();
            instance.setTitle(TITLE + " | Connected as " + instance.client.getUsername());
            instance.setVisible(true);
        });
    }

}
