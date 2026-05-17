# Git commands used to push source code to GitHub

## 0) Init repo (because no .git existed)
```bat
git init
```

## 1) Add ignore rules
```bat
# (created .gitignore file manually)
```

## 2) Stage + commit
```bat
git add .
git config user.name "Your Name"
git config user.email "you@example.com"
git commit -m "Initial implementation - Java bill payment CLI"
```

## 3) Add remote origin + push
```bat
git remote add origin https://github.com/tntran14/momo_test.git
git push -u origin master
```

## 4) Verify status
```bat
git status
