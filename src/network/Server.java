
package network;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Server {
    
    public static List<ServerThread> listClient = new ArrayList<ServerThread>();
    public static void main(String argv[]){
        
        try{
          ServerSocket serverSocket = new ServerSocket(2909);
          // get IP SERVER
            InetAddress myHost = InetAddress.getLocalHost();
            System.out.println("Server IP: " + myHost.getHostAddress());
            System.out.println("Server name: " + myHost.getHostName());
            System.out.println("Server PORT: 2909" );
            while(true){
                Socket client = serverSocket.accept();
                ServerThread serverTheard = new ServerThread(client,client.getInetAddress().toString());
                System.out.println(client.getInetAddress() + " " + client.getPort() + " connected");
                
                listClient.add(serverTheard);
                // respone list onoline
                ResponeListOnlineForPlayer();

                serverTheard.start();
            }
        }
        catch(Exception ex){ex.printStackTrace();}
    }
    
    public static void ResponeListOnlineForPlayer(){
        List<String> list = new ArrayList<String>();
        for(int i = 0; i < listClient.size(); i++){
            list.add(Server.listClient.get(i).getClient().getInetAddress().toString());            
        }
        
        for(int i = 0; i <listClient.size(); i++){
            List<String> temp = new ArrayList<String>(list);
            //temp.remove(i);
            
            Request req = new Request(Request.ONLINE_PLAYER);
            req.setPlayerList(temp);
            try{
                ObjectOutputStream objout = new ObjectOutputStream(Server.listClient.get(i).getClient().getOutputStream());
                objout.writeObject(req);
                objout.flush();
            }
            catch(Exception ex){ex.printStackTrace();}
            
        }
        
    }
}
