package io.github.brenoepics.core.types.timeout;

import lombok.Getter;

import java.time.Instant;

@Getter
public class Timeout
{
    private final int id;
    private final Instant finish;
    
    public Timeout(int id, Instant time) {
        this.id = id;
        this.finish = time;
    }
}
