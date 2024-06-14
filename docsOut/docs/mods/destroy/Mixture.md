# Mixture

## Importing the class

It might be required for you to import the package if you encounter any issues (like casting an Array), so better be safe than sorry and add the import at the very top of the file.
```zenscript
import mods.destroy.Mixture;
```


## Static Methods

:::group{name=create}



Return Type: [Mixture](/mods/destroy/Mixture)

```zenscript
Mixture.create(data as stdlib.List<Percentaged<Molecule>>, temperature as float, translationKey as string) as Mixture
```

|   Parameter    |                                                       Type                                                       |             Description             | Optional | Default Value |
|----------------|------------------------------------------------------------------------------------------------------------------|-------------------------------------|----------|---------------|
| data           | stdlib.List&lt;[Percentaged](/vanilla/api/util/random/Percentaged)&lt;[Molecule](/mods/destroy/Molecule)&gt;&gt; | data of molecules and concentration | false    |               |
| temperature    | float                                                                                                            | In kelvins, 0 for unspecified       | true     | 0.0           |
| translationKey | string                                                                                                           |                                     | true     |               |


:::

:::group{name=createIonFluidIngredient}

Return Type: [FluidIngredient](/forge/api/fluid/FluidIngredient)

```zenscript
Mixture.createIonFluidIngredient(concentration as float, amount as int) as FluidIngredient
```

|   Parameter   | Type  | Optional | Default Value |
|---------------|-------|----------|---------------|
| concentration | float | false    |               |
| amount        | int   | true     | 1000          |


:::

:::group{name=createMixtureStack}

Return Type: [IFluidStack](/vanilla/api/fluid/IFluidStack)

```zenscript
Mixture.createMixtureStack(mixture as Mixture, amount as int) as IFluidStack
```

| Parameter |               Type               | Optional | Default Value |
|-----------|----------------------------------|----------|---------------|
| mixture   | [Mixture](/mods/destroy/Mixture) | false    |               |
| amount    | int                              | true     | 1000          |


:::

:::group{name=createMoleculeFluidIngredient}

Return Type: [FluidIngredient](/forge/api/fluid/FluidIngredient)

```zenscript
Mixture.createMoleculeFluidIngredient(moleculeData as Percentaged<Molecule>, amount as int) as FluidIngredient
```

|  Parameter   |                                             Type                                              | Optional | Default Value |
|--------------|-----------------------------------------------------------------------------------------------|----------|---------------|
| moleculeData | [Percentaged](/vanilla/api/util/random/Percentaged)&lt;[Molecule](/mods/destroy/Molecule)&gt; | false    |               |
| amount       | int                                                                                           | true     | 1000          |


:::

:::group{name=createMoleculeFluidIngredient}

Return Type: [FluidIngredient](/forge/api/fluid/FluidIngredient)

```zenscript
Mixture.createMoleculeFluidIngredient(molecule as Molecule, concentration as float, amount as int) as FluidIngredient
```

|   Parameter   |                Type                | Optional | Default Value |
|---------------|------------------------------------|----------|---------------|
| molecule      | [Molecule](/mods/destroy/Molecule) | false    |               |
| concentration | float                              | false    |               |
| amount        | int                                | true     | 1000          |


:::

:::group{name=createMoleculeTagFluidIngredient}

Return Type: [FluidIngredient](/forge/api/fluid/FluidIngredient)

```zenscript
Mixture.createMoleculeTagFluidIngredient(tag as invalid, concentration as float, amount as int) as FluidIngredient
```

|   Parameter   |    Type     | Optional | Default Value |
|---------------|-------------|----------|---------------|
| tag           | **invalid** | false    |               |
| concentration | float       | false    |               |
| amount        | int         | true     | 1000          |


:::

:::group{name=createSaltFluidIngredient}

Return Type: [FluidIngredient](/forge/api/fluid/FluidIngredient)

```zenscript
Mixture.createSaltFluidIngredient(cation as Molecule, anion as Molecule, concentration as float, amount as int) as FluidIngredient
```

|   Parameter   |                Type                | Optional | Default Value |
|---------------|------------------------------------|----------|---------------|
| cation        | [Molecule](/mods/destroy/Molecule) | false    |               |
| anion         | [Molecule](/mods/destroy/Molecule) | false    |               |
| concentration | float                              | false    |               |
| amount        | int                                | true     | 1000          |


:::

:::group{name=mix}

Return Type: [Mixture](/mods/destroy/Mixture)

```zenscript
Mixture.mix(mixtures as double?[Mixture]) as Mixture
```

| Parameter |                   Type                    |
|-----------|-------------------------------------------|
| mixtures  | double?[[Mixture](/mods/destroy/Mixture)] |


:::

:::group{name=pure}

Return Type: [Mixture](/mods/destroy/Mixture)

```zenscript
Mixture.pure(molecule as Molecule) as Mixture
```

| Parameter |                Type                |
|-----------|------------------------------------|
| molecule  | [Molecule](/mods/destroy/Molecule) |


:::

## Casters

|                  Result Type                  | Is Implicit |
|-----------------------------------------------|-------------|
| [IFluidStack](/vanilla/api/fluid/IFluidStack) | true        |

