#!/bin/bash

# Returns "*" if the current git branch is dirty.
function evil_git_dirty {
  [[ $(git diff --shortstat 2> /dev/null | tail -n1) != "" ]] && echo "*"
}

# For untracked files (Notice the --porcelain flag to git status which gives you nice parse-able output):
# Returns the number of untracked files

function evil_git_num_untracked_files {
  expr `git status --porcelain 2>/dev/null| grep "^??" | wc -l` 
}

function git_local_ref {
  local ref=$(git symbolic-ref HEAD 2>/dev/null | cut -d'/' -f3)
  if [ "$ref" != "" ]
  then
    echo "$ref"
  fi
}

# Returns true when clean, false when dirty
function evil_git_clean {
  DIRTY=$(evil_git_dirty)
  if [ "${DIRTY}" != "*" ] && [ $(evil_git_num_untracked_files) -eq 0 ]; then
    echo "true"
  else 
    echo "false"
  fi
}