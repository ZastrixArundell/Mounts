# Mounts
A spigot plugin for ESO styled mounts. 

## How does this work?
Similar to how ESO works you will train you riding speed and have multiple mount types which you can summon from
anywhere and they only exist when you are mounted/riding them. Afterwards they are removed from the game and can be respawned!

## How to setup the plugin?
The `config.yml` of the plugin is simple:
```yml
use_sqlite: true

# You only need to use this if you are using MySQL
hostname: ''
port: ''
database: ''
username: ''
password: ''
```
By default the plugin creates a SQLite database inside of the plugin folder. If you set `use_sqlite` to false it uses the
define MySQL database which should be defined in the last part of the settings. The `username` and `password` don't need to be
defined but everything before it does.

## How do I set the type of a horse?
You should take a lot at this image: ![Horses and types](https://raw.githubusercontent.com/ZastrixArundell/Mounts/development/assets/horses.png "Horses and types")

In the plugin you can set the type of the horse as an integer. You just count from the top left towards the right and down on 
every row and see the index of the horse. If you look at the image that the horse which is `White` and 
`None` markings is located at position 1, The same way the horse which is `Brown` with `White spots` is 25.  
