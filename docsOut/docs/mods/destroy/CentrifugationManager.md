# CentrifugationManager

## Importing the class

It might be required for you to import the package if you encounter any issues (like casting an Array), so better be safe than sorry and add the import at the very top of the file.
```zenscript
import mods.destroy.CentrifugationManager;
```


## Implemented Interfaces
CentrifugationManager implements the following interfaces. That means all methods defined in these interfaces are also available in CentrifugationManager

- [IDestroyRecipeManager](/mods/destroy/IDestroyRecipeManager)

## Methods

:::group{name=addJsonRecipe}

```zenscript
CentrifugationManager.addJsonRecipe(name as string, mapData as MapData)
```

| Parameter |                 Type                 |
|-----------|--------------------------------------|
| name      | string                               |
| mapData   | [MapData](/vanilla/api/data/MapData) |


:::

:::group{name=addRecipe}

Adds recipe to the centrifuge

```zenscript
CentrifugationManager.addRecipe(name as string, input as FluidIngredient, output as IFluidStack[], processingTime as int)
```

|   Parameter    |                        Type                         |               Description               | Optional | Default Value |
|----------------|-----------------------------------------------------|-----------------------------------------|----------|---------------|
| name           | string                                              | Name of the recipe                      | false    |               |
| input          | [FluidIngredient](/forge/api/fluid/FluidIngredient) | The input fluid                         | false    |               |
| output         | [IFluidStack](/vanilla/api/fluid/IFluidStack)[]     | 2 output fluids per 1 mB of input       | false    |               |
| processingTime | int                                                 | Processing time of this recipe in ticks | true     | 1200          |


:::

:::group{name=getAllRecipes}

Return Type: stdlib.List&lt;T&gt;

```zenscript
// CentrifugationManager.getAllRecipes() as stdlib.List<T>

myCentrifugationManager.getAllRecipes();
```

:::

:::group{name=getRecipeByName}

Return Type: T

```zenscript
CentrifugationManager.getRecipeByName(name as string) as T
```

| Parameter |  Type  |
|-----------|--------|
| name      | string |


:::

:::group{name=getRecipeMap}

Return Type: T[[ResourceLocation](/vanilla/api/resource/ResourceLocation)]

```zenscript
// CentrifugationManager.getRecipeMap() as T[ResourceLocation]

myCentrifugationManager.getRecipeMap();
```

:::

:::group{name=getRecipesByOutput}

Return Type: stdlib.List&lt;T&gt;

```zenscript
CentrifugationManager.getRecipesByOutput(output as IIngredient) as stdlib.List<T>
```

| Parameter |                        Type                        |
|-----------|----------------------------------------------------|
| output    | [IIngredient](/vanilla/api/ingredient/IIngredient) |


:::

:::group{name=registerRecipe}

Registers a recipe using a builder approach.

```zenscript
CentrifugationManager.registerRecipe(name as string, recipeBuilder as Consumer)
```

|   Parameter   |   Type   |       Description       |
|---------------|----------|-------------------------|
| name          | string   | The name of the recipe. |
| recipeBuilder | Consumer | The recipe builder.     |


:::

:::group{name=remove}

```zenscript
CentrifugationManager.remove(output as IIngredient)
```

| Parameter |                        Type                        |
|-----------|----------------------------------------------------|
| output    | [IIngredient](/vanilla/api/ingredient/IIngredient) |


:::

:::group{name=removeAll}

```zenscript
// CentrifugationManager.removeAll()

myCentrifugationManager.removeAll();
```

:::

:::group{name=removeByInput}

```zenscript
CentrifugationManager.removeByInput(input as IItemStack)
```

| Parameter |                    Type                    |
|-----------|--------------------------------------------|
| input     | [IItemStack](/vanilla/api/item/IItemStack) |


:::

:::group{name=removeByModid}

```zenscript
CentrifugationManager.removeByModid(modid as string, exclude as Predicate<string>)
```

| Parameter |          Type           | Optional |           Default Value           |
|-----------|-------------------------|----------|-----------------------------------|
| modid     | string                  | false    |                                   |
| exclude   | Predicate&lt;string&gt; | true     | (name as string) as bool => false |


:::

:::group{name=removeByName}

```zenscript
CentrifugationManager.removeByName(names as string[])
```

| Parameter |   Type   |
|-----------|----------|
| names     | string[] |


:::

:::group{name=removeByRegex}

```zenscript
CentrifugationManager.removeByRegex(regex as string, exclude as Predicate<string>)
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

