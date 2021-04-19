
package network;

import components.core.classic.pieces_v2.Coordinate;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import network.Server;


public class ServerThread extends Thread {
	public static final int CHALLENGE = 103;
	public static final int TO_BE_CHALLENGED = 104;
	private Socket client;
	private String namePlayer;
	private String namePlayerEnemy = "";
	private int capacity;

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getNamePlayer() {
		return namePlayer;
	}

	public void setNamePlayer(String namePlayer) {
		this.namePlayer = namePlayer;
	}

	public String getNamePlayerEnemy() {
		return namePlayerEnemy;
	}

	public void setNamePlayerEnemy(String namePlayerEnemy) {
		this.namePlayerEnemy = namePlayerEnemy;
	}

	public ServerThread(Socket client, String name) {
		this.client = client;
		this.namePlayer = name;
	}

	public Socket getClient() {
		return client;
	}

	public void setClient(Socket client) {
		this.client = client;
	}

	public String getnamePlayer() {
		return namePlayer;
	}

	public void setnamePlayer(String name) {
		this.namePlayer = name;
	}

	public void run() {
		while (true) {
			try {
				ObjectInputStream obj = new ObjectInputStream(client.getInputStream());
				Request request = (Request) obj.readObject();//nhan data
				//System.out.println(request.getType());
				switch (request.getType()) {
			
				case Request.CLIENT2:
					//System.out.println("ACCEPT_CHALLENGE");
					System.out.println("CLient2----The opponent is ready");
					capacity = TO_BE_CHALLENGED;
					namePlayerEnemy = request.getIPChallenge();
					for (int i = 0; i < Server.listClient.size(); i++) {
						if(Server.listClient.size()%2==0 && namePlayerEnemy.equals(Server.listClient.get(i).getNamePlayer()) ) {
							System.out.println("client2");
						//if (namePlayerEnemy.equals(Server.listClient.get(i).getNamePlayer())) {
							Server.listClient.get(i).setNamePlayerEnemy(namePlayer);
							Request reqApp = new Request(Request.CLIENT2);
							reqApp.setIPChallenge(namePlayer);
							try {
								ObjectOutputStream objout = new ObjectOutputStream(Server.listClient.get(i).client.getOutputStream());
								objout.writeObject(reqApp);
								objout.flush();
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							break;
						}else {
							System.out.println("aaaaaaaaa");
						}
					}
					break;
				case Request.CLIENT1:
					System.out.println("CLient1");
					capacity = CHALLENGE;
					for (int i = 0; i < Server.listClient.size(); i++) {						
						if(Server.listClient.size()%2==0 && Server.listClient.get(i).namePlayerEnemy.equals("") && i%2!=0) {
							System.out.println("Play1");
						//if (request.getIPChallenge().equals(Server.listClient.get(i).getnamePlayer()) && Server.listClient.get(i).namePlayerEnemy.equals("")) {
							Request reqChall = new Request(Request.CLIENT1);
							reqChall.setIPChallenge(namePlayer);
							try {
								ObjectOutputStream objout = new ObjectOutputStream(Server.listClient.get(i).client.getOutputStream());
								objout.writeObject(reqChall);
								objout.flush();
								break;
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							
						}												
					}
					break;

				case Request.MOVE_CHESS:
					System.out.println("MOVE_CHESS");

					Request reqMovechess = new Request(Request.MOVE_CHESS);
					reqMovechess.setStart(new Coordinate(request.getStart().getRow(), request.getStart().getCol()));
					reqMovechess.setTarget(new Coordinate(request.getTarget().getRow(), request.getTarget().getCol()));

					for (int i = 0; i < Server.listClient.size(); i++) {
						if (namePlayerEnemy.equals(Server.listClient.get(i).namePlayer)) {
							try {
								ObjectOutputStream objout = new ObjectOutputStream(Server.listClient.get(i).client.getOutputStream());
								objout.writeObject(reqMovechess);
								objout.flush();
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
					break;
				case Request.CROWN:
					System.out.println("CROWN");
					for (int i = 0; i < Server.listClient.size(); i++) {
						if (namePlayerEnemy.equals(Server.listClient.get(i).namePlayer)) {
							try {
								ObjectOutputStream objout = new ObjectOutputStream(Server.listClient.get(i).client.getOutputStream());
								objout.writeObject(request);
								objout.flush();
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
					break;

				case Request.SURRENDER:
					System.out.println("Sur");
					for (int i = 0; i < Server.listClient.size(); i++) {
						if (namePlayerEnemy.equals(Server.listClient.get(i).namePlayer)) {
							try {
								ObjectOutputStream objout = new ObjectOutputStream(Server.listClient.get(i).client.getOutputStream());
								objout.writeObject(request);
								this.client.close();
								Server.listClient.remove(this);
								objout.flush();
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				break;
				case Request.DISCONNECT:
					System.out.println("DISCONNECT");
					this.client.close();
					Server.listClient.remove(this);
					Server.ResponeListOnlineForPlayer();
					System.out.println(Server.listClient.size() + "-----   ");
					this.stop();
					break;
				default:
					System.out.println("HACK GAME");
					System.out.println("DISCONNECT");
					this.client.close();
					Server.listClient.remove(this);
					Server.ResponeListOnlineForPlayer();
					System.out.println(Server.listClient.size() + "-----   ");
					this.stop();
					break;
				}
				
			} catch (Exception ex) {
			}
		}

	}

}
