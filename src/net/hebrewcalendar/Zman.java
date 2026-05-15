package net.hebrewcalendar;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/** A halachic time with zero or more flags describing how it was calculated. */
public class Zman {

    private final ZonedDateTime time;
    private final EnumSet<Zmanim.Flag> flags;

    public Zman(ZonedDateTime time, Zmanim.Flag... flags) {
        this.time = time;
        this.flags = flags.length > 0
            ? EnumSet.copyOf(Arrays.asList(flags))
            : EnumSet.noneOf(Zmanim.Flag.class);
    }

    public ZonedDateTime getTime() { return time; }

    public boolean hasFlag(Zmanim.Flag flag) { return flags.contains(flag); }

    public Set<Zmanim.Flag> getFlags() { return Collections.unmodifiableSet(flags); }
}
