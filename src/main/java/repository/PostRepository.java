package repository;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import static  repository.OfyRepository.ofy;

import java.util.ArrayList;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PostRepository {
    private static final String LIKE_COUNTER_PREFIX = "like_counter#";

    //Enregistrement de la classe persistable auprès de objectify

	 private static PostRepository postObjectify = null;
		static {
	        ObjectifyService.register(Post.class);
	    }

		private PostRepository() {};

		public static synchronized PostRepository getInstance() {
			  if (null == postObjectify) {
				  postObjectify = new PostRepository();
					}
			  return postObjectify;
			 }

		public Collection<Post> listPosts(int limit){
			Collection<Post> posts = ofy().load().type(Post.class).limit(limit).list();
			return posts;
		}

		public Post addPost(Post post) {
			ofy().save().entity(post).now();
			String name = PostRepository.LIKE_COUNTER_PREFIX + post.getId();
			LikeRepository.createShardedCounter(name);
			return post;
		}

		public void removeMessage(Long postId) {
				ofy().delete().type(Post.class).id(postId).now();
    }

		public Post findPost(Long id) {
			Post post = ofy().load().type(Post.class).id(id).now();
			return post;
		}

        public Collection<Post> findPostRecentDate(String user, int limit) {
            return ofy().load().type(Post.class).filter("UserAt ==", user).limit(limit).list();
        } // Order by date aussi normalement

		public Collection<Post> findPostsByUser(String user) {
			Collection<Post> posts = ofy().load().type(Post.class).filter("UserAt ==", user).list();
		    return posts;
		}

		public void removeMessagesByUser(String userId) {
		    Collection<Post> posts = this.findPostsByUser(userId);
            HashSet<Long> ids = new HashSet<>();
            for (Post p: posts) {
                ids.add(p.getId());
                LikeRepository.getShardedCounter("like_counter#"+p.getId()).deleteCounter();
            }
			ofy().delete().type(Post.class).ids(ids).now();
		}

		public void removeAll() {
			Iterable<Key<Post>> posts = ofy().load().type(Post.class).keys();
			ofy().delete().keys(posts);
			LikeRepository.deleteAllCounter();
		}

		public Post postUpdate(Post post){
			if(post.getId() == null)
				return null;

			Post postUpdate = this.findPost(post.getId());

			if(postUpdate != null){
				postUpdate.setDescription(post.getDescription());
			}
			ofy().save().entity(postUpdate).now();
			return postUpdate;
		}

		public void addToLikeCounter(Post p) {
			String name = PostRepository.LIKE_COUNTER_PREFIX + p.getId();
			LikeRepository counter = LikeRepository.getShardedCounter(name);

			if (p.getNbrLike() + 1 <= p.getLikedBy().size()) {
				counter.increment();
				p.setNbrLike(counter.getCount());
			}

		}

		public void removeFromLikeCounter(Post p) {
			String name = PostRepository.LIKE_COUNTER_PREFIX + p.getId();
			LikeRepository counter = LikeRepository.getShardedCounter(name);

			if (p.getNbrLike() >= 1) {
				counter.decrement();
				p.setNbrLike(counter.getCount());
			}
		}
}

