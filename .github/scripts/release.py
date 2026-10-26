"""Check release provenance and both loader artifacts before publication."""
import os
import subprocess
import zipfile
from pathlib import Path
from package import filename, properties, validate

props = properties()
version = props['mod_version']
tag = f'v{version}'
ref = os.environ['GITHUB_REF']
if ref.startswith('refs/tags/') and ref != f'refs/tags/{tag}':
    raise ValueError(f'Release tag must be {tag}, matching mod_version')
# A manual rerun must never attach new binaries to a tag for a different commit.
existing = subprocess.run(
    ['git', 'rev-parse', '--verify', f'refs/tags/{tag}^{{commit}}'],
    capture_output=True, text=True,
)
if existing.returncode == 0 and existing.stdout.strip() != os.environ['GITHUB_SHA']:
    raise ValueError(f'{tag} already points to another commit')
for loader in ('fabric', 'neoforge'):
    jar = Path('dist', filename(loader, props))
    validate(jar, loader, props)
    with zipfile.ZipFile(jar.with_suffix('.zip')) as archive:
        if archive.namelist() != [jar.name] or archive.read(jar.name) != jar.read_bytes():
            raise ValueError(f'ZIP does not contain the matching JAR: {loader}')

changelog = Path('Changelog.md').read_text()
section = changelog.split('\n## ', 1)[1].split('\n## ', 1)[0]
if section.splitlines()[0].split(' - ', 1)[0] != version:
    raise ValueError('Latest changelog entry must match mod_version')
Path('release-notes.md').write_text('## ' + section.strip() + '\n')
with open(os.environ['GITHUB_OUTPUT'], 'a') as output:
    output.write(f'tag={tag}\nversion={version}\n')
print(f'Ready to publish {tag} with Fabric and NeoForge assets')
