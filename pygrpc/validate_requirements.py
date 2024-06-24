import unittest
from pathlib import Path

import pkg_resources

_REQUIREMENTS_PATH = Path(__file__).with_name("requirements.txt")

if __name__ == '__main__':
    """Test that each required package is available."""
    # Ref: https://stackoverflow.com/a/45474387/
    requirements = pkg_resources.parse_requirements(_REQUIREMENTS_PATH.open())
    for requirement in requirements:
        requirement = str(requirement)
        pkg_resources.require(requirement)