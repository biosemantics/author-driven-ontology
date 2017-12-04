package edu.arizona.biosemantics.server;



import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;


//this message is for testing purpose

/**
 * This class includes implemented methods being used to retrieve meaning of and
 * relationships among terms in PATO using OWL API. Keywords, synonyms, and
 * parents of a term could be retrieved by giving the term.
 * 
 * TAO: http://berkeleybop.org/ontologies/tao.owl
 * PATO: http://purl.obolibrary.org/obo/pato.owl
 * 
 * @author Zilong Chang, Hong Cui, Erman Gurses
 * Modified by Erman Gurses
 */
public class OWLAccessorImpl implements OWLAccessor {

	private OWLOntologyManager manager;
	private OWLDataFactory df;
	private OWLOntology ont;
	private Set<OWLAnnotation> set;
    private String key;
    private String value; 
    private HashMap<String, List<String>> hashMap = new HashMap<String, List<String>>();

    private Iterator<OWLAnnotation> itrKey; 
	
	public OWLAccessorImpl(String ontoURL) {
		manager = OWLManager.createOWLOntologyManager();
		df = manager.getOWLDataFactory();
		IRI iri = IRI.create(ontoURL);
		try {
			ont = manager.loadOntologyFromOntologyDocument(iri);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public OWLAccessorImpl(File file) {
		manager = OWLManager.createOWLOntologyManager();
		df = manager.getOWLDataFactory();

		try {
			ont = manager.loadOntologyFromOntologyDocument(file);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
	@SuppressWarnings("deprecation")
	public void MapLabelsToExactSynoyms() {
      for (OWLClass cls : ont.getClassesInSignature()) {
    	  
        // Get the annotations on the class that use the label property
        	set = getLabels(cls);
        itrKey = set.iterator();
        
    		if(itrKey.hasNext()){
    	      key  = getRefinedOutput(itrKey.next().toString());
    	      Set<OWLAnnotation> set = getExactSynonyms(getClassByLabel(key));
    	      Iterator<OWLAnnotation> itrValue = set.iterator();
    	      List<String> synyoyms = new ArrayList<String>();    

    	      while(itrValue.hasNext()) {
        	    value = getRefinedOutput(itrValue.next().toString()); 
        	    synyoyms.add(value);
        	    
        	    
    	      }
      	  hashMap.put(key, synyoyms);

        }   
      }
      System.out.println("Loading is done!");
	}
	public List<String> getExactSynoymsfromMap(String token ) {
		System.out.println(hashMap.get(token).toString());
		return hashMap.get(token);	
	}
	public Set<OWLAnnotation> getLabels(OWLClass c) {
		return EntitySearcher.getAnnotations(c,ont,df.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI())).collect(Collectors.toSet()); 
	}
	
	@Override
	public String getLabel(OWLClass c) {
		if (this.getLabels(c).isEmpty()) {
			return "";
		} else {
			OWLAnnotation label = (OWLAnnotation) this.getLabels(c).toArray()[0];
			return this.getRefinedOutput(label.getValue().toString());
		}
	}

	/**
	 * Remove the non-readable or non-meaningful characters in the retrieval
	 * from OWL API, and return the refined output.
	 */
	public String getRefinedOutput(String origin) {
		if (origin.startsWith("Annotation")) {
			origin = origin.replaceFirst("^Annotation.*>\\s+", "")
					.replaceFirst("^Annotation.*label", "")
					.replaceFirst("\\)\\s*$", "").trim();
		}

		/*
		 * Remove the ^^xsd:string tail from the returned annotation value
		 */
		return origin.replaceAll("\\^\\^xsd:string", "").replaceAll("\"", "")
				.replaceAll("\\.", "");
	}
	@Override
	public OWLClass getClassByLabel(String l) {
		for (OWLClass c : this.getAllClasses()) {
			if (this.getLabel(c).trim().toLowerCase()
					.equals(l.trim().toLowerCase())) {
				return c;
			}
		}
		return null;
	}
	
	@Override
	public String getID(OWLClass c) {
		 Set<OWLAnnotation> ids = (Set<OWLAnnotation>) EntitySearcher.getAnnotations(c,ont,df.getOWLAnnotationProperty(IRI
				.create("http://purl.obolibrary.org/obo/#id"))).collect(Collectors.toSet());

		if(ids.isEmpty()){
			//no id, return empty string
			return "";
		}else{
			return this.getRefinedOutput(((OWLAnnotation)ids.toArray()[0]).toString());
		}
	}
	
	/**
	 * Return the exact synonyms of a term represented by an OWLClass object.
	 */
	public Set<OWLAnnotation> getExactSynonyms(OWLClass c) {
		return EntitySearcher.getAnnotations(c,ont,df.getOWLAnnotationProperty(IRI
				.create("http://www.geneontology.org/formats/oboInOwl#hasExactSynonym"))).collect(Collectors.toSet()); 
	}
	


	@SuppressWarnings("deprecation")
	@Override
	public Set<OWLClass> getAllClasses() {
		// TODO Auto-generated method stub
		return ont.getClassesInSignature();
	}
	
	@Override
	public List<OWLClass> retrieveConcept(String con) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getKeywords(OWLClass c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getSynonymLabels(OWLClass c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getParentsLabels(OWLClass c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getAllOffSprings(OWLClass c) {
		// TODO Auto-generated method stub
		return null;
	}



}