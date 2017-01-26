package Projet;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public interface IPair extends Remote {
	
	public IPair findMainChord(long key) throws RemoteException;
	
	public void joinMainChord(IPair pair) throws RemoteException, UnknownHostException, MalformedURLException, NotBoundException;
	
	public void leaveMainChord() throws RemoteException, UnknownHostException, MalformedURLException, NotBoundException;

	public long getId() throws RemoteException;

	public IPair getPred() throws RemoteException;
	
	public IPair getSucc() throws RemoteException;

	public void setPred(IPair pair) throws RemoteException;

	public void setSucc(IPair succ) throws RemoteException;

	public void forwardMessage(long chatKey, long intkey, String value) throws RemoteException, UnknownHostException, MalformedURLException, NotBoundException;

	public void addChatRoom(long idRoom, long idCreateur, boolean first)throws RemoteException;

	public void joinChatRoom(long idRoom, long idJoiner, boolean b) throws RemoteException, UnknownHostException, MalformedURLException, NotBoundException;

	public void addIpAnnuaire(long idCreateur, String ip, boolean b)throws RemoteException;

	public void leaveChatRoom(long idRoom, long idLeaver, boolean b)throws RemoteException;
	
	public HashMap<Long, ArrayList<Long>> getChatRoomsList() throws RemoteException;

	public ArrayList<String> getAnnuaire() throws RemoteException;

	public int getPort() throws RemoteException;

	public void setPort(int port) throws RemoteException;

	public void deleteIpAnnuaire(long idCreateur, String ip, boolean b)throws RemoteException;

	public IPair findMainChordComplexe(long intkey)throws RemoteException, UnknownHostException, MalformedURLException, NotBoundException;

	public void updateFingerTableConnexion(long idUpdater, boolean b) throws RemoteException, UnknownHostException, MalformedURLException, NotBoundException;

	public void updateFingerTableDeconnexion(long idUpdater, boolean b) throws RemoteException, UnknownHostException, MalformedURLException, NotBoundException;

	public HashMap<Long, ArrayList<String>> getChatRoomsMessages() throws RemoteException;

}
