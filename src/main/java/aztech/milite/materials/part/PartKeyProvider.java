package aztech.milite.materials.part;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface PartKeyProvider {
    /**
     * @return The name of this part, for example "ingot" or "dust".
     */
    PartKey key();

    static List<PartKey> of(PartKeyProvider... providers) {
        return Arrays.stream(providers).map(PartKeyProvider::key).collect(Collectors.toList());
    }
}
