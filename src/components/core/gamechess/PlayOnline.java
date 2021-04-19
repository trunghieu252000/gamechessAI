
package components.core.gamechess;


import build.Main;
import network.Client;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class PlayOnline {

    public Thread listenner;
    private Main main;
    public static int k=0;
    public static int h=0;
    public static int x=1;
    public static List<String> listPlayer = new ArrayList<String>();
    public PlayOnline(Main main) {
        this.main = main;
         
    } 
	public void room() {

		if (listPlayer.size() % 2 == 0 && h%2!=0 ) {
			Client.requestChallenge(listPlayer.get(k));
			System.out.println("room");
			k = k + 2;
			x=x+2;
		}
		if(listPlayer.size() % 2 != 0){
			JOptionPane.showMessageDialog(null, "Waiting" );
		}
		h++;
		
	}
   
    public void LoadListPlayerOnline(){
    	System.out.println(listPlayer.size());
    }

}
