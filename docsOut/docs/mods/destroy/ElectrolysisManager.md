# ElectrolysisManager

## Importing the class

It might be required for you to import the package if you encounter any issues (like casting an Array), so better be safe than sorry and add the import at the very top of the file.
```zenscript
import mods.destroy.ElectrolysisManager;
```


## Implemented Interfaces
ElectrolysisManager implements the following interfaces. That means all methods defined in these interfaces are also available in ElectrolysisManager

- [IDestroyRecipeManager](/mods/destroy/IDestroyRecipeManager)

## Methods

:::group{name=addJsonRecipe}

```zenscript
ElectrolysisManager.addJsonRecipe(name as string, mapData as MapData)
```

| Parameter |                 Type                 |
|-----------|--------------------------------------|
| name      | string                               |
| mapData   | [MapData](/vanilla/api/data/MapData) |


:::

:::group{name=addRecipe}

Adds an electrolysis recipe that outputs FluidStacks.

```zenscript
// ElectrolysisManager.addRecipe(name as string, heat as invalid, outputs as IFluidStack[], itemInputs as IIngredientWithAmount[], fluidInputs as FluidIngredient[], duration as int)

myElectrolysisManager.addRecipe("fluid_electrolysed", <constant:create:heat_condition:none>, [<fluid:minecraft:water> * 200], [<item:minecraft:glass> * 2], [<fluid:minecraft:water> * 250], 200);
```

|  Parameter  |                                   Type                                   |               Description                | Optional |                 Default Value                  |
|-------------|--------------------------------------------------------------------------|------------------------------------------|----------|------------------------------------------------|
| name        | string                                                                   | The name of the recipe.                  | false    |                                                |
| heat        | **invalid**                                                              | The required heat of the recipe.         | false    |                                                |
| outputs     | [IFluidStack](/vanilla/api/fluid/IFluidStack)[]                          | The output FluidStacks of the recipe.    | false    |                                                |
| itemInputs  | [IIngredientWithAmount](/vanilla/api/ingredient/IIngredientWithAmount)[] | The item inputs of the recipe.           | false    |                                                |
| fluidInputs | [FluidIngredient](/forge/api/fluid/FluidIngredient)[]                    | The optional fluid inputs of the recipe. | true     | [] as crafttweaker.api.fluid.FluidIngredient[] |
| duration    | int                                                                      | The duration of the recipe in ticks.     | true     | 100                                            |


:::

:::group{name=addRecipe}

Adds an Electrolysis recipe that outputs ItemStacks.

```zenscript
// ElectrolysisManager.addRecipe(name as string, heat as invalid, outputs as Percentaged<IItemStack>[], itemInputs as IIngredientWithAmount[], fluidInputs as FluidIngredient[], duration as int)

myElectrolysisManager.addRecipe("electrolysed", <constant:create:heat_condition:heated>, [<item:minecraft:diamond> % 50, <item:minecraft:apple>, (<item:minecraft:dirt> * 2) % 12], [<item:minecraft:glass> * 2], [<fluid:minecraft:water> * 250], 200);
```

|  Parameter  |                                                  Type                                                   |               Description                | Optional |                 Default Value                  |
|-------------|---------------------------------------------------------------------------------------------------------|------------------------------------------|----------|------------------------------------------------|
| name        | string                                                                                                  | The name of the recipe.                  | false    |                                                |
| heat        | **invalid**                                                                                             | The required heat of the recipe.         | false    |                                                |
| outputs     | [Percentaged](/vanilla/api/util/random/Percentaged)&lt;[IItemStack](/vanilla/api/item/IItemStack)&gt;[] | The output ItemStacks of the recipe.     | false    |                                                |
| itemInputs  | [IIngredientWithAmount](/vanilla/api/ingredient/IIngredientWithAmount)[]                                | The item inputs of the recipe.           | false    |                                                |
| fluidInputs | [FluidIngredient](/forge/api/fluid/FluidIngredient)[]                                                   | The optional fluid inputs of the recipe. | true     | [] as crafttweaker.api.fluid.FluidIngredient[] |
| duration    | int                                                                                                     | The duration of the recipe in ticks.     | true     | 100                                            |


:::

:::group{name=getAllRecipes}

Return Type: stdlib.List&lt;T&gt;

```zenscript
// ElectrolysisManager.getAllRecipes() as stdlib.List<T>

myElectrolysisManager.getAllRecipes();
```

:::

:::group{name=getRecipeByName}

Return Type: T

```zenscript
ElectrolysisManager.getRecipeByName(name as string) as T
```

| Parameter |  Type  |
|-----------|--------|
| name      | string |


:::

:::group{name=getRecipeMap}

Return Type: T[[ResourceLocation](/vanilla/api/resource/ResourceLocation)]

```zenscript
// ElectrolysisManager.getRecipeMap() as T[ResourceLocation]

myElectrolysisManager.getRecipeMap();
```

:::

:::group{name=getRecipesByOutput}

Return Type: stdlib.List&lt;T&gt;

```zenscript
ElectrolysisManager.getRecipesByOutput(output as IIngredient) as stdlib.List<T>
```

| Parameter |                        Type                        |
|-----------|----------------------------------------------------|
| output    | [IIngredient](/vanilla/api/ingredient/IIngredient) |


:::

:::group{name=registerRecipe}

Registers a recipe using a builder approach.

```zenscript
ElectrolysisManager.registerRecipe(name as string, recipeBuilder as Consumer)
```

|   Parameter   |   Type   |       Description       |
|---------------|----------|-------------------------|
| name          | string   | The name of the recipe. |
| recipeBuilder | Consumer | The recipe builder.     |


:::

:::group{name=remove}

```zenscript
ElectrolysisManager.remove(output as IIngredient)
```

| Parameter |                        Type                        |
|-----------|----------------------------------------------------|
| output    | [IIngredient](/vanilla/api/ingredient/IIngredient) |


:::

:::group{name=removeAll}

```zenscript
// ElectrolysisManager.removeAll()

myElectrolysisManager.removeAll();
```

:::

:::group{name=removeByInput}

```zenscript
ElectrolysisManager.removeByInput(input as IItemStack)
```

| Parameter |                    Type                    |
|-----------|--------------------------------------------|
| input     | [IItemStack](/vanilla/api/item/IItemStack) |


:::

:::group{name=removeByModid}

```zenscript
ElectrolysisManager.removeByModid(modid as string, exclude as Predicate<string>)
```

| Parameter |          Type           | Optional |           Default Value           |
|-----------|-------------------------|----------|-----------------------------------|
| modid     | string                  | false    |                                   |
| exclude   | Predicate&lt;string&gt; | true     | (name as string) as bool => false |


:::

:::group{name=removeByName}

```zenscript
ElectrolysisManager.removeByName(names as string[])
```

| Parameter |   Type   |
|-----------|----------|
| names     | string[] |


:::

:::group{name=removeByRegex}

```zenscript
ElectrolysisManager.removeByRegex(regex as string, exclude as Predicate<string>)
```

| Parameter |          Type           | Optional |           Default Value           |
|-----------|-------------------------|----------|-----------------------------------|
| regex     | string                  | false    |                                   |
| exclude   | Predicate&lt;string&gt; | true     | (name as string) as bool => false |


:::


## Properties

|    Name    |                             Type                              | Has Getter | Has Setter |
|------------|---------------------------------------------------------------|------------|------------|
| allRecipes | stdlib.List&lt;T&gt;                                          | true       | false      |
| recipeMap  | T[[ResourceLocation](/vanilla/api/resource/ResourceLocation)] | true       | false      |

