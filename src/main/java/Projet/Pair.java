package Projet;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Pair extends UnicastRemoteObject implements IPair {
	
	private static final long serialVersionUID = 9128955856645819253L;

	private long myId;
	private static int port = 1099;
	
	private IPair succ;
	private IPair pred;
	private ArrayList<IPair> fingerTable;
	
	private PairFrame fenetre;
	
	// HashMap définissant les chatRooms
	private HashMap<Long, ArrayList<Long>> listeSalon;
	private HashMap<Long, ArrayList<String>> messagesSalon;
	
	private ArrayList<String> annuaire;
	
	private static long tailleAnneau = (long) Math.pow(2, 10);
	
	public Pair(long id) throws RemoteException
	{
		super();
		listeSalon = new HashMap<Long, ArrayList<Long>>();
		messagesSalon = new HashMap<Long, ArrayList<String>>();
		annuaire = new ArrayList<String>();
		this.myId = id;

		this.succ = this;
		this.pred = this;
		
		fenetre = new PairFrame(id, this);
		
		// A la fermeture de la fenetre on appelle leaveMainChord pour quitter l'anneau
		fenetre.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					leaveMainChord();
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NotBoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				try {
					leaveMainChord();
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NotBoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		fingerTable = new ArrayList<IPair>();
	}
	
	public long getId()
	{
		return myId;
	}
	
	public void setSucc(IPair succ) throws RemoteException 
	{
		this.succ = succ;
		fenetre.afficherSuccesseur(succ.getId());
	}

	public void setPred(IPair pred) throws RemoteException 
	{
		this.pred = pred;
		fenetre.afficherPredecesseur(pred.getId());
		fenetre.majIntervalle(pred.getId(), myId);
	}
	
	public IPair getSucc() {
		return succ;
	}

	public IPair getPred() {
		return pred;
	}
	
	public String toString()
	{
		try {
			return "Pair : " + myId + "\n" +
					"\t" + "Pred : " + pred.getId() + "\n" + 
					"\t" + "Succ : " + succ.getId() + "\n";
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public boolean equals(IPair peer) throws RemoteException
	{
		return peer.getId() == myId;
	}
	
	// Méthode simplifiée pour trouver le pair responsable d'une clef (intkey)
	public IPair findMainChord(long intkey) throws RemoteException
	{
		if (this.equals(pred) && this.equals(succ))
		{
			return this;
		}
		
		if (pred.getId() < myId)
		{
			if (intkey > pred.getId() && intkey <= myId)
			{
				return this;
			}
		}
		
		if (pred.getId() > myId)
		{
			if (intkey > pred.getId() || intkey <= myId)
				return this;
		}
		
		return succ.findMainChord(intkey);
	}
	
	// Méthode complexe (passant par la fingertable) pour trouver le pair responsable d'une clef (intkey)
	
	public IPair findMainChordComplexe(long intkey) throws RemoteException, UnknownHostException, MalformedURLException, NotBoundException
	{
		if (this.equals(pred) && this.equals(succ))
		{
			return this;
		}
		
		IPair pairLePlusProche = this;
		
		for (int i=0;i<fingerTable.size(); i++)
		{
			IPair pairCourant = fingerTable.get(i);
			
			if (intkey == pairCourant.getId())
			{
				pairLePlusProche = pairCourant;
				break;
			}
			
			if (intkey > pairCourant.getId() && intkey > pairLePlusProche.getId())
			{
				if (pairCourant.getId() > pairLePlusProche.getId())
				{
					pairLePlusProche = pairCourant;
				}
			}
			
			if (intkey < pairCourant.getId() && intkey < pairLePlusProche.getId())
			{
				if (pairCourant.getId() > pairLePlusProche.getId())
				{
					pairLePlusProche = pairCourant;
				}
			}
			
			if (pairLePlusProche.getId() > intkey && pairCourant.getId() < intkey)
			{
				pairLePlusProche = pairCourant;
			}
		}
		
		if (pairLePlusProche.getId() == myId)
		{
			if (myId == intkey)
				return this;
			else
				return this.getSucc();
		}
		
		return pairLePlusProche.findMainChordComplexe(intkey);
	}
	
	
	/*
	 * join MainChord permet d'insérer notre pair dans l'anneau chord à un emplacement
	 * La méthode permet de chercher une place dans le Chord et d'y insérer notre pair
	 * handle représente un pair déjà présent dans l'anneau (le pair principal toujours actif dans la plupart des cas)
	 */
	public void joinMainChord(IPair handle) throws RemoteException, UnknownHostException, MalformedURLException, NotBoundException
	{
		IPair mySucc = handle.findMainChordComplexe(myId);
		
		IPair myPred = mySucc.getPred();
		
		this.setPred(myPred);
		
		this.setSucc(mySucc);
		
		mySucc.setPred(this);
		
		myPred.setSucc(this);
		
		annuaire = handle.getAnnuaire();
		
		fenetre.displayIP(annuaire);
		
		String ip;
		try {
			ip = InetAddress.getLocalHost().getHostAddress() + ":" + port;
			addIpAnnuaire(myId, ip, true);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		listeSalon = handle.getChatRoomsList();
		messagesSalon = new HashMap<Long, ArrayList<String>>();
		
	    Iterator<Entry<Long, ArrayList<Long>>> it = listeSalon.entrySet().iterator();
	    while (it.hasNext()) {
	        @SuppressWarnings("rawtypes")
			Map.Entry salon = (Map.Entry)it.next();
	        long idSalon =  (long) salon.getKey();
			fenetre.displayChatRooms(idSalon);
	    }
	    
	    updateFingerTableConnexion(myId, true);
	}
	
	/*
	 * updateFingerTableConnexion permet de mettre à jour la fingertable de notre pair qui liste les adresses IP connecté à notre anneau
	 */

	public void updateFingerTableConnexion(long idUpdater, boolean first) throws RemoteException, UnknownHostException, MalformedURLException, NotBoundException {
		if (idUpdater != myId || first)
		{
			fingerTable.clear();
			for (int i=0; i<=(Math.log10(Math.pow(2, 10))); i++)
			{
				long idFinger = (long) (myId + Math.pow(2, i) % Math.pow(2, 10));
				IPair responsableKey = findMainChord(idFinger);
				System.out.println(idFinger + "-----" + responsableKey.getId());
				fingerTable.add(responsableKey);
			}
			System.out.println("\n\n");
			succ.updateFingerTableConnexion(idUpdater, false);
		}
	}
	
	/*
	 * updateFingerTableDeconnexion fonctionne du même principe de updatefingerTableConnexion
	 */
	
	public void updateFingerTableDeconnexion(long idUpdater, boolean first) throws RemoteException, UnknownHostException, MalformedURLException, NotBoundException {
		if (idUpdater != myId || first)
		{
			fingerTable.clear();
			for (int i=0; i<=(Math.log10(Math.pow(2, 10))); i++)
			{
				long idFinger = (long) (myId + Math.pow(2, i) % Math.pow(2, 10));
				IPair responsableKey = findMainChord(idFinger);
				if (responsableKey.getId() == idUpdater)
				{
					fingerTable.add(responsableKey);
				}
				else
				{
					fingerTable.add(responsableKey);
				}
			}
			succ.updateFingerTableDeconnexion(idUpdater, false);
		}
	}
	
	/*
	 * leaveMainChord() met à jour les liens de l'anneau virtuel lors de la deconnexion d'un pair dans l'anneau
	 */
	
	public void leaveMainChord() throws RemoteException, UnknownHostException, MalformedURLException, NotBoundException
	{
		String ip;
		try {
			ip = InetAddress.getLocalHost().getHostAddress() + ":" + port;
			deleteIpAnnuaire(myId, ip, true);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		updateFingerTableConnexion(myId, true);
		
		succ.setPred(pred);
		pred.setSucc(succ);
	}
	
	/*
	 * La méthode forwardMessage permet de faire circuler les messages de notre chat vers son destinataire (le pair responsable de intkey)
	 * Le message parcours l'anneau chord et est retransmis dans l'IHM du pair récepteur
	 * 
	 */
	public void forwardMessage(long chatKey, long intkey, String value) throws RemoteException, UnknownHostException, MalformedURLException, NotBoundException
	{
		IPair pairResponsable = findMainChordComplexe(intkey);
		
		if (pairResponsable == this)
		{
			messagesSalon.get(chatKey).add(value);
			fenetre.recevoirMessage(value);
		}
		else
		{
			pairResponsable.forwardMessage(chatKey, intkey, value);
		}
	}
	
	public HashMap<Long, ArrayList<Long>> getChatRoomsList()
	{
		return listeSalon;
	}
	
	public HashMap<Long, ArrayList<String>> getChatRoomsMessages()
	{
		return messagesSalon;
	}
	
	/*
	 * La méthode addChatRoom permet d'ajouter un nouveau salon à notre chat
	 * Une ChatRoom permet d'ouvrir un salon et d'établir une communication entre plusieurs pairs
	 * Les chatsRooms sont connues par tous les pairs
	 * L'ajout d'une chatRoom est transmis récursivement chez tous les pairs
	 */
	
	public void addChatRoom(long idRoom, long idCreateur, boolean first)throws RemoteException{
		if(idCreateur!=myId || first){
			listeSalon.put(idRoom, new ArrayList<Long>());
			messagesSalon.put(idRoom, new ArrayList<String>());
			fenetre.displayChatRooms(idRoom);
			succ.addChatRoom(idRoom, idCreateur, false);
		}
	}
	
	/*
	 * addIpAnnuaire permet d'ajouter la liste des addresses IP connextés au réseau à notre annuaire
	 * L'annuaire est connu par tous les pairs et mis à jour en temps réel à chaque modification
	 */
	
	public void addIpAnnuaire(long idCreateur, String ip, boolean first)throws RemoteException{
		if(idCreateur!=myId || first){
			annuaire.add(ip);
			fenetre.displayIP(annuaire);
			succ.addIpAnnuaire(idCreateur, ip, false);
		}
	}
	
	/*
	 * Quand un pair se déconnecte de l'application, son IP est supprimé de l'annuaire
	 */
	
	public void deleteIpAnnuaire(long idCreateur, String ip, boolean first)throws RemoteException{
		if(idCreateur!=myId || first){
			annuaire.remove(ip);
			succ.deleteIpAnnuaire(idCreateur, ip, false);
			fenetre.displayIP(annuaire);
		}
	}
	
	/*
	 * joinChatRoom permet à un pair de rejoindre un salon de conversation
	 * Il prend en paramètre son id et l'ajoute directement au salon
	 * Il peut alors transmettre des messages directement aux pairs connectés sur le salon et recevoir les messages de celui-ci
	 * Les modifications concernant les participants d'une chatRoom sont transmises récursivement vers tous les pairs
	 */
	
	public void joinChatRoom(long idRoom, long idJoiner, boolean first) throws RemoteException, UnknownHostException, MalformedURLException, NotBoundException
	{
		ArrayList<Long> participants = listeSalon.get(idRoom);
		
		if (participants.contains(idJoiner))
			return;
		
		if (idJoiner == myId && first)
		{
			if (listeSalon.get(idRoom).size() != 0)
			{
				HashMap<Long, ArrayList<String>> messagesSalonTemp = findMainChordComplexe(listeSalon.get(idRoom).get(0)).getChatRoomsMessages();
				ArrayList<String> messages = messagesSalonTemp.get(idRoom);
			    for (int i=0; i<messages.size(); i++)
			    {
			    	String message = messages.get(i);
			        fenetre.recevoirMessage(message);
			    }
			    messagesSalon.putAll(messagesSalonTemp);
			}
		}
		
		if (idJoiner != myId || first)
		{		
			participants.add(idJoiner);
			
			listeSalon.put(idRoom, participants);
			
			succ.joinChatRoom(idRoom, idJoiner, false);
		}
	}
	
	/*
	 * LeaveChatRoom permet à un pair connecté sur une chatRoom de quitter celle-ci
	 * Il ne reçoit alors plus les messages des autres pairs
	 * Les modifications concernant les participants d'une chatRoom sont transmises récursivement vers tous les pairs
	 */
	
	public void leaveChatRoom(long idRoom, long idLeaver, boolean first) throws RemoteException
	{
		ArrayList<Long> participants = listeSalon.get(idRoom);
		
		if (!participants.contains(idLeaver))
			return;
		
		if (idLeaver != myId || first)
		{		
			participants.remove(idLeaver);
			
			listeSalon.put(idRoom, participants);
			
			succ.leaveChatRoom(idRoom, idLeaver, false);
		}
	}
	
	/*
	 * sendToChatRoom permet à un pair d'envoyer un message dans un salon
	 * On appelle forwardMessage vers tous les membres du salon correspondant
	 */
	
	public void sendToChatRoom(long chatkey, String message) throws RemoteException, UnknownHostException, MalformedURLException, NotBoundException
	{
		ArrayList<Long> participants = listeSalon.get(chatkey);
		
		if(!listeSalon.containsKey(chatkey))
			return;
		if (!participants.contains(myId))
			return;
		
		for (int i=0; i<participants.size(); i++)
		{
			forwardMessage(chatkey, participants.get(i), message);
		}
	}
	
	
	
	public ArrayList<String> getAnnuaire()
	{
		return annuaire;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public long ecartMinEntreClefEtPair(long clef, long pairClef)
	{
		if (pairClef == clef)
			return 0;
		
		if (clef > pairClef)
			return (tailleAnneau-clef) + pairClef;
		else
			return pairClef-clef;
	}
	
	@SuppressWarnings("static-access")
	public void setPort(int port)
	{
		this.port = port;
	}
}
