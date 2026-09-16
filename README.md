# git-practice
A simple project for practicing Git and GitHub commands.


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