package TP5;

import java.util.ArrayList;

public class Chord {
	
	private ArrayList<ChordPeer> peers;
	
	public Chord(ArrayList<Long> ids)
	{
		peers = new ArrayList<ChordPeer>();
		
		if (ids.size() > 0)
		{
			ChordPeer firstPeer = new ChordPeer(ids.get(0));
			peers.add(firstPeer);
			for (int i=1; i<ids.size(); i++)
			{
				ChordPeer newPeer = new ChordPeer(ids.get(i));
				newPeer.joinChord(firstPeer);
				peers.add(newPeer);
			}
			
			firstPeer.setItem(4, "wololo");
			firstPeer.setItem(8, "wolala");
			firstPeer.setItem(15, "wolili");
		}
	}
	
	public String toString()
	{
		String resultat = "";
		
		for (int i=0; i<peers.size(); i++)
		{
			resultat += peers.get(i) + "\n\n";
		}
		
		return resultat;
	}
	
	public static void main(String[] args) {
		
		ArrayList<Long> ids = new ArrayList<Long>();
		ids.add((long) 13);
		ids.add((long) 9);
		ids.add((long) 2);
		ids.add((long) 5);
		ids.add((long) 11);
		
		Chord anneau = new Chord(ids);
		System.out.println(anneau);
	}
}
