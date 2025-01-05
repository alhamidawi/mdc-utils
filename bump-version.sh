#!/bin/bash
#
# works with a file called VERSION in the current directory,
# the contents of which should be a semantic version number
# such as "1.2.3" or even "1.2.3-beta+001.ab"
#
# this script will display the current version, automatically
# suggest a "minor" version update, and ask for input to use
# the suggestion, or a newly entered value.
#
# once the new version number is determined, the script will
# pull a list of changes from git history, prepend this to
# a file called CHANGELOG.md (under the title of the new version
# number), give user a chance to review and update the changelist
# manually if needed.

NOW="$(date +'%B %d, %Y')"
GREEN="\033[0;32m"
YELLOW="\033[1;33m"
CYAN="\033[1;36m"
WHITE="\033[1;37m"

LATEST_HASH=$(git log --pretty=format:'%h' -n 1)

QUESTION_FLAG="${GREEN}?"
WARNING_FLAG="${YELLOW}!"
NOTICE_FLAG="${CYAN}❯"

ADJUSTMENTS_MSG="${QUESTION_FLAG} ${CYAN}Now you can make adjustments to ${WHITE}CHANGELOG.md${CYAN}. Then press enter to continue."
PUSHING_MSG="${NOTICE_FLAG} Pushing new version to the ${WHITE}origin${CYAN}..."

if [ -f VERSION ]; then
    BASE_STRING=$(cat VERSION)
    IFS='.' read -r -a BASE_LIST <<< "$BASE_STRING"
    V_MAJOR=${BASE_LIST[0]}
    V_MINOR=${BASE_LIST[1]}
    V_PATCH=${BASE_LIST[2]}
    echo -e "${NOTICE_FLAG} Current version: ${WHITE}$BASE_STRING"
    echo -e "${NOTICE_FLAG} Latest commit hash: ${WHITE}$LATEST_HASH"
    V_PATCH=$((V_PATCH + 1))
    SUGGESTED_VERSION="$V_MAJOR.$V_MINOR.$V_PATCH"
    echo -ne "${QUESTION_FLAG} ${CYAN}Enter a version number [${WHITE}$SUGGESTED_VERSION${CYAN}]: "
    read -r INPUT_STRING
    if [ "$INPUT_STRING" = "" ]; then
        INPUT_STRING=$SUGGESTED_VERSION
    else
      if [[ "$INPUT_STRING" != v* ]]; then
        echo "Version must start with prefix v, eg: v2.0.0. Please fix it."
        exit
      fi
    fi
    echo -e "${NOTICE_FLAG} Will set new version to be ${WHITE}$INPUT_STRING"
    echo "$INPUT_STRING" > VERSION
    echo "## [$INPUT_STRING] - $NOW" > tmpfile
    {
      git log --pretty=format:"  - %s" "$BASE_STRING"...HEAD
      echo ""
      echo ""
      cat CHANGELOG.md
    } >> tmpfile
    mv tmpfile CHANGELOG.md
    echo -e "$ADJUSTMENTS_MSG"
    read -r
    ./mvnw versions:set -DnewVersion="${INPUT_STRING}"
    ./mvnw versions:commit
    echo -e "$PUSHING_MSG"
    git add CHANGELOG.md VERSION pom.xml
    git commit -m "Bump version to ${INPUT_STRING}."
    git push origin
else
    echo -e "${WARNING_FLAG} Could not find a VERSION file."
    echo -ne "${QUESTION_FLAG} ${CYAN}Do you want to create a version file and start from scratch? [${WHITE}y${CYAN}]: "
    read -r RESPONSE
    if [ "$RESPONSE" = "" ]; then RESPONSE="y"; fi
    if [ "$RESPONSE" = "Y" ]; then RESPONSE="y"; fi
    if [ "$RESPONSE" = "Yes" ]; then RESPONSE="y"; fi
    if [ "$RESPONSE" = "yes" ]; then RESPONSE="y"; fi
    if [ "$RESPONSE" = "YES" ]; then RESPONSE="y"; fi
    if [ "$RESPONSE" = "y" ]; then
        echo "v0.1.0" > VERSION
        echo "## v0.1.0 ($NOW)" > CHANGELOG.md
        {
          git log --pretty=format:"  - %s"
          echo ""
          echo ""
        } >> CHANGELOG.md
        echo -e "$ADJUSTMENTS_MSG"
        read -r
        echo -e "$PUSHING_MSG"
        git add VERSION CHANGELOG.md
        git commit -m "Add VERSION and CHANGELOG.md files, Bump version to v0.1.0."
        git push origin
    fi
fi

echo -e "${NOTICE_FLAG} Finished."