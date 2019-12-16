package endpoint;

import com.google.api.server.spi.config.*;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.common.collect.Maps;
import endpoints.repackaged.org.jose4j.json.internal.json_simple.JSONObject;
import repository.*;


import com.google.appengine.api.blobstore.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


@Api(
		name = "tinyinsta",
		version = "v1",
		namespace = @ApiNamespace(ownerDomain ="tinyinstaej.com", ownerName = "tinyinstaej.com", packagePath="services")
)


public class tinyInstaEndpoint {
	private static final Logger logger = Logger.getLogger(tinyInstaEndpoint.class.getName());


	// -------------------------------------------------------------- //
	// -- Méthodes pour les utilisateurs
	// -------------------------------------------------------------- //

	// -------------------------------------------------------------- //
	// -- Méthodes GET
	// -------------------------------------------------------------- //
	@ApiMethod(name = "listUser", httpMethod = ApiMethod.HttpMethod.GET, path="users/all")
	public Collection<User> listUser(@Nullable @Named("limit") @DefaultValue("10") int limit){
		return UserRepository.getInstance().listUser(limit);
	}

	@ApiMethod(name = "getUserByAt", httpMethod = ApiMethod.HttpMethod.GET, path="users/at/{userAt}")
	public User getUserByName(@Named("userAt") String userAt){
		return UserRepository.getInstance().findUserByAt(userAt);
	}

	@ApiMethod(name = "getUserById", httpMethod = ApiMethod.HttpMethod.GET, path="users/{id}")
	public User getUserById(@Named("id") Long userId){
		return UserRepository.getInstance().findById(userId);
	}

	@ApiMethod(name = "getUsersByName", httpMethod = ApiMethod.HttpMethod.GET, path="users/name/{userName}")
	public Collection<User> getUsersByName(@Named("userName") String userName, @Nullable @Named("limit")@DefaultValue("10") int limit){
		return UserRepository.getInstance().findUsersByName(userName, limit);
	}

	@ApiMethod(name = "getFollowers", httpMethod = ApiMethod.HttpMethod.GET, path = "users/{userId}/followers")
	public Collection<String> getFollowers(@Named("userId") Long userId) {
		User user = UserRepository.getInstance().findById(userId);
		if(user != null && user.getFollowers() != null){
			Collection<String> followerUserAt = user.getFollowers();
			return followerUserAt;
		}
		return null;
	}

	@ApiMethod(name = "getFollowing", httpMethod = ApiMethod.HttpMethod.GET, path = "users/{userId}/following")
	public Collection<String> getFollowing(@Named("userId") Long userId) {
		User user = UserRepository.getInstance().findById(userId);
		if(user != null){
			Collection<String> followingUserAt = user.getFollowing();
			return followingUserAt;
		}
		return null;
	}

	// -------------------------------------------------------------- //
	// -- Méthodes POST
	// -------------------------------------------------------------- //
	@ApiMethod(name = "addUser", httpMethod = ApiMethod.HttpMethod.POST, path="users/add/{userAt}/{userName}")
	public User addUser(@Named("userAt") String userAt, @Named("userName") String userName){
		User user = new User(userAt, userName);
		if(UserRepository.getInstance().findUserByAt(userAt) == null)
			return UserRepository.getInstance().addUser(user);
		else
			return UserRepository.getInstance().findUserByAt(userAt);
	}

	// -------------------------------------------------------------- //
	// -- Méthodes DELETE
	// -------------------------------------------------------------- //
	@ApiMethod(name = "removeUser", httpMethod = ApiMethod.HttpMethod.DELETE, path = "users/delete/{userId}")
	public void removeUser(@Named("userId") Long userId) {
		User user = UserRepository.getInstance().findById(userId);
		//On ne vérifié pas dans tous les cas on supprimera s'il le faut ou on ne fera rien si l'utilisateur ou les posts de l'utilisateur n'existe pas
		UserRepository.getInstance().removeUserById(userId);
		PostRepository.getInstance().removeMessagesByUser(user.getUserAt());
	}

	// -------------------------------------------------------------- //
	// -- Méthodes PUT
	// -------------------------------------------------------------- //
	@ApiMethod(name =  "updateUser", httpMethod = ApiMethod.HttpMethod.PUT, path = "users/update/{userId}")
	public User updateUser(@Named("userId") Long userId, @Nullable @Named("userName") String userName, @Nullable @Named("userAt") String userAt){
		User userUpdate = UserRepository.getInstance().findById(userId);

		if(userUpdate != null){
			if(userName != null){
				userUpdate.setUserName(userName);
			}
			if(userAt != null){
				userUpdate.setUserAt(userAt);
			}
		}
		return UserRepository.getInstance().userUpdate(userUpdate);
	}

