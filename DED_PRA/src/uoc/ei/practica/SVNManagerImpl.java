package uoc.ei.practica;

import java.util.Date;

import uoc.ei.tads.DiccionariAVLImpl;
import uoc.ei.tads.Iterador;
import uoc.ei.tads.LlistaEncadenada;
import uoc.ei.tads.TaulaDispersio;

public class SVNManagerImpl implements SVNManager {

	/**
	 * AVL de repositoris
	 */
	private TaulaDispersio<String, Repository> repositories;
	private int len;

	/**
	 * AVL d'usuaris
	 */
	private DiccionariAVLImpl<String, User> users;

	/**
	 * llista encadenada de groups
	 * 
	 */
	private DiccionariAVLImpl<String, Group> groups;

	/**
	 * apuntador al repositori més actiu
	 */
	public static Repository mostActiveRepository;

	/**
	 * apuntador al grup més actiu
	 */
	public static Group mostActiveGroup;

	public SVNManagerImpl() {
		this.repositories = new TaulaDispersio<String, Repository>();
		this.len = 0;
		this.users = new DiccionariAVLImpl<String, User>();
		this.groups = new DiccionariAVLImpl<String, Group>();
		SVNManagerImpl.mostActiveRepository = null;
		SVNManagerImpl.mostActiveGroup = null;
	}

	@Override
	public void addUser(String idUser, String email, String password) throws EIException {
		if (idUser.length() <= 1 || email.length() <= 1) {
			throw new EIException(Messages.ARGUMENT_ERROR);
		}

		User user = this.users.consultar(idUser);
		if (user == null) {
			user = new User(idUser, email, password);
			this.users.afegir(idUser, user);
		} else {
			user.update(email, password);
		}

	}

	@Override
	public void addGroup(String idGroup, String name) throws EIException {
		if (idGroup.length() <= 1 || name.length() <= 1) {
			throw new EIException(Messages.ARGUMENT_ERROR);
		}
		this.groups.afegir(idGroup, new Group(idGroup, name));
	}

	@Override
	public void groupAddUser(String idGroup, String idUser) throws EIException {
		if (idGroup.length() <= 1 || idUser.length() <= 1) {
			throw new EIException(Messages.ARGUMENT_ERROR);
		}
		Group g = this.groups.consultar(idGroup);
		User u = this.users.consultar(idUser);
		if (u == null) {
			throw new EIException(Messages.USER_NOT_FOUND);
		}
		if (g == null) {
			throw new EIException(Messages.GROUP_NOT_FOUND);
		}
		u.addGroup(g);
	}

	private void updateMostActiveRepository(Repository repo) {
		if (mostActiveRepository == null || mostActiveRepository.getActivity() < repo.getActivity()) {
			mostActiveRepository = repo;
		}
	}

	@Override
	public Group mostActiveGroup() throws EIException {
		if (mostActiveGroup == null) {
			throw new EIException(Messages.NO_GROUPS);
		}
		return SVNManagerImpl.mostActiveGroup;
	}

	@Override
	public Iterador<User> users() throws EIException {
		if (this.users.estaBuit()) {
			throw new EIException(Messages.NO_USERS);
		}
		return this.users.elements();
	}

	@Override
	public Iterador<Group> groups() throws EIException {
		if (this.groups.estaBuit()) {
			throw new EIException(Messages.NO_GROUPS);
		}
		return this.groups.elements();
	}

	@Override
	public Iterador<Repository> repositories() throws EIException {
		if (this.len == 0) {
			throw new EIException(Messages.NO_REPOSITORIES);
		}
		Iterador<Repository> it = this.repositories.elements();
		return it;
	}

	@Override
	public void addRepository(String idRepository, String path, String description) throws EIException {
		if (idRepository.length() <= 1) {
			throw new EIException(Messages.ARGUMENT_ERROR);
		}
		Repository repository = new Repository(idRepository, path, description);
		this.repositories.afegir(idRepository, repository);
		len++;
	}

	@Override
	public void repositoryAddGroup(String idRepository, String idGroup) throws EIException {
		if (idRepository.length() <= 1 || idGroup.length() <= 1) {
			throw new EIException(Messages.ARGUMENT_ERROR);
		}
		Repository repository = this.getRepository(idRepository);
		Group group = this.groups.consultar(idGroup);
		if (group == null) {
			throw new EIException(Messages.GROUP_NOT_FOUND);
		}
		repository.addGroup(group);
	}

	@Override
	public void commit(String idRepository, String idBranch, int idRevision, String idUser, Date dateTime,
			String filePath, String newSourceCode) throws EIException {
		Repository repo = this.getRepository(idRepository);

		if (repo == null) {
			throw new EIException(Messages.REPOSITORY_NOT_FOUND);
		}
		User user = this.users.consultar(idUser);
		if (user == null) {
			throw new EIException(Messages.USER_NOT_FOUND);
		}
		if (repo.hasPermission(user)) {
			Branch bran = repo.getBranch(idBranch);
			if (bran != null) {
				Revision r = bran.getRevision(filePath, idRevision);
				if (r != null) {
					throw new EIException(Messages.REVISION_ALREADY_EXISTS);
				}
				bran.addRevision(user, dateTime, filePath, newSourceCode, idRevision);
				repo.incActivity();
				user.incActivity();
				updateMostActiveRepository(repo);
			} else {
				throw new EIException(Messages.BRANCH_NOT_FOUND);
			}
		} else {
			throw new EIException(Messages.NO_PRIVILEGES);
		}

	}