## Methods

:::group{name=addMolecule}

Return Type: [Mixture](/mods/destroy/Mixture)

```zenscript
Mixture.addMolecule(molecule as Molecule, concentration as float) as Mixture
```

|   Parameter   |                Type                |
|---------------|------------------------------------|
| molecule      | [Molecule](/mods/destroy/Molecule) |
| concentration | float                              |


:::

:::group{name=disturbEquilibrium}

```zenscript
// Mixture.disturbEquilibrium()

myMixture.disturbEquilibrium();
```

:::

:::group{name=getColor}

Return Type: int

```zenscript
// Mixture.getColor() as int

myMixture.getColor();
```

:::

:::group{name=getConcentrationOf}

Return Type: float

```zenscript
Mixture.getConcentrationOf(molecule as Molecule) as float
```

| Parameter |                Type                |
|-----------|------------------------------------|
| molecule  | [Molecule](/mods/destroy/Molecule) |


:::

:::group{name=getContents}

Return Type: stdlib.List&lt;[Molecule](/mods/destroy/Molecule)&gt;

```zenscript
Mixture.getContents(excludeNovel as boolean) as stdlib.List<Molecule>
```

|  Parameter   |  Type   |
|--------------|---------|
| excludeNovel | boolean |


:::

:::group{name=getContentsString}

Return Type: string

```zenscript
// Mixture.getContentsString() as string

myMixture.getContentsString();
```

:::

:::group{name=getContentsTooltip}

Return Type: stdlib.List&lt;[Component](/vanilla/api/text/Component)&gt;

```zenscript
Mixture.getContentsTooltip(iupac as boolean, monospace as boolean, useMoles as boolean, amount as int, concentrationFormatter as invalid) as stdlib.List<Component>
```

|       Parameter        |    Type     |
|------------------------|-------------|
| iupac                  | boolean     |
| monospace              | boolean     |
| useMoles               | boolean     |
| amount                 | int         |
| concentrationFormatter | **invalid** |


:::

:::group{name=getTemperature}

Return Type: float

```zenscript
// Mixture.getTemperature() as float

myMixture.getTemperature();
```

:::

:::group{name=getTotalConcentration}

Return Type: float

```zenscript
// Mixture.getTotalConcentration() as float

myMixture.getTotalConcentration();
```

:::

:::group{name=getVolumetricHeatCapacity}

Return Type: float

```zenscript
// Mixture.getVolumetricHeatCapacity() as float

myMixture.getVolumetricHeatCapacity();
```

:::

:::group{name=hasUsableMolecule}

Return Type: boolean

```zenscript
Mixture.hasUsableMolecule(molecule as Molecule, minConcentration as float, maxConcentration as float, ignore as Predicate<Molecule>) as boolean
```

|    Parameter     |                        Type                         |
|------------------|-----------------------------------------------------|
| molecule         | [Molecule](/mods/destroy/Molecule)                  |
| minConcentration | float                                               |
| maxConcentration | float                                               |
| ignore           | Predicate&lt;[Molecule](/mods/destroy/Molecule)&gt; |


:::

:::group{name=hasUsableMolecules}

Return Type: boolean

```zenscript
Mixture.hasUsableMolecules(molecules as Predicate<Molecule>, minConcentration as float, maxConcentration as float, ignore as Predicate<Molecule>) as boolean
```

|    Parameter     |                        Type                         |
|------------------|-----------------------------------------------------|
| molecules        | Predicate&lt;[Molecule](/mods/destroy/Molecule)&gt; |
| minConcentration | float                                               |
| maxConcentration | float                                               |
| ignore           | Predicate&lt;[Molecule](/mods/destroy/Molecule)&gt; |


:::

:::group{name=heat}

```zenscript
Mixture.heat(energyDensity as float)
```

|   Parameter   | Type  |
|---------------|-------|
| energyDensity | float |


:::

:::group{name=isAtEquilibrium}

Return Type: boolean

```zenscript
// Mixture.isAtEquilibrium() as boolean

myMixture.isAtEquilibrium();
```

:::

:::group{name=isEmpty}

Return Type: boolean

```zenscript
// Mixture.isEmpty() as boolean

myMixture.isEmpty();
```

:::

:::group{name=scale}

```zenscript
Mixture.scale(volumeIncreaseFactor as float)
```

|      Parameter       | Type  |
|----------------------|-------|
| volumeIncreaseFactor | float |


:::

:::group{name=setTemperature}

Return Type: [Mixture](/mods/destroy/Mixture)

```zenscript
Mixture.setTemperature(temperature as float) as Mixture
```

|  Parameter  | Type  |
|-------------|-------|
| temperature | float |


:::

:::group{name=setTranslationKey}

```zenscript
Mixture.setTranslationKey(translationKey as string)
```

|   Parameter    |  Type  |
|----------------|--------|
| translationKey | string |


:::


## Properties

|    Name     |               Type               | Has Getter | Has Setter |
|-------------|----------------------------------|------------|------------|
| temperature | [Mixture](/mods/destroy/Mixture) | false      | true       |

