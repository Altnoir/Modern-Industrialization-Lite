package aztech.milite.items;

public final class SortOrderGroup {
    public static final class Counted {
        private final SortOrder sortOrder;
        private int localOrder = 0;

        public Counted(SortOrder sortOrder) {
            this.sortOrder = sortOrder;
        }

        /**
         * The group every entry of this counter is derived from, for callers that need to name the group rather than
         * hand out the next place in it.
         */
        public SortOrder root() {
            return sortOrder;
        }

        public SortOrder next() {
            return sortOrder.and(localOrder++);
        }
    }

    public static final class Parametrized<T extends Comparable<T>> {
        private final SortOrder sortOrder;

        public Parametrized(SortOrder sortOrder) {
            this.sortOrder = sortOrder;
        }

        /**
         * The group every entry of this group is derived from, for callers that need to name the group rather than
         * build an entry of it.
         */
        public SortOrder root() {
            return sortOrder;
        }

        public SortOrder create(T parameter) {
            return sortOrder.and(parameter);
        }
    }
}
