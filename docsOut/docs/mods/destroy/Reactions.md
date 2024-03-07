# Reactions

## Importing the class

It might be required for you to import the package if you encounter any issues (like casting an Array), so better be safe than sorry and add the import at the very top of the file.
```zenscript
import mods.destroy.Reactions;
```


## Static Methods

:::group{name=create}

Return Type: [ReactionBuilder](/mods/destroy/ReactionBuilder)

```zenscript
Reactions.create(id as string) as ReactionBuilder
```

| Parameter |  Type  |
|-----------|--------|
| id        | string |


:::

:::group{name=getReaction}

Return Type: [Reaction](/mods/destroy/Reaction)

```zenscript
Reactions.getReaction(tokens as string) as Reaction
```

| Parameter |  Type  |
|-----------|--------|
| tokens    | string |


:::

:::group{name=getReactionById}

Return Type: [Reaction](/mods/destroy/Reaction)

```zenscript
Reactions.getReactionById(reactionId as string) as Reaction
```

| Parameter  |  Type  |
|------------|--------|
| reactionId | string |


:::

:::group{name=removeReaction}

```zenscript
Reactions.removeReaction(reaction as Reaction)
```

| Parameter |                Type                |
|-----------|------------------------------------|
| reaction  | [Reaction](/mods/destroy/Reaction) |


:::

