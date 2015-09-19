package edu.rit.csci759.fuzzylogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.RuleBlock;
import net.sourceforge.jFuzzyLogic.rule.RuleExpression;
import net.sourceforge.jFuzzyLogic.rule.RuleTerm;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodAndMin;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodOrMax;


public class FuzzyController {

	private static String filename = "FuzzyLogic/tipper.fcl";
	private static FIS fis = FIS.load(filename, true);
	private static int ruleCount = 0;
	private static FuzzyController fuzzyContollerInstance = null;
	private FuzzyController(){

	}

	public static FuzzyController getFuzzyControllerInstance() {
		if(fuzzyContollerInstance == null)
			return new FuzzyController();
		else
			return fuzzyContollerInstance;
	}

	/**
	 * initiate Fuzzy Process.
	 * 
	 */
	public String initiateFuzzyProcessAndGetBlindStatus(double temperatureValue, double ambient){

		Map<String , String> map = new HashMap<String, String>();
		map.put("1", "close");
		map.put("2", "half");
		map.put("3", "half");
		map.put("4", "close");
		map.put("5", "open");

		if (fis == null) {
			System.err.println("Can't load file: '" + filename + "'");
			System.exit(1);
		}

		FunctionBlock fb = fis.getFunctionBlock(null);

		fb.setVariable("temperature", temperatureValue);
		fb.setVariable("ambient", ambient);

		fb.evaluate();

		fb.getVariable("blind").defuzzify();

		double max = 0;
		String term = "";

		//System.out.println(fis.getFunctionBlock("BLIND").getFuzzyRuleBlock("No1").getRules());

		for(Rule r : fis.getFunctionBlock("BLIND").getFuzzyRuleBlock("No1").getRules())
		{
			if(max < r.getDegreeOfSupport())
			{
				max = r.getDegreeOfSupport();
				term = r.getName();
			}

			//System.out.println(r.getDegreeOfSupport());
		}
		//System.out.println(term);

		String blindStatus;

		if(term != null){
			blindStatus = map.get(term);
			return blindStatus;
		}

		return null;


	}

	public int getRuleCount(){
		return fis.getFunctionBlock("BLIND").getFuzzyRuleBlock("No1").getRules().size();
	}

