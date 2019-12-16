package repository;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.TxnType;
import org.w3c.dom.css.Counter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;

import static  repository.OfyRepository.ofy;

public class LikeRepository {
    public static final long INITIAL_SHARDS = 5;
    private static final long MAX_SHARDS = 15;

    public static final String SHARD_PREFIX = "counterShard-";

    private final Random generator = new Random();

    private String name;
    private String shardName;

    static {
        ObjectifyService.register(Like.class);
        ObjectifyService.register(ShardedLike.class);
    }

    private static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    private LikeRepository() {
    }

    private LikeRepository(String name) {
        this.name = name;
        this.shardName = SHARD_PREFIX + name + "#";
    }

    public void addShards(final long count) {
        Like cnt = new Like(this.name);
        Key counterKey = ofy().save().entity(cnt).now();
        incrementPropTx(counterKey, count, LikeRepository.INITIAL_SHARDS + count);
    }

    public String getShardName() {
        return shardName;
    }

    private int getShardCount(Like c) {
        Long result;
        if (c != null) {
            result = c.getShardCount();

        } else {
            result = LikeRepository.INITIAL_SHARDS;
        }
        return result.intValue();
    }

    private Key<ShardedLike> shardKey(String name) {
        Optional<Key<ShardedLike>> q = ofy().load().type(ShardedLike.class).keys().list().stream()
                .filter((key) -> (key.getName().equals(name)))
                .findFirst();

        if (q.isPresent()) {
            return q.get();
        } else {
            ShardedLike shard = new ShardedLike(name);
            return ofy().save().entity(shard).now();
        }
    }

    public final void increment() {
        Like c = getCounter(name);

        if (c != null) {
            int numShards = getShardCount(c);
            long shardNum = generator.nextInt(numShards);

            Key shardKey = shardKey(shardName + shardNum);
            ShardedLike shard = (ShardedLike) incrementPropTx(shardKey, 1L, 1L);
            c.addShard(shard);
        }
    }

    public final void decrement() {
        Like c = getCounter(name);

        if (c != null) {
            int numShards = getShardCount(c);
            long shardNum = generator.nextInt(numShards);

            Key shardKey = shardKey(shardName + shardNum);
            ShardedLike shard = (ShardedLike) incrementPropTx(shardKey, -1L, -1L);
            c.addShard(shard);
        }
    }

    private Object incrementPropTx(Key key, final long amount, final long init) {
        switch (key.getKind()) {
            case "Like":
            default:
                return ofy().execute(TxnType.REQUIRES_NEW, () -> {
                    Like cnt = (Like) ofy().load().key(key).now();

                    if (cnt != null) {
                        Long value = cnt.getShardCount() + amount > LikeRepository.MAX_SHARDS ? LikeRepository.MAX_SHARDS : cnt.getShardCount() + amount;
                        cnt.setShardCount(value);
                    } else {
                        cnt = new Like(key.getName());
                        cnt.setShardCount(init);
                    }
                    ofy().save().entity(cnt);
                    return cnt;
                });

            case "ShardedLike":
                return ofy().execute(TxnType.REQUIRES_NEW, () -> {
                    ShardedLike shard = (ShardedLike) ofy().load().key(key).now();

                    if (shard != null) {
                        shard.setCount(shard.getCount() + amount);
                    } else {
                        shard = new ShardedLike(key.getName());
                        shard.setCount(init);
                    }
                    ofy().save().entity(shard);
                    return shard;
                });
        }
    }

    public final long getCount() {
        Long sum = 0L;
        Like c = getCounter(name);
        if (c != null) {
            sum = c.getShards().stream()
                    .map(ShardedLike::getCount)
                    .reduce(sum, (accumulator, _item) -> accumulator + _item);
        }

        return sum;
    }

    public String getName() {
        return name;
    }

    public static Collection<Like> getAllCounter() {
        return ofy().load().type(Like.class).list();
    }

    private static Like getCounter(String name) {
        return ofy().load().type(Like.class).id(name).now();
    }

    public static LikeRepository getShardedCounter(String name) {
        return new LikeRepository(name);
    }

    public static LikeRepository createShardedCounter(String name) {
        LikeRepository sc = new LikeRepository(name);
        sc.addShards(0L);
        return sc;
    }

    public void deleteCounter() {
        Like c = (Like) ofy().load().type(Like.class).id(name).now();

        if (c != null) {
            HashSet<String> ids = new HashSet<>();
            c.getShards().forEach((shard) -> {
                ids.add(shard.getName());
            });

            ofy().delete().type(ShardedLike.class).ids(ids);
            ofy().delete().type(Like.class).id(name).now();
        }
    }

    public static void deleteAllCounter(){
        ofy().delete().keys(ofy().load().type(Like.class).keys());
        ofy().delete().keys(ofy().load().type(ShardedLike.class).keys());
    }
}
