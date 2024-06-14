# ChargingManager

## Importing the class

It might be required for you to import the package if you encounter any issues (like casting an Array), so better be safe than sorry and add the import at the very top of the file.
```zenscript
import mods.destroy.ChargingManager;
```


## Implemented Interfaces
ChargingManager implements the following interfaces. That means all methods defined in these interfaces are also available in ChargingManager

- [IDestroyRecipeManager](/mods/destroy/IDestroyRecipeManager)

## Methods

:::group{name=addJsonRecipe}

```zenscript
ChargingManager.addJsonRecipe(name as string, mapData as MapData)
```

| Parameter |                 Type                 |
|-----------|--------------------------------------|
| name      | string                               |
| mapData   | [MapData](/vanilla/api/data/MapData) |


:::

:::group{name=addRecipe}

Adds a recipe to the dynamo machine. (This feature is likely to get removed or heavily modified in future release of Destroy)

```zenscript
// ChargingManager.addRecipe(name as string, input as IIngredient, output as IItemStack)

myChargingManager.addRecipe("charge_iron", <item:minecraft:coal>, <item:minecraft:iron_ingot>);
```

| Parameter |                        Type                        |    Description     |
|-----------|----------------------------------------------------|--------------------|
| name      | string                                             | Name of the recipe |
| input     | [IIngredient](/vanilla/api/ingredient/IIngredient) | Item to charge     |
| output    | [IItemStack](/vanilla/api/item/IItemStack)         | Output item        |


:::

:::group{name=getAllRecipes}

Return Type: stdlib.List&lt;T&gt;

```zenscript
// ChargingManager.getAllRecipes() as stdlib.List<T>

myChargingManager.getAllRecipes();
```

:::

:::group{name=getRecipeByName}

Return Type: T

```zenscript
ChargingManager.getRecipeByName(name as string) as T
```

| Parameter |  Type  |
|-----------|--------|
| name      | string |


:::

:::group{name=getRecipeMap}

Return Type: T[[ResourceLocation](/vanilla/api/resource/ResourceLocation)]

```zenscript
// ChargingManager.getRecipeMap() as T[ResourceLocation]

myChargingManager.getRecipeMap();
```

:::

:::group{name=getRecipesByOutput}

Return Type: stdlib.List&lt;T&gt;

```zenscript
ChargingManager.getRecipesByOutput(output as IIngredient) as stdlib.List<T>
```

| Parameter |                        Type                        |
|-----------|----------------------------------------------------|
| output    | [IIngredient](/vanilla/api/ingredient/IIngredient) |


:::

:::group{name=registerRecipe}

Registers a recipe using a builder approach.

```zenscript
ChargingManager.registerRecipe(name as string, recipeBuilder as Consumer)
```

|   Parameter   |   Type   |       Description       |
|---------------|----------|-------------------------|
| name          | string   | The name of the recipe. |
| recipeBuilder | Consumer | The recipe builder.     |


:::

:::group{name=remove}

```zenscript
ChargingManager.remove(output as IIngredient)
```

| Parameter |                        Type                        |
|-----------|----------------------------------------------------|
| output    | [IIngredient](/vanilla/api/ingredient/IIngredient) |


:::

:::group{name=removeAll}

```zenscript
// ChargingManager.removeAll()

myChargingManager.removeAll();
```

:::

:::group{name=removeByInput}

```zenscript
ChargingManager.removeByInput(input as IItemStack)
```

| Parameter |                    Type                    |
|-----------|--------------------------------------------|
| input     | [IItemStack](/vanilla/api/item/IItemStack) |


:::

:::group{name=removeByModid}

```zenscript
ChargingManager.removeByModid(modid as string, exclude as Predicate<string>)
```

| Parameter |          Type           | Optional |           Default Value           |
|-----------|-------------------------|----------|-----------------------------------|
| modid     | string                  | false    |                                   |
| exclude   | Predicate&lt;string&gt; | true     | (name as string) as bool => false |


:::

:::group{name=removeByName}

```zenscript
ChargingManager.removeByName(names as string[])
```

| Parameter |   Type   |
|-----------|----------|
| names     | string[] |


:::

:::group{name=removeByRegex}

```zenscript
ChargingManager.removeByRegex(regex as string, exclude as Predicate<string>)
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

