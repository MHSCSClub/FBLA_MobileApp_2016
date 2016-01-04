# Git Documentation
Standards and practices for git.

#### Commits
Commit messages should be in the following format: `[action] work done`. Examples:
+ **Updated** server code to match standards
+ **Created** new activity to handle login
+ **Edited** SQL documentation
+ **Fixed** bugs present in server communication
+ **Moved** documentation to new folder

#### Basic Guide

**Before** I have made any changes:
+ `git pull`: get latest changes

I've made my changes and I'm ready to push!
+ `git push`: push up your changes

Oh no! Errors!
+ `git fetch`: fetch the latest changes
+ `git rebase origin/master`: Rebase changes **DO NOT MERGE**

