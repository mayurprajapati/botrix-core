#!/bin/bash

# # !/bin/bash
# scripts/git_precommit.sh
# paste above two lines in .git/hooks/pre-commit

echo "🚀 Deploying botrix-core..."

# Increment pom.xml version
current_version=$(grep -oPm1 "(?<=<version>)[^<]+" pom.xml)
echo "🔢 Current version: $current_version"

# Increment the version (Assuming semantic versioning like 0.0.10 → 0.0.11)
IFS='.' read -r -a version_parts <<< "$current_version"
new_version="${version_parts[0]}.${version_parts[1]}.$((version_parts[2] + 1))"
echo "🔼 New version: $new_version"

# Update version in pom.xml
sed -i "s|<version>$current_version</version>|<version>$new_version</version>|" pom.xml
git add pom.xml
echo "✅ Updated pom.xml version to $new_version"

# Deploy the project
# Separate build dir: the IDE rebuilds target/ when this script edits pom.xml (see pom <build>)
mvn -Dbotrix.build.dir=target-deploy clean deploy
if [ $? -ne 0 ]; then
    echo "❌ Maven deploy failed!"
    exit 1
fi

# Add updated pom.xml to the current commit
git add pom.xml
echo "✅ Updated pom.xml version and added to commit."

# Move to botrix-flow directory
cd ../botrix-flow || { echo "❌ Failed to navigate to botrix-flow directory"; exit 1; }

# Update dependency version in botrix-flow/pom.xml
sed -i "s|\(<botrix.core.version>\)[^<]*\(</botrix.core.version>\)|\1$new_version\2|" pom.xml

# Commit and push the changes
# git add pom.xml
# git commit -m "pom update"
# git push

echo "✅ Successfully updated botrix-flow dependency version."