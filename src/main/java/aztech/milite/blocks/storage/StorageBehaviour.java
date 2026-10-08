package aztech.milite.blocks.storage;

public abstract class StorageBehaviour<T> {
    public boolean isLockable() {
        return false;
    }

    public boolean isCreative() {
        return false;
    }

    public abstract long getCapacityForResource(T resource);

    public static <U> StorageBehaviour<U> creative() {
        return new StorageBehaviour<U>() {
            @Override
            public boolean isLockable() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return true;
            }

            @Override
            public long getCapacityForResource(U resource) {
                return Long.MAX_VALUE;
            }
        };
    }

    public static <U> StorageBehaviour<U> uniformQuantity(long quantity) {
        return new StorageBehaviour<U>() {
            @Override
            public boolean isLockable() {
                return true;
            }

            @Override
            public boolean isCreative() {
                return false;
            }

            @Override
            public long getCapacityForResource(U resource) {
                return quantity;
            }
        };
    }

    public boolean canInsert(T resource) {
        return true;
    }

    public boolean canExtract(T resource) {
        return true;
    }
}
