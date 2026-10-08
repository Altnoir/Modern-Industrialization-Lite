package aztech.milite.materials.part;

/*
 * Key to be type safe in the material builder
 */
public final class PartKey implements Comparable<PartKey>, PartKeyProvider {
    public final String key;

    public PartKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PartKey partKey) {
            return partKey.key.equals(key);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return key;
    }

    @Override
    public int compareTo(PartKey o) {
        return key.compareTo(o.key);
    }

    @Override
    public PartKey key() {
        return this;
    }
}
