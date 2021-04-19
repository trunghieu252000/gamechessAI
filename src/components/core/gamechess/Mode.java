
package components.core.gamechess;

import build.Main;
import network.Connect;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Mode extends JFrame {

    private Main mainJFrame;
    public Mode() {
        initComponents();
    }
    public Mode(Main main) {
        this.mainJFrame = main;
        initComponents();
    }

    private void initComponents() {

        btn_player_player = new JButton();
        btn_player_AI = new JButton();
        btn_playonline = new JButton();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Mode");

        btn_player_player.setText("Player vs player");
        btn_player_player.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btn_player_playerActionPerformed(evt);
            }
        });

        btn_player_AI.setText("Player vs Computer");
        btn_player_AI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btn_player_AIActionPerformed(evt);
            }
        });

        btn_playonline.setText("Play Online");
        btn_playonline.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btn_playonlineActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_playonline, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_player_AI)
                    .addComponent(btn_player_player, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 33, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_player_player)
                .addGap(18, 18, 18)
                .addComponent(btn_player_AI)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_playonline)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }

    private void btn_player_playerActionPerformed(ActionEvent evt) {
        mainJFrame.newGamePlayerAndPlayer();
        this.setVisible(false);
    }

    private void btn_player_AIActionPerformed(ActionEvent evt) {
        mainJFrame.newGamePlayrtAndAI();
        this.setVisible(false);
    }

    private void btn_playonlineActionPerformed(ActionEvent evt) {
        new Connect(mainJFrame).setVisible(true);
        this.setVisible(false);
    }
    
    private JButton btn_player_AI;
    private JButton btn_player_player;
    private JButton btn_playonline;
  
}
