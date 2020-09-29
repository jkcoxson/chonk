# Chonk
This is a fork of vktec's [original mod](https://github.com/vktec/chonk), updated for 1.16.2 with a few more features.

> vktec 27/02/2020
>
> Random idea for how remote chunk loading could be implemented nicely: block updates into a border chunk loads it lazily for 8gt (makes permanent lazy loading possible, also allows instant wires etc to travel through unloaded chunks without breaking), triggering a dropper that's not facing into an inventory or a dispenser in a lazy chunk loads that chunk as entity processing for 8gt (allows remote loading of portal permaloaders, remote loaded entity processing chunks for pearl cannons, permaloaded entity processing chunks in the end, etc.)

## Configuration

The config file is saved to `<world folder>/chonk.yml`. The following is the default config
```yaml
# See the Github for recommended and default settings
# https://github.com/logwet/chonk

# Config file schema version
version: 1

# Enables the mod
enabled: true

# Lifetime of each ticket
life: 8

# Controls the pot feature: Can be: include|exclude|disable
pot: INCLUDE

# The plant that activates the pot feature
# Format it like this: FERN or ORANGE_TULIP. Full list of possible values in Github
potType: CACTUS

# Chunks registered in the pot system
worlds: []
```
##### version
Changes whenever there is a backwards incompatible revision to the config file schema.

##### enabled
If false, all other features of Chonk will be active except the actual chunk loading. Ie. Block and Dispenser updates
will be recognised and processed but will not trigger a Chonk load.

##### life
The lifetime of each chunk load ticket in ticks.

##### pot
If `INCLUDE` or `EXCLUDE`, Chonk will tag every chunk in which you place a (by default) cactus in a flower pot, and
serialize this data to the `worlds` field in the config file.

If `INCLUDE`, only tagged chunks can be loaded by Chonk. A block update or dispenser firing into an untagged chunk won't
do anything. A good use case for this is if you want to only Chonk load your instant wires, just place cactus pots along
those lines.

If `EXCLUDE`, all chunks in all worlds can be Chonk loaded except for tagged chunks. This is useful if you want to use
Chonk globally without the hassle of pots but still want to protect unsafe redstone (eg. clocks that cross chunk borders).

`DISABLE` will completely disable this feature (but previously tagged chunks will stay saved if you renable the feature.)

##### potType
The type of pottable plant that will trigger that chunk to be tagged.
[Full list here](https://minecraft.gamepedia.com/Flower_Pot#Data_values)
*Note: Omit the `potted_` prefix*

##### worlds
This array will be populated with tagged chunks and the world they are in.