	@ApiMethod(name = "follow", httpMethod = ApiMethod.HttpMethod.PUT, path = "users/{from}/follow/{to}")
	public User follow(@Named("from") Long userId , @Named("to") Long targetUser) {
		User userFollowed =  UserRepository.getInstance().findById(targetUser);
		User userFollowing = UserRepository.getInstance().findById(userId);
		if (userFollowed != null && userFollowing != null && !userFollowed.getFollowers().contains(userFollowing.getUserAt()) && !userFollowing.getFollowing().contains(userFollowed.getUserAt())) {
			userFollowed.addFollower(userFollowing.getUserAt());
			userFollowing.addFollowing(userFollowed.getUserAt());
			UserRepository.getInstance().userUpdate(userFollowed);
			UserRepository.getInstance().userUpdate(userFollowing);
		}else {
			this.unfollow(userId,targetUser);
		}
		return userFollowing;
	}

	@ApiMethod(name = "unfollow", httpMethod = ApiMethod.HttpMethod.PUT, path = "users/{from}/unfollow/{to}")
	public User unfollow(@Named("from") Long userId , @Named("to") Long targetUser){
			User userUnfollowing = UserRepository.getInstance().findById(userId);
		    User userUnfollowed = UserRepository.getInstance().findById(targetUser);

		if (userUnfollowed != null && userUnfollowing != null) {
			userUnfollowed.removeFollower(userUnfollowing.getUserAt());
			userUnfollowing.removeFollowing(userUnfollowed.getUserAt());
			UserRepository.getInstance().userUpdate(userUnfollowed);
			UserRepository.getInstance().userUpdate(userUnfollowing);
		}
		return userUnfollowing;
	}
	// -------------------------------------------------------------- //
	// -- Méthodes pour les posts
	// -------------------------------------------------------------- //

	// -------------------------------------------------------------- //
	// -- Méthodes GET
	// -------------------------------------------------------------- //
	@ApiMethod(name = "listPost", httpMethod = ApiMethod.HttpMethod.GET, path="posts/all")
	public Collection<Post> listPost(@Nullable @Named("limit") @DefaultValue("10")  int limit){
		return PostRepository.getInstance().listPosts(limit);
	}

	@ApiMethod(name = "getPost", httpMethod = ApiMethod.HttpMethod.GET, path="posts/{postId}")
	public Post getPost(@Named("postId") Long postId){
		return PostRepository.getInstance().findPost(postId);
	}

	@ApiMethod(name = "getPostByUserId", httpMethod = ApiMethod.HttpMethod.GET, path = "posts/user/{userAt}")
	public Collection<Post> getPostByUserId(@Named("userAt") String userAt) {
		return PostRepository.getInstance().findPostsByUser(userAt);
	}

	@ApiMethod(name = "getPostByUserMostRecent", httpMethod = ApiMethod.HttpMethod.GET, path = "posts/user/{userAt}/recent")
	public Collection<Post> getPostByUserMostRecent(@Named("userAt") String userAt, @Nullable @Named("limit") @DefaultValue("10") int limit) {
		return PostRepository.getInstance().findPostRecentDate(userAt, limit);
	}

	@ApiMethod(name = "getTimeline", httpMethod = ApiMethod.HttpMethod.GET, path = "posts/{userId}/timeline")
	public Collection<Post> getTimeline(@Named("userId") Long userId,@Nullable @Named("limit") @DefaultValue("10") int limit) {
		Collection<String> usersFollowed = this.getFollowing(userId);
		List<Post> posts = new ArrayList<>();
		for(String u: usersFollowed) {
			Collection<Post> post = PostRepository.getInstance().findPostRecentDate(u, limit);
			if(post != null)
				posts.addAll(post);
		}
		return posts;
	}

	@ApiMethod(name = "getLikedUser", httpMethod = ApiMethod.HttpMethod.GET, path = "posts/{userId}/liked")
	public User getLikedUser(@Named("userId") Long userId, @Named("postId") Long postId){
		Post post = PostRepository.getInstance().findPost(postId);
		if(post.getLikedBy().contains(userId))
			return UserRepository.getInstance().findById(userId);
		return null;
	}
	// -------------------------------------------------------------- //
	// -- Méthodes POST
	// -------------------------------------------------------------- //

	@ApiMethod(name = "addPost", httpMethod = ApiMethod.HttpMethod.POST, path="posts/{userId}/addPost/{desc}/{photo}")
	public Post addPost(@Named("userId") Long userId, @Named("desc") String description, @Named("photo") String photo, @Nullable @Named("lieu") String lieu ){
		Post post = null;
		User user = UserRepository.getInstance().findById(userId);
		if(lieu != null && user != null) {
			post = new Post(userId, description, photo, lieu);
			post.setAt(user.getUserAt());
		}
		else if (lieu == null && user != null) {
			post = new Post(userId, description, photo);
			post.setAt(user.getUserAt());
		}
		return PostRepository.getInstance().addPost(post);
	}
	// -------------------------------------------------------------- //
	// -- Méthodes PUT
	// -------------------------------------------------------------- //
	@ApiMethod(name = "updatePost", httpMethod = ApiMethod.HttpMethod.PUT, path = "posts/{postId}/updatePost")
	public Post updatePost(@Named("postId") Long postId,@Nullable @Named("description") String description){
		Post postUpdate = PostRepository.getInstance().findPost(postId);

		if(postUpdate != null){
			if(description != null){
				postUpdate.setDescription(description);
			}
		}
		return PostRepository.getInstance().postUpdate(postUpdate);
	}

