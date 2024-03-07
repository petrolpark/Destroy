# Formula

## Importing the class

It might be required for you to import the package if you encounter any issues (like casting an Array), so better be safe than sorry and add the import at the very top of the file.
```zenscript
import mods.destroy.Formula;
```


## Implemented Interfaces
Formula implements the following interfaces. That means all methods defined in these interfaces are also available in Formula

- Cloneable

## Static Methods

:::group{name=alcohol}

Return Type: [Formula](/mods/destroy/Formula)

```zenscript
// Formula.alcohol() as Formula

Formula.alcohol();
```

:::

:::group{name=atom}

Return Type: [Formula](/mods/destroy/Formula)

```zenscript
Formula.atom(element as Element) as Formula
```

| Parameter |               Type               |
|-----------|----------------------------------|
| element   | [Element](/mods/destroy/Element) |


:::

:::group{name=carbonChain}

Return Type: [Formula](/mods/destroy/Formula)

```zenscript
Formula.carbonChain(length as int) as Formula
```

| Parameter | Type |
|-----------|------|
| length    | int  |


:::

:::group{name=deserialize}

Return Type: [Formula](/mods/destroy/Formula)

```zenscript
Formula.deserialize(FROWNSstring as string) as Formula
```

|  Parameter   |  Type  |
|--------------|--------|
| FROWNSstring | string |


:::

## Constructors


```zenscript
new Formula(startingAtom as Atom) as Formula
```
|  Parameter   |            Type            |
|--------------|----------------------------|
| startingAtom | [Atom](/mods/destroy/Atom) |



## Methods

:::group{name=addAllHydrogens}

Return Type: [Formula](/mods/destroy/Formula)

```zenscript
// Formula.addAllHydrogens() as Formula

myFormula.addAllHydrogens();
```

:::

:::group{name=addAtom}

Return Type: [Formula](/mods/destroy/Formula)

```zenscript
Formula.addAtom(atom as Atom) as Formula
```

| Parameter |            Type            |
|-----------|----------------------------|
| atom      | [Atom](/mods/destroy/Atom) |


:::

:::group{name=addAtom}

Return Type: [Formula](/mods/destroy/Formula)

```zenscript
Formula.addAtom(element as Element) as Formula
```

| Parameter |               Type               |
|-----------|----------------------------------|
| element   | [Element](/mods/destroy/Element) |


:::

:::group{name=addAtom}

Return Type: [Formula](/mods/destroy/Formula)

```zenscript
Formula.addAtom(atom as Atom, bondType as invalid) as Formula
```

| Parameter |            Type            |
|-----------|----------------------------|
| atom      | [Atom](/mods/destroy/Atom) |
| bondType  | **invalid**                |


:::

:::group{name=addAtom}

Return Type: [Formula](/mods/destroy/Formula)

```zenscript
Formula.addAtom(element as Element, bondType as invalid) as Formula
```

| Parameter |               Type               |
|-----------|----------------------------------|
| element   | [Element](/mods/destroy/Element) |
| bondType  | **invalid**                      |


:::

:::group{name=addCarbonyl}

Return Type: [Formula](/mods/destroy/Formula)

```zenscript
// Formula.addCarbonyl() as Formula

myFormula.addCarbonyl();
```

:::

:::group{name=addGroup}

Return Type: [Formula](/mods/destroy/Formula)

```zenscript
Formula.addGroup(group as Formula) as Formula
```

| Parameter |               Type               |
|-----------|----------------------------------|
| group     | [Formula](/mods/destroy/Formula) |


:::

:::group{name=addGroup}

Return Type: [Formula](/mods/destroy/Formula)

```zenscript
Formula.addGroup(group as Formula, isSideGroup as boolean) as Formula
```

|  Parameter  |               Type               |
|-------------|----------------------------------|
| group       | [Formula](/mods/destroy/Formula) |
| isSideGroup | boolean                          |


