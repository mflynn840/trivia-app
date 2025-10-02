# clean_history.ps1
# WARNING: This rewrites history. Make a backup first!

# Change to your repo folder
Set-Location "C:\Users\micha\AndroidStudioProjects\Trivia-App"  

# Step 0: Backup current main branch
git branch -m main main-backup

# Step 1: Define new commit messages (example for your trivia game)
$cleanCommits = @(
    "Initial project setup for trivia game"
    "Implemented question and answer model"
    "Added player lobby system"
    "Integrated timer and score tracking"
    "Refactored networking and STOMP integration"
    "Added UI components for game over and trophies"
    "Polished code, removed debug logs"
    "Final cleanup and readme update"
)

# Step 2: Get all commit hashes in chronological order
$hashes = git rev-list --reverse main | ForEach-Object { $_ }

# Step 3: Amend commits with cleaned messages
for ($i = 0; $i -lt $cleanCommits.Count; $i++) {
    if ($i -ge $hashes.Count) {
        Write-Warning "Fewer commits than messages. Skipping extra messages."
        break
    }
    $hash = $hashes[$i]
    $msg = $cleanCommits[$i]

    # Checkout the commit to amend
    git checkout $hash

    # Amend commit message
    git commit --amend -m $msg --no-edit
}

# Step 4: Rebase interactively to squash duplicates (manual step recommended)
Write-Host "`nManual step: run the following to squash duplicates interactively:"
Write-Host "git rebase -i --root"

# Step 5: Once satisfied, you can force push (manual confirmation recommended)
Write-Host "`nHistory rewritten. Ready to force-push (after checking):"
Write-Host "git push --force origin main"
