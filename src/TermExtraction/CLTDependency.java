package TermExtraction;

public class CLTDependency implements Comparable<CLTDependency> {
	private String dependant;
	private String governor;
	private String rel;
	private String sentence;
	
	private boolean useless;
	private boolean negative;
	private boolean positive;
	private boolean pnDecide;
	
	private boolean functional;
	private boolean structural;
	private boolean discriptive;
	private boolean command;
	private boolean interCLT;
	private boolean other;	
	private boolean typeDecide;

	public CLTDependency()
	{}
	
	public CLTDependency(String gov, String dep, String rel)
	{
		this.governor = gov;
		this.dependant = dep;
		this.rel = rel;
	}
	
	public CLTDependency(String gov, String dep, String rel, String sentence)
	{
		this(gov,dep,rel);
		this.sentence = sentence;
	}
	
	public String getDependant() {
		return dependant;
	}
	public void setDependant(String dependant) {
		this.dependant = dependant;
	}
	public String getGovernor() {
		return governor;
	}
	public void setGovernor(String governor) {
		this.governor = governor;
	}
	public String getRel() {
		return rel;
	}
	public void setRel(String rel) {
		this.rel = rel;
	}
	
	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public boolean isPositive() {
		return positive;
	}

	public void setPositive(boolean positive) {
		this.positive = positive;
	}
	
	
	public boolean isFunctional() {
		return functional;
	}

	public void setFunctional(boolean functional) {
		this.functional = functional;
	}
	
	
	public boolean isNegative() {
		return negative;
	}

	public void setNegative(boolean negative) {
		this.negative = negative;
	}

	public boolean isStructural() {
		return structural;
	}

	public void setStructural(boolean structural) {
		this.structural = structural;
	}
	
	public boolean isDiscriptive() {
		return discriptive;
	}

	public void setDiscriptive(boolean discriptive) {
		this.discriptive = discriptive;
	}
	
	public boolean isCommand() {
		return command;
	}

	public void setCommand(boolean command) {
		this.command = command;
	}
	
	@Override
	public int compareTo(CLTDependency dep) {
		if (this.dependant.equals(dep.dependant) 
				&& this.governor.equals(dep.governor)
				&& this.rel.equals(dep.rel))
			return 0;
		else
			return -1;
	}
	
	@Override 
	public boolean equals( Object oDep) 
	{
		CLTDependency dep = (CLTDependency)oDep;
		return this.compareTo(dep) == 0;
	}
	
	@Override 
	public String toString() 
	{	
		return governor + "," + dependant + "," + rel;
	}
	
	@Override
	public int hashCode()
	{
		return governor.hashCode()+dependant.hashCode()+rel.hashCode();
	}

	public boolean isUseless() {
		return useless;
	}

	public void setUseless(boolean useless) {
		this.useless = useless;
	}

	public boolean isPnDecide() {
		return pnDecide;
	}

	public void setPnDecide(boolean pnDecide) {
		this.pnDecide = pnDecide;
	}

	public boolean isInterCLT() {
		return interCLT;
	}

	public void setInterCLT(boolean interCLT) {
		this.interCLT = interCLT;
	}

	public boolean isOther() {
		return other;
	}

	public void setOther(boolean other) {
		this.other = other;
	}

	public boolean isTypeDecide() {
		return typeDecide;
	}

	public void setTypeDecide(boolean typeDecide) {
		this.typeDecide = typeDecide;
	}
		   
	
}
