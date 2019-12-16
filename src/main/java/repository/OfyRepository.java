package repository;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

public class OfyRepository {
    static {
        ObjectifyService.register(User.class);
        ObjectifyService.register(Post.class);
        ObjectifyService.register(Like.class);
        ObjectifyService.register(ShardedLike.class);
    }

    public static Objectify ofy(){
        return ObjectifyService.ofy();
    }
}
