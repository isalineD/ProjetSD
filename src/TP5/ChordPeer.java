package TP5;

import java.util.HashMap;

// Un pair possède un identifiant se trouvant dans la liste 
// de clefs données : s'il existe 2^4 clefs, on aura
// 0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15

// S'il existe 3 pairs dont les id sont 4, 8, 11
// Le predecesseur du pair 8 est 4 et son successeur est 11

// Chaque pair est responsable des clefs se trouvant entre
// son predecesseur et lui même
// Dans l'exemple, 
// le pair 8 est responsable de l'intervalle [5, 8]
// le pair 11 est responsable de l'intervalle [9, 11]
// le pair 4 est responsable de l'intervalle [12, 15]u[0, 4]

public class ChordPeer {
	
	// L'identifiant du pair
	private long myId;
	
	// Chaque pair connait le pair qui lui succède
	// et qui lui précède
	private ChordPeer succ;
	private ChordPeer pred;
	
	// Contenu que va gérer ce pair
	private HashMap<Long, String> dictionnaire;
	
	// Pas besoin de commenter ca loul
	public ChordPeer(long id)
	{
		this.myId = id;
		// Par defaut mon successeur et mon predecesseur 
		// sont moi meme (on pourra modifier ca avec la
		// méthode joinChord)
		this.succ = this;
		this.pred = this;
		
		this.dictionnaire = new HashMap<Long, String>();
	}
	
	// Getters et Setters
	public long getId()
	{
		return myId;
	}
	
	public void setSucc(ChordPeer succ) 
	{
		this.succ = succ;
	}

	public void setPred(ChordPeer pred) 
	{
		this.pred = pred;
	}
	
	public ChordPeer getSucc() {
		return succ;
	}

	public ChordPeer getPred() {
		return pred;
	}
	
	// Méthode d'affichage du pair
	public String toString()
	{
		return "Pair : " + myId + "\n" +
				"\t" + "Pred : " + pred.getId() + "\n" + 
				"\t" + "Succ : " + succ.getId() + "\n" +
				"Valeur : " + dictionnaire;
	}
	
	// Méthode permettant de savoir si un pair est égal à
	// un autre
	public boolean equals(ChordPeer peer)
	{
		return peer.getId() == myId;
	}
	
	// Le but de cette méthode va être de trouver le pair
	// qui est responsable de la clef "intkey" (si la clef
	// se trouve dans l'intervalle dont je suis responsable)
	public ChordPeer findKey(long intkey)
	{
		// Cas ou il n'existe qu'un seul pair, je suis 
		// forcement le responsable
		if (this.equals(pred) && this.equals(succ))
		{
			return this;
		}
		
		// Cas ou mon predecesseur se trouve bien derrière moi
		// 0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15
		// Ex : je suis 11 et mon predecesseur est 6
		if (pred.getId() < myId)
		{
			// Si la clef est supérieur à l'id de mon
			// prédecesseur et inferieur au mien
			// Je suis responsable de l'intervalle où
			// se trouve cette clef
			if (intkey > pred.getId() && intkey <= myId)
			{
				return this;
			}
		}
		
		// Cas ou mon predecesseur est le dernier de la liste
		// 0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|15
		// Ex : je suis 3 et mon predecesseur est 14
		if (pred.getId() > myId)
		{
			if (intkey > pred.getId() || intkey <= myId)
				return this;
		}
		
		// Si je ne suis pas responsable de cette clef,
		// je demande à mon successeur s'il l'est
		return succ.findKey(intkey);
	}
	
	// Methode pour m'ajouter à la liste des pairs (anneau)
	// En connaissant un pair quelconque de celle-ci (ici "handle")
	public void joinChord(ChordPeer handle)
	{
		// On cherche mon futur successeur
		ChordPeer mySucc = handle.findKey(myId);
		
		// On cherche mon futur predecesseur
		ChordPeer myPred = mySucc.getPred();
		
		// Mon predecesseur est l'ancien predecesseur de mySucc
		this.setPred(mySucc.getPred());
		
		// Mon successeur est mySucc
		this.setSucc(mySucc);
		
		// Le predecesseur de mySucc est moi meme
		mySucc.setPred(this);
		
		// Le successeur de myPred est moi meme
		myPred.setSucc(this);
	}
	
	// Methode pour me supprimer de la liste des pairs (anneau)
	public void leaveChord()
	{
		succ.setPred(pred);
		pred.setSucc(succ);
	}
	
	public String getItem(long intkey)
	{
		ChordPeer pairResponsable = findKey(intkey);
		
		if (pairResponsable == this)
		{
			return dictionnaire.get(intkey);
		}
		else
		{
			return pairResponsable.getItem(intkey);
		}
	}
	
	public void setItem(long intkey, String value)
	{
		ChordPeer pairResponsable = findKey(intkey);
		
		if (pairResponsable == this)
		{
			dictionnaire.put(intkey, value);
		}
		else
		{
			pairResponsable.setItem(intkey, value);
		}
	}
	
}
