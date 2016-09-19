# LittleBits-Bukkit

Bukkit server-side plugin for integrating with littleBits.

## Installation
Just drop the [`LittleBits-Bukkit.jar`](https://github.com/rlf/LittleBits-Bukkit/releases/latest) in the `plugins` folder of your Bukkit compatible minecraft server, and restart the server.

## Configuration

Before configuring the LittleBits-Bukkit plugin, you need to find the `TOKEN` of your littleBits CloudBit account.
The littleBits have a page describing this process here: http://developers.littlebitscloud.cc/access

Once you have that token, you can either use the in-game command:
```
/littlebits account add TOKEN
```
Where you simply copy-paste the 64-character token in place of the `TOKEN` above, or you can manually add the following section to your `plugins/LittleBits-Bukkit/devices.yml` file:
```
accounts:
  <64-character-TOKEN-here>:
    label: My Account
```
and then do a `/littlebits reload` (not needed when adding it using the in-game command above).

To test whether the connection to the bitCloud went as planned, invoke the `/bits acc list` command.
If the output is something along the lines of:
```
All accounts:
  Account: My Account
    - device1 (My Account:00e04c52b9a2, in=0, out=0, connected)
```
Your are all set to continue.

## Usage

The plugin adds a recipe to manually create a `littleBits` block, using a diamond, an emerald and a repeater.
Place the two gems on either side of the comparator, and you will get an enchanted Repeater named `littleBits`.

This item can also be obtained by invoking:
```
/littlebits block give [playername]
```

When this block is placed in the world, it will be `unassigned` per default.
Right-clicking the block will cycle through all the devices registered in the plugin.

Once a device has been associated to a block, that block will start outputting what-ever input the `littleBits` device in the real-world is receiving.

If a redstone signal (using redstone wire) is directed into the back-side of the `littleBits` block in minecraft, the `littleBits` device in the real-world will be instructed to output a signal accordingly.

Note: I/O from the devices in the real-world is from 0-100, where as the redstone signals in the minecraft world is from 0-15.

## TODO

* Support changing label of devices (requires `admin` role in OAuth against littlebits.cc).
* Support registering devices (requires `admin` scope).
* Support custom recipe for LittleBits device (move recipe to `config.yml`)
* Support more detailed debugging (i.e. raw-cloud-api feedback).
* ~~Support more languages (i18n) -  esp. Danish~~
* More minified jar-file (we include too much of apache httpcomponents atm).

## License
This is licensed under Apache License v2.0
