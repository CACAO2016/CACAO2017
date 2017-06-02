package abstraction.distributeur.europe;

import java.util.ArrayList;
import abstraction.transformateur.usa.interfacemarche.*;
import abstraction.fourni.Acteur;
import abstraction.fourni.Journal;
import abstraction.fourni.Monde;

public class Marche implements Acteur{


	private static double BORNESMIN=0.004;
	private static double BORNESMAX=0.008;
	private ArrayList<IDistributeur> distributeur;
	private ArrayList<transformateur> transformateur;
	private ArrayList<IDistributeur> distributeurActif;
	private ArrayList<transformateur> transformateurActif;
	private boolean onEchange;
	private double prixMoyen;
	private Journal journal;	


	public static final double UNITE=1000;

	public Marche(){
		this.journal = new Journal("Journal du marche");
		this.distributeur= new ArrayList<IDistributeur>();
		this.transformateur= new ArrayList<transformateur>();
		Monde.LE_MONDE.ajouterJournal(this.journal);
	}

	public ArrayList<IDistributeur> getDistrib(){
		return this.distributeur;
	}

	public ArrayList<transformateur> getTransfo(){
		return this.transformateur;
	}

	public int indiceMaximum(){
		int indice_max = 0;
		for (int i=1; i<this.distributeurActif.size(); i++){
			indice_max = ((this.distributeurActif.get(i).getPrixMax()>this.distributeurActif.get(indice_max).getPrixMax()) ? i : indice_max);
		}
		return indice_max;
	}

	public int indiceMinimum(){
		int indice_min = 0;
		for (int i=1; i<this.transformateurActif.size(); i++){
			indice_min = ((this.transformateurActif.get(i).getprixMin()<this.transformateurActif.get(indice_min).getprixMin()) ? i : indice_min);
		}
		return indice_min;		
	}
	
	

	public void Actif(){
		
		this.distributeurActif=new ArrayList<IDistributeur>();
		this.transformateurActif=new ArrayList<transformateur>();
		for (int i=0; i<transformateur.size();i++){
			if (transformateur.get(i).getprixMin()>=BORNESMIN&&transformateur.get(i).getprixMin()<=BORNESMAX){
				transformateurActif.add(transformateur.get(i));
				
			}
		}
		for (IDistributeur a:this.distributeur){
			if (a.getPrixMax()>=BORNESMIN && a.getPrixMax()<=BORNESMAX){
				this.distributeurActif.add(a);
			}
		}
	}

	public void Echanges(){
		IDistributeur prioD;
		transformateur prioT;
		this.Actif();
		if (this.distributeurActif.isEmpty() || this.transformateurActif.isEmpty()){
			this.onEchange=false;
		}
		
		if (onEchange){
			prioD= this.distributeurActif.get(this.indiceMaximum());
			this.journal.ajouter("Le distributeur a remporte le marche : " + prioD.getNom());
			prioT=this.transformateurActif.get(this.indiceMinimum());
			this.journal.ajouter("Le transformateur a remporte le marche : " + prioT.getprixMin());
			if (prioD.getPrixMax()>=prioT.getprixMin()){
				prixMoyen=(prioD.getPrixMax()+prioT.getprixMin())/2;
				this.journal.ajouter("Prix moyen : " + prixMoyen);
				prioD.notif(new Vente(prixMoyen,UNITE));
				prioT.notif(prixMoyen,UNITE);	
			}
			else{
				onEchange=false;
			}
			
		}

	}

	@Override
	public String getNom() {
		return "Marche Distributeur / Transformateur";
	}

	public void next(){
		this.onEchange=true;

		while(this.onEchange){
			this.Echanges();
			
		}
	}



}