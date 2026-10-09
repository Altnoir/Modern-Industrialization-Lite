# Adding machine tiers
A tier groups machines by the amount of FE/t they are built for.
Tiers are not voltages: MI Lite ships no cables and no transformers,
machines simply expose NeoForge's standard energy capability.

Registering a tier also registers the [machine casing](ADDING_MACHINES.md#adding-new-casing-types) that machines of this tier use,
so you will need to provide the corresponding casing textures.

Use the `MIRegistrationEvents.registerCableTiers` startup event:
```js
MIRegistrationEvents.registerCableTiers(event => {
    event.register(
        // Internal name for the tier, and the name of its machine casing.
        "iv",
        // Short English name for the tier.
        "IV",
        // Long English name for the tier.
        "Insane Voltage",
        // Base FE/t of the tier: how much FE/t a machine of this tier is built for.
        65536,
        // Resource location of the machine hull that upgrades a machine to this tier.
        "mypack:iv_machine_hull",
    );
});
```

The tier name, its FE/t value and its machine hull must all be unique.

A machine of a tier can hold up to eight times the FE/t of its tier in its internal buffer.

Add the `cable_tier_short.milite.<name>` and `cable_tier_long.milite.<name>` translation keys so that the tier name shows up in game.
Use `event.get("aluminum")` to read an already-registered tier, for example to derive your FE/t value from it.

Energy input and output hatches are not registered automatically for a new tier:
see the page on [adding hatches](ADDING_HATCHES.md).
