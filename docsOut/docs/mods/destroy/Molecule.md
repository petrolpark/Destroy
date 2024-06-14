# Molecule

## Importing the class

It might be required for you to import the package if you encounter any issues (like casting an Array), so better be safe than sorry and add the import at the very top of the file.
```zenscript
import mods.destroy.Molecule;
```


## Casters

|                                          Result Type                                          | Is Implicit |
|-----------------------------------------------------------------------------------------------|-------------|
| [Percentaged](/vanilla/api/util/random/Percentaged)&lt;[Molecule](/mods/destroy/Molecule)&gt; | true        |

## Methods

:::group{name=addProductReaction}

Mark this Molecule as being a necessary reactant in the given **invalid**.
 There should never be any need to call this method (it is done automatically when **invalid**#build building a Reaction).

```zenscript
Molecule.addProductReaction(reaction as Reaction)
```

| Parameter |                Type                | Description |
|-----------|------------------------------------|-------------|
| reaction  | [Reaction](/mods/destroy/Reaction) | Reaction    |


:::

:::group{name=addReactantReaction}

```zenscript
Molecule.addReactantReaction(reaction as Reaction)
```

| Parameter |                Type                |
|-----------|------------------------------------|
| reaction  | [Reaction](/mods/destroy/Reaction) |


:::

:::group{name=getAtoms}

Return Type: Set&lt;[Atom](/mods/destroy/Atom)&gt;

```zenscript
// Molecule.getAtoms() as Set<Atom>

myMolecule.getAtoms();
```

:::

:::group{name=getBoilingPoint}

Return Type: float

```zenscript
// Molecule.getBoilingPoint() as float

myMolecule.getBoilingPoint();
```

:::

:::group{name=getCarbocationStability}

Return Type: float

```zenscript
Molecule.getCarbocationStability(carbon as Atom, isCarbanion as boolean) as float
```

|  Parameter  |            Type            |
|-------------|----------------------------|
| carbon      | [Atom](/mods/destroy/Atom) |
| isCarbanion | boolean                    |


:::

:::group{name=getCharge}

Return Type: int

```zenscript
// Molecule.getCharge() as int

myMolecule.getCharge();
```

:::

:::group{name=getColor}

Return Type: int

```zenscript
// Molecule.getColor() as int

myMolecule.getColor();
```

:::

:::group{name=getDensity}

Return Type: float

```zenscript
// Molecule.getDensity() as float

myMolecule.getDensity();
```

:::

:::group{name=getDipoleMoment}

Return Type: float

```zenscript
// Molecule.getDipoleMoment() as float

myMolecule.getDipoleMoment();
```

:::

:::group{name=getEquivalent}

Return Type: [Molecule](/mods/destroy/Molecule)

```zenscript
// Molecule.getEquivalent() as Molecule

myMolecule.getEquivalent();
```

:::

:::group{name=getFROWNSCode}

Return Type: string

```zenscript
// Molecule.getFROWNSCode() as string

myMolecule.getFROWNSCode();
```

:::

:::group{name=getId}

Return Type: string

```zenscript
// Molecule.getId() as string

myMolecule.getId();
```

:::

:::group{name=getLatentHeat}

Return Type: float

```zenscript
// Molecule.getLatentHeat() as float

myMolecule.getLatentHeat();
```

:::

:::group{name=getMass}

Return Type: float

```zenscript
// Molecule.getMass() as float

myMolecule.getMass();
```

:::

:::group{name=getMolarHeatCapacity}

Return Type: float

```zenscript
// Molecule.getMolarHeatCapacity() as float

myMolecule.getMolarHeatCapacity();
```

:::

:::group{name=getName}

Return Type: [Component](/vanilla/api/text/Component)

```zenscript
Molecule.getName(iupac as boolean) as Component
```

| Parameter |  Type   |
|-----------|---------|
| iupac     | boolean |


:::

:::group{name=getProductReactions}

Get the list of **invalid** by which this Molecule is made.

Returns: List of Reactions ordered by declaration  
Return Type: stdlib.List&lt;[Reaction](/mods/destroy/Reaction)&gt;

```zenscript
// Molecule.getProductReactions() as stdlib.List<Reaction>

myMolecule.getProductReactions();
```

:::

:::group{name=getPureConcentration}

Return Type: float

```zenscript
// Molecule.getPureConcentration() as float

myMolecule.getPureConcentration();
```

:::

:::group{name=getReactantReactions}

Get the list of **invalid** of which this Molecule is a necessary Reactant.

Returns: List of Reactions ordered by declaration  
Return Type: stdlib.List&lt;[Reaction](/mods/destroy/Reaction)&gt;

```zenscript
// Molecule.getReactantReactions() as stdlib.List<Reaction>

myMolecule.getReactantReactions();
```

:::

:::group{name=getStructuralFormula}

Return Type: string

```zenscript
// Molecule.getStructuralFormula() as string

myMolecule.getStructuralFormula();
```

:::

:::group{name=getTags}

Return Type: Set

```zenscript
// Molecule.getTags() as Set

myMolecule.getTags();
```

:::

:::group{name=getTranslationKey}

Return Type: string

```zenscript
Molecule.getTranslationKey(iupac as boolean) as string
```

| Parameter |  Type   |
|-----------|---------|
| iupac     | boolean |


:::

:::group{name=hasTag}

Return Type: boolean

```zenscript
Molecule.hasTag(tag as invalid) as boolean
```

| Parameter |    Type     |
|-----------|-------------|
| tag       | **invalid** |


:::

:::group{name=isColorless}

Return Type: boolean

```zenscript
// Molecule.isColorless() as boolean

myMolecule.isColorless();
```

:::

:::group{name=isCyclic}

Return Type: boolean

```zenscript
// Molecule.isCyclic() as boolean

myMolecule.isCyclic();
```

:::

:::group{name=isHypothetical}

Return Type: boolean

```zenscript
// Molecule.isHypothetical() as boolean

myMolecule.isHypothetical();
```

:::

:::group{name=isNovel}

Return Type: boolean

```zenscript
// Molecule.isNovel() as boolean

myMolecule.isNovel();
```

:::


## Properties

|     Name      |  Type  | Has Getter | Has Setter |
|---------------|--------|------------|------------|
| commandString | string | true       | false      |

