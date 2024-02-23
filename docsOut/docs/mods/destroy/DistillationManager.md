# DistillationManager

## Importing the class

It might be required for you to import the package if you encounter any issues (like casting an Array), so better be safe than sorry and add the import at the very top of the file.
```zenscript
import mods.destroy.DistillationManager;
```


## Implemented Interfaces
DistillationManager implements the following interfaces. That means all methods defined in these interfaces are also available in DistillationManager

- [IDestroyRecipeManager](/mods/destroy/IDestroyRecipeManager)

## Methods

:::group{name=addJsonRecipe}

```zenscript
DistillationManager.addJsonRecipe(name as string, mapData as MapData)
```

| Parameter |                 Type                 |
|-----------|--------------------------------------|
| name      | string                               |
| mapData   | [MapData](/vanilla/api/data/MapData) |


:::

:::group{name=addRecipe}

Adds recipe to the distillation tower.

```zenscript
DistillationManager.addRecipe(name as string, heat as invalid, input as FluidIngredient, fractions as IFluidStack[])
```

| Parameter |                        Type                         |                         Description                          |
|-----------|-----------------------------------------------------|--------------------------------------------------------------|
| name      | string                                              | Name of the recipe                                           |
| heat      | **invalid**                                         | Heat required                                                |
| input     | [FluidIngredient](/forge/api/fluid/FluidIngredient) | Input fluid, can't be a mixture                              |
| fractions | [IFluidStack](/vanilla/api/fluid/IFluidStack)[]     | How much of other fluids should be created per 1 mB of input |


:::

:::group{name=getAllRecipes}

Return Type: stdlib.List&lt;T&gt;

```zenscript
// DistillationManager.getAllRecipes() as stdlib.List<T>

myDistillationManager.getAllRecipes();
```

:::

:::group{name=getRecipeByName}

Return Type: T

```zenscript
DistillationManager.getRecipeByName(name as string) as T
```

| Parameter |  Type  |
|-----------|--------|
| name      | string |


:::

:::group{name=getRecipeMap}

Return Type: T[[ResourceLocation](/vanilla/api/resource/ResourceLocation)]

```zenscript
// DistillationManager.getRecipeMap() as T[ResourceLocation]

myDistillationManager.getRecipeMap();
```

:::

:::group{name=getRecipesByOutput}

Return Type: stdlib.List&lt;T&gt;

```zenscript
DistillationManager.getRecipesByOutput(output as IIngredient) as stdlib.List<T>
```

| Parameter |                        Type                        |
|-----------|----------------------------------------------------|
| output    | [IIngredient](/vanilla/api/ingredient/IIngredient) |


:::

:::group{name=registerRecipe}

Registers a recipe using a builder approach.

```zenscript
DistillationManager.registerRecipe(name as string, recipeBuilder as Consumer)
```

|   Parameter   |   Type   |       Description       |
|---------------|----------|-------------------------|
| name          | string   | The name of the recipe. |
| recipeBuilder | Consumer | The recipe builder.     |


:::

:::group{name=remove}

```zenscript
DistillationManager.remove(output as IIngredient)
```

| Parameter |                        Type                        |
|-----------|----------------------------------------------------|
| output    | [IIngredient](/vanilla/api/ingredient/IIngredient) |


:::

:::group{name=removeAll}

```zenscript
// DistillationManager.removeAll()

myDistillationManager.removeAll();
```

:::

:::group{name=removeByInput}

```zenscript
DistillationManager.removeByInput(input as IItemStack)
```

| Parameter |                    Type                    |
|-----------|--------------------------------------------|
| input     | [IItemStack](/vanilla/api/item/IItemStack) |


:::

:::group{name=removeByModid}

```zenscript
DistillationManager.removeByModid(modid as string, exclude as Predicate<string>)
```

| Parameter |          Type           | Optional |           Default Value           |
|-----------|-------------------------|----------|-----------------------------------|
| modid     | string                  | false    |                                   |
| exclude   | Predicate&lt;string&gt; | true     | (name as string) as bool => false |


:::

:::group{name=removeByName}

```zenscript
DistillationManager.removeByName(names as string[])
```

| Parameter |   Type   |
|-----------|----------|
| names     | string[] |


:::

:::group{name=removeByRegex}

```zenscript
DistillationManager.removeByRegex(regex as string, exclude as Predicate<string>)
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

