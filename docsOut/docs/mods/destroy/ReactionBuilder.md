# ReactionBuilder

## Importing the class

It might be required for you to import the package if you encounter any issues (like casting an Array), so better be safe than sorry and add the import at the very top of the file.
```zenscript
import mods.destroy.ReactionBuilder;
```


## Methods

:::group{name=acid}

Return Type: [Reaction](/mods/destroy/Reaction)

```zenscript
ReactionBuilder.acid(acid as Molecule, conjugateBase as Molecule, pKa as float) as Reaction
```

|   Parameter   |                Type                |
|---------------|------------------------------------|
| acid          | [Molecule](/mods/destroy/Molecule) |
| conjugateBase | [Molecule](/mods/destroy/Molecule) |
| pKa           | float                              |


:::

:::group{name=activationEnergy}

Return Type: [ReactionBuilder](/mods/destroy/ReactionBuilder)

```zenscript
ReactionBuilder.activationEnergy(activationEnergy as float) as ReactionBuilder
```

|    Parameter     | Type  |
|------------------|-------|
| activationEnergy | float |


:::

:::group{name=addCatalyst}

Return Type: [ReactionBuilder](/mods/destroy/ReactionBuilder)

```zenscript
ReactionBuilder.addCatalyst(molecule as Molecule, order as int) as ReactionBuilder
```

| Parameter |                Type                |
|-----------|------------------------------------|
| molecule  | [Molecule](/mods/destroy/Molecule) |
| order     | int                                |


:::

:::group{name=addItemReactant}

Return Type: [ReactionBuilder](/mods/destroy/ReactionBuilder)

```zenscript
ReactionBuilder.addItemReactant(itemReactant as invalid, moles as float) as ReactionBuilder
```

|  Parameter   |    Type     |
|--------------|-------------|
| itemReactant | **invalid** |
| moles        | float       |


:::

:::group{name=addProduct}

Return Type: [ReactionBuilder](/mods/destroy/ReactionBuilder)

```zenscript
ReactionBuilder.addProduct(molecule as Molecule) as ReactionBuilder
```

| Parameter |                Type                |
|-----------|------------------------------------|
| molecule  | [Molecule](/mods/destroy/Molecule) |


:::

:::group{name=addProduct}

Return Type: [ReactionBuilder](/mods/destroy/ReactionBuilder)

```zenscript
ReactionBuilder.addProduct(molecule as Molecule, ratio as int) as ReactionBuilder
```

| Parameter |                Type                |
|-----------|------------------------------------|
| molecule  | [Molecule](/mods/destroy/Molecule) |
| ratio     | int                                |


:::

:::group{name=addReactant}

Return Type: [ReactionBuilder](/mods/destroy/ReactionBuilder)

```zenscript
ReactionBuilder.addReactant(molecule as Molecule) as ReactionBuilder
```

| Parameter |                Type                |
|-----------|------------------------------------|
| molecule  | [Molecule](/mods/destroy/Molecule) |


:::

:::group{name=addReactant}

Return Type: [ReactionBuilder](/mods/destroy/ReactionBuilder)

```zenscript
ReactionBuilder.addReactant(molecule as Molecule, ratio as int) as ReactionBuilder
```

| Parameter |                Type                |
|-----------|------------------------------------|
| molecule  | [Molecule](/mods/destroy/Molecule) |
| ratio     | int                                |


:::

:::group{name=addReactant}

Return Type: [ReactionBuilder](/mods/destroy/ReactionBuilder)

```zenscript
ReactionBuilder.addReactant(molecule as Molecule, ratio as int, order as int) as ReactionBuilder
```

| Parameter |                Type                |
|-----------|------------------------------------|
| molecule  | [Molecule](/mods/destroy/Molecule) |
| ratio     | int                                |
| order     | int                                |


:::

:::group{name=addSimpleItemCatalyst}

Return Type: [ReactionBuilder](/mods/destroy/ReactionBuilder)

