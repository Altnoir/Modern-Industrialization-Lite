package aztech.milite.materials.part;

public sealed interface PartEnglishNameFormatter {
    String format(String materialEnglishName);

    record Default(String name) implements PartEnglishNameFormatter {
        @Override
        public String format(String materialEnglishName) {
            if (!name.contains("%s")) {
                return materialEnglishName + " " + name;
            } else {
                return String.format(name, materialEnglishName);
            }
        }
    }

    record Overridden(String name) implements PartEnglishNameFormatter {
        @Override
        public String format(String materialEnglishName) {
            return name;
        }
    }
}