:::

:::group{name=addGroup}

Return Type: [Formula](/mods/destroy/Formula)

```zenscript
Formula.addGroup(group as Formula, isSideGroup as boolean, bondType as invalid) as Formula
```

|  Parameter  |               Type               |
|-------------|----------------------------------|
| group       | [Formula](/mods/destroy/Formula) |
| isSideGroup | boolean                          |
| bondType    | **invalid**                      |


:::

:::group{name=getAllAtoms}

Return Type: Set&lt;[Atom](/mods/destroy/Atom)&gt;

```zenscript
// Formula.getAllAtoms() as Set<Atom>

myFormula.getAllAtoms();
```

:::

:::group{name=getBondedAtomsOfElement}

Return Type: stdlib.List&lt;[Atom](/mods/destroy/Atom)&gt;

```zenscript
Formula.getBondedAtomsOfElement(element as Element) as stdlib.List<Atom>
```

| Parameter |               Type               |
|-----------|----------------------------------|
| element   | [Element](/mods/destroy/Element) |


:::

:::group{name=getCarbocationStability}

Return Type: float

```zenscript
Formula.getCarbocationStability(carbon as Atom, isCarbanion as boolean) as float
```

|  Parameter  |            Type            |
|-------------|----------------------------|
| carbon      | [Atom](/mods/destroy/Atom) |
| isCarbanion | boolean                    |


:::

:::group{name=getTotalBonds}

Return Type: double

```zenscript
Formula.getTotalBonds(bonds as stdlib.List) as double
```

| Parameter |    Type     |
|-----------|-------------|
| bonds     | stdlib.List |


:::

:::group{name=isCyclic}

Return Type: boolean

```zenscript
// Formula.isCyclic() as boolean

myFormula.isCyclic();
```

:::

:::group{name=joinFormulae}

Return Type: [Formula](/mods/destroy/Formula)

```zenscript
Formula.joinFormulae(formula2 as Formula, bondType as invalid) as Formula
```

| Parameter |               Type               |
|-----------|----------------------------------|
| formula2  | [Formula](/mods/destroy/Formula) |
| bondType  | **invalid**                      |


:::

:::group{name=moveTo}

Return Type: [Formula](/mods/destroy/Formula)

```zenscript
Formula.moveTo(atom as Atom) as Formula
```

| Parameter |            Type            |
|-----------|----------------------------|
| atom      | [Atom](/mods/destroy/Atom) |


:::

:::group{name=remove}

Return Type: [Formula](/mods/destroy/Formula)

```zenscript
Formula.remove(atom as Atom) as Formula
```

| Parameter |            Type            |
|-----------|----------------------------|
| atom      | [Atom](/mods/destroy/Atom) |


:::

:::group{name=replace}

Return Type: [Formula](/mods/destroy/Formula)

```zenscript
Formula.replace(oldAtom as Atom, newAtom as Atom) as Formula
```

| Parameter |            Type            |
|-----------|----------------------------|
| oldAtom   | [Atom](/mods/destroy/Atom) |
| newAtom   | [Atom](/mods/destroy/Atom) |


:::

:::group{name=replaceBondTo}

Return Type: [Formula](/mods/destroy/Formula)

```zenscript
Formula.replaceBondTo(otherAtom as Atom, bondType as invalid) as Formula
```

| Parameter |            Type            |
|-----------|----------------------------|
| otherAtom | [Atom](/mods/destroy/Atom) |
| bondType  | **invalid**                |


:::

:::group{name=setStartingAtom}

Return Type: [Formula](/mods/destroy/Formula)

```zenscript
Formula.setStartingAtom(atom as Atom) as Formula
```

| Parameter |            Type            |
|-----------|----------------------------|
| atom      | [Atom](/mods/destroy/Atom) |


:::

:::group{name=toString}

Return Type: string

```zenscript
// Formula.toString() as string

myFormula.toString();
```

:::


