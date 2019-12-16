package repository;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.Objects;

@Cache
@Entity
public class ShardedLike {
    @Id
    private String name;
    private Long count;

    private ShardedLike() {
    }

    public ShardedLike(String name) {
        this.name = name;
        this.count = 0L;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}