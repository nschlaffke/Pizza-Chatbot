package pl.poznan.put.cs.si.puttalky;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.api.runtime.KieSession;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

/** Author: agalawrynowicz<br>
 * Date: 19-Dec-2016 */

public class BazaWiedzy {

    private OWLOntologyManager manager = null;
    private OWLOntology ontologia;
    private Set<OWLClass> listaKlas;
    private Set<OWLClass> listaDodatkow;
    private Set<OWLClass> listaNazwanychPizz;
    private Set<OWLClass> listaTypowPizz;
    
    OWLReasoner silnik;
    
    public void inicjalizuj() {
		InputStream plik = this.getClass().getResourceAsStream("/pizza.owl");
		manager = OWLManager.createOWLOntologyManager();
		
		try {
			ontologia = manager.loadOntologyFromOntologyDocument(plik);
			silnik = new Reasoner.ReasonerFactory().createReasoner(ontologia);
			listaKlas = ontologia.getClassesInSignature();
			listaDodatkow = new HashSet<OWLClass>();
			listaNazwanychPizz = new HashSet<OWLClass>();
			listaTypowPizz = new HashSet<OWLClass>();

			OWLClass dodatek  = manager.getOWLDataFactory().getOWLClass(IRI.create("http://semantic.cs.put.poznan.pl/ontologie/pizza.owl#Dodatek"));
			for (org.semanticweb.owlapi.reasoner.Node<OWLClass> klasa: silnik.getSubClasses(dodatek, false)) {
				listaDodatkow.add(klasa.getRepresentativeElement());
			}

            OWLClass pizza  = manager.getOWLDataFactory().getOWLClass(IRI.create("http://semantic.cs.put.poznan.pl/ontologie/pizza.owl#Pizza"));
            for (org.semanticweb.owlapi.reasoner.Node<OWLClass> klasa: silnik.getSubClasses(pizza, true)) {
                if(klasa.getRepresentativeElement().toStringID().toLowerCase().contains("nazwanapizza")){
                    continue;
                }
                listaTypowPizz.add(klasa.getRepresentativeElement());
            }

            OWLClass nazwanaPizza  = manager.getOWLDataFactory().getOWLClass(IRI.create("http://semantic.cs.put.poznan.pl/ontologie/pizza.owl#NazwanaPizza"));
            for (org.semanticweb.owlapi.reasoner.Node<OWLClass> klasa: silnik.getSubClasses(nazwanaPizza, false)) {
                listaNazwanychPizz.add(klasa.getRepresentativeElement());
            }
			
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    
    public Set<String> dopasujDodatek(String s){
    	Set<String> result = new HashSet<String>();
    	for (OWLClass klasa : listaDodatkow){
    		if (klasa.toString().toLowerCase().contains(s.toLowerCase()) && s.length()>2){
    			result.add(klasa.getIRI().toString());
    		}
    	}
    	return result;
    }

    public Set<String> wyszukajPizzePoLiscieDodatkow(List<String> dodatki)
	{
		Set<String> poprzednik = wyszukajPizzePoDodatkach(dodatki.get(0));
		for(String dodatek : dodatki)
		{
			Set<String> nastepnik = wyszukajPizzePoDodatkach(dodatek);
			poprzednik.retainAll(nastepnik);
		}
		return poprzednik;
	}
    public Set<String> wyszukajPizzePoDodatkach(String iri){
    	Set<String> pizze = new HashSet<String>();
    	OWLObjectProperty maDodatek = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create("http://semantic.cs.put.poznan.pl/ontologie/pizza.owl#maDodatek"));
    	Set<OWLClassExpression> ograniczeniaEgzystencjalne = new HashSet<OWLClassExpression>();
    	
    	OWLClass dodatek = manager.getOWLDataFactory().getOWLClass(IRI.create(iri));
    	OWLClassExpression wyrazenie = manager.getOWLDataFactory().getOWLObjectSomeValuesFrom(maDodatek, dodatek);
    	ograniczeniaEgzystencjalne.add(wyrazenie);
  	
    	OWLClassExpression pozadanaPizza = manager.getOWLDataFactory().getOWLObjectIntersectionOf(ograniczeniaEgzystencjalne);
    	
		for (org.semanticweb.owlapi.reasoner.Node<OWLClass> klasa: silnik.getSubClasses(pozadanaPizza, false)) {
			pizze.add(klasa.getEntities().iterator().next().asOWLClass().getIRI().getFragment());
		}
	
		return pizze;
    }

    public Set<String> dopasujNazwePizzy(String s){
        Set<String> result = new HashSet<String>();
        for (OWLClass klasa : listaNazwanychPizz){
            if (klasa.toString().toLowerCase().contains(s.toLowerCase()) && s.length()>2){
                result.add(klasa.getIRI().toString());
            }
        }
        return result;
    }

	public Set<String> dopasujTypPizzy(String s){
		Set<String> result = new HashSet<String>();
		Parser parser = new Parser();
		for (OWLClass klasa : listaTypowPizz){
			System.out.println(klasa.toString().toLowerCase());
			if (klasa.toString().toLowerCase().contains(s.toLowerCase()) && s.length()>2){
				result.add(klasa.getIRI().toString());
			}
		}
		return result;
	}

    public static void main(String[] args) {
		BazaWiedzy baza = new BazaWiedzy();
		baza.inicjalizuj();
		
		OWLClass mieso = baza.manager.getOWLDataFactory().getOWLClass(IRI.create("http://semantic.cs.put.poznan.pl/ontologie/pizza.owl#DodatekMięsny"));
		for (org.semanticweb.owlapi.reasoner.Node<OWLClass> klasa: baza.silnik.getSubClasses(mieso, true)) {
			System.out.println("klasa:"+klasa.toString());
		}
		for (OWLClass d:  baza.listaDodatkow){
			System.out.println("dodatek: "+d.toString());
		}
	}

}
