
package network;

import build.Main;
import components.core.gamechess.PlayOnline;

import javax.swing.*;

public class Connect extends JFrame {

    private Main mainJFrame;
    public Connect() {
        initComponents();
    }
    
    public Connect(Main main) {
        mainJFrame = main;
        initComponents();
    }
    
    private void initComponents() {

        text_IP = new JTextField();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        text_port = new JTextField();
        btn_OK = new JButton();
        btn_Cancel = new JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Connect Server");

        text_IP.setText("localhost");

        jLabel1.setText("IP Server");
        jLabel1.setToolTipText("");

        jLabel2.setText("Name");

        text_port.setText("CLient");

        btn_OK.setText("OK");
        btn_OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_OKActionPerformed(evt);
            }
        });

        btn_Cancel.setText("Cancel");
        btn_Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_CancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(57, 57, 57)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(text_port)
                            .addComponent(text_IP, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(103, 103, 103)
                        .addComponent(btn_OK)
                        .addGap(46, 46, 46)
                        .addComponent(btn_Cancel)))
                .addContainerGap(68, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(text_IP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(text_port, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_OK)
                    .addComponent(btn_Cancel))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }
    private void btn_OKActionPerformed(java.awt.event.ActionEvent evt) {
        String ip = text_IP.getText();
       // int port = Integer.parseInt(text_port.getText());
        int port=2909;
        Client.connect(ip, port);
         
        mainJFrame.playonline = new PlayOnline(mainJFrame);
       // mainJFrame.playonline.setVisible(true);
        mainJFrame.listener.start();
        Main.Mymain = mainJFrame;
        this.setVisible(false);
    }

    private void btn_CancelActionPerformed(java.awt.event.ActionEvent evt) {
        this.setVisible(false);
    }
    private JButton btn_Cancel;
    private JButton btn_OK;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JTextField text_IP;
    private JTextField text_port;

}
