package aztech.milite.materials.part;

public sealed interface PartItemPathFormatter {
    static String idFromPath(String path) {
        return "moderntech:" + path;
    }

    String getPartItemPath(String materialName, PartKey partKey);

    String getPartItemTag(String materialName, PartKey partKey);

    default String getPartItemId(String materialName, PartKey partKey) {
        return "moderntech:" + getPartItemPath(materialName, partKey);
    }

    record Default() implements PartItemPathFormatter {
        @Override
        public String getPartItemPath(String materialName, PartKey partKey) {
            return materialName + "_" + partKey.key;
        }

        @Override
        public String getPartItemTag(String materialName, PartKey partKey) {
            if (MIParts.TAGGED_PARTS.contains(partKey)) {
                return "#c:%ss/%s".formatted(partKey.key, materialName);
            } else {
                return idFromPath(getPartItemPath(materialName, partKey));
            }
        }
    }

    record Overridden(String path, String tag) implements PartItemPathFormatter {
        @Override
        public String getPartItemPath(String materialName, PartKey partKey) {
            if (path.contains("%s")) {
                return String.format(path, materialName);
            } else {
                return path;
            }
        }

        @Override
        public String getPartItemTag(String materialName, PartKey partKey) {
            if (MIParts.TAGGED_PARTS.contains(partKey)) {
                if (tag.contains("%s")) {
                    return "#c:" + String.format(tag, materialName);
                } else {
                    return tag;
                }
            } else {
                return idFromPath(getPartItemPath(materialName, partKey));
            }
        }
    }
}
