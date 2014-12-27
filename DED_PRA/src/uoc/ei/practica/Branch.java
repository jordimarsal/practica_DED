package uoc.ei.practica;

import java.util.Date;

import uoc.ei.tads.DiccionariAVLImpl;
import uoc.ei.tads.Iterador;

public class Branch {

	/**
	 * identificació del usuari
	 */
	private String idUser;

	/**
	 * identificació de la branca destinació
	 */
	private String idBranch;

	/**
	 * identificació de la branca origen
	 */
	private String idSourceBranch;

	/**
	 * AVL de fitxers
	 */
	private DiccionariAVLImpl<String, File> files;

	/**
	 * Constructor de la classe
	 */
	public Branch(String idSource, String idTargetBranch, String idUser) {
		this.idUser = idUser;
		this.setIdSourceBranch(idSource);
		this.idBranch = idTargetBranch;
		this.files = new DiccionariAVLImpl<String, File>();
	}

	public String getIdBranch() {
		return idBranch;
	}

	public String getIdSourceBranch() {
		return idSourceBranch;
	}

	public void setIdSourceBranch(String idSourceBranch) {
		this.idSourceBranch = idSourceBranch;
	}

	/**
	 * mètode que proporciona una representació en forma de string d'una branca
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(idBranch);// .append(Messages.LS)
		return sb.toString();
	}

	/**
	 * mètode que afegex un nou fitxer sobre la branca
	 * 
	 * @param filePath
	 *            path del nou fitxer a afegir
	 * @return retorna un nou Fitxer en el sistema
	 */
	private File addFile(String filePath) {
		File f = new File(filePath);
		this.files.afegir(filePath, f);
		return f;
	}

	/**
	 * mètode que retorna un iterador de fitxers
	 * 
	 * @return retorna el iterador de fitxers
	 */
	public Iterador<File> ItFiles() {
		return this.files.elements();
	}

	/**
	 * mètode que retorna un fitxer del repositori
	 * 
	 * @param filePath
	 *            path del fitxer a cercar
	 * @return retorna el fitxer a cercar en cas que existeix
	 * @throws EIException
	 *             llença una excepció en el cas que el fitxer no existeixi
	 */
	public File getFile(String filePath) throws EIException {
		File f = this.files.consultar(filePath);
		if (f == null) {
			throw new EIException(Messages.FILE_NOT_FOUND);
		}
		return f;
	}

	/**
	 * mètode que afegeix una nova revisió sobre un fitxer
	 * 
	 * @param user
	 *            usuari que afegeix una nova revisió
	 * @param dateTime
	 *            data en la que es produeix una nova revisió
	 * @param filePath
	 *            fitxer sobre el que es realitza una nova revisió
	 * @param newSourceCode
	 *            codi de la nova revisió
	 * @param idRevision
	 *            identificador de la nova revisió
	 */
	public void addRevision(User user, Date dateTime, String filePath, String newSourceCode, int idRevision) {
		File f = this.files.consultar(filePath);
		if (f == null) {
			f = addFile(filePath);
		}
		f.addRevision(user, dateTime, newSourceCode, idRevision);
	}

	/**
	 * mètode que retorna una revisió d'un fitxer sobre una determinada revisió
	 * 
	 * @param filePath
	 *            path del fitxer
	 * @param idRevision
	 *            identificador de la revisió
	 * @return retorna la revisió a cercar o null en el cas que no existeixi
	 */
	public Revision getRevision(String filePath, int idRevision) {
		Revision r = null;
		File f = this.files.consultar(filePath);
		if (f != null) {
			r = f.getRevision(idRevision);
		}
		return r;
	}

	public void initAVLfiles(DiccionariAVLImpl<String, File> filesCheck) {
		files=filesCheck;
	}
}
