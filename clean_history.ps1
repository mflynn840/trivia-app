# 1. Make a test branch
git checkout -b test-clean-history main

# 2. Start interactive rebase
git rebase -i --root
# In the editor:
#   - Replace old commit messages with your clean ones
#   - Squash duplicates as needed
#   - Save and exit
