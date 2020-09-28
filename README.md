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