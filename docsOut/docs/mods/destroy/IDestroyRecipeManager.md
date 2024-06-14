# IDestroyRecipeManager&LT;T : invalid&GT;

## Importing the class

It might be required for you to import the package if you encounter any issues (like casting an Array), so better be safe than sorry and add the import at the very top of the file.
```zenscript
import mods.destroy.IDestroyRecipeManager;
```


## Implemented Interfaces
IDestroyRecipeManager implements the following interfaces. That means all methods defined in these interfaces are also available in IDestroyRecipeManager

- [IRecipeManager](/vanilla/api/recipe/manager/IRecipeManager)&lt;T&gt;

## Methods

:::group{name=addJsonRecipe}

```zenscript
IDestroyRecipeManager.addJsonRecipe(name as string, mapData as MapData)
```

| Parameter |                 Type                 |
|-----------|--------------------------------------|
| name      | string                               |
| mapData   | [MapData](/vanilla/api/data/MapData) |


:::

:::group{name=getAllRecipes}

Return Type: stdlib.List&lt;T&gt;

```zenscript
// IDestroyRecipeManager.getAllRecipes() as stdlib.List<T>

myIDestroyRecipeManager.getAllRecipes();
```

:::

:::group{name=getRecipeByName}

Return Type: T

```zenscript
IDestroyRecipeManager.getRecipeByName(name as string) as T
```

| Parameter |  Type  |
|-----------|--------|
| name      | string |


:::

:::group{name=getRecipeMap}

Return Type: T[[ResourceLocation](/vanilla/api/resource/ResourceLocation)]

```zenscript
// IDestroyRecipeManager.getRecipeMap() as T[ResourceLocation]

myIDestroyRecipeManager.getRecipeMap();
```

:::

:::group{name=getRecipesByOutput}

Return Type: stdlib.List&lt;T&gt;

```zenscript
IDestroyRecipeManager.getRecipesByOutput(output as IIngredient) as stdlib.List<T>
```

| Parameter |                        Type                        |
|-----------|----------------------------------------------------|
| output    | [IIngredient](/vanilla/api/ingredient/IIngredient) |


:::

:::group{name=registerRecipe}

Registers a recipe using a builder approach.

```zenscript
IDestroyRecipeManager.registerRecipe(name as string, recipeBuilder as Consumer)
```

|   Parameter   |   Type   |       Description       |
|---------------|----------|-------------------------|
| name          | string   | The name of the recipe. |
| recipeBuilder | Consumer | The recipe builder.     |


:::

:::group{name=remove}

```zenscript
IDestroyRecipeManager.remove(output as IIngredient)
```

| Parameter |                        Type                        |
|-----------|----------------------------------------------------|
| output    | [IIngredient](/vanilla/api/ingredient/IIngredient) |


:::

:::group{name=removeAll}

```zenscript
// IDestroyRecipeManager.removeAll()

myIDestroyRecipeManager.removeAll();
```

:::

:::group{name=removeByInput}

```zenscript
IDestroyRecipeManager.removeByInput(input as IItemStack)
```

| Parameter |                    Type                    |
|-----------|--------------------------------------------|
| input     | [IItemStack](/vanilla/api/item/IItemStack) |


:::

:::group{name=removeByModid}

```zenscript
IDestroyRecipeManager.removeByModid(modid as string, exclude as Predicate<string>)
```

| Parameter |          Type           | Optional |           Default Value           |
|-----------|-------------------------|----------|-----------------------------------|
| modid     | string                  | false    |                                   |
| exclude   | Predicate&lt;string&gt; | true     | (name as string) as bool => false |


:::

:::group{name=removeByName}

```zenscript
IDestroyRecipeManager.removeByName(names as string[])
```

| Parameter |   Type   |
|-----------|----------|
| names     | string[] |


:::

:::group{name=removeByRegex}

```zenscript
IDestroyRecipeManager.removeByRegex(regex as string, exclude as Predicate<string>)
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

