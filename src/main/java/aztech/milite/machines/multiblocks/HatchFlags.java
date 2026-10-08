package aztech.milite.machines.multiblocks;

import java.util.HashSet;
import java.util.Set;

public class HatchFlags {
    public static final HatchFlags NO_HATCH = new Builder().build();

    private final Set<HatchType> allowed;

    public HatchFlags(Set<HatchType> allowed) {
        this.allowed = Set.copyOf(allowed);
    }

    public boolean allows(HatchType type) {
        return allowed.contains(type);
    }

    public Set<HatchType> values() {
        return allowed;
    }

    public static class Builder {
        private final Set<HatchType> allowed = new HashSet<>();

        public Builder with(HatchType type) {
            allowed.add(type);
            return this;
        }

        public Builder with(HatchType... types) {
            for (HatchType type : types) {
                with(type);
            }

            return this;
        }

        public HatchFlags build() {
            return new HatchFlags(allowed);
        }
    }
}
