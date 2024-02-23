# Reaction

## Importing the class

It might be required for you to import the package if you encounter any issues (like casting an Array), so better be safe than sorry and add the import at the very top of the file.
```zenscript
import mods.destroy.Reaction;
```


## Methods

:::group{name=consumesItem}

Whether this Reaction needs any Item Stack as a **invalid**. Even if this is
 `true`, the Reaction may still have **invalid**#isCatalyst Item Stack catalysts.

Return Type: boolean

```zenscript
// Reaction.consumesItem() as boolean

myReaction.consumesItem();
```

:::

:::group{name=containsProduct}

Whether this Molecule is created in this Reaction.

Return Type: boolean

```zenscript
Reaction.containsProduct(molecule as Molecule) as boolean
```

| Parameter |                Type                |
|-----------|------------------------------------|
| molecule  | [Molecule](/mods/destroy/Molecule) |


:::

:::group{name=containsReactant}

Whether this Molecule gets consumed in this Reaction (does not include catalysts).

Return Type: boolean

```zenscript
Reaction.containsReactant(molecule as Molecule) as boolean
```

| Parameter |                Type                |
|-----------|------------------------------------|
| molecule  | [Molecule](/mods/destroy/Molecule) |


:::

:::group{name=displayAsReversible}

Whether this Reaction should be displayed in JEI with an equilibrium arrow rather than a normal one.

Return Type: boolean

```zenscript
// Reaction.displayAsReversible() as boolean

myReaction.displayAsReversible();
```

:::

:::group{name=getActivationEnergy}

Return Type: float

```zenscript
// Reaction.getActivationEnergy() as float

myReaction.getActivationEnergy();
```

:::

:::group{name=getEnthalpyChange}

Return Type: float

```zenscript
// Reaction.getEnthalpyChange() as float

myReaction.getEnthalpyChange();
```

:::

:::group{name=getFullID}

Get the fully unique ID for this Reaction, in the format `<namespace>:
 <id>`, for example `destroy:chloroform_fluorination`.

Return Type: string

```zenscript
// Reaction.getFullID() as string

myReaction.getFullID();
```

:::

:::group{name=getID}

Return Type: string

```zenscript
// Reaction.getID() as string

myReaction.getID();
```

:::

:::group{name=getItemReactants}

Get the **invalid** for this Reaction.

Return Type: stdlib.List

```zenscript
// Reaction.getItemReactants() as stdlib.List

myReaction.getItemReactants();
```

:::

:::group{name=getMolesPerItem}

Return Type: float

```zenscript
// Reaction.getMolesPerItem() as float

myReaction.getMolesPerItem();
```

:::

:::group{name=getOrders}

Get every **invalid** reactant and catalyst in this Reaction, mapped to their
 orders in the rate equation.

Return Type: int?[[Molecule](/mods/destroy/Molecule)]

```zenscript
// Reaction.getOrders() as int?[Molecule]

myReaction.getOrders();
```

:::

:::group{name=getPreexponentialFactor}

Return Type: float

```zenscript
// Reaction.getPreexponentialFactor() as float

myReaction.getPreexponentialFactor();
```

:::

:::group{name=getProductMolarRatio}

Get the stoichometric ratio of this **invalid** in this Reaction.

Returns: `0` if this Molecule is not a product  
Return Type: int

```zenscript
Reaction.getProductMolarRatio(product as Molecule) as int
```

| Parameter |                Type                |
|-----------|------------------------------------|
| product   | [Molecule](/mods/destroy/Molecule) |


:::

:::group{name=getProducts}

All Molecules which are created in this Reaction (but not their molar ratios).

Return Type: Set&lt;[Molecule](/mods/destroy/Molecule)&gt;

```zenscript
// Reaction.getProducts() as Set<Molecule>

myReaction.getProducts();
```

:::

:::group{name=getRateConstant}

The rate constant of this Reaction at the given temperature.

Return Type: float

```zenscript
Reaction.getRateConstant(temperature as float) as float
```

|  Parameter  | Type  |  Description  |
|-------------|-------|---------------|
| temperature | float | (in kelvins). |


:::

:::group{name=getReactantMolarRatio}

Get the stoichometric ratio of this **invalid** or catalyst in this Reaction.

Returns: `0` if this Molecule is not a reactant  
Return Type: int

```zenscript
Reaction.getReactantMolarRatio(reactant as Molecule) as int
```

| Parameter |                Type                |
|-----------|------------------------------------|
| reactant  | [Molecule](/mods/destroy/Molecule) |


:::

:::group{name=getReactants}

All Molecules which are consumed in this Reaction (but not their molar ratios).

Return Type: Set&lt;[Molecule](/mods/destroy/Molecule)&gt;

```zenscript
// Reaction.getReactants() as Set<Molecule>

myReaction.getReactants();
```

:::

:::group{name=getResult}

The **invalid** of this Reaction, which occurs once a set
 number of moles of Reaction have occured.

Returns: `null` if this Reaction has no result.  
Return Type: **invalid**

```zenscript
// Reaction.getResult() as invalid

myReaction.getResult();
```

:::

:::group{name=getReverseReactionForDisplay}

If this is the 'forward' half of a reversible Reaction, this contains the reverse Reaction. This is so JEI
 knows the products of the forward Reaction are the reactants of the reverse, and vice versa. If this is not
 part of a reversible Reaction, this is empty. This is just for display; if a Reaction has a reverse and is needed
 for logic (e.g. Reacting in a Mixture) it should not be accessed in this way.

Return Type: **invalid**

```zenscript
// Reaction.getReverseReactionForDisplay() as invalid

myReaction.getReverseReactionForDisplay();
```

:::

:::group{name=hasResult}

Whether this Reaction has a **invalid**.

Return Type: boolean

```zenscript
// Reaction.hasResult() as boolean

myReaction.hasResult();
```

:::

:::group{name=includeInJei}

Whether this Reaction should be displayed in the list of Reactions in JEI.

Return Type: boolean

```zenscript
// Reaction.includeInJei() as boolean

myReaction.includeInJei();
```

:::

:::group{name=needsUV}

Whether this Reaction needs UV light to proceed.

Return Type: boolean

```zenscript
// Reaction.needsUV() as boolean

myReaction.needsUV();
```

:::


