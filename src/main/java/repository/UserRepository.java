package repository;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import static  repository.OfyRepository.ofy;

import java.util.ArrayList;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UserRepository {
	private final static Logger log = Logger.getLogger(UserRepository.class.getName());
	//Enregistrement de la classe persistable auprès de objectify
	private static UserRepository userObjectify = null;

	static {
		ObjectifyService.register(User.class);
	}

	private UserRepository() {};

	public static synchronized UserRepository getInstance() {
		if (null == userObjectify) {
			userObjectify = new UserRepository();
		}
		return userObjectify;
	}

	public Collection<User> listUser(int limit){
		Collection<User> users = ofy().load().type(User.class).limit(limit).list();
		return users;
	}

	public User addUser(User user) {
		ofy().save().entity(user).now();
		return user;
	}

	public void removeAll() {
		Iterable<Key<User>> users = ofy().load().type(User.class).keys();
		ofy().delete().keys(users);
	}

	public void removeUserById(Long userId) {
		ofy().delete().type(User.class).id(userId).now();
	}

	public void removeUser(User user) {
		ofy().delete().type(User.class).id(user.getId());
	}

	public User findUserByAt(String userAt) {
		return ofy().load().type(User.class).filter("UserAt ==", userAt).first().now();
	}

    public Collection<User> findUsersByAt(Collection<String> userAt, int limit) {
		Collection<User> listUser = null;
		for (String u: userAt) {
			listUser.add(ofy().load().type(User.class).filter("UserAt ==",u).first().now());
		}
		return listUser;
	}

	public User findById(Long id){
		return ofy().load().type(User.class).id(id).now();
	}

	public Collection<User> findUsersByName(String userName, int limit) {
		List<User> users = ofy().load().type(User.class).filter("UserName ==", userName).limit(limit).list();
		return users;
		// UserNam n'est pas unique alors on récupère une liste d'utilisateur avec le même nom
	}

	public User userUpdate(User user){

		User userUpdate = this.findById(user.getId());

		if(userUpdate != null){
			userUpdate.setUserAt(user.getUserAt());
			userUpdate.setFollowers(user.getFollowers());
			userUpdate.setFollowing(user.getFollowing());
			userUpdate.setUserName(user.getUserName());
		}
		ofy().save().entity(userUpdate).now();
		return userUpdate;
	}
}
