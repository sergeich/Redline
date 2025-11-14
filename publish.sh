#!/bin/bash

# Script to update version in build.gradle.kts and README.md, then publish to Maven Central

set -e  # Exit on error

# Check if version parameter is provided
if [ -z "$1" ]; then
    echo "❌ Error: Version parameter is required"
    echo "Usage: ./publish.sh <version>"
    echo "Example: ./publish.sh 1.0.0"
    exit 1
fi

# Check if on main branch
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
if [ "$CURRENT_BRANCH" != "main" ]; then
    echo "❌ Error: Must be on 'main' branch to publish. Current branch: $CURRENT_BRANCH"
    exit 1
fi

VERSION="$1"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_GRADLE="${SCRIPT_DIR}/redline/build.gradle.kts"
README="${SCRIPT_DIR}/README.md"

# Update version in build.gradle.kts
if [ ! -f "$BUILD_GRADLE" ]; then
    echo "❌ Error: build.gradle.kts not found at $BUILD_GRADLE"
    exit 1
fi

# Update the libraryVersion variable
sed -i '' "s/val libraryVersion = \".*\"/val libraryVersion = \"${VERSION}\"/" "$BUILD_GRADLE"

# Update version in README.md
if [ ! -f "$README" ]; then
    echo "❌ Error: README.md not found at $README"
    exit 1
fi

# Update the implementation line in README.md
sed -i '' "s/implementation 'me\.sergeich:redline:[^']*'/implementation 'me.sergeich:redline:${VERSION}'/" "$README"

cd "$SCRIPT_DIR"
./gradlew :redline:clean :redline:build :redline:publishToMavenCentral

# Create a commit with the message "Bump version."
git add $BUILD_GRADLE $README
git commit -m "Bump version. $VERSION"

# Create a tag for the version
git tag -a "v$VERSION" -m "Bump version. $VERSION"
git push --follow-tags origin main
