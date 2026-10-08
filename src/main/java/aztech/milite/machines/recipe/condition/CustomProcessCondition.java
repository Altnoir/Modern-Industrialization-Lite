package aztech.milite.machines.recipe.condition;

import aztech.milite.compat.kubejs.KubeJSProxy;
import aztech.milite.machines.recipe.MachineRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiPredicate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CustomProcessCondition implements MachineProcessCondition {
    static final Map<String, Definition> definitions = new HashMap<>();

    public static void onReload() {
        definitions.clear();

        KubeJSProxy.instance.fireCustomConditionEvent();
    }

    public static void register(String id, BiPredicate<MachineProcessCondition.Context, MachineRecipe> predicate, ItemStack icon,
            Component... description) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(icon);

        if (definitions.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate custom process condition definition: " + id);
        }

        if (description.length == 0) {
            throw new IllegalArgumentException("Custom process condition must have a description");
        }

        definitions.put(id, new Definition(predicate, icon, List.of(description)));
    }

    private record Definition(
            BiPredicate<MachineProcessCondition.Context, MachineRecipe> predicate,
            ItemStack icon, List<Component> description) {}

    static final MapCodec<CustomProcessCondition> CODEC = Codec.STRING.fieldOf("custom_id").xmap(CustomProcessCondition::new, c -> c.id);
    static final StreamCodec<RegistryFriendlyByteBuf, CustomProcessCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            c -> c.id,
            ItemStack.OPTIONAL_STREAM_CODEC,
            c -> c.icon,
            ComponentSerialization.TRUSTED_STREAM_CODEC.apply(ByteBufCodecs.list()),
            c -> c.description,
            CustomProcessCondition::new);

    public CustomProcessCondition(String id) {
        var definition = definitions.get(id);
        if (definition == null) {
            throw new IllegalArgumentException("Unknown custom process condition definition: " + id);
        }
        this.id = id;
        this.icon = definition.icon;
        this.description = definition.description;
    }

    public CustomProcessCondition(String id, ItemStack icon, List<Component> description) {
        this.id = id;
        this.icon = icon;
        this.description = description;
    }

    private final String id;
    private final ItemStack icon;
    private final List<Component> description;

    @Override
    public boolean canProcessRecipe(Context context, MachineRecipe recipe) {
        return definitions.get(id).predicate.test(context, recipe);
    }

    @Override
    public void appendDescription(List<Component> list) {
        list.addAll(description);
    }

    @Override
    public ItemStack icon() {
        return icon;
    }

    @Override
    public MapCodec<? extends MachineProcessCondition> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, ? extends MachineProcessCondition> streamCodec() {
        return STREAM_CODEC;
    }
}
