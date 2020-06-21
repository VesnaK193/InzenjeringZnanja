package com.klinika.pregled.cbrApplication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.klinika.pregled.dto.CBRResponseDTO;
import com.klinika.pregled.dto.CBRTestDTO;
import com.klinika.pregled.dto.TestDTO;
import com.klinika.pregled.dto.CBRLekDTO;
import com.klinika.pregled.repository.PregledRepository;
//
//import ucm.gaia.jcolibri.method.retrieve.RetrievalResult;
//import ucm.gaia.jcolibri.method.retrieve.selection.SelectCases;
import com.klinika.pregled.repository.TestRepository;
import com.klinika.pregled.repository.LekRepository;

import ucm.gaia.jcolibri.method.retrieve.RetrievalResult;
import ucm.gaia.jcolibri.method.retrieve.selection.SelectCases;

@Service
public class CBRService {

	@Autowired
	private PregledRepository repo;

	@Autowired
	private TestRepository testRepo;
	
	@Autowired
	private LekRepository lekRepository;
	
	public List<CBRResponseDTO> getMatches(CBRModelPregled cbr){
		CbrApplication app = new CbrApplication(repo.findAll());
		
		Collection<RetrievalResult> eval = app.evaluate(cbr);
		eval = SelectCases.selectTopKRR(eval, 5);
		
		ArrayList<CBRResponseDTO> rezultati = new ArrayList<>();
		for(RetrievalResult r : eval) {
			if(r.getEval() > 0) {
				CBRResponseDTO novi = new CBRResponseDTO();
				novi.setDijagnoza(((CBRModelPregled)r.get_case().getDescription()).getDijagnoza());;
				//novi.setId(((CBRModelSimptomPregled)r.get_case().getDescription()).getId());
				novi.setSimptomi(((CBRModelPregled)r.get_case().getDescription()).getSimptom());
				rezultati.add(novi);
			}else {
				System.out.println("Nothing matches!");
			}
		}
		
		return rezultati;
	}
	
	public List<TestDTO> getTestMatches(CBRModelTest cbr){
		CBRApplicationTest app = new CBRApplicationTest(repo.findAll());
		
		Collection<RetrievalResult> eval = app.evaluate(cbr);
		eval = SelectCases.selectTopKRR(eval, 5);
		
		Set<String> uniqueSetOfTest = new HashSet<>();
		ArrayList<CBRTestDTO> rezultati = new ArrayList<>();
		List<TestDTO> tests = new ArrayList<>();
		
		for(RetrievalResult r : eval) {
			if(r.getEval() > 0) {
				CBRTestDTO novi = new CBRTestDTO();
				novi.setTestovi(((CBRModelTest)r.get_case().getDescription()).getTestovi());
				rezultati.add(novi);
			}else {
				System.out.println("Nothing matches!");
			}
		}
		
		for(CBRTestDTO testDTO : rezultati) {
			for(String test : testDTO.getTestovi()) {
				if(!uniqueSetOfTest.contains(test)) {
					uniqueSetOfTest.add(test);
				}
			}
		}
		//Isfiltrirani testovi
		for(String s : uniqueSetOfTest) {
			TestDTO newTest = new TestDTO(s);
			tests.add(newTest);
		}
		
		return tests;
	}
	
	public List<CBRLekDTO> getLekMatches(CBRModelLek cbr){
		CBRApplicationLek app = new CBRApplicationLek(lekRepository.findAll());
		
		Collection<RetrievalResult> eval = app.evaluate(cbr);
		eval = SelectCases.selectTopKRR(eval, 5);
		
		ArrayList<CBRLekDTO> rezultati = new ArrayList<>();
		for(RetrievalResult r : eval) {
			if(r.getEval() > 0) {
				CBRLekDTO novi = new CBRLekDTO();
				novi.setLek(((CBRModelLek)r.get_case().getDescription()).getLek());
				rezultati.add(novi);
			}else {
				System.out.println("Nothing matches!");
			}
		}
		return rezultati;
	}
}