    @ApiMethod(name = "like", httpMethod = ApiMethod.HttpMethod.PUT, path = "user/{userId}/like/{postId}")
    public Post like(@Named("userId") Long userId,@Named("postId") Long postId) {
        User user = UserRepository.getInstance().findById(userId);
        Post post = PostRepository.getInstance().findPost(postId);
        if (user != null && post != null) {
            post.addLike(user.getId());
            PostRepository.getInstance().addToLikeCounter(post);
            PostRepository.getInstance().postUpdate(post);
            return post;
        }
        return null;
    }

    @ApiMethod(name = "like", httpMethod = ApiMethod.HttpMethod.PUT, path = "user/{userId}/unlike/{postId}")
    public Post unlike(@Named("userId") Long userId,@Named("postId") Long postId) {
        User user = UserRepository.getInstance().findById(userId);
        Post post = PostRepository.getInstance().findPost(postId);
        if (user != null && post != null) {
            post.removeLike(user.getId());
            PostRepository.getInstance().removeFromLikeCounter(post);
            PostRepository.getInstance().postUpdate(post);
            return post;
        }
        return null;
    }

    // -------------------------------------------------------------- //
	// -- Méthodes DELETE
	// -------------------------------------------------------------- //
	@ApiMethod(name = "removePost", httpMethod = ApiMethod.HttpMethod.DELETE, path = "posts/remove/{postId}")
	public void removePost(@Named("postId") Long postId){
		PostRepository.getInstance().removeMessage(postId);
	}

	// -------------------------------------------------------------- //
	// -- Méthodes générales
	// -------------------------------------------------------------- //

	// -------------------------------------------------------------- //
	// -- Méthodes DELETE
	// -------------------------------------------------------------- //

	//On supprime toutes les données stockées dans le datastore
	@ApiMethod(name = "purgeDatastore", httpMethod = ApiMethod.HttpMethod.DELETE, path = "deleteAll")
	public void removeAllUser(){
		UserRepository.getInstance().removeAll();
		PostRepository.getInstance().removeAll();
	}

    // -------------------------------------------------------------- //
    // -- Méthodes TEST/BENCHMARK
    // -------------------------------------------------------------- //
	@ApiMethod(name = "testFollower", httpMethod = ApiMethod.HttpMethod.GET, path = "test")
    public Map<String, Object> test(@Nullable @Named("nbrfollower") @DefaultValue("10") int nbrFollowers){
		Map<String, Object> json = Maps.newHashMap();
		long tempsTotal = 0;
		long endTime = 0;
		long startTime = 0;
        for(int i =0; i <= 30; i++) {
			User userTest = new User("test", "testUsername");
			for (int j = 0; j < nbrFollowers; j++) {
				User userTestFollower = new User("testFollower" + j, "testFollowerUserAt" + j);
				userTest.addFollower(userTestFollower.getUserAt());
			}
			Post post = new Post(userTest.getId(), "Description test", "Photo exceptionelle");
			startTime = System.currentTimeMillis();
			PostRepository.getInstance().addPost(post);
			endTime = System.currentTimeMillis();
			tempsTotal += (endTime - startTime);
		}
		json.put("time : ", tempsTotal/30);
		return json;
	}

	@ApiMethod(name = "testPost", httpMethod = ApiMethod.HttpMethod.GET, path = "test1")
	public Map<String, Object> test1(@Nullable @Named("nbrpost") @DefaultValue("10") int nbrPost) {
		Map<String, Object> json = Maps.newHashMap();
		long tempsTotal = 0;
		long endTime = 0;
		long startTime = 0;
		User userTest = new User("test", "testUsername");
		for (int j = 0; j < nbrPost; j++) {
			Post post = new Post(userTest.getId(), "Description test" + j, "Photo exceptionelle" + j);
			PostRepository.getInstance().addPost(post);
		}
		startTime = System.currentTimeMillis();
		PostRepository.getInstance().listPosts(nbrPost);
		endTime = System.currentTimeMillis();
		tempsTotal = (endTime - startTime);
		json.put("time : ", tempsTotal);
		return json;
	}

	@ApiMethod(name = "testLike", httpMethod = ApiMethod.HttpMethod.GET, path = "test2")
	public Map<String, Object> test2(@Nullable @Named("nbrLike") @DefaultValue("10") int nbrLike){
		Map<String, Object> json = Maps.newHashMap();
		long tempsTotal = 0;
		long endTime = 0;
		long startTime = 0;
		User userTest = new User("test", "testUsername");
		Post post = new Post(userTest.getId(), "Description test", "Photo exceptionelle");

		for (int i = 0; i <= 30; i++) {
			PostRepository.getInstance().addPost(post);
			for (int j = 0; j < nbrLike; j++) {
				User userTestFollower = new User("testLike" + i, "testLike" + i);
				UserRepository.getInstance().addUser(userTestFollower);
				startTime = System.currentTimeMillis();
				this.like(userTestFollower.getId(), post.getId());
				endTime = System.currentTimeMillis();
			}
			tempsTotal += (endTime - startTime);
		}
		json.put("time : ", tempsTotal/30);
		return json;
	}
}