"""Validate an installable platform JAR and produce its distributable ZIP."""
import json
import re
import shutil
import sys
import tomllib
import zipfile
from pathlib import Path


def properties():
    props = dict(
        line.strip().split('=', 1)
        for line in Path('gradle.properties').read_text().splitlines()
        if '=' in line and not line.lstrip().startswith('#')
    )
    for key in ('mod_version', 'minecraft_version', 'archives_name'):
        if not re.fullmatch(r'[A-Za-z0-9][A-Za-z0-9.+_-]*', props[key]):
            raise ValueError(f'Invalid {key}')
    if props['minecraft_version'] != '1.21.1':
        raise ValueError('This workflow targets Minecraft 1.21.1')
    return props


def filename(loader, props):
    if loader not in ('fabric', 'neoforge'):
        raise ValueError(f'Unknown loader: {loader}')
    return f"{props['archives_name']}-{loader}-{props['minecraft_version']}-{props['mod_version']}.jar"


def validate(jar, loader, props):
    with zipfile.ZipFile(jar) as archive:
        if archive.testzip() is not None:
            raise ValueError(f'Corrupt JAR: {jar}')
        if loader == 'fabric':
            mod = json.loads(archive.read('fabric.mod.json'))
        else:
            mod = tomllib.loads(archive.read('META-INF/neoforge.mods.toml').decode())['mods'][0]
        if mod['version'] != props['mod_version']:
            raise ValueError('JAR mod version differs from gradle.properties')
        if 'io/github/mortuusars/chalk/world/item/ChalkCraftingRecipe.class' not in archive.namelist():
            raise ValueError('Common mod classes missing from platform JAR')


def main(loader):
    props = properties()
    source = Path(loader, 'build', 'libs', filename(loader, props))
    validate(source, loader, props)
    target = Path('dist')
    target.mkdir(exist_ok=True)
    shutil.copy2(source, target / source.name)
    with zipfile.ZipFile(target / source.with_suffix('.zip').name, 'w', zipfile.ZIP_DEFLATED) as archive:
        archive.write(source, source.name)
    print(f'Packaged {source.name}')


if __name__ == '__main__':
    main(sys.argv[1])
