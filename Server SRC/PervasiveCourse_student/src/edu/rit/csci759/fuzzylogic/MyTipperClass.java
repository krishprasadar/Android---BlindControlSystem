package edu.rit.csci759.fuzzylogic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.RuleBlock;
import net.sourceforge.jFuzzyLogic.rule.RuleExpression;
import net.sourceforge.jFuzzyLogic.rule.RuleTerm;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethod;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodAndMin;
import net.sourceforge.jFuzzyLogic.ruleConnectionMethod.RuleConnectionMethodOrMax;

public class MyTipperClass {

	private static String filename = "FuzzyLogic/tipper.fcl";
	private static FIS fis = FIS.load(filename, true);
	private static int ruleCount = 0;
	
public static void main(String[] args) throws Exception {

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

		fb.setVariable("temperature", 25);
		fb.setVariable("ambient", 87);

		fb.evaluate();

		fb.getVariable("blind").defuzzify();


		//System.out.println(fb);

		double max = 0;
		String term = "";

		for(Rule r : fis.getFunctionBlock("BLIND").getFuzzyRuleBlock("No1").getRules())
		{
			if(max < r.getDegreeOfSupport())
			{
				max = r.getDegreeOfSupport();
				term = r.getName();
			}

			System.out.println(r.getDegreeOfSupport());
		}
		System.out.println(term);
		String blindStatus = map.get(term);
		System.out.println(blindStatus);
		
		System.out.println("--------------");
		System.out.println(fis.getFunctionBlock("BLIND").getFuzzyRuleBlock("No1"));
		System.out.println("--------------");
		
		getFuzzyRules();
		System.out.println(fis.getVariable("blind").getLinguisticTerms());
		
		Map<Variable,String> addRuleMap = new HashMap<Variable,String>();
		Variable temperature = fis.getVariable("temperature");
		Variable ambient =  fis.getVariable("ambient");
		Variable blind =  fis.getVariable("blind");
	
		
		addRuleMap.put(temperature, "hot");
		addRuleMap.put(ambient, "bright");
		addRuleMap.put(blind, "open");
		addRule(addRuleMap);
		
		System.out.println("--------------");
		
		
		Map<Variable,String> updateRuleMap = new HashMap<Variable,String>();
		
		updateRuleMap.put(temperature, "hot");
		//updateRuleMap.put(ambient, "dark");
		updateRuleMap.put(blind, "open");
		
		Map<String, Map<Variable,String>> map1 = new HashMap<String, Map<Variable,String>>();
		map1.put("2",updateRuleMap);
		
		
		updateRule(map1);
	}

public static int getRuleCount(){
	return fis.getFunctionBlock("BLIND").getFuzzyRuleBlock("No1").getRules().size();
}

/**
 * update a fuzzy Rule.
 * pass(ruleNo, Map<Varaible,linguisticTerm> )
 * @param newRuleMap
 */
public static void updateRule(Map< String, Map<Variable,String>> updateRuleMap){


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
	
	for(Entry< String, Map<Variable,String>> e1 : updateRuleMap.entrySet()){
		
		Map<Variable,String> map = e1.getValue();
		
	
	if(map.containsKey(temperature) && map.containsKey(ambient) 
			&& map.containsKey(blind) ){
		
		term1 = new RuleTerm(temperature, map.get(temperature), false);
		term2 = new RuleTerm(ambient, map.get(ambient), false);
		antecedentAnd = new RuleExpression(term1, term2, RuleConnectionMethodAndMin.get());
		newRule.setAntecedents(antecedentAnd);
		newRule.addConsequent(blind, map.get(blind), false);
	}
	
	else if(map.containsKey(temperature) && map.containsKey(blind) ){

		newRule.addAntecedent(temperature, map.get(temperature), false);
		newRule.addConsequent(blind, map.get(blind), false);
	}
	
	else if(map.containsKey(ambient) && map.containsKey(blind) ){
		
		newRule.addAntecedent(temperature, map.get(temperature), false);
		newRule.addConsequent(blind, map.get(blind), false);
	}
	}
	ruleBlock.add(newRule);
	ruleBlockMap.put("No1", ruleBlock);
	
	fis.getFunctionBlock("BLIND").setRuleBlocks(ruleBlockMap);
	
	getFuzzyRules();
}

/**
 * add a new Rule.
 * Input Map<Varaible,linguisticTerm>.
 * @param newRuleMap
 */
public static void addRule(Map<Variable,String> newRuleMap){

	HashMap<String, RuleBlock> ruleBlockMap = fis.getFunctionBlock("BLIND").getRuleBlocks();
	ruleCount = getRuleCount();
	int newRuleCount = ruleCount + 1;
	RuleBlock ruleBlock = ruleBlockMap.get("No1");
	
	Rule rule6 = new Rule(String.valueOf(newRuleCount), ruleBlock);
	
	Variable temperature = fis.getVariable("temperature");
	Variable ambient =  fis.getVariable("ambient");
	Variable blind =  fis.getVariable("blind");
	
	RuleTerm term1 = new RuleTerm(temperature, newRuleMap.get(temperature), false);
	RuleTerm term2 = new RuleTerm(ambient, newRuleMap.get(ambient), false);

	RuleExpression antecedentAnd = new RuleExpression(term1, term2, RuleConnectionMethodAndMin.get());

	rule6.setAntecedents(antecedentAnd);
	rule6.addConsequent(blind, newRuleMap.get(blind), false);
	ruleBlock.add(rule6);
	ruleBlockMap.put("No1", ruleBlock);
	
	fis.getFunctionBlock("BLIND").setRuleBlocks(ruleBlockMap);
	
	System.out.println("_______________________________");
	getFuzzyRules();

	
	
}

public static  void getFuzzyRules(){
	
	for(Rule rule : fis.getFunctionBlock("BLIND").getRuleBlocks().get("No1").getRules())
		System.out.println(rule);
}

public static Map<String,Variable> getVariables(){
	return fis.getFunctionBlock("BLIND").getVariables();
}


}