	/**
	 * get FuzzyRules.
	 * @return list of strings.
	 */
	public List<String> getFuzzyRules()
	{
		List<String> rules = new ArrayList<String>();

		List<Rule> newRules = fis.getFunctionBlock("BLIND").getRuleBlocks().get("No1").getRules();

		Collections.sort(newRules, new Comparator<Rule>() {

			@Override
			public int compare(Rule o1, Rule o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		for(Rule rule : newRules)
			rules.add(rule.toStringFcl());

		return rules;

	}

	/**
	 * update a fuzzy Rule.
	 * pass(ruleNo, Map<Variable,linguisticTerm> )
	 * @param newRuleMap
	 */
	public boolean updateRule(Map< String, Map<String,String>> updateRuleMap){

		HashMap<String, RuleBlock> ruleBlockMap = fis.getFunctionBlock("BLIND").getRuleBlocks();

		RuleBlock ruleBlock = ruleBlockMap.get("No1");

		Set<String> keys = updateRuleMap.keySet();
		Iterator<String> it = keys.iterator();
		String key = it.next();


		ruleBlock.getRules().remove(Integer.parseInt(key) - 1);

		Rule newRule = new Rule(key, ruleBlock);

		Variable temperature = fis.getVariable("temperature");
		Variable ambient =  fis.getVariable("ambient");
		Variable blind =  fis.getVariable("blind");

		RuleTerm term1;
		RuleTerm term2;
		RuleExpression antecedentAnd;
		RuleExpression antecedentOr;

		for(Entry< String, Map<String,String>> e1 : updateRuleMap.entrySet()){

			Map<String,String> map = e1.getValue();

			for(Entry<String,String> e : map.entrySet()){

				if(map.containsKey("condition") && e.getValue().equals("OR")){

					if(map.containsKey(temperature.toStringFcl()) && map.containsKey(ambient.toStringFcl()) 
							&& map.containsKey(blind.toStringFcl()) ){

						term1 = new RuleTerm(temperature, map.get(temperature.toStringFcl()), false);
						term2 = new RuleTerm(ambient, map.get(ambient.toStringFcl()), false);
						antecedentOr = new RuleExpression(term1, term2, RuleConnectionMethodOrMax.get());
						newRule.setAntecedents(antecedentOr);
						newRule.addConsequent(blind, map.get(blind.toStringFcl()), false);
					}

					else if(map.containsKey(temperature.toStringFcl()) && map.containsKey(blind.toStringFcl()) ){

						newRule.addAntecedent(temperature, map.get(temperature.toStringFcl()), false);
						newRule.addConsequent(blind, map.get(blind.toStringFcl()), false);
					}

					else if(map.containsKey(ambient.toStringFcl()) && map.containsKey(blind.toStringFcl()) ){

						newRule.addAntecedent(ambient, map.get(ambient.toStringFcl()), false);
						newRule.addConsequent(blind, map.get(blind.toStringFcl()), false);
					}
					else
						return false;
				}

				else if(map.containsKey("condition") && e.getValue().equals("AND")){

					if(map.containsKey(temperature.toStringFcl()) && map.containsKey(ambient.toStringFcl()) 
							&& map.containsKey(blind.toStringFcl()) ){

						term1 = new RuleTerm(temperature, map.get(temperature.toStringFcl()), false);
						term2 = new RuleTerm(ambient, map.get(ambient.toStringFcl()), false);
						antecedentAnd = new RuleExpression(term1, term2, RuleConnectionMethodAndMin.get());
						newRule.setAntecedents(antecedentAnd);
						newRule.addConsequent(blind, map.get(blind.toStringFcl()), false);
					}

					else if(map.containsKey(temperature.toStringFcl()) && map.containsKey(blind.toStringFcl()) ){

						newRule.addAntecedent(temperature, map.get(temperature.toStringFcl()), false);
						newRule.addConsequent(blind, map.get(blind.toStringFcl()), false);
					}

					else if(map.containsKey(ambient.toStringFcl()) && map.containsKey(blind.toStringFcl()) ){

						newRule.addAntecedent(ambient, map.get(ambient.toStringFcl()), false);
						newRule.addConsequent(blind, map.get(blind.toStringFcl()), false);
					}
					else
						return false;
				}
				
			}
		}
		ruleBlock.add(newRule);
		ruleBlockMap.put("No1", ruleBlock);
		fis.getFunctionBlock("BLIND").setRuleBlocks(ruleBlockMap);
		return true;

	}

	/**
	 * add a new Rule.
	 * Input Map<Varaible,linguisticTerm>.
	 * @param newRuleMap
	 */
	public boolean addRule(Map<String,String> addRuleMap){

		if(checkifDuplicateRule(addRuleMap))
			return false;

		HashMap<String, RuleBlock> ruleBlockMap = fis.getFunctionBlock("BLIND").getRuleBlocks();
		ruleCount = getRuleCount();
		int newRuleCount = ruleCount + 1;
		RuleBlock ruleBlock = ruleBlockMap.get("No1");

		Rule newRule = new Rule(String.valueOf(newRuleCount), ruleBlock);

		Variable temperature = fis.getVariable("temperature");
		Variable ambient =  fis.getVariable("ambient");
		Variable blind =  fis.getVariable("blind");

		RuleTerm term1;
		RuleTerm term2;
		RuleExpression antecedentAnd;
		RuleExpression antecedentOr;


		Map<String,String> map = addRuleMap;

		for(Entry<String,String> e : map.entrySet()){

			if(map.containsKey("condition") && e.getValue().equals("AND")){

				if(map.containsKey(temperature.toStringFcl()) && map.containsKey(ambient.toStringFcl()) 
						&& map.containsKey(blind.toStringFcl()) ){

					term1 = new RuleTerm(temperature, map.get(temperature.toStringFcl()), false);
					term2 = new RuleTerm(ambient, map.get(ambient.toStringFcl()), false);
					antecedentAnd = new RuleExpression(term1, term2, RuleConnectionMethodAndMin.get());
					newRule.setAntecedents(antecedentAnd);
					newRule.addConsequent(blind, map.get(blind.toStringFcl()), false);
				}

				else if(map.containsKey(temperature.toStringFcl()) && map.containsKey(blind.toStringFcl()) ){

					newRule.addAntecedent(temperature, map.get(temperature.toStringFcl()), false);
					newRule.addConsequent(blind, map.get(blind.toStringFcl()), false);
				}

				else if(map.containsKey(ambient.toStringFcl()) && map.containsKey(blind.toStringFcl()) ){

					newRule.addAntecedent(ambient, map.get(ambient.toStringFcl()), false);
					newRule.addConsequent(blind, map.get(blind.toStringFcl()), false);
				}
				else
					return false;
			}

			else if(map.containsKey("condition") && e.getValue().equals("OR")){

				if(map.containsKey(temperature.toStringFcl()) && map.containsKey(ambient.toStringFcl()) 
						&& map.containsKey(blind.toStringFcl()) ){

					term1 = new RuleTerm(temperature, map.get(temperature.toStringFcl()), false);
					term2 = new RuleTerm(ambient, map.get(ambient.toStringFcl()), false);
					antecedentOr = new RuleExpression(term1, term2, RuleConnectionMethodOrMax.get());
					newRule.setAntecedents(antecedentOr);
					newRule.addConsequent(blind, map.get(blind.toStringFcl()), false);
				}

				else if(map.containsKey(temperature.toStringFcl()) && map.containsKey(blind.toStringFcl()) ){

					newRule.addAntecedent(temperature, map.get(temperature.toStringFcl()), false);
					newRule.addConsequent(blind, map.get(blind.toStringFcl()), false);
				}

				else if(map.containsKey(ambient.toStringFcl()) && map.containsKey(blind.toStringFcl()) ){

					newRule.addAntecedent(ambient, map.get(ambient.toStringFcl()), false);
					newRule.addConsequent(blind, map.get(blind.toStringFcl()), false);
				}
				else
					return false;
			}
		}

		ruleBlock.add(newRule);
		ruleBlockMap.put("No1", ruleBlock);
		fis.getFunctionBlock("BLIND").setRuleBlocks(ruleBlockMap);

		return true;
	}

	/**
	 * if duplicate rule return true else false.
	 * @param addRuleMap
	 * @return
	 */
	private boolean checkifDuplicateRule(Map<String, String> addRuleMap) {

		List<String> rules = getFuzzyRules();
		boolean valuePresent = false;

		for(String rule : rules){

			int valCount = 0;
		//	System.out.println(rule);

			if(addRuleMap.size() == 4){
			
			for(Entry<String,String> e : addRuleMap.entrySet()){

			//	System.out.println("MapValue = "+e.getValue());
				
				if(rule.contains(e.getValue())){
					valuePresent = true;
					valCount += 1;
					continue;
				}
				else{
					valuePresent = false;
					break;
				}
			}
			if(valuePresent && valCount == addRuleMap.size())
				return true;
		}
		
		else if(addRuleMap.size() == 3){
			
			for(Entry<String,String> e : addRuleMap.entrySet()){

				System.out.println("MapValue = "+e.getValue());
				
				if(rule.contains(e.getValue())){
					valuePresent = true;
					valCount += 1;
					continue;
				}
				else{
					
					if(e.getKey().equals("condition") && !rule.contains(e.getValue())){
						valuePresent = true;
						valCount += 1;
						continue;
					}
					
					valuePresent = false;
					break;
				}
			}
			if(valuePresent && valCount == addRuleMap.size())
				return true;
		}
			
		}
		
		return false;
	}

	/**
	 * get Variables.
	 * @return
	 */
	public Map<String,Variable> getVariables(){
		return fis.getFunctionBlock("BLIND").getVariables();
	}

	public Variable getVariable(String name){
		return fis.getFunctionBlock("BLIND").getVariable(name);
	}

	/**
	 * Remove a particular rule.
	 * @param object
	 * @return
	 */
	public boolean removeRule(Object object) {

		String ruleToBeRemoved = (String)object;

		List<Rule> currentRules = fis.getFunctionBlock("BLIND").getFuzzyRuleBlock("No1").getRules();
		Iterator<Rule> it = currentRules.iterator();

		while(it.hasNext()){

			if(it.next().getName().equals(ruleToBeRemoved)){
				it.remove();
				return true;
			}
		}
		return false;
	}

	/**
	 * Reset to initial rules.
	 * @return
	 */
	public List<String> resetToInitialRules() {
		fis = FIS.load(filename, true);
		return getFuzzyRules() ;
	}

}