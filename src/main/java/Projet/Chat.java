package Projet;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import util.FocusTextField;

public class Chat  {
	
	private static long tailleAnneau = (long) Math.pow(2, 10);
	
	public static void main(String[] args) throws RemoteException, MalformedURLException, UnknownHostException, NotBoundException {	
		//Création de la fenêtre principale
		JFrame accueil = new JFrame();
		
		JPanel panel = new JPanel(new GridLayout(3, 1));
		JPanel panelNorth = new JPanel(new FlowLayout());
		
		//Création JCheckBox qui permettent de décider entre rejoindre le chat ou alors le créer
		JCheckBox rejoindreAnneau = new JCheckBox("Join cord");
		JCheckBox checkboxPrincipale = new JCheckBox("Main pair");
		
		//Cette classe va nous permettre de surligner le texte lorsqu'on coche "Rejoindre anneau"
		FocusTextField IPpair = new FocusTextField();
		
		/*Cet actionlistener permet d'enlever le texte si l'utilisateur choisit de rejoindre l'anneau
		 * puis finalement d'être l'anneau principal
		 */
		checkboxPrincipale.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(rejoindreAnneau.isSelected()) {
					rejoindreAnneau.setSelected(false);
					IPpair.setText("");
				} 				
			}
		});
		panelNorth.add(checkboxPrincipale);
		
		/*Cet actionlistener nous permet d'insérer un texte lorsque que l'utilisateur
		 * décide de rejoindre l'anneau et permet également de ne pas pouvoir cocher
		 * les deux possibilitées (rejoindre l'anneau et être le pair principal
		 */
		rejoindreAnneau.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(rejoindreAnneau.isSelected()) {
					IPpair.setText("Enter the IP address");
				} else {
					IPpair.setText("");
				}
				if(checkboxPrincipale.isSelected()) {
					checkboxPrincipale.setSelected(false);
				} 
				
			}
		});
		//Ajout de tous les panels dans le panel principal
		panelNorth.add(rejoindreAnneau);
		
		panel.add(panelNorth);		
		
		panel.add(IPpair);
		//Affichage de la fenêtre d'accueil qui va permettre la connection au chat
		accueil.setContentPane(panel);
		accueil.setSize(400, 200);
		accueil.setVisible(true);
		
		//Bouton qui va permettre la connexion au chat
		JButton button = new JButton("Connection");
		panel.add(button);
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				IPair pair;
				try {
					pair = new Pair((long) (Math.random()  * tailleAnneau));
					
					boolean success = false;
					
					while(!success)
					{
						try
						{
							LocateRegistry.createRegistry(pair.getPort());
							/*RMI va nous permettre d'invoquer les méthodes d'un autre ordinateur de la même
							 * façon que si elle était sur le même ordinateur
							 * La fonction rebind va nous permettre de remplacer
							 * noter ibjet dans le registre RMI
							 */
							String url = "rmi://" + InetAddress.getLocalHost().getHostAddress() + ":" + pair.getPort() + "/Pair";
							Naming.rebind(url, pair);
							success = true;
							System.out.println("Enregistrement de l'objet Pair avec l'url : " + url);
						}
						catch(Exception e1)
						{
							success = false;
							pair.setPort(pair.getPort()+1);
						}
					}
					
					// On va récupérer un Pair qui va obligatoirement être en activité
					//Remote r = Naming.lookup("rmi://" + "10.154.118.232" + ":1099/Pair");
					//La fontcion lookup va nous permettre de rechercher un objet distant
					//dans le registre RMI
					Remote r = null;
					if (checkboxPrincipale.isSelected()) {
						r = Naming.lookup("rmi://" + InetAddress.getLocalHost().getHostAddress() + ":1099/Pair");
					}
					else {
						String IPTextField = IPpair.getText();
						r = Naming.lookup("rmi://" + IPTextField + ":1099/Pair");
					}
					if (r instanceof IPair) {
						pair.joinMainChord((IPair) r);
					}
				} catch (RemoteException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NotBoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//Disparition de la fenêtre d'acceuil
				accueil.setVisible(false);
			
			}
		});
		
		
}
}
