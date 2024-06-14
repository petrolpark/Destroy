# Molecules

Use Molecules to manage molecules of the mod. Use <molecule> bracket handler to query
 a molecule. Example: <molecule:destroy:water>
 
 When creating a molecule, you may want to specify how does the molecule impact the environment, use
 <moleculetag> bracket handler. Example: <moleculetag:destroy:acutely_toxic> (specifies that the molecule is toxic)
 
 Use [Molecules](/mods/destroy/Molecules)#create(String) to create a **invalid**
 if you want to create your own molecule
 
 Use [Molecules](/mods/destroy/Molecules)#removeMolecule(com.petrolpark.destroy.compat.crafttweaker.natives.CTMolecule) to remove a molecule (all reactions involving this molecule will also be removed)

## Importing the class

It might be required for you to import the package if you encounter any issues (like casting an Array), so better be safe than sorry and add the import at the very top of the file.
```zenscript
import mods.destroy.Molecules;
```


## Static Methods

:::group{name=create}

Creates a molecule builder. Call .build() to build the molecule

Returns: The **invalid**  
Return Type: [MoleculeBuilder](/mods/destroy/MoleculeBuilder)

```zenscript
// Molecules.create(id as string) as MoleculeBuilder

Molecules.create("tellurium_copper");
```

| Parameter |  Type  |      Description       |
|-----------|--------|------------------------|
| id        | string | ID of the new molecule |


:::

:::group{name=getElement}

Return Type: [Element](/mods/destroy/Element)

```zenscript
Molecules.getElement(tokens as string) as Element
```

| Parameter |  Type  |
|-----------|--------|
| tokens    | string |


:::

:::group{name=getMolecule}

Return Type: [Molecule](/mods/destroy/Molecule)

```zenscript
Molecules.getMolecule(tokens as string) as Molecule
```

| Parameter |  Type  |
|-----------|--------|
| tokens    | string |


:::

:::group{name=getMoleculeById}

Gets a molecule by full ID

Returns: A **invalid** which was found by ID or null if molecule doesn't exist  
Return Type: [Molecule](/mods/destroy/Molecule)

```zenscript
// Molecules.getMoleculeById(moleculeId as string) as Molecule

Molecules.getMoleculeById("destroy:water");
```

| Parameter  |  Type  |      Description      |
|------------|--------|-----------------------|
| moleculeId | string | Molecule ID to search |


:::

:::group{name=getMoleculeTag}

Return Type: **invalid**

```zenscript
Molecules.getMoleculeTag(tokens as string) as invalid
```

| Parameter |  Type  |
|-----------|--------|
| tokens    | string |


:::

:::group{name=removeMolecule}

Removes this molecule from registry. This makes all reactions involving this molecule to unregister as well.

```zenscript
// Molecules.removeMolecule(molecule as Molecule)

Molecules.removeMolecule(<molecule:destroy:methylamine>);
```

| Parameter |                Type                |      Description      |
|-----------|------------------------------------|-----------------------|
| molecule  | [Molecule](/mods/destroy/Molecule) | **invalid** to remove |


:::

