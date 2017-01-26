package Projet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Date;

import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PairFrame extends JFrame{
	
	private static final long serialVersionUID = -2170703286389237609L;
	
	private JLabel successeur;
	private JLabel predecesseur;
	private JLabel intervalleResp;
	
	
	private JPanel textField;
	
	private JTextArea chatRoomCible;
	private JTextArea textArea;
	private JButton boutonSend;
	
	private JPanel listChannel;
	private JPanel panelIp;
	
	private String fileString = "";
	private String fileName = "";
	
	private Pair pair;
	
	private JButton addFile;
	
	/**
	 * Création de la fenetre d'un pair
	 * @param idPair
	 * @param pair
	 */
	public PairFrame(long idPair, Pair pair)
	{
		super("Pair " + idPair);
		
		this.pair = pair;
		
		setSize(800, 400);
		
		//Le Panel Nord contient les information sur le pair
		predecesseur = new JLabel("", SwingConstants.CENTER);
		successeur = new JLabel("", SwingConstants.CENTER);
		intervalleResp = new JLabel("", SwingConstants.CENTER);
		JPanel north = new JPanel(new GridLayout(1, 3));
		north.add(predecesseur);
		north.add(intervalleResp);
		north.add(successeur);
		
		//Le Panel Est contient l'annuaire
		JPanel east = new JPanel(new BorderLayout());
		east.setPreferredSize(new Dimension(200,400));
		east.add(new JLabel(" Directory "), BorderLayout.NORTH);
		panelIp = new JPanel(new GridLayout(50, 1));
		JScrollPane scrollPaneIP = new JScrollPane(panelIp);
		east.add(scrollPaneIP, BorderLayout.CENTER);
		
		//Le Panel West contient la liste des Salons de conversation
		JPanel west = new JPanel();
		west.setPreferredSize(new Dimension(150,400));
		listChannel = new JPanel(new GridLayout(15, 1));
		JPanel addRoom = new JPanel();
		listChannel.add(new JLabel("Channel"));
		//Bouton pour l'ajout de Salon
		JButton bAddRoom = new JButton("Add ChatRoom");
		addRoom.add(bAddRoom);
		bAddRoom.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					//Au click sur le bouton on ajoute un salon
					pair.addChatRoom(pair.getChatRoomsList().size(), pair.getId(), true);
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}				
			}
		});
		
		west.add(listChannel, BorderLayout.CENTER);
		west.add(addRoom, BorderLayout.SOUTH);
		
		//scrollpane contient la conversation
		textField = new JPanel();
		textField.setLayout(new BoxLayout(textField, BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(textField);
		
		//Le panel South contient les champs correspondant à l'envoi de message
		JPanel south = new JPanel(new GridLayout(2, 3));
		//TextArea pour le message
		textArea = new JTextArea();
		//TextArea pour le salon de conversation ciblé
		chatRoomCible = new JTextArea();
		textArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

		//Bouton d'envoi
		boutonSend = new JButton("Send");
		boutonSend.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				
				if (addFile.isEnabled() == false) {
					addFile.setEnabled(true);
					addFile.setText("Add file to message");
				}
				try {
					//On récupère le message dans la textArea
					String content = textArea.getText();
					//On passe le message en Json tout en ajoutant les différentes informations comme
					//l'id du pair emetteur, le salon ciblé, l'ip, la date
					JsonObject objet = new JsonObject();
					objet.addProperty("transmitter", "Pair " + pair.getId());
					objet.addProperty("room", "Room " + chatRoomCible.getText());
					objet.addProperty("ip", InetAddress.getLocalHost().getHostAddress());
					objet.addProperty("date", new Date().toString());
					objet.addProperty("timestamp", new Date().getTime());
					
					JsonObject contentArray = new JsonObject();				
					contentArray.addProperty("message", content);
					contentArray.addProperty("fileContent", fileString);
					contentArray.addProperty("fileName", fileName);
					objet.add("content", contentArray);
					//On envoie le message au salon correspondant
					pair.sendToChatRoom(Long.parseLong(chatRoomCible.getText()), objet.toString());
					
					//on vide les champs
					chatRoomCible.setText("");
					textArea.setText("");
					
					fileName = "";
					fileString = "";
				} catch (NumberFormatException | RemoteException e1) {
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
		});
		
		south.add(new JLabel("Number ChatRoom :", JLabel.CENTER));
		south.add(new JLabel("Message : ", JLabel.CENTER));
		// Bouton pour l'ajout de fichier
		addFile = new JButton("Add file to message");
		addFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//Au click sur le bouton, on ouvre un FileChooser
				JFileChooser chooser = new JFileChooser();
			    int returnVal = chooser.showOpenDialog(new JFrame());
			    
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	try {
			    		//On créé un FileInputStream sur le fichier sélectionné
						FileInputStream flux = new FileInputStream(chooser.getSelectedFile());
						//On créé un buffer de la taile du fichier sélectionné
						byte[] buffer = new byte[(int) chooser.getSelectedFile().length()];
						//on lit le fichier sélectionné dans le buffer
						flux.read(buffer);
						Encoder encoder = Base64.getEncoder();
						//on encode le tableau de byte en String
						fileString = encoder.encodeToString(buffer);
						fileName = chooser.getSelectedFile().getName();
						flux.close();
						//si le bouton est disponible, on le rend indisponible et on écrit le nom du fichier dessus
						if(addFile.isEnabled()) {
					    	addFile.setEnabled(false);
					    	addFile.setText(fileName);
					    }
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			    }
			    
				
			}
		});
		south.add(addFile);
		
		south.add(chatRoomCible);
		south.add(textArea);
		south.add(boutonSend);
		
		//On ajoute les différents composants à la fenêtre
		add(east, BorderLayout.EAST);
		add(west, BorderLayout.WEST);
		add(north, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(south, BorderLayout.SOUTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	/**
	 * Affiche le successeur dans le label du successeur
	 * @param idSuccesseur
	 */
	public void afficherSuccesseur(long idSuccesseur)
	{
		successeur.setText("Successor : " + idSuccesseur);
	}
	
	/**
	 * Affiche le prédécesseur dans le label du prédécesseur
	 * @param idPredecesseur
	 */
	public void afficherPredecesseur(long idPredecesseur)
	{
		predecesseur.setText("Predecessor : " + idPredecesseur);
	}
	
	/**
	 * Mettre à jour le label correspondant à l'intervalle
	 * @param idPredecesseur
	 * @param myId
	 */
	public void majIntervalle(long idPredecesseur, long myId)
	{
		intervalleResp.setText("[ " + (idPredecesseur + 1) + ", " + myId + " ]");
	}
	
	/**
	 * 
	 * @param message
	 */
	public void recevoirMessage(String message)
	{
		//On recupère le message sous forme d'objet Json
		JsonParser parser = new JsonParser();
		JsonObject o = parser.parse(message).getAsJsonObject();
		
		//On affiche les informations correspondants à l'envoi du message
		textField.add(new JLabel("<html><br>" + o.get("transmitter").getAsString() + " from " + o.get("room").getAsString() + " (" + o.get("date").getAsString() + ") : </html>"));
		//On affiche le message 
		textField.add(new JLabel("    > " + o.get("content").getAsJsonObject().get("message").getAsString()));
		
		//On récupère le nom et le contenu du fichier en String
		String contenuFichierRecu = o.get("content").getAsJsonObject().get("fileContent").getAsString();
		String nomFichierRecu = o.get("content").getAsJsonObject().get("fileName").getAsString();
		
		//Si le fichier n'est pas vide
		if (!contenuFichierRecu.equals(""))
		{
			//On affiche un message pour pouvoir télécharger le fichier reçu
			JLabel labelFichier = new JLabel("Cliquez ici pour le télécharger");
			Map<TextAttribute, Integer> fontAttributes = new HashMap<TextAttribute, Integer>();
			fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
			labelFichier.setFont(new Font("Serif", Font.BOLD, 12).deriveFont(fontAttributes));
			textField.add(new JLabel("Vous avez reçu un fichier"));
			textField.add(labelFichier);
			
			labelFichier.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mouseClicked(MouseEvent e) {
					//Lorsque l'on clique sur le lien, un fileChooser s'ouvre pour sélectionner l'endroit où enregistrer le fichier
					JFileChooser chooser = new JFileChooser();
					chooser.setCurrentDirectory(new java.io.File("."));
				    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				    int returnVal = chooser.showOpenDialog(new JFrame());
				    if(returnVal == JFileChooser.APPROVE_OPTION) {
				    	try {
				    		//On récupère le chemin sélectionné
							String directoryString = chooser.getSelectedFile().getAbsolutePath();
							//On créé un nouveau fichier
							File f = new File(directoryString + File.separator + nomFichierRecu);
							Decoder d = Base64.getDecoder();
							byte[] buffer = d.decode(contenuFichierRecu);
							FileOutputStream fos = new FileOutputStream(f);
							//On vient lire le contenu dans le File f
							fos.write(buffer);
							f.createNewFile();
							fos.close();
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
				    }	
				}
			});
		}
		
		this.validate();
	}
	
	/**
	 * Afficher un salon de conversation
	 * @param id
	 */
	public void displayChatRooms(long id){
		JLabel label = new JLabel("Room " + Long.toString(id));
		label.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				//Si on clique sur un salon où on est présent
				if (label.getForeground() == Color.GREEN)
				{
					try {
						//On se sort du salon
						pair.leaveChatRoom(id, pair.getId(), true);
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}
					//On remet le salon en noir
					label.setForeground(Color.BLACK);
				}
				//Si on est pas dans le salon
				else
				{
					try {
						//on rejoint le salon
						pair.joinChatRoom(id, pair.getId(), true);
					} catch (RemoteException e1) {
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
					//On met le salon en vert
					label.setForeground(Color.GREEN);
				}
			}
		});
		//On ajoute le salon dans la liste des salons
		listChannel.add(label);
		this.validate();
	}
	
	/**
	 * Afficher l'annuaire d'adresses IP
	 * @param annuaire ArrayList des adresses IP
	 */
	public void displayIP(ArrayList<String> annuaire){
		//Suppression des adresses IP
		panelIp.removeAll();
		panelIp.revalidate();
		panelIp.repaint();
		//On parcourt l'annuaire et on créé un label pour chaque adresse
		for (String ip : annuaire) {
			JLabel labelIp = new JLabel(ip);
			panelIp.add(labelIp);
		}
		panelIp.validate();
		this.validate();
	}
}
