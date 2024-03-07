# Element

## Importing the class

It might be required for you to import the package if you encounter any issues (like casting an Array), so better be safe than sorry and add the import at the very top of the file.
```zenscript
import mods.destroy.Element;
```


## Enum Constants

Element is an enum. It has 20 enum constants. They are accessible using the code below.

```zenscript
Element.R_GROUP
Element.CARBON
Element.HYDROGEN
Element.SULFUR
Element.NITROGEN
Element.OXYGEN
Element.FLUORINE
Element.SODIUM
Element.CHLORINE
Element.POTASSIUM
Element.CALCIUM
Element.IRON
Element.NICKEL
Element.COPPER
Element.ZINC
Element.ZIRCONIUM
Element.IODINE
Element.PLATINUM
Element.GOLD
Element.MERCURY
```
## Methods

:::group{name=getElectronegativity}

Return Type: float?

```zenscript
// Element.getElectronegativity() as float?

myElement.getElectronegativity();
```

:::

:::group{name=getGeometry}

Return Type: **invalid**

```zenscript
Element.getGeometry(connections as int) as invalid
```

|  Parameter  | Type |
|-------------|------|
| connections | int  |


:::

:::group{name=getMass}

Return Type: float?

```zenscript
// Element.getMass() as float?

myElement.getMass();
```

:::

:::group{name=getMaxValency}

Return Type: double

```zenscript
// Element.getMaxValency() as double

myElement.getMaxValency();
```

:::

:::group{name=getNextLowestValency}

Return Type: double

```zenscript
Element.getNextLowestValency(valency as double) as double
```

| Parameter |  Type  |
|-----------|--------|
| valency   | double |


:::

:::group{name=getPartial}

Return Type: **invalid**

```zenscript
// Element.getPartial() as invalid

myElement.getPartial();
```

:::

:::group{name=getSymbol}

Return Type: string

```zenscript
// Element.getSymbol() as string

myElement.getSymbol();
```

:::

:::group{name=isValidValency}

Return Type: boolean

```zenscript
Element.isValidValency(valency as double) as boolean
```

| Parameter |  Type  |
|-----------|--------|
| valency   | double |


:::

:::group{name=setPartial}

```zenscript
Element.setPartial(partial as invalid)
```

| Parameter |    Type     |
|-----------|-------------|
| partial   | **invalid** |


:::


