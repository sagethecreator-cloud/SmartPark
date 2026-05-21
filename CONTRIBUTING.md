Contributing to SmartPark
Thanks for being part of the SmartPark team! This guide explains our Git workflow so all three members stay in sync and nothing breaks on `main`.
---
🌿 Branch Structure
```
main          ← Stable, demo-ready code only. Never commit directly here.
  └── dev     ← Integration branch. All features merge here first.
        ├── member1/Saad   ← Saad's working branch
        └── member2/Taha   ← Taha's working branch
```
> \*\*Sage\*\* manages the `dev → main` merge after review.
---
🔄 Daily Workflow
1. Before you start working — always pull first
```bash
git checkout dev
git pull origin dev
git checkout member1/Saad    # or your own branch
git merge dev                # bring in latest changes
```
2. Make your changes and commit
```bash
git add .
git commit -m "feat: add slot availability check in ParkingLot"
```
3. Push to your branch
```bash
git push origin member1/Saad
```
4. Open a Pull Request → `dev`
Go to GitHub → Pull Requests → New Pull Request
Base: `dev` | Compare: `member1/Saad`
Add a short description of what you changed
Request a review from another team member
---
✍️ Commit Message Format
Use this pattern so the history is easy to read:
```
type: short description
```
Type	Use for
`feat`	A new feature
`fix`	A bug fix
`refactor`	Code restructure (no behavior change)
`style`	Formatting, CSS tweaks
`docs`	README or comment updates
`test`	Adding or editing tests
`chore`	Build config, .gitignore changes
Examples:
```
feat: add EV charging rate controller
fix: null pointer in ExitVehicleController
docs: update README with setup instructions
refactor: extract billing logic into Bill.calculate()
```
---
⚠️ Rules
Never push directly to `main` — only Sage merges there after final review
Never force-push (`git push --force`) to `dev` or `main`
If you have a conflict, resolve it on your own branch before the PR
Keep commits small and focused — one logical change per commit
---
🆘 Stuck?
Message the group chat or open a GitHub Issue. Don't let a blocker sit for more than a day.
