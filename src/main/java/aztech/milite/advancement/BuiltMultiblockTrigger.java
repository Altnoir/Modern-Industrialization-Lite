package aztech.milite.advancement;

import aztech.milite.MIAdvancementTriggers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class BuiltMultiblockTrigger extends SimpleCriterionTrigger<BuiltMultiblockTrigger.TriggerInstance> {
    public static Criterion<TriggerInstance> builtMultiblock(ResourceLocation multiblockId, @Nullable String shapeId) {
        return MIAdvancementTriggers.BUILT_MULTIBLOCK.get().createCriterion(new TriggerInstance(
                Optional.empty(),
                Optional.of(new MultiblockPredicate(multiblockId, Optional.ofNullable(shapeId)))));
    }

    public static Criterion<TriggerInstance> builtMultiblock(ResourceLocation multiblockId) {
        return builtMultiblock(multiblockId, null);
    }

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, ResourceLocation multiblockId, @Nullable String shapeId) {
        this.trigger(player, instance -> instance.matches(multiblockId, shapeId));
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<MultiblockPredicate> multiblock)
            implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        MultiblockPredicate.CODEC.optionalFieldOf("multiblock").forGetter(TriggerInstance::multiblock))
                .apply(instance, TriggerInstance::new));

        public boolean matches(ResourceLocation multiblockId, @Nullable String shapeId) {
            return multiblock.map(predicate -> predicate.matches(multiblockId, shapeId)).orElse(true);
        }
    }

    public record MultiblockPredicate(ResourceLocation id, Optional<String> shape) {
        public static final Codec<MultiblockPredicate> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(
                        ResourceLocation.CODEC.fieldOf("id").forGetter(MultiblockPredicate::id),
                        Codec.STRING.optionalFieldOf("shape").forGetter(MultiblockPredicate::shape))
                .apply(instance, MultiblockPredicate::new));

        public boolean matches(ResourceLocation multiblockId, @Nullable String shapeId) {
            return id.equals(multiblockId) && (shape.isEmpty() || shape.get().equals(shapeId));
        }
    }
}
