# AgingManager

## Importing the class

It might be required for you to import the package if you encounter any issues (like casting an Array), so better be safe than sorry and add the import at the very top of the file.
```zenscript
import mods.destroy.AgingManager;
```


## Implemented Interfaces
AgingManager implements the following interfaces. That means all methods defined in these interfaces are also available in AgingManager

- [IDestroyRecipeManager](/mods/destroy/IDestroyRecipeManager)

## Methods

:::group{name=addJsonRecipe}

```zenscript
AgingManager.addJsonRecipe(name as string, mapData as MapData)
```

| Parameter |                 Type                 |
|-----------|--------------------------------------|
| name      | string                               |
| mapData   | [MapData](/vanilla/api/data/MapData) |


:::

:::group{name=addRecipe}

Adds an aging recipe to the aging barrel

```zenscript
// AgingManager.addRecipe(name as string, input as FluidIngredient, items as IIngredient[], result as IFluidStack, processingTime as int)

myAgingManager.addRecipe("wine_aging", <fluid:minecraft:water>, [<item:minecraft:apple>, <item:minecraft:bonemeal>], <fluid:minecraft:lava> per 1 bucket of input, 1200);
```

|   Parameter    |                         Type                         |              Description               | Optional | Default Value |
|----------------|------------------------------------------------------|----------------------------------------|----------|---------------|
| name           | string                                               | Name of the recipe                     | false    |               |
| input          | [FluidIngredient](/forge/api/fluid/FluidIngredient)  | Input fluid of the recipe              | false    |               |
| items          | [IIngredient](/vanilla/api/ingredient/IIngredient)[] | Additional items to the aging process  | false    |               |
| result         | [IFluidStack](/vanilla/api/fluid/IFluidStack)        | Resulting fluid                        | false    |               |
| processingTime | int                                                  | Processing time of the recipe in ticks | true     | 1200          |


:::

:::group{name=getAllRecipes}

Return Type: stdlib.List&lt;T&gt;

```zenscript
// AgingManager.getAllRecipes() as stdlib.List<T>

myAgingManager.getAllRecipes();
```

:::

:::group{name=getRecipeByName}

Return Type: T

```zenscript
AgingManager.getRecipeByName(name as string) as T
```

| Parameter |  Type  |
|-----------|--------|
| name      | string |


:::

:::group{name=getRecipeMap}

Return Type: T[[ResourceLocation](/vanilla/api/resource/ResourceLocation)]

```zenscript
// AgingManager.getRecipeMap() as T[ResourceLocation]

myAgingManager.getRecipeMap();
```

:::

:::group{name=getRecipesByOutput}

Return Type: stdlib.List&lt;T&gt;

```zenscript
AgingManager.getRecipesByOutput(output as IIngredient) as stdlib.List<T>
```

| Parameter |                        Type                        |
|-----------|----------------------------------------------------|
| output    | [IIngredient](/vanilla/api/ingredient/IIngredient) |


:::

:::group{name=registerRecipe}

Registers a recipe using a builder approach.

```zenscript
AgingManager.registerRecipe(name as string, recipeBuilder as Consumer)
```

|   Parameter   |   Type   |       Description       |
|---------------|----------|-------------------------|
| name          | string   | The name of the recipe. |
| recipeBuilder | Consumer | The recipe builder.     |


:::

:::group{name=remove}

```zenscript
AgingManager.remove(output as IIngredient)
```

| Parameter |                        Type                        |
|-----------|----------------------------------------------------|
| output    | [IIngredient](/vanilla/api/ingredient/IIngredient) |


:::

:::group{name=removeAll}

```zenscript
// AgingManager.removeAll()

myAgingManager.removeAll();
```

:::

:::group{name=removeByInput}

```zenscript
AgingManager.removeByInput(input as IItemStack)
```

| Parameter |                    Type                    |
|-----------|--------------------------------------------|
| input     | [IItemStack](/vanilla/api/item/IItemStack) |


:::

:::group{name=removeByModid}

```zenscript
AgingManager.removeByModid(modid as string, exclude as Predicate<string>)
```

| Parameter |          Type           | Optional |           Default Value           |
|-----------|-------------------------|----------|-----------------------------------|
| modid     | string                  | false    |                                   |
| exclude   | Predicate&lt;string&gt; | true     | (name as string) as bool => false |


:::

:::group{name=removeByName}

```zenscript
AgingManager.removeByName(names as string[])
```

| Parameter |   Type   |
|-----------|----------|
| names     | string[] |


:::

:::group{name=removeByRegex}

```zenscript
AgingManager.removeByRegex(regex as string, exclude as Predicate<string>)
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

