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