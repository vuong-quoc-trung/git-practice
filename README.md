# git-practice
A simple project for practicing Git and GitHub commands.

# some general principle and git commands





# types of git clone
1. Normal clone
   git clone URL
   -> Clone a repository normally.

2. Clone and rename the folder
   git clone URL folder-name
   -> Clone the repo into a folder with a custom name.

3. Clone and check out a specific branch
   git clone -b develop URL
   -> Clone the repo and check out the specified branch.

4. Clone only one branch
   git clone --single-branch -b develop URL
   -> Clone only the specified branch.

5. Shallow clone
   git clone --depth 1 URL
   -> Clone only the latest 1 commit.
   -> Change 1 to another number to get more commits.

6. Single branch + shallow clone
   git clone --depth 1 --single-branch -b develop URL
   -> Clone only one branch and only its latest commit(s).

7. Clone with submodules
   git clone --recurse-submodules URL
   -> Clone the repo and its submodules.

8. Bare clone
   git clone --bare URL
   -> Clone only the Git repository, without a working tree.

9. Mirror clone
   git clone --mirror URL
   -> Create a mirrored Git repository, mainly for backup,
    migration, or synchronization.


# types of git add
1. Add a specific file
   git add file.txt
   -> Add a specific file to the staging area.

2. Add all files in the current directory (inculding subdirectories, but not farther directories or on equal levels)
   git add . 
   -> Add all files in the current directory to the staging area ( including new files, modified files, and deleted files).
3. Add all files in the current directory and its subdirectories
   git add -A | git add --all
   -> All files in the repository will be added to the staging area (including new files, modified files, and deleted files).

4. All files updated since the last commit
   git add -u | git add --update
   -> Add all files that have been modified or deleted since the last commit to the staging area, but not new files.
5. Add a hunk of a file
   git add -p file.txt
   -> Add only specific changes (hunks) from a file to the staging area.(a hunk is a contiguous block of changes in a file)

6. Git add * ( not recommended)
   git add *
   -> Add all files in the current directory to the staging area, but it may not include hidden files or files in subdirectories.( * is a wildcard of shell, not a git command, it may not work as expected in some cases, so it's better to use git add . or git add -A instead.)


# types of git commit
1. Commit with a message
   git commit -m "commit message"
   -> Commit the staged changes (git add )with a message. 

2. Commit with a message and all changes, including new files without git add
   git commit -a -m "commit message"
   -> Commit all changes (including modified, deleted files) with a message.

3. Fix the last commit message
   git commit --amend -m "new commit message"
   -> Amend the last commit with a new message. This will replace the previous commit message.

4. Commit the bug fix and point directly to the issue number
   git commit -m "Fixes #123: bug fix description"
   -> Commit the changes and link the commit to a specific issue number (e.g., #123) in the repository. This will automatically close the issue when the commit is pushed to the main branch.   

5. Commit a hunk of a file
   git commit -p file.txt -m "commit message"
   -> Commit only specific changes (hunks) from a file with a message.

# principles of git commit message

1. add new feature: "feat: add new feature"
2. fix a bug: "fix: fix a bug"
3. update documentation: "docs: update documentation"
4. update code style: "style: update code style"
5. refactor code: "refactor: refactor code"
6. add a test: "test: add a test"
7. update build system: "build: update build system"
8. update dependencies: "chore: update dependencies"
9. revert a commit: "revert: revert a commit"
10. update configuration: "config: update configuration"
11. update translation: "i18n: update translation"
12. update performance: "perf: update performance"
13. update security: "security: update security"
14. update accessibility: "accessibility: update accessibility"
15. update internationalization: "l10n: update internationalization"
16. update localization: "l10n: update localization"
17. update logging: "log: update logging"
18. update monitoring: "monitor: update monitoring"
19. update analytics: "analytics: update analytics"
20. update testing: "test: update testing"
21. update deployment: "deploy: update deployment"
22. update CI/CD: "ci: update CI/CD"
23. update release notes: "release: update release notes"
24. update version: "version: update version"


# types of git stash

1. Stash changes
   git stash
   -> Stash the changes in the working directory and index (staging area) to a new stash.

2. Stash changes with a message
   git stash save "stash message"
   -> Stash the changes in the working directory and index (staging area) to a new stash with a message.

3. Stash changes and include untracked files
   git stash -u
   -> Stash the changes in the working directory and index (staging area) to a new stash, including untracked files.

4. Restore stashed changes
   git stash apply
   -> Restore the most recent stashed changes to the working directory and index (staging area).

5. Restore stashed changes and remove the stash
   git stash pop
   -> Restore the most recent stashed changes to the working directory and index (staging area), and remove the stash from the stash list.

6. List stashes
   git stash list
   -> List all stashes in the repository.

7. Restore a specific stash
   git stash apply stash@{n}
   -> Restore a specific stash (where n is the index of the stash in the list).
