package uoc.ei.practica;

import uoc.ei.tads.DiccionariAVLImpl;
import uoc.ei.tads.Iterador;
import uoc.ei.tads.LlistaEncadenada;

/**
 * Classe que modela una entitat Repositori
 */
public class Repository {

	/**
	 * llista encadenada de branques
	 */
	private LlistaEncadenada<Branch> branches;

	/**
	 * path del repositori
	 */
	private String path;

	/**
	 * descripció del repositori
	 */
	private String description;

	/**
	 * identificació del repositori
	 */
	private String idRepository;

	/**
	 * llista encadenada de grups d'un repositori
	 */
	private IdentifiedList<Group> groups;

	/**
	 * indicador de l'activitat del repositori
	 */
	private int activity;

	/**
	 * Constructor de la classe
	 * 
	 * @param idRepository
	 *            identificador del repositori
	 * @param path
	 *            path del repositori
	 * @param description
	 *            descripció del repositori
	 */
	public Repository(String idRepository, String path, String description) {
		this.groups = new IdentifiedList<Group>();
		this.branches = new LlistaEncadenada<Branch>();
		this.path = path;
		this.description = description;
		this.idRepository = idRepository;
		this.addBranch(new Branch("Trunk", "Trunk", "TR"));
	}

	/**
	 * mètode que proprociona una representació en forma de string d'un repositori
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("path: ").append(this.path).append(Messages.LS);
		sb.append("description: ").append(this.description).append(Messages.LS);
		sb.append("activity: ").append(this.activity).append(Messages.LS);
		sb.append("branches: ");

		if (!this.branches.estaBuit()) {
			sb.append(Messages.LS);
			Iterador<Branch> it = this.branches.elements();
			while (it.hiHaSeguent()) {
				sb.append(Messages.PREFIX).append(it.seguent());
			}
		} else {
			sb.append(Messages.LS);
			sb.append("    Trunk ");
		}
		if (!this.groups.estaBuit()) {
			sb.append(Messages.LS);
			sb.append("groups: ").append(Messages.LS);

			Iterador<Group> it = this.groups.elements();
			while (it.hiHaSeguent()) {
				sb.append(Messages.PREFIX).append(it.seguent());
			}
		}
		sb.append(Messages.LS);
		return sb.toString();
	}

	/**
	 * mètode que afegeix un nou grup a un repositori
	 * 
	 * @param group
	 */
	public void addGroup(Group group) {
		this.groups.afegirAlFinal(group);
	}

	/**
	 * mètode que afegeix una nova branca a un repositori
	 * 
	 * @param branch
	 */
	public void addBranch(Branch branch) {
		initialiceBranch(branch);
		this.branches.afegirAlFinal(branch);

		// System.out.println("added " + branch.getIdBranch());
	}

	/**
	 * mètode que inicialitza una nova branca,
	 * copia només la última revisió de cada arxiu de la branca font
	 * 
	 * @param branch
	 */
	private void initialiceBranch(Branch branch) {
		Branch source = getBranch(branch.getIdSourceBranch());
		DiccionariAVLImpl<String, File> filesCheck = new DiccionariAVLImpl<String, File>();

		if (source != null) {
			final Iterador<File> it = source.ItFiles();

			Revision raux = null;
			while (it.hiHaSeguent()) {
				File fi = it.seguent();

				Iterador<Revision> itreve = fi.ItRevisions();
				while (itreve.hiHaSeguent()) {
					Revision revi = itreve.seguent();
					raux = revi;
				}
				if (raux != null) {
					String nounom = fi.getIdentifier();
					File fnou = new File(nounom);
					fnou.addRevision(raux);
					filesCheck.afegir(nounom, fnou);
				}
			}
			branch.initAVLfiles(filesCheck);
		}
	}

	/**
	 * mètode que verifica que algun grups de l'usuari està a la llista de grups del repositori
	 * 
	 * @param user
	 *            la instància d'usuari
	 * @return boolean
	 *         cert/fals
	 */
	public boolean hasPermission(User user) {
		boolean found = false;
		Group g = null;

		Iterador<Group> groupsUser = user.groups();
		while (groupsUser.hiHaSeguent() && !found) {
			g = groupsUser.seguent();
			found = (this.groups.getIdentifiedObject(g.getIdentifier()) != null);
		}

		return found;
	}

	/**
	 * mètode que retorna un iterador de branques
	 * 
	 * @return retorna el iterador de branques
	 */
	public Iterador<Branch> ItBranches() {
		return this.branches.elements();
	}

	/**
	 * mètode que incrementa en una unitat l'activitat d'un repositori
	 */
	public void incActivity() {
		this.activity++;
	}

	/**
	 * mètode que retorna l'activitat d'un repositori
	 * 
	 * @return
	 */
	public int getActivity() {
		return this.activity;
	}

	/**
	 * mètode que retorna una branca d'un repositori
	 * 
	 * @param idBranch
	 *            identificador de la branca
	 * @return retorna la branca a cercar o null en el cas que no existeixi
	 */
	public Branch getBranch(String idBranch) {
		Branch b = null;
		Iterador<Branch> it = branches.elements();
		while (it.hiHaSeguent()) {
			Branch bF = it.seguent();
			if (bF.getIdBranch().equals(idBranch)) {
				b = bF;
			}
		}
		return b;
	}

}
