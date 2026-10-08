package aztech.milite.datagen.loot;

public sealed interface MIBlockLoot {
    record DropSelf() implements MIBlockLoot {}

    record Ore(String loot) implements MIBlockLoot {}
}