	@Override
	public Iterador<Revision> checkout(String idRepository, String idBranch, int idRevision) throws EIException {
		Repository repo = this.getRepository(idRepository);
		if (repo == null) {
			throw new EIException(Messages.REPOSITORY_NOT_FOUND);
		}
		Branch bran = repo.getBranch(idBranch);
		if (bran == null) {
			throw new EIException(Messages.BRANCH_NOT_FOUND);
		}

		Iterador<File> it = bran.ItFiles();
		if (it == null) {
			throw new EIException(Messages.NO_FILES);
		}

		LlistaEncadenada<Revision> reviCheck = new LlistaEncadenada<Revision>();
		Revision raux = null;
		Revision revi = null;
		while (it.hiHaSeguent()) {
			File fi = it.seguent();

			Iterador<Revision> itreve = fi.ItRevisions();
			while (itreve.hiHaSeguent()) {
				revi = itreve.seguent();
				if (revi.getIdRevision() <= idRevision) {
					raux = revi;
				}
			}
			if (raux != null) {
				reviCheck.afegirAlFinal(raux);
				raux = null;
				revi = null;
			}
		}
		if (reviCheck.estaBuit()) {
			throw new EIException(Messages.NO_REVISION);
		}
		return reviCheck.elements();

	}

	@Override
	public Revision getFile(String idRepository, String idBranch, int idRevision, String filePath) throws EIException {
		Repository repo = this.getRepository(idRepository);
		if (repo == null) {
			throw new EIException(Messages.REPOSITORY_NOT_FOUND);
		}
		Branch bran = repo.getBranch(idBranch);
		if (bran == null) {
			throw new EIException(Messages.BRANCH_NOT_FOUND);
		}

		File f = bran.getFile(filePath);
		if (f == null) {
			throw new EIException(Messages.FILE_NOT_FOUND);
		}

		Revision revision = f.getRevision(idRevision);
		if (revision == null) {
			throw new EIException(Messages.REVISION_NOT_FOUND);
		}

		return revision;
	}

	@Override
	public Repository getRepository(String idRepository) throws EIException {
		Repository ret = null;
		ret = this.repositories.consultar(idRepository);
		if (ret == null) {
			throw new EIException(Messages.REPOSITORY_NOT_FOUND);
		}
		return ret;
	}

	@Override
	public Iterador<Branch> branches(String idRepository) throws EIException {
		Repository repo = this.repositories.consultar(idRepository);
		if (repo == null) {
			throw new EIException(Messages.REPOSITORY_NOT_FOUND);
		}
		return repo.ItBranches();
	}

	@Override
	public void branch(String idRepository, String idSourceBranch, String idTargetBranch, String idUser)
			throws EIException {
		Repository repo = this.repositories.consultar(idRepository);
		if (repo == null) {
			throw new EIException(Messages.REPOSITORY_NOT_FOUND);
		}
		User user = this.users.consultar(idUser);
		if (user == null) {
			throw new EIException(Messages.USER_NOT_FOUND);
		}
		if (repo.getBranch(idSourceBranch) == null) {
			throw new EIException(Messages.BRANCH_NOT_FOUND);
		}
		if (repo.hasPermission(user)) {
			repo.addBranch(new Branch(idSourceBranch, idTargetBranch, idUser));
		} else {
			throw new EIException(Messages.NO_PRIVILEGES);
		}

	}

	@Override
	public void merge(String idRepository, String idSourceBranch, String idTargetBranch, String idUser)
			throws EIException {
		Repository repo = this.repositories.consultar(idRepository);
		if (repo == null) {
			throw new EIException(Messages.REPOSITORY_NOT_FOUND);
		}
		User user = this.users.consultar(idUser);
		if (user == null) {
			throw new EIException(Messages.USER_NOT_FOUND);
		}
		Branch bSource = repo.getBranch(idSourceBranch);
		if (bSource == null) {
			throw new EIException(Messages.BRANCH_NOT_FOUND);
		}
		Branch bTarget = repo.getBranch(idTargetBranch);
		if (bTarget == null) {
			throw new EIException(Messages.BRANCH_NOT_FOUND);
		}
		if (!bSource.getIdSourceBranch().equals(bTarget.getIdBranch())) {
			throw new EIException(Messages.MERGE_CONFLICT);
		}
		if (repo.hasPermission(user)) {
			Iterador<File> itS = bSource.ItFiles();
			Revision reSource = null;
			Revision reTarget = null;
			Revision revi = null;
			File exisFaT = null;

			while (itS.hiHaSeguent()) {
				File fiS = itS.seguent();
				Iterador<Revision> itreve = fiS.ItRevisions();
				while (itreve.hiHaSeguent()) {
					revi = itreve.seguent();
				}
				reSource = revi;
				// cerquem aquest file a la branca de Target
				if (bTarget.hasFile(fiS.getIdentifier())) {
					exisFaT = bTarget.getFile(fiS.getIdentifier());
				}
				if (exisFaT != null) {
					// darrera versió
					Iterador<Revision> itT = exisFaT.ItRevisions();
					while (itT.hiHaSeguent()) {
						revi = itT.seguent();
					}
					reTarget = revi;
					if (reTarget.getIdRevision() < reSource.getIdRevision()) {
						bTarget.addRevision(fiS.getIdentifier(), reSource);
					}
				} else {
					bTarget.addRevision(fiS.getIdentifier(), reSource);
				}
				exisFaT = null;
				revi = null;
				reSource = null;
				reTarget = null;
			}
		} else {
			throw new EIException(Messages.NO_PRIVILEGES);
		}
	}

	@Override
	public Repository mostActiveRepository() throws EIException {
		if (mostActiveRepository == null) {
			throw new EIException(Messages.NO_REPOSITORIES);
		}
		return mostActiveRepository;
	}

}
