package repository;

import java.util.*;

import com.googlecode.objectify.annotation.*;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;

@Cache
@Entity

public class Post {
	@Id Long id;
	String desc;
	@Index String UserAt;
	String photo;
	@Index Date date;
	String lieu;
	Long user;
	Long likes = 0L;
	HashSet<Long> likedBy = new HashSet<>();

	public Post(Long user, String description, String photo, String lieu) {
		this.user = user;
		this.UserAt = "";
		this.desc = description;
		this.photo = photo;
		this.date = new Date();
		this.lieu = lieu;
	}

	public Post(Long user, String description, String photo){
		this.user = user;
		this.UserAt = "";
		this.desc = description;
		this.date = new Date();
		this.photo = photo;
	}

	public Post(){};

	public Long getUser() {
		return user;
	}

	public void setUser(Long user) {
		this.user = user;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAt() {
		return UserAt;
	}

	public void setAt(String at) {
		this.UserAt = at;
	}

	public String getDescription() {
		return desc;
	}

	public void setDescription(String description) {
		this.desc = description;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getLieu() {
		return lieu;
	}

	public void setLieu(String lieu) {
		this.lieu = lieu;
	}
	public Long getNbrLike() {
		return likes;
	}

	public void setNbrLike(Long likes) {
		this.likes = likes;
	}

	public void addLike(Long u) {
		this.likedBy.add(u);
	}

	public void removeLike(Long u) {
		this.likedBy.remove(u);
	}

	public HashSet<Long> getLikedBy() {
		return likedBy;
	}


}
