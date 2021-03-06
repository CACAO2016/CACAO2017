package abstraction.producteur.cotedivoire.contrats;

import java.util.ArrayList;
import java.util.List;

import abstraction.fourni.Acteur;
import abstraction.fourni.Journal;
import abstraction.fourni.Monde;

public class AgentContrat implements Acteur{
	private List<IContratProd> producteurs; 
	private List<IContratTrans> transformateurs;
	public List<IContratTrans> demandeurs;
	public Journal journal;
	
	
	public AgentContrat () {
		this.producteurs= new ArrayList<IContratProd>(); 
		this.transformateurs= new ArrayList<IContratTrans>(); 
		this.demandeurs= new ArrayList<IContratTrans>();
		this.journal= new Journal ("Journal de "+this.getNom());
		Monde.LE_MONDE.ajouterJournal(this.journal);
	}
	
	public List<IContratProd> getProducteurs() {
		return this.producteurs;
	}
	
	public List<IContratTrans> getTransformateurs() {
		return this.transformateurs;
	}
	
	public void addProd(IContratProd prod){
		this.getProducteurs().add(prod);
	}
	
	public void addTrans(IContratTrans trans){
		this.getTransformateurs().add(trans);
	}
	
	public List<IContratTrans> getDemandeurs () {
		return this.demandeurs;
	}
	
	public void demandeDeContrat(IContratTrans t) {
		this.getDemandeurs().add(t);
	}

	@Override
	public String getNom() {
		return "Agent contrat";
	}

	@Override
	public void next() {
		List<Devis> l = new ArrayList<Devis>();
		for(IContratProd p : this.getProducteurs()) {     // Création de l'ensemble des devis.
			for (IContratTrans t : this.getDemandeurs()){
				l.add(new Devis(p,t));
				this.journal.ajouter("Création du devis entre "+p.getClass().getName()+" et "+t.getClass().getName());
			}
		}
		for (IContratTrans t : this.getDemandeurs()){  // Création et envoie de la list des devis dans lesquels t est impliqué.
			List<Devis> lt = new ArrayList<Devis>();
			for (Devis d : l){
				if (d.getTrans()==t){
					t.envoieDevis(d);
				}
			}
		}
		for (IContratProd p : this.getProducteurs()){  // Création et envoie de la list des devis dans lesquels p est impliqué.
			List<Devis> lp = new ArrayList<Devis>();
			for (Devis d : l){
				if (d.getProd()==p){
					p.envoieDevis(d);
				}
			}
		}

		for (IContratTrans t : this.getDemandeurs()){  // Les transfo vont modifiés la qttVoulue de chaque devis
			t.qttVoulue();
		}
		for(IContratProd p : this.getProducteurs()){  // Les prod vont modifiés prix et qttLivrable en fct de qttVoulue et de leur possibilité
			p.qttLivrablePrix();
		}
		for (Devis d : l){								// On bloque les setters des variables. 
			d.setVerrouillage();
		}
		for (IContratTrans t : this.getDemandeurs()){     // Transfo modifient la qttFinale qui sera la valeur recu chaque next pdt 1an.
			t.finContrat();
		}
		for(IContratProd p : this.getProducteurs()){   // Indique aux prod que les nego sont finis, ils peuvent donc récupérer l'info de la qttFinale
			p.notifContrat();
		}
	}
		

}