```zenscript
ReactionBuilder.addSimpleItemCatalyst(item as Supplier<ItemDefinition>, moles as float) as ReactionBuilder
```

| Parameter |                                Type                                |
|-----------|--------------------------------------------------------------------|
| item      | Supplier&lt;[ItemDefinition](/vanilla/api/item/ItemDefinition)&gt; |
| moles     | float                                                              |


:::

:::group{name=addSimpleItemReactant}

Return Type: [ReactionBuilder](/mods/destroy/ReactionBuilder)

```zenscript
ReactionBuilder.addSimpleItemReactant(item as Supplier<ItemDefinition>, moles as float) as ReactionBuilder
```

| Parameter |                                Type                                |
|-----------|--------------------------------------------------------------------|
| item      | Supplier&lt;[ItemDefinition](/vanilla/api/item/ItemDefinition)&gt; |
| moles     | float                                                              |


:::

:::group{name=build}

Return Type: [Reaction](/mods/destroy/Reaction)

```zenscript
// ReactionBuilder.build() as Reaction

myReactionBuilder.build();
```

:::

:::group{name=displayAsReversible}

Return Type: [ReactionBuilder](/mods/destroy/ReactionBuilder)

```zenscript
// ReactionBuilder.displayAsReversible() as ReactionBuilder

myReactionBuilder.displayAsReversible();
```

:::

:::group{name=dontIncludeInJei}

Return Type: [ReactionBuilder](/mods/destroy/ReactionBuilder)

```zenscript
// ReactionBuilder.dontIncludeInJei() as ReactionBuilder

myReactionBuilder.dontIncludeInJei();
```

:::

:::group{name=enthalpyChange}

Return Type: [ReactionBuilder](/mods/destroy/ReactionBuilder)

```zenscript
ReactionBuilder.enthalpyChange(enthalpyChange as float) as ReactionBuilder
```

|   Parameter    | Type  |
|----------------|-------|
| enthalpyChange | float |


:::

:::group{name=id}

Return Type: [ReactionBuilder](/mods/destroy/ReactionBuilder)

```zenscript
ReactionBuilder.id(id as string) as ReactionBuilder
```

| Parameter |  Type  |
|-----------|--------|
| id        | string |


:::

:::group{name=preexponentialFactor}

Return Type: [ReactionBuilder](/mods/destroy/ReactionBuilder)

```zenscript
ReactionBuilder.preexponentialFactor(preexponentialFactor as float) as ReactionBuilder
```

|      Parameter       | Type  |
|----------------------|-------|
| preexponentialFactor | float |


:::

:::group{name=requireUV}

Return Type: [ReactionBuilder](/mods/destroy/ReactionBuilder)

```zenscript
// ReactionBuilder.requireUV() as ReactionBuilder

myReactionBuilder.requireUV();
```

:::

:::group{name=reverseReaction}

Return Type: [ReactionBuilder](/mods/destroy/ReactionBuilder)

```zenscript
ReactionBuilder.reverseReaction(reverseReactionModifier as Consumer<ReactionBuilder>) as ReactionBuilder
```

|        Parameter        |                               Type                               |
|-------------------------|------------------------------------------------------------------|
| reverseReactionModifier | Consumer&lt;[ReactionBuilder](/mods/destroy/ReactionBuilder)&gt; |


:::

:::group{name=setOrder}

Return Type: [ReactionBuilder](/mods/destroy/ReactionBuilder)

```zenscript
ReactionBuilder.setOrder(molecule as Molecule, order as int) as ReactionBuilder
```

| Parameter |                Type                |
|-----------|------------------------------------|
| molecule  | [Molecule](/mods/destroy/Molecule) |
| order     | int                                |


:::

:::group{name=withResult}

Return Type: [ReactionBuilder](/mods/destroy/ReactionBuilder)

```zenscript
ReactionBuilder.withResult(moles as float, reactionresultFactory as BiFunction) as ReactionBuilder
```

|       Parameter       |    Type    |
|-----------------------|------------|
| moles                 | float      |
| reactionresultFactory | BiFunction |


:::


