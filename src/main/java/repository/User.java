package repository;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import com.googlecode.objectify.annotation.*;


@Cache
@Entity

public class User {
	@Id Long id;
	@Index String UserAt;
	@Index String UserName;
	HashSet<String> Followers = new HashSet<>();
	HashSet<String> Following  = new HashSet<>();

	public User(String UserAt, String UserName) {
		this.UserAt = UserAt;
		this.UserName = UserName;
	}

	public User() {};

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserAt() {
		return UserAt;
	}

	public void setUserAt(String userAt) {
		this.UserAt = userAt;
	}

	public void setFollowers(HashSet<String> f) {
		this.Followers = f;
	}

	public void setFollowing(HashSet<String> f) {
		this.Following = f;
	}

	public HashSet<String> getFollowers() {
		return this.Followers;
	}

	public HashSet<String> getFollowing(){
		return this.Following;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		this.UserName = userName;
	}

	public void addFollower(String follower) {
		if(this.Followers == null)
			this.Followers = new HashSet<>();
		this.Followers.add(follower);
	}

	public void removeFollower(String follower) {
		this.Followers.remove(follower);
	}

	public void addFollowing(String following) {
		if(this.Following == null)
			this.Following = new HashSet<>();
		this.Following.add(following);
	}

	public void removeFollowing(String following){
		this.Following.remove(following);
	}
}
