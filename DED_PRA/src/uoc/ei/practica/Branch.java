package uoc.ei.practica;

public class Branch extends IdentifiedObject {

	/**
	 * identificació del usuari
	 */
	private String idUser;

	/**
	 * identificació de la branca destinació
	 */
	private String idTargetBranch;

	/**
	 * identificació de la branca origen
	 */
	private String idSourceBranch;

	/**
	 * Constructor de la classe
	 */
	public Branch(String idSourceBranch, String idTargetBranch, String idUser) {
		this.idUser = idUser;
		this.idSourceBranch = idSourceBranch;
		this.idTargetBranch = idTargetBranch;
	}

	/**
	 * mètode que proporciona una representació en forma de string d'una branca
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(idSourceBranch);// .append(Messages.LS)
		return sb.toString();
	}
}
