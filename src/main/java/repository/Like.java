package repository;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import java.util.HashSet;

@Entity
@Cache
public class Like {
    @Id
    private String name;
    private Long shardCount = LikeRepository.INITIAL_SHARDS;
    private HashSet<ShardedLike> shards = new HashSet<>();

    private Like() {
    }

    public Like(String counterName) {
        this.name = counterName;
    }

    public Long getShardCount() {
        return shardCount;
    }

    public void setShardCount(Long shardCount) {
        this.shardCount = shardCount;
    }

    public void addShard(ShardedLike shard) {
        this.shards.add(shard);
    }

    public String getName() {
        return name;
    }

    public void setName(String counterName) {
        this.name = counterName;
    }

    public HashSet<ShardedLike> getShards() {
        return shards;
    }

    public void setShards(HashSet<ShardedLike> shards) {
        this.shards = shards;
    }
}
